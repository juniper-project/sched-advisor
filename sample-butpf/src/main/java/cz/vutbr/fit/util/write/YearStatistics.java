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
package cz.vutbr.fit.util.write;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import com.modeliosoft.modelio.javadesigner.annotations.objid;

/**
 * Class for holding statistics for a year
 * @author ikouril
 */
@objid ("5e11e237-7728-4b2a-97d2-fab887faac9c")
public class YearStatistics implements Serializable {
    @objid ("f4985519-96f9-4106-b0c1-56bad1b2b203")
    private static final long serialVersionUID = -1350071528718570964L;

    @objid ("a407d9de-d2a1-41a3-b62e-fd82223bfd11")
     TreeMap<String, MonthStatistics> stats;

    @objid ("298f5942-2f65-4e74-bee7-99da18d10838")
     Comparator<String> comparator = null;

    /**
     * Creates a new YearStatistics object
     * @param comparator - how should be months sorted
     */
    @objid ("9c30f941-f816-4aff-84a4-14067b6ac197")
    public YearStatistics(Comparator<String> comparator) {
        stats=new TreeMap<String,MonthStatistics>(comparator);
        this.comparator=comparator;
    }

    /**
     * Gets statistics of given month
     * @param month - requested month
     * @return month statistics
     */
    @objid ("16ce527b-66ac-4f79-9f0d-2abe6b2bd35e")
    public MonthStatistics get(String month) {
        MonthStatistics m=stats.get(month);
        if (m!=null){
            return m;
        }
        else{
            MonthStatistics ms=new MonthStatistics(comparator);
            stats.put(month,ms);
            return ms;
        }
    }

    /**
     * Gets number of operations in given month of requested event
     * @param month - given month
     * @param event - requested event
     * @return number of operations
     */
    @objid ("ae66ad05-94ba-48ed-88e6-41381b55d155")
    public int numOperations(String month, String event) {
        MonthStatistics monthStats=stats.get(month);
        return monthStats.getTotalOperations(event);
    }

    /**
     * Computes absolute deviation of n-th hour of a day in this year by particular event
     * @param event - requested event
     * @param hour - target hour
     * @return absolute deviation
     */
    @objid ("1ff17f1e-67fb-4b40-afcd-e37cfb938dbf")
    public Float getDeviationByHour(String event, String hour) {
        int total=0;
        ArrayList<Integer>values=new ArrayList<Integer>();
        
        for (MonthStatistics month:stats.values()){
            for (DayStatistics day:month.getStats().values()){
                int val=day.numOperations(hour, event);
                total+=val;
                values.add(val);
            }
        }
        
        Float average=(float)total/values.size();
        Float dev=0.0F;
        for (Integer val:values){
            dev+=Math.abs(average-(float)val);
        }
        return dev/(float)total;
    }

    /**
     * Computes absolute deviation of n-th day of a month in this year by particular event
     * @param event - requested event
     * @param day - target day
     * @return absolute deviation
     */
    @objid ("01372d1e-d23f-49f4-b679-7a4cafad5603")
    public Float getDeviationByDay(String event, String day) {
        int total=0;
        ArrayList<Integer>values=new ArrayList<Integer>();
        for (MonthStatistics month:stats.values()){
                int val=month.numOperations(day, event);
                total+=val;
                values.add(val);
        }
        
        Float average=(float)total/values.size();
        Float dev=0.0F;
        for (Integer val:values){
            dev+=Math.abs(average-(float)val);
        }
        return dev/(float)total;
    }

    /**
     * Computes standard deviation over this year over its months by particular event
     * @param event - requested event
     * @return absolute deviation
     */
    @objid ("61ce3619-4d6c-4d3b-95f5-338f7c46841c")
    public Float getDeviation(String event) {
        int total=getTotalOperations(event);
        Float average=(float)total/stats.size();
        Float dev=0.0F;
        for (MonthStatistics val:stats.values()){
            dev+=Math.abs(average-(float)val.getTotalOperations(event));
        }
        return dev/(float)total;
    }

    /**
     * Gets total number of operations this year by given event
     * @param event - given event to retrieve statistics from
     * @return number of operations
     */
    @objid ("fb966ece-e571-4699-8188-4017b7118fe3")
    public int getTotalOperations(String event) {
        int total=0;
        for (MonthStatistics val:stats.values()){
            total+=val.getTotalOperations(event);
        }
        return total;
    }

}
