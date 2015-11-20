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
 * A class representing replenishment data
 * @author ikouril
 */
@objid ("d9370c30-7e38-4f21-a721-5f854c7e1dbd")
public class Replenishment implements Serializable {
    @objid ("b9ef92ec-6202-479c-8389-0657cb759f8d")
    private static final long serialVersionUID = -3075219320025596770L;

    @objid ("9d5a0af7-cf88-4d8b-aece-88c2a19570e9")
    private String product = "";

    @objid ("4b0c6109-4c4f-482d-a83c-d61b96588647")
    private DateTime date = new DateTime();

    @objid ("0acb5d5d-c5bc-449d-acb7-f158e58cad5d")
    private float transferamount = 0.0F;

    @objid ("cd1cd582-2dfd-4c13-9d6f-008318ac69fd")
    private String card_id = "";

    @objid ("1f2c5f43-d2a2-4fd2-92d2-28bcf67f30d4")
    private String users_id = "";

    /**
     * Creates a new Replenishment object.
     * @param in the line of csv data from which is object created
     */
    @objid ("4d0912d7-0b93-43fd-9fba-e98c4c52c8cf")
    public Replenishment(String in) {
        try{
            String[] params=in.split(",");
            product=params[0];
            if (!params[1].isEmpty())
                date=DateTime.parse(params[1],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            if (!params[2].isEmpty())
                transferamount=Float.parseFloat(params[2]);
            card_id=params[3];
            users_id=params[4];
            }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("85f3f4c7-94ce-4af3-ab1c-9ab4629e1de2")
    public String getProduct() {
        return product;
    }

    @objid ("efd30f3f-b271-40db-b790-bfadde657e14")
    public void setProduct(String product) {
        this.product = product;
    }

    @objid ("8ccfcc65-dadf-4627-ba6e-edcc01be3d34")
    public DateTime getDate() {
        return date;
    }

    @objid ("29c30628-c610-4597-867e-225ab37606da")
    public void setDate(DateTime date) {
        this.date = date;
    }

    @objid ("a2562e1c-5f9c-498d-bc2c-70d0633546cf")
    public String getCard_id() {
        return card_id;
    }

    @objid ("9450df46-c004-4bd1-8360-1278c7e93542")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    @objid ("69b7b7e0-9f89-4a66-bcf5-ba2102950474")
    public float getTransferamount() {
        return transferamount;
    }

    @objid ("41fa5b8b-5463-48cf-a1da-d1603ffd1d0f")
    public void setTransferamount(float transferamount) {
        this.transferamount = transferamount;
    }

    @objid ("0e72d596-1de6-462c-8013-58d05d83bab4")
    public String getUsers_id() {
        return users_id;
    }

    @objid ("4193383c-b98e-4aba-9dba-c37c04056bdc")
    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }

}
