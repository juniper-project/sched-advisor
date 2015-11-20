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

import java.util.Random;
import com.modeliosoft.modelio.javadesigner.annotations.objid;
import cz.vutbr.fit.BusinessObjects.Chargeback;
import cz.vutbr.fit.BusinessObjects.Message;
import org.joda.time.DateTime;

@objid ("9da1f46c-a258-4021-a5bd-ed3c60af50f6")
public class Storage extends org.modelio.juniper.platform.JuniperProgram {
    @objid ("c520a4bc-f460-4d06-b88a-12d2cae3840a")
    public IUnify unifyIUnify = (cz.vutbr.fit.IUnify) communicationToolkit.getProxyToInterface(cz.vutbr.fit.Unify.class, cz.vutbr.fit.IUnify.class);

    @objid ("a10f1e25-7bc7-471c-ade9-6d0264ab7f0f")
    public IJoin joinIJoin = (cz.vutbr.fit.IJoin) communicationToolkit.getProxyToInterface(cz.vutbr.fit.Join.class, cz.vutbr.fit.IJoin.class);

    @objid ("238d37e5-24f9-42c4-b8f3-ad447805a149")
    public void execute() {
        System.out.println("Entering storage");
        
        try {
            Thread.sleep(001);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Random random = new Random();
        if (random.nextBoolean()) {
            Chargeback cb = new Chargeback();
            joinIJoin.acceptChargeback(cb);
        } else {
            Message m = new Message();
            joinIJoin.acceptMessage(m);
        }
        
        DateTime date=DateTime.now();
        System.out.println("Everything processed: "+date.toString());
    }

}
