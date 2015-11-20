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
public class ParallelIterator<FirstComponent, SecondComponent> implements Iterator<Pair<FirstComponent, SecondComponent>> {

    private final Iterator<FirstComponent> firstComponentIterator;
    private final Iterator<SecondComponent> secondComponentIterator;

    public ParallelIterator(Iterator<FirstComponent> firstComponentIterator, Iterator<SecondComponent> secondComponentIterator) {
        this.firstComponentIterator = firstComponentIterator;
        this.secondComponentIterator = secondComponentIterator;
    }

    @Override
    public boolean hasNext() {
        return firstComponentIterator.hasNext() && secondComponentIterator.hasNext();
    }

    @Override
    public Pair<FirstComponent, SecondComponent> next() {
        return new Pair<>(firstComponentIterator.next(), secondComponentIterator.next());
    }

    @Override
    public void remove() {
        firstComponentIterator.remove();
        secondComponentIterator.remove();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.firstComponentIterator);
        hash = 59 * hash + Objects.hashCode(this.secondComponentIterator);
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
        final ParallelIterator<?, ?> other = (ParallelIterator<?, ?>) obj;
        if (!Objects.equals(this.firstComponentIterator, other.firstComponentIterator)) {
            return false;
        }
        return Objects.equals(this.secondComponentIterator, other.secondComponentIterator);
    }
}
