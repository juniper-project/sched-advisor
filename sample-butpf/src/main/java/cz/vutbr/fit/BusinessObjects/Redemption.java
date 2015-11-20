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
 * A class representing redemption data
 * @author ikouril
 */
@objid ("5b2dbf51-397f-4e45-972c-655f83e97423")
public class Redemption implements Serializable {
    @objid ("478716ab-52d4-4122-8e3f-4b4bda710ae7")
    private static final long serialVersionUID = 8619206531677162079L;

    @objid ("08b46eab-d5a5-4b78-9d50-2ed2f223b635")
    private String product = "";

    @objid ("dd6a67b5-672c-4060-853d-18ccbbad9cd4")
    private String card_id = "";

    @objid ("e7df19d9-fb03-4c78-90f1-fa4060e17efd")
    private float amount = 0.0F;

    @objid ("24b10bed-c9b3-433c-a87e-214463fcc8d3")
    private DateTime date = new DateTime();

    @objid ("1b28c262-85a6-4a87-96dc-69b52e7c8ea4")
    private boolean isfeeclearing = false;

    @objid ("5fb92904-6b5b-4eed-8f5e-81a24901e70a")
    private boolean stopped = false;

    /**
     * Creates a new Redemption object.
     * @param in the line of csv data from which is object created
     */
    @objid ("5472f431-c4df-4518-a306-9e98a0649485")
    public Redemption(String in) {
        try{
            String[] params=in.split(",");
            product=params[0];
            card_id=params[1];
            if (!params[2].isEmpty())
                amount=Float.parseFloat(params[2]);
            if (!params[3].isEmpty())
                date=DateTime.parse(params[3],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            isfeeclearing=params[4].equals("1");
            stopped=params[5].equals("1");
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("2c6969af-c2fd-4d93-8a46-28401e25d8a9")
    public String getProduct() {
        return product;
    }

    @objid ("1aa0054b-e434-4e28-bda7-70719ea58e53")
    public void setProduct(String product) {
        this.product = product;
    }

    @objid ("c21a3481-b187-45b7-9368-f3be4130e152")
    public String getCard_id() {
        return card_id;
    }

    @objid ("e686ee80-139b-430f-a8b7-4989a7658e46")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    @objid ("45d35e93-386b-4d51-926b-42ad0e5575c2")
    public float getAmount() {
        return amount;
    }

    @objid ("394b9c1f-5488-4ad9-ac75-1dbf29a1399f")
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @objid ("2f899852-a74f-4a9a-b20e-9d1c0b8c986d")
    public DateTime getDate() {
        return date;
    }

    @objid ("ef533ab8-1c57-4547-af0f-ab9aa33351aa")
    public void setDate(DateTime date) {
        this.date = date;
    }

    @objid ("0234166f-30d9-4bbd-9c10-d26aeccccb3c")
    public boolean isIsfeeclearing() {
        return isfeeclearing;
    }

    @objid ("cfc83106-b9a2-43d1-a676-f52bce9e70cf")
    public void setIsfeeclearing(boolean isfeeclearing) {
        this.isfeeclearing = isfeeclearing;
    }

    @objid ("b2b80060-cd67-448d-8b5f-2b56c2384e46")
    public boolean isStopped() {
        return stopped;
    }

    @objid ("29281895-b9eb-4892-99be-361b0fa2cf75")
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

}
