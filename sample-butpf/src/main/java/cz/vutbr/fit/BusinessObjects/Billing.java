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
 * A class representing billing data
 * @author ikouril
 */
@objid ("24be6a8a-5870-4909-bc13-1224b4ce24c2")
public class Billing implements Serializable {
    @objid ("66e507e1-af59-4ad0-a3ef-86a69a0320f1")
    private static final long serialVersionUID = 6111725121333329986L;

    @objid ("0798d3f2-9b74-42b9-b523-3520dfccdf23")
    private String product = "";

    @objid ("22c3307d-2276-4e91-b2e0-9cfeadc038da")
    private DateTime date = new DateTime();

    @objid ("b5ebed62-897d-491a-b19f-7baa1ad22a01")
    private String id = "";

    @objid ("41344ca8-e38d-456c-a868-1d6e6ee0bb97")
    private String card_id = "";

    @objid ("88737cf6-3364-48e8-a01d-5db631999259")
    private float transferamount = 0.0F;

    @objid ("32fefc70-43c2-4017-87f7-4bb2cdb61771")
    private String users_id = "";

    @objid ("42b6bf82-e3b4-45b7-be0a-ae2a8f449619")
    private boolean isrefund = false;

    @objid ("da26302c-fa63-4bd3-abec-e79aacc3f70b")
    private String tjfrecords_id = "";

    /**
     * Creates a new Billing object.
     * @param in the line of csv data from which is object created
     */
    @objid ("5be8682f-929c-484c-8131-44706799648a")
    public Billing(String in) {
        try{
            String[] params=in.split(",");
            product=params[0];
            if (!params[1].isEmpty())
                date=DateTime.parse(params[1],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            id=params[2];
            card_id=params[3];
            if (!params[4].isEmpty())
                transferamount=Float.valueOf(params[4]);
            users_id=params[5];
            isrefund=params[6].equals("1");
            tjfrecords_id=params[7];
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("f21587d7-2218-4215-a2ec-cde312150f94")
    public DateTime getDate() {
        return date;
    }

    @objid ("f7ba8100-1ed0-46e6-bebf-b4c4133fd106")
    public void setDate(DateTime date) {
        this.date = date;
    }

    @objid ("56293dda-9325-4648-be28-7da3a60eb656")
    public String getCard_id() {
        return card_id;
    }

    @objid ("0d439809-b429-4635-ae55-f12a2cc97225")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    @objid ("c586527e-eb9c-4879-8d1d-2e73c418e5aa")
    public String getId() {
        return id;
    }

    @objid ("0aed82ce-9f79-4941-846c-106b5682d700")
    public void setId(String id) {
        this.id = id;
    }

    @objid ("9fba65c4-10d7-496e-9813-79caaabb75c4")
    public float getTransferamount() {
        return transferamount;
    }

    @objid ("67671587-ab30-4d11-ace4-f9714f53916b")
    public void setTransferamount(float transferamount) {
        this.transferamount = transferamount;
    }

    @objid ("4152ff9e-8742-4076-9071-3046ee7ba41d")
    public String getUsers_id() {
        return users_id;
    }

    @objid ("c781f1b0-fff7-49e0-a8d4-c4e3b47e7c1b")
    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }

    @objid ("5d78f339-b882-4eaf-a52b-fc9df4a8e6c4")
    public boolean isIsrefund() {
        return isrefund;
    }

    @objid ("86024e5c-0043-46e0-ae45-bb438fe44909")
    public void setIsrefund(boolean isrefund) {
        this.isrefund = isrefund;
    }

    @objid ("0cf2f6a2-e991-4469-8331-c166f1d12840")
    public String getTjfrecords_id() {
        return tjfrecords_id;
    }

    @objid ("2deb0ac0-01f4-4c9d-860b-bb86e6a0d63c")
    public void setTjfrecords_id(String tjfrecords_id) {
        this.tjfrecords_id = tjfrecords_id;
    }

    @objid ("3a91a08b-ee20-4958-9faa-7ec2ebba5c18")
    public String getProduct() {
        return product;
    }

    @objid ("59662bb5-2e30-4dcb-a6a7-0d680e1a2313")
    public void setProduct(String product) {
        this.product = product;
    }

}
