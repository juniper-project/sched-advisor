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
package eu.juniper.sa.deployment.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * The class for aggregated monitoring metrics.
 *
 * @author rychly
 */
public class AggregatedMetric {

    /**
     * Count of metric values in the aggregation.
     */
    public final Integer count;
    /**
     * Minimum of metric values in the aggregation.
     */
    public final Double min;
    /**
     * Maximum of metric values in the aggregation.
     */
    public final Double max;
    /**
     * Arithmetic average of metric values in the aggregation.
     */
    public final Double avg;
    /**
     * Sum of metric values in the aggregation.
     */
    public final Double sum;
    /**
     * Sum of squares of metric values in the aggregation.
     */
    public final Double sumOfSquares;
    /**
     * Population variance of metric values in the aggregation.
     */
    public final Double variance;
    /**
     * Population standard deviation of metric values in the aggregation.
     */
    public final Double stdDeviation;

    /**
     * Create an aggregated metric from given aggregated values.
     *
     * @param count a count of metric values in the aggregation
     * @param min a minimum of metric values in the aggregation
     * @param max a maximum of metric values in the aggregation
     * @param avg an arithmetic average of metric values in the aggregation
     * @param sum a sum of metric values in the aggregation
     * @param sumOfSquares a sum of squares of metric values in the aggregation
     * @param variance a population variance of metric values in the aggregation
     * @param stdDeviation a population standard deviation of metric values in
     * the aggregation
     */
    public AggregatedMetric(Integer count, Double min, Double max, Double avg, Double sum, Double sumOfSquares, Double variance, Double stdDeviation) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.sum = sum;
        this.sumOfSquares = sumOfSquares;
        this.variance = variance;
        this.stdDeviation = stdDeviation;
    }

    /**
     * Create an aggregated metric from a given JSON representation of
     * aggregated values.
     *
     * @param jsonRepresentation a JSON representation of aggregated values
     */
    public AggregatedMetric(String jsonRepresentation) {
        Integer myCount = null;
        Double myMin = null;
        Double myMax = null;
        Double myAvg = null;
        Double mySum = null;
        Double mySumOfSquares = null;
        Double myVariance = null;
        Double myStdDeviation = null;
        try (BufferedReader reader = new BufferedReader(new StringReader(jsonRepresentation))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int mark1, mark2;
                // key in quotation marks
                if (((mark1 = line.indexOf('"')) < 0) || ((mark2 = line.indexOf('"', mark1 + 1)) < 0)) {
                    continue;
                }
                String key = line.substring(mark1 + 1, mark2);
                // val without quotation marks
                if ((mark1 = line.indexOf(':', mark2)) < 0) {
                    continue;
                }
                if ((mark2 = line.indexOf(',', mark1 + 1)) < 0) {
                    mark2 = line.length();
                }
                String val = line.substring(mark1 + 1, mark2).trim();
                // assign
                if ("null".equals(val)) {
                    continue;
                }
                switch (key) {
                    case "count":
                        myCount = Integer.parseInt(val);
                        break;
                    case "min":
                        myMin = Double.parseDouble(val);
                        break;
                    case "max":
                        myMax = Double.parseDouble(val);
                        break;
                    case "avg":
                        myAvg = Double.parseDouble(val);
                        break;
                    case "sum":
                        mySum = Double.parseDouble(val);
                        break;
                    case "sum_of_squares":
                        mySumOfSquares = Double.parseDouble(val);
                        break;
                    case "variance":
                        myVariance = Double.parseDouble(val);
                        break;
                    case "std_deviation":
                        myStdDeviation = Double.parseDouble(val);
                        break;
                }
            }
        }
        catch (IOException ex) {
            // nothing, it cannot happen
        }
        this.count = myCount;
        this.min = myMin;
        this.max = myMax;
        this.avg = myAvg;
        this.sum = mySum;
        this.sumOfSquares = mySumOfSquares;
        this.variance = myVariance;
        this.stdDeviation = myStdDeviation;
    }

    @Override
    public String toString() {
        return "AggregatedMetric{" + "count=" + count + ", min=" + min + ", max=" + max + ", avg=" + avg + ", sum=" + sum + ", sumOfSquares=" + sumOfSquares + ", variance=" + variance + ", stdDeviation=" + stdDeviation + '}';
    }

}
