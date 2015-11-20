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
package cz.vutbr.fit.util.join;

import java.io.Serializable;
import java.util.ArrayList;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import cz.vutbr.fit.BusinessObjects.Chargeback;
import cz.vutbr.fit.BusinessObjects.Message;

/**
 * A class representing 1 to N relationship between messages and chargebacks
 * @author ikouril
 */
@objid ("c81511dd-d799-44ad-bc4f-bf4ea813e508")
public class MessageChargebacks implements Serializable {
    @objid ("8d3c09f1-4626-4f46-b500-35006dfd4790")
    private static final long serialVersionUID = 8155422710160330591L;

    @objid ("a1fa6c22-8286-48e1-b58c-9189cf936a45")
    private Message message;

    @objid ("2e4ae3c5-ebe2-41f7-9bad-e7caa26e4e5e")
    private ArrayList<Chargeback> chargebacks;

    /**
     * Creates a new MessageChargebacks object.
     * @param message the message object from which is MessageChargebacks created
     */
    @objid ("65f9ba2b-df5d-4f8e-a9d3-301b85de0314")
    public MessageChargebacks(Message message) {
        this.message=message;
        chargebacks=new ArrayList<Chargeback>();
    }

    /**
     * Creates a new CardTransactions object.
     * @param card the chargeback object from which is MessageChargebacks created
     */
    @objid ("43bce3bc-bf58-4213-89e5-cff4629c2292")
    public MessageChargebacks(Chargeback chargeback) {
        chargebacks=new ArrayList<Chargeback>();
        chargebacks.add(chargeback);
        message=null;
    }

    @objid ("21c648ad-24b2-4380-bc82-0a3467ba7aa4")
    public boolean hasMessage() {
        return message!=null;
    }

    @objid ("243b7872-c086-45d7-9cee-d5052bafc324")
    public void addChargeback(Chargeback chargeback) {
        chargebacks.add(chargeback);
    }

    @objid ("2fcfe5d5-a3d2-4718-9500-203170fe8814")
    public ArrayList<Chargeback> getChargebacks() {
        return chargebacks;
    }

    @objid ("644678c6-fa58-41e7-a1b4-9f166dc09d15")
    public void clearChargebacks() {
        chargebacks.clear();
    }

    @objid ("c25ccf9b-fd4c-4485-b02f-bb9736ba6a54")
    public Message getMessage() {
        return message;
    }

    @objid ("773f99da-841f-406c-8643-66b37b922d5c")
    public void setMessage(Message message) {
        this.message=message;
    }

}
