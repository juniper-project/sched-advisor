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
package cz.vutbr.fit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import cz.vutbr.fit.BusinessObjects.Aggregated;
import cz.vutbr.fit.BusinessObjects.Chargeback;
import cz.vutbr.fit.BusinessObjects.Message;
import cz.vutbr.fit.BusinessObjects.ProductCard;
import cz.vutbr.fit.BusinessObjects.Transaction;
import cz.vutbr.fit.util.join.MessageChargebacks;
import org.modelio.juniper.platform.Provided;

@objid ("d11a9b1e-7b8e-40b5-9db7-a2a6eb40aab9")
public class Join extends org.modelio.juniper.platform.JuniperProgram {
    @objid ("ae4ba02f-f5dd-4a2a-9146-9e1a715edd09")
    @Provided
    public IJoin iJoinImpl = new cz.vutbr.fit.IJoin() {
		@Override
		public void acceptChargeback(Chargeback c) {
			long startTime = System.nanoTime();
			Aggregated output=null;			
			List<Aggregated> rc=new ArrayList<Aggregated>();
			Integer key=c.getAll_messages_id();
			if (messageChargebacks.containsKey(key)){
				MessageChargebacks mc=(MessageChargebacks) messageChargebacks.get(key);
				if (mc.hasMessage()){
					Message m=mc.getMessage();
					String product=m.getProduct();
					String card_id=m.getCard_id();
					
					ArrayList<Chargeback> chargebacks=mc.getChargebacks();
					chargebacks.add(c);
					for (Chargeback chargeback:chargebacks){
						output=new Aggregated(product,card_id,"chargeback",chargeback.getDate_time_local_transaction(),chargeback.getAmount_transaction());
						rc.add(output);
						System.out.println("Processed: "+output.toString());
					}
					mc.clearChargebacks();
				}
				else{
					mc.addChargeback(c);
				}
			}
			else{
				messageChargebacks.put(key, new MessageChargebacks(c));
			}
			if (rc.size()>0){
				Long estimatedTime = System.nanoTime() - startTime;
				System.out.println("Join process time: "+String.valueOf(estimatedTime));
				writeIWrite.acceptAggregate(rc);
			}
		}
		@Override
		public void acceptMessage(Message m) {
			long startTime = System.nanoTime();
			Aggregated output=null;			
			List<Aggregated> rc=new ArrayList<Aggregated>();
			Integer key=m.getId();
			if (messageChargebacks.containsKey(key)){
				MessageChargebacks mc=(MessageChargebacks) messageChargebacks.get(key);
				mc.setMessage(m);
				String product=m.getProduct();
				String card_id=m.getCard_id();
				ArrayList<Chargeback> chargebacks=mc.getChargebacks();
				for (Chargeback chargeback:chargebacks){
					output=new Aggregated(product,card_id,"chargeback",chargeback.getDate_time_local_transaction(),chargeback.getAmount_transaction());
					rc.add(output);
					System.out.println("Processed: "+output.toString());
				}
				mc.clearChargebacks();
			}
			else{
				messageChargebacks.put(key, new MessageChargebacks(m));
			}	
		}
		@Override
		public void acceptTransaction(Transaction p) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void acceptProductCard(ProductCard p) {
			// TODO Auto-generated method stub
			
		}};

    @objid ("cc3ee029-36c7-4610-be54-2c6b956af732")
    public IWrite writeIWrite = (cz.vutbr.fit.IWrite) communicationToolkit.getProxyToInterface(cz.vutbr.fit.Write.class, cz.vutbr.fit.IWrite.class);

    @objid ("f27ee70e-3709-4683-80fa-37bb441ece85")
    private HashMap<Integer, Object> messageChargebacks = new HashMap<Integer,Object>();

    @objid ("e162987a-b871-460b-9a19-049bbd40a61e")
    private HashMap<String, Object> cardTransactions = new HashMap<String,Object>();

}
