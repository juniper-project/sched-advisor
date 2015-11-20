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
package cz.vutbr.fit.util.storage;

import java.io.Serializable;
import com.modeliosoft.modelio.javadesigner.annotations.objid;

/**
 * A class for a pair
 * @author ikouril
 */
@objid ("27cba489-1671-4d36-a95e-0931e329bff6")
public class Pair<A, B> implements Serializable {
    @objid ("572f7629-70c9-484a-bcba-29596cafa5bc")
    private static final long serialVersionUID = 4791587865024402874L;

    @objid ("a2f95b9a-ab4b-4b63-a400-0964cb255ada")
    private A first;

    @objid ("edf3daec-90ea-4f7b-81fc-8d83a13f6cc5")
    private B second;

    /**
     * Creates a new Pair object.
     * @param first the first object
     * @param second the second object
     */
    @objid ("54350221-e1e3-4c77-9dbf-6392ad62074b")
    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    @objid ("7a4f5495-1d3e-42d5-848f-659431a6c88c")
    public A getFirst() {
        return first;
    }

    @objid ("b360e507-da35-4e6e-a122-fa04f89d6e5d")
    public void setFirst(A first) {
        this.first = first;
    }

    @objid ("389196fc-cbb1-4330-9779-2dd2a147aaec")
    public B getSecond() {
        return second;
    }

    @objid ("693aa406-abae-4062-bd9c-ef2a8b7b4a36")
    public void setSecond(B second) {
        this.second = second;
    }

}
