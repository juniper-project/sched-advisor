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
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import com.modeliosoft.modelio.javadesigner.annotations.objid;

/**
 * Class for holding statistics for a day
 * @author ikouril
 */
@objid ("b61bd617-85cc-4afa-8d2f-057d29d47aa9")
public class DayStatistics implements Serializable {
    @objid ("76ca817e-5316-41c4-be4c-da6190bea623")
    private static final long serialVersionUID = 1693109981011138840L;

    @objid ("71f86863-f209-4eee-91a2-c3c0e27113a7")
     Comparator<String> comparator = null;

    @objid ("0ed5db44-b6bd-4015-a591-a14d7ef642f0")
     TreeMap<String, HourStatistics> stats;

    /**
     * Creates a new DayStatistics object
     * @param comparator - how should be days sorted
     */
    @objid ("5240b6db-c016-477e-bca5-0fafe5763c29")
    public DayStatistics(Comparator<String> comparator) {
        stats=new TreeMap<String,HourStatistics>(comparator);
        this.comparator=comparator;
    }

    /**
     * Gets hour statistics
     * @param hour - for which should be statistics retrieved
     * @return requested HourStatistics object
     */
    @objid ("204c2ffa-c7a4-4f8c-9bf1-fac307b960a0")
    public HourStatistics get(String hour) {
        HourStatistics h=stats.get(hour);
        if (h!=null){
            return h;
        }
        else{
            HourStatistics ns=new HourStatistics();
            stats.put(hour,ns);
            return ns;
        }
    }

    /**
     * Gets number of operations in given hour for requested event
     * @param hour - given hour
     * @param event - requested event
     * @return number of operations
     */
    @objid ("7222c26c-f4fe-40e0-b5cc-001ef05a5d30")
    public int numOperations(String hour, String event) {
        HourStatistics h=stats.get(hour);
        if (h==null)
            return 0;
        HourStatistic hs=h.get(event);
        return hs.getNumOperations();
    }

    /**
     * Gets number of operations in given hour for requested event
     * @param hour - given hour
     * @param event - requested event
     * @return number of operations
     */
    @objid ("a7796f62-1659-40be-898e-3a247a109ab7")
    public int numOperations(int hour, String event) {
        return numOperations(String.valueOf(hour),event);
    }

    /**
     * Computes absolute deviation of this day over its hours by particular event
     * @param event - requested event
     * @return absolute deviation across hours over day
     */
    @objid ("4d56f280-3227-4865-a6f1-6bc40a627fa4")
    public float getDeviation(String event) {
        int total=getTotalOperations(event);
        Float average=(float)total/stats.size();
        Float dev=0.0F;
        for (HourStatistics val:stats.values()){
            dev+=Math.abs(average-(float)val.getTotalOperations(event));
        }
        return dev/(float)total;
    }

    /**
     * Computes number of operations over day
     * @param event - requested event
     * @return total number of operations
     */
    @objid ("58c791e5-a2b8-411a-b01e-20e063626d9d")
    public int getTotalOperations(String event) {
        int total=0;
        for (HourStatistics val:stats.values()){
            total+=val.getTotalOperations(event);
        }
        return total;
    }

}
