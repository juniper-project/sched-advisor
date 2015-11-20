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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author rychly
 */
public class DataConnection extends ModelEntity {

    private final String connectionName;
    private MpiGroup sendingGroup;
    private MpiGroup receivingGroup;
    private CommunicationModel model;
    private ConnectionType type;

    public DataConnection(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public MpiGroup getSendingGroup() {
        return this.sendingGroup;
    }

    public void setSendingGroup(MpiGroup sendingGroup) {
        this.sendingGroup = sendingGroup;
    }

    public MpiGroup getReceivingGroup() {
        return this.receivingGroup;
    }

    public void setReceivingGroup(MpiGroup receivingGroup) {
        this.receivingGroup = receivingGroup;
    }

    public CommunicationModel getModel() {
        return this.model;
    }

    protected void setModel(CommunicationModel model) {
        this.model = model;
    }

    public ConnectionType getType() {
        return this.type;
    }

    public String getTypeAsString() {
        return ConnectionType.valueToString(this.type);
    }

    public void setType(ConnectionType type) {
        this.type = type;
    }

    public boolean setType(String type) {
        return (this.type = ConnectionType.stringToValue(type)) != null;
    }

    public Set<MpiGroupMember> getReceivers(MpiGroupMember sender) {
        Set<MpiGroupMember> otherParty = new HashSet<>();
        switch (this.type) {
            case all_to_all:
            case one_to_all:
                otherParty.addAll(this.receivingGroup.getMembers());
                break;
            case all_to_one:
                otherParty.add(this.receivingGroup.getMembers().get(0));
                break;
            case symmetric:
                int index = this.sendingGroup.getMembers().indexOf(sender);
                if (index < this.receivingGroup.getMembers().size()) {
                    otherParty.add(this.receivingGroup.getMembers().get(index));
                }
                break;
        }
        return otherParty;
    }

    public Set<MpiGroupMember> getSenders(MpiGroupMember receiver) {
        Set<MpiGroupMember> otherParty = new HashSet<>();
        switch (this.type) {
            case all_to_all:
            case all_to_one:
                otherParty.addAll(this.sendingGroup.getMembers());
                break;
            case one_to_all:
                otherParty.add(this.sendingGroup.getMembers().get(0));
                break;
            case symmetric:
                int index = this.receivingGroup.getMembers().indexOf(receiver);
                if (index < this.sendingGroup.getMembers().size()) {
                    otherParty.add(this.sendingGroup.getMembers().get(index));
                }
                break;
        }
        return otherParty;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.connectionName);
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
        final DataConnection other = (DataConnection) obj;
        return Objects.equals(this.connectionName, other.connectionName);
    }

    @Override
    public String toString() {
        return this.getTypeAsString() + " data connection '" + connectionName + "'";
    }

}
