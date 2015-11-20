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
 * Class for holding statistics for a month
 * @author ikouril
 */
@objid ("4c479d66-cbcd-4d94-bea8-763c15e0a67e")
public class MonthStatistics implements Serializable {
    @objid ("bc211e37-82cb-4b23-934f-411cc7fc0c4b")
    private static final long serialVersionUID = -1350071528718570964L;

    @objid ("373ea162-007e-4fd6-a541-3c02b43fc7ff")
     TreeMap<String, DayStatistics> stats;

    @objid ("b14b5237-cf87-481b-95d3-593530672722")
     Comparator<String> comparator = null;

    /**
     * Creates a new MonthStatistics object
     * @param comparator - how should be months sorted
     */
    @objid ("6e0590b5-62c7-4a55-bae2-3516a4517bf8")
    public MonthStatistics(Comparator<String> comparator) {
        stats=new TreeMap<String,DayStatistics>(comparator);
        this.comparator=comparator;
    }

    /**
     * Gets number of days we have statistics for
     * @return
     */
    @objid ("d94d1d84-7ad3-4508-a256-9ca6e28b9e81")
    public int statsSize() {
        return stats.size();
    }

    /**
     * Gets map of day statistics
     * @return all day statistics
     */
    @objid ("af05e25c-ed18-47b8-bafd-e83353e224f2")
    public TreeMap<String, DayStatistics> getStats() {
        return stats;
    }

    /**
     * Gets statistics of given day
     * @param day - requested day
     * @return day statistics
     */
    @objid ("693d2c5a-0007-46d1-acf0-3c26701a9e6f")
    public DayStatistics get(String day) {
        DayStatistics d=stats.get(day);
        if (d!=null){
            return d;
        }
        else{
            DayStatistics ds=new DayStatistics(comparator);
            stats.put(day,ds);
            return ds;
        }
    }

    /**
     * Gets number of operations in given day of requested event
     * @param day - given day
     * @param event - requested event
     * @return number of operations
     */
    @objid ("f8809dd8-afe8-418f-92a5-40248bc8bfdd")
    public int numOperations(String day, String event) {
        DayStatistics dayStats=stats.get(day);
        return dayStats.getTotalOperations(event);
    }

    /**
     * Computes absolute deviation of n-th hour of a day in this month by particular event
     * @param event - requested event
     * @param hour - n-th hour of day in time
     * @return absolute deviation
     */
    @objid ("9ca2b204-70f5-4759-b762-20d3f0509fe8")
    public float getDeviationByHour(String event, String hour) {
        int total=0;
        ArrayList<Integer>values=new ArrayList<Integer>();
        for (DayStatistics day:stats.values()){
                int val=day.numOperations(hour, event);
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
     * Computes standard deviation over this month over its days by particular event
     * @param event - requested event
     * @return absolute deviation
     */
    @objid ("30b7000d-abdc-4412-ab9e-17d0759ed584")
    public float getDeviation(String event) {
        int total=getTotalOperations(event);
        Float average=(float)total/stats.size();
        Float dev=0.0F;
        for (DayStatistics val:stats.values()){
            dev+=Math.abs(average-(float)val.getTotalOperations(event));
        }
        return dev/(float)total;
    }

    /**
     * Gets total number of operation in this month by particular event
     * @param event - requested event
     * @return total operations
     */
    @objid ("92fb8816-43b5-423f-9435-ffd7afedc23c")
    public int getTotalOperations(String event) {
        int total=0;
        for (DayStatistics val:stats.values()){
            total+=val.getTotalOperations(event);
        }
        return total;
    }

}
