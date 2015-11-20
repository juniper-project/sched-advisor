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
public class CloudNode extends ModelEntity {

    private final String hostIPAddr;
    private final Set<ProgramInstance> programInstances = new HashSet<>();
    private DeploymentModel model;

    public CloudNode(String hostIPAddr) {
        this.hostIPAddr = hostIPAddr;
    }

    public String getHostIPAddr() {
        return this.hostIPAddr;
    }

    public Set<ProgramInstance> getProgramInstances() {
        return Collections.unmodifiableSet(this.programInstances);
    }

    public ProgramInstance addProgramInstance(ProgramInstance programInstance) {
        this.programInstances.add(programInstance);
        programInstance.setCloudNode(this);
        for (ProgramInstance addedProgramInstance : this.programInstances) {
            if (addedProgramInstance.equals(programInstance)) {
                return addedProgramInstance;
            }
        }
        return null;
    }

    public void removeProgramInstance(ProgramInstance programInstance) {
        this.programInstances.remove(programInstance);
        programInstance.setCloudNode(null);
    }

    public DeploymentModel getModel() {
        return this.model;
    }

    protected void setModel(DeploymentModel model) {
        this.model = model;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.hostIPAddr);
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
        final CloudNode other = (CloudNode) obj;
        return Objects.equals(this.hostIPAddr, other.hostIPAddr);
    }

    @Override
    public String toString() {
        return "cloud node with host name/IP address " + hostIPAddr;
    }

}
