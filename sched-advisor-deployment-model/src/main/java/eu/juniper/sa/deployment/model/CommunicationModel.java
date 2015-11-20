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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author rychly
 */
public class CommunicationModel extends ModelEntity {

    private JuniperApplication application;
    private final Set<DataConnection> connections = new HashSet<>();

    public JuniperApplication getApplication() {
        return this.application;
    }

    protected void setApplication(JuniperApplication application) {
        this.application = application;
    }

    public Set<DataConnection> getConnections() {
        return Collections.unmodifiableSet(this.connections);
    }

    public DataConnection addConnection(DataConnection dataConnection) {
        this.connections.add(dataConnection);
        dataConnection.setModel(this);
        for (DataConnection addedDataConnection : this.connections) {
            if (addedDataConnection.equals(dataConnection)) {
                return addedDataConnection;
            }
        }
        return null;
    }

    public void removeConnection(DataConnection dataConnection) {
        this.connections.remove(dataConnection);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.application);
        hash = 79 * hash + Objects.hashCode(this.connections);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CommunicationModel other = (CommunicationModel) obj;
        if (!Objects.equals(this.application, other.application)) {
            return false;
        }
        return Objects.equals(this.connections, other.connections);
    }

    @Override
    public String toString() {
        return "the communication model of " + application.toString();
    }

}
