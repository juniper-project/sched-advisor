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
 * A class representing refund data
 * @author ikouril
 */
@objid ("c2c1ffdd-bfea-43b7-a15d-2ee2aedce72d")
public class Refund implements Serializable {
    @objid ("1de52579-dfff-41e1-8041-fe994a44de2e")
    private static final long serialVersionUID = 2759376916978191112L;

    @objid ("0707c93e-328f-4918-b882-4c511dd9ae24")
    private String product = "";

    @objid ("0274c580-20c6-46ca-8b96-a151347e9741")
    private DateTime date = new DateTime();

    @objid ("6ea8456e-7ad8-4a7b-80a7-caa03f4a713a")
    private String card_id = "";

    @objid ("eacdf171-fd48-4eea-a493-7effb909792a")
    private String users_id = "";

    @objid ("f2768be7-4da0-4d72-acb9-52a36db5ce4c")
    private float amount = 0.0F;

    @objid ("95bfd23c-53ee-4703-84b7-ac64c000e42d")
    private String reason = "";

    @objid ("753917c5-169e-4247-ae16-d0d4aed1bc89")
    private boolean iscredit = false;

    @objid ("c39bf00b-1e10-4919-8f28-cfdf4d5a534b")
    private int dta_refund_reasoncode = 0;

    /**
     * Creates a new Refund object.
     * @param in the line of csv data from which is object created
     */
    @objid ("c45ee2ab-1209-4e7a-8b93-332c58b42a3b")
    public Refund(String in) {
        try{
            String[] params=in.split(",");
            product=params[0];
            if (!params[1].isEmpty())
                date=DateTime.parse(params[1],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            card_id=params[2];
            users_id=params[3];
            if (!params[4].isEmpty())
                amount=Float.parseFloat(params[4]);
            reason=params[5];
            iscredit=params[6].equals("1");
            if (params[7].isEmpty())
                dta_refund_reasoncode=Integer.parseInt(params[7]);
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("5ab7cf09-62cd-44a1-9974-51c301ad4ab5")
    public String getProduct() {
        return product;
    }

    @objid ("b73ac47e-747f-45aa-8b7b-867392ee0008")
    public void setProduct(String product) {
        this.product = product;
    }

    @objid ("679bf59a-79f7-4095-b4e2-924cbfcbfa8f")
    public DateTime getDate() {
        return date;
    }

    @objid ("cd2e5cc8-7fa1-4d61-b5d7-e0a94497cc5c")
    public void setDate(DateTime date) {
        this.date = date;
    }

    @objid ("a1986bb1-949c-4f64-820f-b14d0e6d9507")
    public String getCard_id() {
        return card_id;
    }

    @objid ("93afda19-dedf-4649-89e4-e1dc3380697a")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    @objid ("b68ddd2c-823b-432a-a401-57bae6d16a14")
    public String getUsers_id() {
        return users_id;
    }

    @objid ("87700a1c-e43c-4144-932a-edb05ea72450")
    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }

    @objid ("0a7a153b-28b0-4c80-b13f-f64e9ddd4283")
    public float getAmount() {
        return amount;
    }

    @objid ("ad3c1076-355c-42cb-a193-03963a359799")
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @objid ("6fac6042-a2b6-48ba-9ee5-2d8ef0195a13")
    public String getReason() {
        return reason;
    }

    @objid ("9959cef4-011e-467d-9dc6-f80e067bdb2f")
    public void setReason(String reason) {
        this.reason = reason;
    }

    @objid ("401044fd-02da-49fa-8a74-0498e7f72e14")
    public boolean isIscredit() {
        return iscredit;
    }

    @objid ("4bd3f53b-802b-44d4-a18a-b5cd52342cc0")
    public void setIscredit(boolean iscredit) {
        this.iscredit = iscredit;
    }

    @objid ("b7d75a09-458d-41fa-a087-642161e43957")
    public int getDta_refund_reasoncode() {
        return dta_refund_reasoncode;
    }

    @objid ("821bef54-2097-428d-9b0a-f58dbccb2072")
    public void setDta_refund_reasoncode(int dta_refund_reasoncode) {
        this.dta_refund_reasoncode = dta_refund_reasoncode;
    }

}
