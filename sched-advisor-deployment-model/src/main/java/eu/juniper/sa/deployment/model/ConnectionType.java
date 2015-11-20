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

/**
 * The enumeration class for types of data connection between Juniper programs.
 *
 * @author rychly
 */
public enum ConnectionType {

    symmetric, all_to_one, one_to_all, all_to_all;

    private final static String SYMMETRIC = "symmetric";
    private final static String ALL_TO_ONE = "all_to_one";
    private final static String ONE_TO_ALL = "one_to_all";
    private final static String ALL_TO_ALL = "all_to_all";

    public static String valueToString(ConnectionType value) {
        if (value != null) {
            switch (value) {
                case all_to_all:
                    return ALL_TO_ALL;
                case one_to_all:
                    return ONE_TO_ALL;
                case all_to_one:
                    return ALL_TO_ONE;
                case symmetric:
                    return SYMMETRIC;
            }
        }
        return null;
    }

    public static ConnectionType stringToValue(String string) {
        if (string != null) {
            switch (string) {
                case ALL_TO_ALL:
                case "alltoall": // old notation
                    return all_to_all;
                case ONE_TO_ALL:
                case "onetoall": // old notation
                    return one_to_all;
                case ALL_TO_ONE:
                case "alltoone": // old notation
                    return all_to_one;
                case SYMMETRIC:
                    return symmetric;
            }
        }
        return null;
    }

}
