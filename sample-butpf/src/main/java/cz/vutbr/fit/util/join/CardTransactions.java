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
import java.util.List;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import cz.vutbr.fit.BusinessObjects.ProductCard;
import cz.vutbr.fit.BusinessObjects.Transaction;

/**
 * A class representing 1 to N relationship between product cards and transactions
 * @author ikouril
 */
@objid ("542a03a0-2809-4349-afa5-47ba87889cc1")
public class CardTransactions implements Serializable {
    @objid ("7f89f518-b6c6-45d9-9a9e-1907833f31df")
    private static final long serialVersionUID = 2979663353407994967L;

    @objid ("576a6743-f2d2-40fd-b16a-3b05af26a51e")
    public ArrayList<Transaction> transactions = new ArrayList<Transaction> ();

    @objid ("3370e08d-21b3-490d-9c18-1608159a9c9c")
    public ProductCard card;

    /**
     * Creates a new CardTransactions object.
     * @param card the product card object from which is CardTransactions created
     */
    @objid ("12358df0-f1e2-480f-a6a3-376897faa618")
    public CardTransactions(ProductCard card) {
        this.card=card;
        transactions=new ArrayList<Transaction>();
    }

    /**
     * Creates a new CardTransactions object.
     * @param transaction the transaction object from which is CardTransactions created
     */
    @objid ("ee18a732-be54-4af1-9269-f1b6cbe56c27")
    public CardTransactions(Transaction transaction) {
        transactions=new ArrayList<Transaction>();
        transactions.add(transaction);
        card=null;
    }

    @objid ("6c3ff013-866a-4eea-aec7-13f55557bef6")
    public boolean hasCard() {
        return card!=null;
    }

    @objid ("a47c00dc-2368-4c11-8dae-d22b6a44d931")
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    @objid ("b2e0f9e8-6378-4660-8597-5756b8de57e5")
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    @objid ("c7922dbb-a9b8-41f9-9644-1b2719d4f9e9")
    public void clearTransactions() {
        transactions.clear();
    }

    @objid ("13daae3b-e436-44fc-9f2c-8973fbbd728f")
    public ProductCard getCard() {
        return card;
    }

    @objid ("d1bbb1b3-f401-4e74-bbd7-b4332296f9e1")
    public void setCard(ProductCard card) {
        this.card=card;
    }

}
