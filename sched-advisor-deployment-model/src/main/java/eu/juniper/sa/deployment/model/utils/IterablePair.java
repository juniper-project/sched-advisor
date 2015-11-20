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
package eu.juniper.sa.deployment.model.utils;

import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author rychly
 * @author <a href="https://stackoverflow.com/users/314345/dhardy">dhardy</a>
 */
public class IterablePair<FirstIterable, SecondIterable> implements Iterable<Pair<FirstIterable, SecondIterable>> {

    private final Iterable<FirstIterable> firstIterable;
    private final Iterable<SecondIterable> secondIterable;

    public IterablePair(Iterable<FirstIterable> firstIterable, Iterable<SecondIterable> secondIterable) {
        this.firstIterable = firstIterable;
        this.secondIterable = secondIterable;
    }

    @Override
    public Iterator<Pair<FirstIterable, SecondIterable>> iterator() {
        return new ParallelIterator<>(firstIterable.iterator(), secondIterable.iterator());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.firstIterable);
        hash = 97 * hash + Objects.hashCode(this.secondIterable);
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
        final IterablePair<?, ?> other = (IterablePair<?, ?>) obj;
        if (!Objects.equals(this.firstIterable, other.firstIterable)) {
            return false;
        }
        return Objects.equals(this.secondIterable, other.secondIterable);
    }
}
