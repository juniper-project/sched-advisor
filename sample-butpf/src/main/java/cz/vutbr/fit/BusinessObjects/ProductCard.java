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

/**
 * A class representing product card
 * @author ikouril
 */
@objid ("803f6018-bd63-458d-b587-f93ea9a5952a")
public class ProductCard implements Serializable {
    @objid ("5103f5bc-cd28-4809-91b9-e8c3a18660dd")
    private static final long serialVersionUID = -4308832720752605527L;

    @objid ("61397e79-9fc0-4e23-80ee-0f076aaf892a")
    private String creditcards_pan = "";

    @objid ("4b597dcc-ca4d-4469-a9ee-a1a0872f29eb")
    private String product = "";

    @objid ("a2675e1b-8e91-464e-b8ef-cfbd43e06b48")
    private String card_id = "";

    /**
     * Creates a new ProductCard object.
     * @param in the line of csv data from which is object created
     */
    @objid ("d8037f27-277c-40ca-9d22-59a352418086")
    public ProductCard(String in) {
        try{
            String[] params=in.split(",");
            creditcards_pan=params[0];
            product=params[1];
            card_id=params[2];
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @objid ("bc5d3517-4164-460d-81e9-7fd36fc314a1")
    public String getCreditcards_pan() {
        return creditcards_pan;
    }

    @objid ("0293ee63-f3c5-441c-ad44-c3b91420efdc")
    public void setCreditcards_pan(String creditcards_pan) {
        this.creditcards_pan = creditcards_pan;
    }

    @objid ("3ccf97f8-fe74-49aa-bc93-d1efddd92b2c")
    public String getProduct() {
        return product;
    }

    @objid ("a63236d0-01c7-4b28-89d4-27f94e73be3f")
    public void setProduct(String product) {
        this.product = product;
    }

    @objid ("8e752e07-5cdd-40f2-a39e-f58f5d8791e0")
    public String getCard_id() {
        return card_id;
    }

    @objid ("43341e9b-bfbb-496d-bdee-3c759b2b77bf")
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

}
