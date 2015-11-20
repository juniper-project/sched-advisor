/*
 * Copyright (c) 2015, Brno University of Technology, Faculty of Information Technology
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of sched-advisor nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cz.vutbr.fit;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import cz.vutbr.fit.BusinessObjects.Aggregated;
import cz.vutbr.fit.util.write.AggregatedList;
import cz.vutbr.fit.util.write.DayStatistics;
import cz.vutbr.fit.util.write.HourStatistics;
import cz.vutbr.fit.util.write.MonthStatistics;
import cz.vutbr.fit.util.write.YearStatistics;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.modelio.juniper.platform.Provided;

@objid ("c63749e6-a1b4-4122-b91a-322a663fa670")
public class Write extends org.modelio.juniper.platform.JuniperProgram {
    @objid ("db1a902a-bac5-4ff9-ae9c-ec66a7507e75")
    @Provided
    public IWrite iWriteImpl = new cz.vutbr.fit.IWrite() {
		@Override
		public void acceptAggregate(List<Aggregated> inList) {
			long startTime = System.nanoTime();
			System.out.println("Entering write");
			for (Aggregated in:inList){
				DateTime currentDate=in.getDate();
				String event=in.getEvent();
				TreeMap<String, AggregatedList>  map=events.get(event);
				String inTimestamp=in.getTimestamp();
				AggregatedList nearbyEvents=(AggregatedList) map.get(inTimestamp);
				
				//first should be incoming Aggregated object added to appropriate 5-min chunk
				if (nearbyEvents!=null){
					boolean found=false;
					for (Aggregated nearbyEvent:nearbyEvents.values()){
		
						if (nearbyEvent.getCard_id().equals(in.getCard_id())){
							nearbyEvent.incBalance(in.getBalance_change());
							found=true;
							break;
						}
					}
					if (!found)
						nearbyEvents.add(in);
				}
				else{
					AggregatedList newEvents=new AggregatedList();
					newEvents.add(in);
					map.put(inTimestamp, newEvents);
				}
				
				
				
				//going through every type of object to resolve whether to flush it, make statistics, or delete it
				for (TreeMap<String, AggregatedList>  particularEvents : events.values()) {
		
					Iterator<AggregatedList> iter=particularEvents.values().iterator();
					while(iter.hasNext()){
						AggregatedList eventsToFlush=iter.next();
						boolean makeFlush=false;
						int between=0;
						for (Aggregated agg:eventsToFlush.values()){
							if (!makeFlush){
								String timestamp=agg.getTimestamp();
								
								String[] timeValues=timestamp.split("-");
								DateTime date=new DateTime(Integer.parseInt(timeValues[0]),Integer.parseInt(timeValues[1]),Integer.parseInt(timeValues[2]),Integer.parseInt(timeValues[3]),Integer.parseInt(timeValues[4]));
								
								//event distant more than 5 minutes and "flushInterval" seconds from start of event "window"
								between=Seconds.secondsBetween(date, currentDate).getSeconds();
								if (between>300+flushInterval)
									makeFlush=true;
								else
									break;
							}
							if (!eventsToFlush.getFlushed()){
								System.out.println("Flushing event: "+agg.toString());
								//event 5-min chunk aggregated, can be stored to some database, or emitted ...
							}
							else
								break;
						}
						boolean canMakeStats=false;
						if (makeFlush){
							if (eventsToFlush.getFlushed()){
								//value already added -- outlier
								System.out.println("Adding to already flushed events: "+in.toString());	
								canMakeStats=true;
							}
							eventsToFlush.setFlushed(true);
						}
						else{
							//since events are sorted in a treemap, first not to be flushed event means, that there are no more events to flush
							//oldest events will come first
							break;
						}
						
						//updating statistics
						if (between>300+statisticInterval){
							if (!eventsToFlush.hasStatistics() || canMakeStats){
								
								String[] timestampValues=inTimestamp.split("-");
								
								YearStatistics year=stats.get(timestampValues[0]);
								if (year==null){
									year=new YearStatistics(numberComparator);
									stats.put(timestampValues[0], year);
								}
								
								MonthStatistics month=year.get(timestampValues[1].replaceFirst("^0+(?!$)", ""));
								DayStatistics day=month.get(timestampValues[2].replaceFirst("^0+(?!$)", ""));
								HourStatistics hour=day.get(timestampValues[3].replaceFirst("^0+(?!$)", ""));
								//outlier
								if (canMakeStats)
									hour.get(event).add(in);
								else
									hour.get(event).add(eventsToFlush);
								
								eventsToFlush.setStatistics(true);
								
								
								//some emit or storing data to database should be here
								//statistic objects have methods for returning number of operations and deviations by event (transaction, billing ...)
								//these data should form a histogram
								
							}
						}
							
						
						//if actual event is a lot newer than stored one, delete the stored one
						if (between>300+removeInterval){
							iter.remove();
						}
						
					}
				}
			}
			
			Long estimatedTime = System.nanoTime() - startTime;
			System.out.println("Write process time: "+String.valueOf(estimatedTime));
		}};

    @objid ("c0eedd84-2a53-4ebe-b652-0e2d63491091")
     int flushInterval;

    @objid ("f7389dfe-1d83-4eb0-ae84-a81ec10b0f67")
     int removeInterval;

    @objid ("a2f7bb6e-a2f6-4d3a-814e-a1dc5e4f3867")
     int statisticInterval;

    @objid ("b2953f0a-aea8-4ba5-b058-daa13de4b7ed")
     TreeMap<String, YearStatistics> stats;

    @objid ("ee66ea78-6d5d-49a9-9de7-6505cc2f6ed7")
     TreeMap<String, AggregatedList> billings;

    @objid ("12b8076c-7097-43b9-b619-2fb9ed964211")
     TreeMap<String, AggregatedList> redemptions;

    @objid ("9badcd0c-d16b-4e5b-85fb-a47265ddc2f1")
     TreeMap<String, AggregatedList> refunds;

    @objid ("5731e4c4-2fd0-49f4-80c0-ed765496b1b3")
     TreeMap<String, AggregatedList> replenishments;

    @objid ("f8f7ae4b-bdf8-40cc-87d4-aafc88c27d2b")
     TreeMap<String, AggregatedList> transactions;

    @objid ("86fa876a-7c9c-435e-873b-3b8d9ebb1264")
     TreeMap<String, AggregatedList> chargebacks;

    @objid ("a5c2f3ae-c7fc-402e-aebb-cd5891090e68")
     HashMap<String, TreeMap<String,AggregatedList>> events = new HashMap<String,TreeMap<String,AggregatedList>>();

    @objid ("80ec6302-b806-440f-b9af-301ec2e47b33")
     DateComparator dateComparator = null;

    @objid ("9c85cd01-4de3-46c4-84da-5d1ef7f68374")
     NumberComparator numberComparator = null;

    @objid ("ac4afd94-b878-4541-a9b3-b2fffc1fa62e")
    public Write() {
        super();
        
        numberComparator=new NumberComparator();
        dateComparator=new DateComparator();
        this.flushInterval=30;
        this.statisticInterval=3600;
        this.removeInterval=600;
        
        TreeMap<String, AggregatedList> billings = new TreeMap<String, AggregatedList>(dateComparator);
        TreeMap<String, AggregatedList> redemptions = new TreeMap<String, AggregatedList>(dateComparator);
        TreeMap<String, AggregatedList> refunds = new TreeMap<String, AggregatedList>(dateComparator);
        TreeMap<String, AggregatedList> replenishments = new TreeMap<String, AggregatedList>(dateComparator);
        TreeMap<String, AggregatedList> transactions = new TreeMap<String, AggregatedList>(dateComparator);
        TreeMap<String, AggregatedList> chargebacks = new TreeMap<String, AggregatedList>(dateComparator);
        
        events.put("billing",billings);
        events.put("redemption",redemptions);
        events.put("refund",refunds);
        events.put("replenishment",replenishments);
        events.put("transaction",transactions);
        events.put("chargeback",chargebacks);
        
        stats=new TreeMap<String,YearStatistics>(numberComparator);
    }

    /**
     * Comparator for sorting data in TreeMaps holding hours, days, months and years
     */
    @objid ("444123af-7ff2-4992-a42e-5b55ecbe1241")
    class NumberComparator implements Comparator<String>, Serializable {
        @objid ("c243c9b9-95fc-450a-ac50-d9174a19c36b")
        private static final long serialVersionUID = -8309113527026653000L;

        @objid ("ece95edc-7237-49f0-bed6-c9a75780cd94")
        @Override
        public int compare(String h1, String h2) {
            Integer h1val=Integer.parseInt(h1);
            Integer h2val=Integer.parseInt(h2);
            return h1val.compareTo(h2val);
        }

    }

    /**
     * Comparator for sorting incoming Aggregated data by timestamps
     */
    @objid ("5b1c9e4b-9596-4677-a8c0-c5f5e5e2df9a")
    class DateComparator implements Comparator<String>, Serializable {
        @objid ("e734f882-c444-48f4-a2ee-732ba73c921c")
        private static final long serialVersionUID = -8309113527026653000L;

        @objid ("11bcfe1e-fb02-4cad-bed8-98b3816295c0")
        @Override
        public int compare(String o1, String o2) {
            String[] timeValues1=o1.split("-");
            DateTime date1=new DateTime(Integer.parseInt(timeValues1[0]),Integer.parseInt(timeValues1[1]),Integer.parseInt(timeValues1[2]),Integer.parseInt(timeValues1[3]),Integer.parseInt(timeValues1[4]));
            String[] timeValues2=o2.split("-");
            DateTime date2=new DateTime(Integer.parseInt(timeValues2[0]),Integer.parseInt(timeValues2[1]),Integer.parseInt(timeValues2[2]),Integer.parseInt(timeValues2[3]),Integer.parseInt(timeValues2[4]));
            return date1.compareTo(date2);
        }

    }

}
