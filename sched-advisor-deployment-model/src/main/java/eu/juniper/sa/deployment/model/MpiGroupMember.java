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

import java.util.Objects;

/**
 *
 * @author rychly
 */
public class MpiGroupMember extends ModelEntity {

    private final int mpiLocalRank;
    private MpiGroup group;
    private ProgramInstance programInstance;

    public MpiGroupMember(int mpiLocalRank, MpiGroup group) {
        this.mpiLocalRank = mpiLocalRank;
        this.group = group;
    }

    public int getMpiLocalRank() {
        return this.mpiLocalRank;
    }

    public MpiGroup getGroup() {
        return this.group;
    }

    protected void setGroup(MpiGroup group) {
        this.group = group;
    }

    public ProgramInstance getProgramInstance() {
        return this.programInstance;
    }

    protected void setProgramInstance(ProgramInstance programInstance) {
        this.programInstance = programInstance;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.mpiLocalRank;
        hash = 31 * hash + Objects.hashCode(this.group);
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
        final MpiGroupMember other = (MpiGroupMember) obj;
        if (this.mpiLocalRank != other.mpiLocalRank) {
            return false;
        }
        return Objects.equals(this.group, other.group);
    }

    @Override
    public String toString() {
        return programInstance.toString() + " as a member with local rank " + mpiLocalRank + " of " + group.toString();
    }

}
