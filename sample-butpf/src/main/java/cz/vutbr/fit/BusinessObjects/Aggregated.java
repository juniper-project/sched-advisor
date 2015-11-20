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

/**
 * A class representing aggregated data
 * @author ikouril
 */
@objid ("5e4d307e-158d-4fd5-a13f-0024350888d6")
public class Aggregated implements Serializable {
    @objid ("1b44e937-db0a-41c0-8093-b2747d0a9324")
    private static final long serialVersionUID = -5776792751993227649L;

    @objid ("7092eea0-bf4f-4785-997f-faabfa87c8a4")
    private float balance_change;

    @objid ("1636666b-e494-43d5-8fe1-a1279b951df9")
    private String event;

    @objid ("62f0a35b-38df-465f-8d94-268fd5914f6b")
    private String timestamp;

    @objid ("8c5f29c2-715d-493d-9c84-26ebb5ba99dd")
    private String product;

    @objid ("a4ec9657-6324-42ea-af62-3674f8a4b3bf")
    private String card_id;

    @objid ("0e172ee6-9162-4e7f-8d01-f04f6595ba92")
    private DateTime date;

    @objid ("0aae30c2-6a34-4afa-944c-1a247576164d")
    private int numChanged;

    /**
     * Creates a new Aggregated object.
     * @param timestamp the timestamp comprising of year, month, day, hour and 5 minute interval
     * @param product the name of product
     * @param card_id the id of a card
     * @param event the name of a data from which this aggregated object is creater
     */
    @objid ("78bbe773-c229-4ac7-9f32-8dcb7c71ebb8")
    public Aggregated(String product, String card_id, String event, DateTime date, float balance_change) {
        this.product=product;
        this.card_id=card_id;
        this.event=event;
        this.setDate(date);
        this.balance_change=balance_change;
        int fiveMinutesInterval=date.getMinuteOfHour()/5;
        this.timestamp=String.valueOf(date.getYear())+"-"+String.valueOf(date.getMonthOfYear())+"-"+String.valueOf(date.getDayOfMonth())+"-"+String.valueOf(date.getHourOfDay())+"-"+String.valueOf(fiveMinutesInterval);
        numChanged=1;
    }

    @objid ("c60ba28e-2b6e-4dc4-b43f-e894f6e2c4d3")
    public boolean equals(Aggregated other) {
        return product.equals(other.getProduct()) && card_id.equals(other.getCard_id()) && event.equals(other.getEvent()) && timestamp.equals(other.getTimestamp());
    }

    @objid ("8f643cc6-1705-4d7a-9e98-43a8179acdbf")
    public String toString() {
        return "Product: "+product+", Card_id: "+card_id+", Event: "+event+", Timestamp: "+timestamp+", Balance change: "+String.valueOf(balance_change);
    }

    @objid ("f75f251e-7e9c-4126-b680-353ca193a8e5")
    public void incBalance(float add) {
        balance_change+=add;
        setNumChanged(getNumChanged() + 1);
    }

    @objid ("fb81e7f4-c8ad-465c-b2a5-ec412717481e")
    public void decBalance(float sub) {
        balance_change-=sub;
        setNumChanged(getNumChanged() + 1);
    }

    @objid ("e3ca51eb-238e-47c1-a413-d74098c3dadf")
    public float getBalance_change() {
        return balance_change;
    }

    @objid ("359f09ce-0131-480f-a5d0-2976692eef40")
    public void setBalance_change(float balance_change) {
        this.balance_change = balance_change;
    }

    @objid ("aeb6e3e4-adce-416e-8d08-ae77b7ceaeca")
    public String getEvent() {
        return event;
    }

    @objid ("51160de6-296a-43a4-b50a-b13bb197ca1d")
    public void setEvent(String event) {
        this.event = event;
    }

    @objid ("855ea73f-3c05-4486-85ca-4ae4a0a11f76")
    public String getTimestamp() {
        return timestamp;
    }

    @objid ("c0287d15-b6d5-4dca-9c94-2e4b2eb00559")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @objid ("7d2abdde-6246-4a30-ac8c-51f06d34fdc6")
    public String getProduct() {
        return product;
    }

    @objid ("72c72dca-3b9f-42b1-b14f-25938a49046b")
    public void setProduct(String product) {
        this.product = product;
    }

    @objid ("852a0243-52cb-475b-a30f-15be2657250f")
    public String getCard_id() {
        return card_id;
    }

    @objid ("fa110719-77e1-4af6-91c0-67411201ec8e")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    @objid ("4a1fa1df-156d-4fae-a4af-7f06f3aab84b")
    public DateTime getDate() {
        return date;
    }

    @objid ("ba7b76f9-05f6-40c7-a0c2-d52759b04e96")
    public void setDate(DateTime date) {
        this.date = date;
    }

    @objid ("2c2c026c-5483-4a8e-b20a-5585adcd7958")
    public int getNumChanged() {
        return numChanged;
    }

    @objid ("6058ad61-6f62-415a-ac81-fd477ccc01c3")
    public void setNumChanged(int numChanged) {
        this.numChanged = numChanged;
    }

}
