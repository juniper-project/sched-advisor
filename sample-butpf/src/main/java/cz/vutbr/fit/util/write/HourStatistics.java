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
import java.util.HashMap;
import com.modeliosoft.modelio.javadesigner.annotations.objid;

/**
 * Object holding hour statistics for every type of event
 * @author ikouril
 */
@objid ("26364e90-7a65-4f12-a2a8-be0334f89f8d")
public class HourStatistics implements Serializable {
    @objid ("8985f2a1-e89d-4201-aa3a-93df24a73fe4")
    private static final long serialVersionUID = -5872795782710983064L;

    @objid ("b1f9f871-7496-4260-b1b4-d620cad0db27")
     HashMap<String, HourStatistic> eventStatistics;

    /**
     * Creates new HourStatistics object
     */
    @objid ("0bc7bd30-b5cd-4b3a-969f-192b1358b89c")
    public HourStatistics() {
        eventStatistics=new HashMap<String,HourStatistic>();
        eventStatistics.put("billing", new HourStatistic());
        eventStatistics.put("redemption", new HourStatistic());
        eventStatistics.put("refund", new HourStatistic());
        eventStatistics.put("replenishment", new HourStatistic());
        eventStatistics.put("transaction", new HourStatistic());
        eventStatistics.put("chargeback", new HourStatistic());
    }

    /**
     * Gets hour statistic of particular event
     * @param event - given event to retrieve statistics from
     * @return requested HourStatistic object
     */
    @objid ("97ae21f1-341b-48ce-a5f5-0ddbb8121256")
    public HourStatistic get(String event) {
        return eventStatistics.get(event);
    }

    /**
     * Gets total number of operations in an hour
     * @param event - given event to retrieve statistics from
     * @return number of operations
     */
    @objid ("b63e1b44-1e23-461c-8fe7-4beaa164d3bf")
    public int getTotalOperations(String event) {
        HourStatistic h=eventStatistics.get(event);
        return h.getNumOperations();
    }

}
