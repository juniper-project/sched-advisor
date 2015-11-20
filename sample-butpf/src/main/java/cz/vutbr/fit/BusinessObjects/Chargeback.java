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
import java.util.Random;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * A class representing chargeback data
 * @author ikouril
 */
@objid ("0d5aced5-1573-4772-8efa-9e3e62f269ae")
public class Chargeback implements Serializable {
    @objid ("14f47fa3-0130-4a7f-b344-3e7630fbe75a")
    private static final long serialVersionUID = -1036567227732679219L;

    @objid ("ccbfe4cc-e7f7-46a2-8a0a-fd3f1f1b56c1")
    private Integer all_messages_id = 0;

    @objid ("6e6ffb51-0ba6-424f-9c24-e81b9d377dd7")
    private float amount_transaction = 0.0F;

    @objid ("fc942407-0bcb-47f4-9dff-61c2685d09cd")
    private DateTime date_time_local_transaction = new DateTime();

    @objid ("07f81129-74da-48b6-bf37-28914188d9c9")
    private String approval_code = "";

    @objid ("7671fd92-aece-4b66-8cfa-1ce73343e9a6")
    private String currency_code_transaction = "";

    @objid ("7dffafd4-3de6-4a43-a028-4ecb7de6be73")
    private String earlyrecon_status = "";

    /**
     * Creates a new Chargeback object.
     * @param in the line of csv data from which is object created
     */
    @objid ("46922ea0-cd99-43e2-a566-e0067c1c2e43")
    public Chargeback(String in) {
        try{
            String[] params=in.split(",");
            if (!params[0].isEmpty())
                all_messages_id=Integer.parseInt(params[0]);
            if (!params[1].isEmpty())
            amount_transaction=Float.parseFloat(params[1]);
            if (!params[2].isEmpty())
                date_time_local_transaction=DateTime.parse(params[2],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));        
            approval_code=params[3];
            currency_code_transaction=params[4];
            earlyrecon_status=params[5];
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("65ced5e2-e909-4032-9942-e4fc399a006b")
    public float getAmount_transaction() {
        return amount_transaction;
    }

    @objid ("778850bf-3713-4708-a9f9-e71356ad21fe")
    public void setAmount_transaction(float amount_transaction) {
        this.amount_transaction = amount_transaction;
    }

    @objid ("4fc74e4d-0549-4993-81a2-bedd6d67e602")
    public DateTime getDate_time_local_transaction() {
        return date_time_local_transaction;
    }

    @objid ("810c6214-27c7-4d15-9eaa-faf72491c6b5")
    public void setDate_time_local_transaction(DateTime date_time_local_transaction) {
        this.date_time_local_transaction = date_time_local_transaction;
    }

    @objid ("ddfc18a0-6aee-484b-89b7-8f819bfe867f")
    public String getApproval_code() {
        return approval_code;
    }

    @objid ("818d15e9-936f-439b-ab91-4f06fd9cd829")
    public void setApproval_code(String approval_code) {
        this.approval_code = approval_code;
    }

    @objid ("8dfb5dba-73e6-41c0-8c7f-2c69d149ebce")
    public String getCurrency_code_transaction() {
        return currency_code_transaction;
    }

    @objid ("4de18897-7c08-4f85-9964-1d38210e8222")
    public void setCurrency_code_transaction(String currency_code_transaction) {
        this.currency_code_transaction = currency_code_transaction;
    }

    @objid ("35a87474-1731-4f3b-b262-c881016be147")
    public String getEarlyrecon_status() {
        return earlyrecon_status;
    }

    @objid ("08d369a3-7b2b-45fc-80d3-285a683f6900")
    public void setEarlyrecon_status(String earlyrecon_status) {
        this.earlyrecon_status = earlyrecon_status;
    }

    @objid ("f9614298-631f-478c-9710-776355f63923")
    public Integer getAll_messages_id() {
        return all_messages_id;
    }

    @objid ("d273cd91-85d0-4c98-b274-5f15aa676e0d")
    public void setAll_messages_id(Integer all_messages_id) {
        this.all_messages_id = all_messages_id;
    }

    @objid ("c3f3a6d7-775b-4ba2-94da-85ebd0f51925")
    public Chargeback() {
        Random random = new Random();
        setAmount_transaction(random.nextFloat() * 2000);
        setDate_time_local_transaction(new DateTime());
        setApproval_code(""+random.nextInt(999999));
        setCurrency_code_transaction(new String[]{"EUR", "USD", "RMB"}[random.nextInt(3)]);
        setEarlyrecon_status(""+random.nextInt(10));
        setAll_messages_id(random.nextInt(100));
    }

}
