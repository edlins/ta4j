/*
  The MIT License (MIT)

  Copyright (c) 2014-2017 Marc de Verdelhan, Ta4j Organization & respective authors (see AUTHORS)

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ta4j.core;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.num.BigDecimalNum;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Utility class for {@code Num} tests.
 */
public class TestUtils {

    public static final int DEFAULT_PRECISION = 4;
    // override the default BigDecimalNum precision for high precision conversions
    // this allows for reading expected value Strings with higher precision than the default (32)
    public static final int HIGH_PRECISION = 64;
    private static final Function<String, Num> highPrecisionNumFunc = (val -> BigDecimalNum.valueOf(val, HIGH_PRECISION));
    /** Offset for double equality checking */
    // TODO: @Deprecated
    public static final double GENERAL_OFFSET = DoubleNum.valueOf(DoubleNum.EPS).doubleValue();
    private static final Num HIGH_PRECISION_OFFSET = highPrecisionNumFunc.apply("0.0001");
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);


    // four Indicator matches precision/delta and positive/negative
    public static void assertIndicatorMatches(Indicator<Num> expected, Indicator<Num> actual, int precision) {
        for (int i = 0; i < expected.getTimeSeries().getBarCount(); i++) {
            Num exp = expected.getValue(i);
            Num act = actual.getValue(i);
            String message = String.format("Failed at index %s: ", i);
            assertNumMatches(exp, act, precision, message);
        }
    }

    public static void assertIndicatorNotMatches(Indicator<Num> expected, Indicator<Num> actual, int precision) {
        if (expected.getTimeSeries().getBarCount() == actual.getTimeSeries().getBarCount()) {
            for (int i = 0; i < expected.getTimeSeries().getBarCount(); i++) {
                Num exp = expected.getValue(i);
                Num act = actual.getValue(i);
                if (!numsMatch(exp, act, precision)) {
                    log.debug("Passed at index {}:", i);
                    log.debug("{} does not match expected", act);
                    log.debug("{} to precision {}", exp, precision);
                    return;
                }
            }
        }
        throw new AssertionError("Indicators match within precision " + precision);
    }

    public static void assertIndicatorMatches(Indicator<Num> expected, Indicator<Num> actual, Num delta) {
        for (int i = 0; i < expected.getTimeSeries().getBarCount(); i++) {
            Num exp = expected.getValue(i);
            Num act = actual.getValue(i);
            String message = String.format("Failed at index %s: ", i);
            assertNumMatches(exp, act, delta, message);
        }
    }

    public static void assertIndicatorNotMatches(Indicator<Num> expected, Indicator<Num> actual, Num delta) {
        for (int i = 0; i < expected.getTimeSeries().getBarCount(); i++) {
            Num exp = expected.getValue(i);
            Num act = actual.getValue(i);
            if (!numsMatch(exp, act, delta)) {
                log.debug("Passed at index {}:", i);
                log.debug("{} does not match expected", act);
                log.debug("{} within offset", exp);
                log.debug("{}", delta);
                return;
            }
        }
        throw new AssertionError("Indicators match within delta " + delta);
    }

    // four String matches precision/delta and positive/negative
    public static void assertNumMatches(String expected, Num actual, int precision) {
        assertNumMatches(highPrecisionNumFunc.apply(expected), actual, precision);
    }

    public static void assertNumMatches(String expected, Num actual, Num delta) {
        assertNumMatches(highPrecisionNumFunc.apply(expected), actual, delta);
    }

    public static void assertNumNotMatches(String expected, Num actual, int precision) {
        assertNumNotMatches(highPrecisionNumFunc.apply(expected), actual, precision);
    }

    public static void assertNumNotMatches(String expected, Num actual, Num delta) {
        assertNumNotMatches(highPrecisionNumFunc.apply(expected), actual, delta);
    }

    //     four Number matches precision/delta and positive/negative
    public static void assertNumMatches(Number expected, Num actual, int precision) {
        assertNumMatches(highPrecisionNumFunc.apply(expected.toString()), actual, precision);
    }

    public static void assertNumMatches(Number expected, Num actual, Num delta) {
        assertNumMatches(highPrecisionNumFunc.apply(expected.toString()), actual, delta);
    }

    public static void assertNumNotMatches(Number expected, Num actual, int precision) {
        assertNumNotMatches(highPrecisionNumFunc.apply(expected.toString()), actual, precision);
    }

    public static void assertNumNotMatches(Number expected, Num actual, Num delta) {
        assertNumNotMatches(highPrecisionNumFunc.apply(expected.toString()), actual, delta);
    }

    // four Num matches precision/delta and positive/negative
    public static void assertNumMatches(Num expected, Num actual, int precision, String... message) {
        String msg = message == null || message.length == 0 || message[0] == null ? "" : message[0];
        if (!numsMatch(expected, actual, precision, message)) {
            throw new AssertionError(
                    msg + "Value " + actual.toString() + " does not match expected " + expected.toString() + " to precision " + precision);
        }
    }

    public static void assertNumMatches(Num expected, Num actual, Num delta, String... message) {
        if (!numsMatch(expected, actual, delta, message)) {
            throw new AssertionError(
                    "Value " + actual.toString() + " does not match " + expected.toString() + " within offset " + delta);
        }
    }

    public static void assertNumNotMatches(Num expected, Num actual, int precision, String... message) {
        if (numsMatch(expected, actual, precision, message)) {
            throw new AssertionError(
                    "Value " + actual.toString() + " matches expected " + expected.toString() + " to precision " + precision);
        }
    }

    public static void assertNumNotMatches(Num expected, Num actual, Num delta, String... message) {
        if (numsMatch(expected, actual, delta, message)) {
            throw new AssertionError(
                    "Value " + actual.toString() + " matches expected " + expected.toString() + " within offset " + delta);
        }
    }

    private static boolean numsMatch(Num expected, Num actual, int precision, String... message) {
        Num exp = BigDecimalNum.valueOf(expected.toString(), precision);
        Num act = BigDecimalNum.valueOf(actual.toString(), precision);
        if (exp.compareTo(act) == 0) {
            log.trace("{} from {} matches expected", act, actual);
            log.trace("{} from {} at precision {}", exp, expected, precision);
            return true;
        }
        if (message != null && message.length > 0 && message[0] != null) log.debug(message[0]);
        log.debug("{} from {} does not match", act, actual);
        log.debug("{} from {} at precision {}", exp, expected, precision);
        return false;
    }

    private static boolean numsMatch(Num expected, Num actual, Num delta, String... message) {
        Num exp = highPrecisionNumFunc.apply(expected.toString());
        Num act = highPrecisionNumFunc.apply(actual.toString());
        Num difference = exp.minus(act).abs();
        if (!difference.isGreaterThan(delta)) {
            log.trace("{} from {} matches expected", act, actual);
            log.trace("{} from {} within offset", exp, expected);
            log.trace("{} with difference", delta);
            log.trace("{}", difference);
            return true;
        }
        if (message != null && message.length > 0 && message[0] != null) log.debug(message[0]);
        log.debug("{} from {} does not match expected", act, actual);
        log.debug("{} from {} within offset", exp, expected);
        log.debug("{} with difference", delta);
        log.debug("{}", difference);
        return false;
    }

    public static void assertIndicatorEquals(Indicator<Num> expected, Indicator<Num> actual) {
        for (int i = 0; i < expected.getTimeSeries().getBarCount(); i++) {
            Num exp = expected.getValue(i);
            Num act = actual.getValue(i);
            assertNumEquals(exp, act);
        }
    }

    /**
     * Verifies that the actual {@code Decimal} value is equal to the given {@code String} representation.
     *
     * @param actual the actual {@code Decimal} value
     * @param expected the given {@code String} representation to compare the actual value to
     * @throws AssertionError if the actual value is not equal to the given {@code String} representation
     */
    public static void assertNumEquals(String expected, Num actual) {
        assertNumEquals(actual.numOf(new BigDecimal(expected)), actual);
    }

    public static void assertNumNotEquals(String expected, Num actual) {
        assertNumNotEquals(BigDecimalNum.valueOf(expected), actual);
    }

    public static void assertNumEquals(Num expected, Num actual) {
        assertEquals(expected, actual);
    }

    //    public static void assertNumEquals(Num expected, Num actual, Num delta) {
    //        if (expected.minus(actual).abs().isGreaterThan(delta)) {
    //            String message = "Value " + actual.toString() + " does not match expected " + expected.toString() + " within delta";
    //            throw new AssertionError(message);
    //        }
    //    }

    public static void assertNumNotEquals(Num expected, Num actual) {
        assertNotEquals(expected, actual);
    }

    //    public static void assertNumNotEquals(Num expected, Num actual, Num delta) {
    //        if (!expected.minus(actual).abs().isGreaterThan(delta)) {
    //            String message = "Value " + actual.toString() + " matches expected " + expected.toString() + " within delta";
    //            throw new AssertionError(message);
    //        }
    //    }

    /**
     * Verifies that the actual {@code Decimal} value is equal to the given {@code Integer} representation.
     *
     * @param actual the actual {@code Decimal} value
     * @param expected the given {@code Integer} representation to compare the actual value to
     * @throws AssertionError if the actual value is not equal to the given {@code Integer} representation
     */
    //    public static void assertNumEquals(int expected, Num actual) {
    //        assertEquals(expected, actual.intValue());
    //    }

    /**
     * Verifies that the actual {@code Decimal} value is equal (within a positive offset) to the given {@code Double} representation.
     *
     * @param actual the actual {@code Decimal} value
     * @param expected the given {@code Double} representation to compare the actual value to
     * @throws AssertionError if the actual value is not equal to the given {@code Double} representation
     */
    //    public static void assertNumEquals(double expected, Num actual, Num... delta) {
    //        Num deltaVal = delta == null || delta.length == 0 ? HIGH_PRECISION_OFFSET : delta[0];
    //        assertNumEquals(actual.numOf(expected), actual, deltaVal);
    //    }
    //
    //    public static void assertNumNotEquals(double expected, Num actual, Num... delta) {
    //        Num deltaVal = delta == null || delta.length == 0 ? HIGH_PRECISION_OFFSET : delta[0];
    //        assertNumNotEquals(actual.numOf(expected), actual, deltaVal);
    //    }
    public static void assertNumEquals(double expected, Num actual) {
        assertNumEquals(actual.numOf(expected), actual);
    }

    public static void assertNumNotEquals(double expected, Num actual) {
        assertNumNotEquals(actual.numOf(expected), actual);
    }

    /**
     * Verifies that two indicators have the same size and values
     * @param expected indicator of expected values
     * @param actual indicator of actual values
     */
    //    public static void assertIndicatorEquals(Indicator<Num> expected, Indicator<Num> actual) {
    //        org.junit.Assert.assertEquals("Size does not match,",
    //                expected.getTimeSeries().getBarCount(), actual.getTimeSeries().getBarCount());
    //        for (int i = 0; i < expected.getTimeSeries().getBarCount(); i++) {
    //            assertEquals(String.format("Failed at index %s: %s",i,actual.toString()),
    //                    expected.getValue(i).doubleValue(),
    //                    actual.getValue(i).doubleValue(), GENERAL_OFFSET);
    //        }
    //    }

}