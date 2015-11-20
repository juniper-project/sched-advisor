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
import java.util.Set;

/**
 *
 * @author rychly
 */
public class ProgramInstance extends ModelEntity {

    private final int mpiGlobalRank;
    private JuniperProgram program;
    private CloudNode cloudNode;
    private final Set<MpiGroupMember> memberships = new HashSet<>();

    public ProgramInstance(int mpiGlobalRank) {
        this.mpiGlobalRank = mpiGlobalRank;
    }

    public int getMpiGlobalRank() {
        return this.mpiGlobalRank;
    }

    public JuniperProgram getProgram() {
        return this.program;
    }

    protected void setProgram(JuniperProgram program) {
        this.program = program;
    }

    public CloudNode getCloudNode() {
        return cloudNode;
    }

    protected void setCloudNode(CloudNode cloudNode) {
        this.cloudNode = cloudNode;
    }

    public Set<MpiGroupMember> getMemberships() {
        return Collections.unmodifiableSet(this.memberships);
    }

    public MpiGroupMember addMembership(MpiGroupMember mpiGroupMember) {
        this.memberships.add(mpiGroupMember);
        mpiGroupMember.setProgramInstance(this);
        for (MpiGroupMember addedMpiGroupMember : this.memberships) {
            if (addedMpiGroupMember.equals(mpiGroupMember)) {
                return addedMpiGroupMember;
            }
        }
        return null;
    }

    public void removeMembership(MpiGroupMember mpiGroupMember) {
        this.memberships.remove(mpiGroupMember);
        mpiGroupMember.setProgramInstance(null);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.mpiGlobalRank;
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
        final ProgramInstance other = (ProgramInstance) obj;
        return this.mpiGlobalRank == other.mpiGlobalRank;
    }

    @Override
    public String toString() {
        return "instance with global rank " + mpiGlobalRank + " of " + program.toString();
    }

}
