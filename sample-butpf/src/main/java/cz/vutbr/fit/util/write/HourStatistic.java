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
import com.modeliosoft.modelio.javadesigner.annotations.objid;

/**
 * A class for holding hour statistics of particular event
 * @author ikouril
 */
@objid ("b89f4274-7099-4098-b128-53774d4422c6")
public class HourStatistic implements Serializable {
    @objid ("5f0402ff-5b76-44fe-937b-3cbbfd250b83")
    private static final long serialVersionUID = -8657310140690031277L;

    @objid ("cee7ffd9-afc8-4315-831e-d945274e0df8")
    private int numOperations;

    /**
     * Creates HourStatistic object
     */
    @objid ("58f3d219-a880-4aec-b226-5daef2757d88")
    public HourStatistic() {
        numOperations=0;
    }

    /**
     * Adds list of aggregated events in 5-min chunk to hour statistics
     * @param events - given events to add
     */
    @objid ("76f7acc1-f8a6-4d21-8366-dddeb272db83")
    public void add(AggregatedList events) {
        for (cz.vutbr.fit.BusinessObjects.Aggregated a:events.values()){
            numOperations+=a.getNumChanged();
        }
    }

    /**
     * Adds one outlier event to hour statistics
     * @param event - given event to add
     */
    @objid ("6d4eb648-3508-4048-8d59-a75533be4474")
    public void add(cz.vutbr.fit.BusinessObjects.Aggregated event) {
        numOperations+=event.getNumChanged();
    }

    /**
     * Gets total number of operations in an hour
     * @return number of operations
     */
    @objid ("aadc3c5f-c811-4306-a6ae-7b466217b251")
    public int getNumOperations() {
        return numOperations;
    }

}
