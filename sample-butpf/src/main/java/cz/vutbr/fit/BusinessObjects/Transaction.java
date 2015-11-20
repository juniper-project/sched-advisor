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
package cz.vutbr.fit.BusinessObjects;

import java.io.Serializable;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * A class representing transaction data
 * @author ikouril
 */
@objid ("88a1684f-4325-48e7-9403-ab9cc3d09f47")
public class Transaction implements Serializable {
    @objid ("30c31ae1-ebd6-4d9c-b111-8738a34e91c5")
    private static final long serialVersionUID = 2524052710308718019L;

    @objid ("ab2e7a13-39f6-4d5e-a696-4f8ef98f964f")
    private String creditcards_pan = "";

    @objid ("52916019-0c23-4cdc-9858-4358e876a245")
    private DateTime creation_timestamp = new DateTime();

    @objid ("1d4f9216-7b9e-4d91-92e3-92cc337c4bfb")
    private String incomming_messages_uuid = "";

    @objid ("16f97833-86c1-4489-b968-841b56e15384")
    private boolean is_atm = false;

    @objid ("a983b40d-9da8-49cf-b8e7-d8ee8808d9bd")
    private float amount = 0.0F;

    /**
     * Creates a new Transaction object.
     * @param in the line of csv data from which is object created
     */
    @objid ("0fbf832b-30aa-4f18-b535-976bd83655c8")
    public Transaction(String in) {
        try{
            String[] params=in.split(",");
            creditcards_pan=params[0];
            if (!params[1].isEmpty())
                creation_timestamp=DateTime.parse(params[1],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            incomming_messages_uuid=params[2];
            is_atm=params[3].equals("1");
            if (params[4].isEmpty())
                amount=Float.parseFloat(params[4]);
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("e48ff99e-1930-486d-83d1-e48c5c6f2582")
    public String getCreditcards_pan() {
        return creditcards_pan;
    }

    @objid ("6619c6a3-31aa-437f-9d19-941bf695e1f9")
    public void setCreditcards_pan(String creditcards_pan) {
        this.creditcards_pan = creditcards_pan;
    }

    @objid ("95199dad-cd3d-4c36-8d47-ce43b5e05e3b")
    public DateTime getCreation_timestamp() {
        return creation_timestamp;
    }

    @objid ("ff7e06e6-4bf0-47d5-9295-f72cfbcac385")
    public void setCreation_timestamp(DateTime creation_timestamp) {
        this.creation_timestamp = creation_timestamp;
    }

    @objid ("de7780eb-1b27-4f75-bf8b-ebbf88ebaf0d")
    public String getIncomming_messages_uuid() {
        return incomming_messages_uuid;
    }

    @objid ("335e1c3a-5463-4775-a591-5ef9d4d13635")
    public void setIncomming_messages_uuid(String incomming_messages_uuid) {
        this.incomming_messages_uuid = incomming_messages_uuid;
    }

    @objid ("5aed69f9-e4da-489f-9081-395334621632")
    public boolean isIs_atm() {
        return is_atm;
    }

    @objid ("619a0be3-c67f-4e63-80da-c9706d984b70")
    public void setIs_atm(boolean is_atm) {
        this.is_atm = is_atm;
    }

    @objid ("e854de6f-dc34-4833-87a3-8bcf0752e1a8")
    public float getAmount() {
        return amount;
    }

    @objid ("bc2eb082-d8c9-4472-bad0-deb12904385b")
    public void setAmount(float amount) {
        this.amount = amount;
    }

}
