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

import com.modeliosoft.modelio.javadesigner.annotations.objid;
import org.modelio.juniper.platform.Provided;

@objid ("bb144f5a-f4fc-440d-8507-d99a68d3d004")
public class Unify extends org.modelio.juniper.platform.JuniperProgram {
    @objid ("a50b99f9-1a40-45c8-8910-75b79c1a4dca")
    @Provided
    public IUnify iUnifyImpl = new cz.vutbr.fit.IUnify() {
		@Override
		public void acceptBilling(String p) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void acceptRedemption(String p) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void acceptRefund(String p) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void acceptReplenishment(String p) {
			// TODO Auto-generated method stub
			
		}};

    @objid ("013ec6b9-c36a-44fc-957f-89d623b9749b")
    public IWrite writeIWrite = (cz.vutbr.fit.IWrite) communicationToolkit.getProxyToInterface(cz.vutbr.fit.Write.class, cz.vutbr.fit.IWrite.class);

}
