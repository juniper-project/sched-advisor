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
 * A class representing message data
 * @author ikouril
 */
@objid ("78c4b395-ed5f-4a22-b9c4-e080f3f9f2cf")
public class Message implements Serializable {
    @objid ("8098b376-5793-48b5-a494-ec58f0c25d1b")
    private static final long serialVersionUID = -2196067630170204611L;

    @objid ("17275634-c788-43dd-8ccb-dcca66bc4144")
    private Integer id = 0;

    @objid ("ddc750c9-beca-4d6c-855d-9ff86088febc")
    private String mti = "";

    @objid ("61c19d93-c7d6-494d-afc8-1527531bf8c7")
    private DateTime date = new DateTime();

    @objid ("c564be78-5681-442d-b30f-925f0508c38c")
    private String product = "";

    @objid ("8d06027a-8802-4e74-9a7d-139ec5e0baeb")
    private String card_id = "";

    @objid ("fada6943-6096-4110-b5f7-bcfb56c98e31")
    private String destinationinstitutionidcode = "";

    /**
     * Creates a new Message object.
     * @param in the line of csv data from which is object created
     */
    @objid ("bbfead64-5bf2-49b1-b344-0432b1e89223")
    public Message(String in) {
        try{
            String[] params=in.split(",");
            if (!params[0].isEmpty())
                id=Integer.parseInt(params[0]);
            mti=params[1];
            if (!params[2].isEmpty())
                date=DateTime.parse(params[2],DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            product=params[3];
            card_id=params[4];
            destinationinstitutionidcode=params[5];
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("9b4d8a18-fd7f-42ee-b9dd-c266168afc6a")
    public Integer getId() {
        return id;
    }

    @objid ("5a5dc801-619e-48c3-8e1a-54a87e9f6525")
    public void setId(Integer id) {
        this.id = id;
    }

    @objid ("d1c2bf95-f17f-4c7c-94d8-4e98bf1331fc")
    public String getMti() {
        return mti;
    }

    @objid ("065e7483-6459-409c-ae0c-3a62131cef85")
    public void setMti(String mti) {
        this.mti = mti;
    }

    @objid ("ddca626a-0f51-4a3b-9049-1baf434b1e66")
    public DateTime getDate() {
        return date;
    }

    @objid ("c8d21cdc-f508-4f43-b0e7-6ed661ee5219")
    public void setDate(DateTime date) {
        this.date = date;
    }

    @objid ("756e1023-dc3f-4bda-be4c-34ff394cae44")
    public String getProduct() {
        return product;
    }

    @objid ("bc3b9a1c-f72b-4502-94e5-0c4f2d6ae1cd")
    public void setProduct(String product) {
        this.product = product;
    }

    @objid ("08a1eb17-e524-4651-96ef-b1462d679d5a")
    public String getCard_id() {
        return card_id;
    }

    @objid ("314ed0cb-ca0b-4a63-8102-3fbd62582df9")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    @objid ("c9897983-1118-4cc0-b815-d9109a890d1a")
    public String getDestinationinstitutionidcode() {
        return destinationinstitutionidcode;
    }

    @objid ("c7ae89af-9bad-40f2-8f4b-5406f81d161d")
    public void setDestinationinstitutionidcode(String destinationinstitutionidcode) {
        this.destinationinstitutionidcode = destinationinstitutionidcode;
    }

    @objid ("5739420d-0c76-44ea-89d3-273eaa4bc7fe")
    public Message() {
        Random random = new Random();
        setId(random.nextInt(1000));
        setMti(""+random.nextInt());
        setDate(new DateTime());
        setProduct(""+random.nextInt());
        setCard_id(""+random.nextInt());
        setDestinationinstitutionidcode(""+random.nextInt());
    }

}
