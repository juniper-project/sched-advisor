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
import cz.vutbr.fit.BusinessObjects.Chargeback;
import cz.vutbr.fit.BusinessObjects.Message;
import cz.vutbr.fit.BusinessObjects.ProductCard;
import cz.vutbr.fit.BusinessObjects.Transaction;

@objid ("cebe6901-8c02-46ea-b172-6929e6cb331a")
public interface IJoin {
    @objid ("7f0f2487-bcc4-4fe0-8f73-536b7f3294fb")
    void acceptChargeback(final Chargeback p);

    @objid ("760ffc06-8518-4d51-acb8-808602591d86")
    void acceptMessage(final Message p);

    @objid ("bb910cec-7bfe-4a2a-8394-6549626ce24f")
    void acceptTransaction(final Transaction p);

    @objid ("03da8b21-b5ce-4b8a-934e-58851e5c61a8")
    void acceptProductCard(final ProductCard p);

}
