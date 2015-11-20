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
import java.util.List;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import cz.vutbr.fit.BusinessObjects.Aggregated;

/**
 * Class for holding list of aggregated objects in a 5-min chunk
 * @author ikouril
 */
@objid ("3a86ada6-ca2d-4083-9595-6dd317ba9548")
public class AggregatedList implements Serializable {
    @objid ("664f8802-59df-4cb5-93bc-2aeefdc16794")
    private static final long serialVersionUID = 3265149736248661529L;

    @objid ("6c5eca6b-d6d8-4177-8ca2-474628f7d175")
     boolean flushed;

    @objid ("1d48a55a-83a3-48d2-89ad-87b0e8345712")
     boolean statisticsCreated;

    @objid ("b6b0bc22-ac48-4a30-b483-053f198ea460")
    private List<Aggregated> vals;

    @objid ("8bb9e5ce-d7ad-418a-9446-673c99362fc4")
    public AggregatedList() {
        vals=new ArrayList<Aggregated>();
        flushed=false;
        statisticsCreated=false;
    }

    @objid ("e41ade95-d5d6-46aa-a95c-932a4ce07683")
    public void setFlushed(boolean f) {
        flushed=f;
    }

    @objid ("6371c8ef-8d00-4f9e-84b4-27d57c4566bc")
    public boolean getFlushed() {
        return flushed;
    }

    @objid ("0a028962-2242-4476-8cb0-90def61c097a")
    public List<Aggregated> values() {
        return vals;
    }

    @objid ("c41fb828-6134-4282-82db-44a15bff4443")
    public void add(Aggregated a) {
        vals.add(a);
    }

    @objid ("af4bb899-5779-4c15-8c4a-4cf103ddbb3f")
    public int size() {
        return vals.size();
    }

    @objid ("12c1db0d-b34d-4d4f-b136-83f5871935e3")
    public void setStatistics(boolean b) {
        statisticsCreated=b;
    }

    @objid ("5a407d1b-8f11-405a-b7e7-3d23df5a520a")
    public boolean hasStatistics() {
        return statisticsCreated;
    }

}
