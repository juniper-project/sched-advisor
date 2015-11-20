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
package eu.juniper.sa.deployment.model;

import java.util.UUID;

/**
 * The class representing a core model entity.
 *
 * @author rychly
 */
public class ModelEntity {

    private UUID uuid = null;

    /**
     * Get a type 4 (pseudo randomly generated) UUID of the entity.
     *
     * @return a type 4 (pseudo randomly generated) UUID of the entity
     */
    public UUID getUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        return this.uuid;
    }

    /**
     * Get a type 4 (pseudo randomly generated) UUID of the entity as a
     * well-formated URN.
     *
     * @return a type 4 (pseudo randomly generated) UUID of the entity as a
     * well-formated URN
     */
    public String getUuidAsUrn() {
        return "urn:uuid:" + this.getUuid().toString();
    }

    /**
     * Get a type 4 (pseudo randomly generated) UUID of the entity as a
     * well-formated CNAME.
     *
     * @return a type 4 (pseudo randomly generated) UUID of the entity as a
     * well-formated CNAME
     */
    public String getUuidAsCName() {
        return "uuid_" + this.getUuid().toString();
    }

    /**
     * Check if the entity has assigned an UUID by a previous call of
     * <code>getUuid()</code>, <code>getUuidAsUrn()</code>, or
     * <code>getUuidAsCName</code> method.
     *
     * @return <code>true</code> if the entity has assigned an UUID,
     * <code>false</code> otherwise
     */
    public boolean hasUuid() {
        return this.uuid != null;
    }
}
