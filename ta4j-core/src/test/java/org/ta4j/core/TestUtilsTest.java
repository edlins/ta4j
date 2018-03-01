package org.ta4j.core;

import java.util.function.Function;

import org.junit.Test;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.mocks.MockIndicator;
import org.ta4j.core.num.BigDecimalNum;
import org.ta4j.core.num.Num;

import static org.ta4j.core.TestUtils.assertNumMatches;
import static org.ta4j.core.TestUtils.assertNumNotMatches;
import static org.ta4j.core.TestUtils.assertIndicatorMatches;
import static org.ta4j.core.TestUtils.assertIndicatorNotMatches;

public class TestUtilsTest extends AbstractIndicatorTest {

    public TestUtilsTest(Function<Number, Num> numFunction) {
        super(numFunction);
    }

    @Test
    public void testStringNumPass() {
        stringNum(BigDecimalNum.valueOf("0.0001", 64));
    }

    @Test(expected = AssertionError.class)
    public void testStringNumFail() {
        stringNum(BigDecimalNum.valueOf("0.00001", 64));
    }

    public void stringNum(Num delta) {
        assertNumMatches("0.1234", numOf(0.123456789), delta);
        assertNumNotMatches("0.123", numOf(0.123456789), delta);
        assertNumMatches("0.123456789", numOf(0.1234), delta);
        assertNumNotMatches("0.123456789", numOf(0.123), delta);
        assertNumMatches("0.123456789", numOf(0.123456789), delta);
        assertNumMatches("0.12345678", numOf(0.123456789), delta);
        assertNumMatches("0.12345", numOf(0.123456789), delta);
        assertNumMatches("0.12345", numOf(0.12344), delta);
        assertNumNotMatches("0.12345", numOf(0.123), delta);
        assertNumMatches("0.12344", numOf(0.12345), delta);
        assertNumNotMatches("0.123", numOf(0.12345), delta);
    }

    @Test
    public void testDoubleNumPass() {
        doubleNum(BigDecimalNum.valueOf("0.0001", 64));
    }

    @Test(expected = AssertionError.class)
    public void testDoubleNumFail() {
        doubleNum(BigDecimalNum.valueOf("0.00001", 64));
    }

    public void doubleNum(Num delta) {
        assertNumMatches(0.1234, numOf(0.123456789), delta);
        assertNumNotMatches(0.123, numOf(0.123456789), delta);
        assertNumMatches(0.123456789, numOf(0.1234), delta);
        assertNumNotMatches(0.123456789, numOf(0.123), delta);
        assertNumMatches(0.123456789, numOf(0.123456789), delta);
        assertNumMatches(0.12345678, numOf(0.123456789), delta);
        assertNumMatches(0.12345, numOf(0.123456789), delta);
        assertNumMatches(0.12345, numOf(0.12344), delta);
        assertNumNotMatches(0.12345, numOf(0.123), delta);
        assertNumMatches(0.12344, numOf(0.12345), delta);
        assertNumNotMatches(0.123, numOf(0.12345), delta);
    }

    @Test
    public void testNumNumPass() {
        numNum(BigDecimalNum.valueOf("0.0001", 64));
    }

    @Test(expected = AssertionError.class)
    public void testNumNumFail() {
        numNum(BigDecimalNum.valueOf("0.00001", 64));
    }

    public void numNum(Num delta) {
        assertNumMatches(numOf(0.1234), numOf(0.123456789), delta);
        assertNumNotMatches(numOf(0.123), numOf(0.123456789), delta);
        assertNumMatches(numOf(0.123456789), numOf(0.1234), delta);
        assertNumNotMatches(numOf(0.123456789), numOf(0.123), delta);
        assertNumMatches(numOf(0.123456789), numOf(0.123456789), delta);
        assertNumMatches(numOf(0.12345678), numOf(0.123456789), delta);
        assertNumMatches(numOf(0.12345), numOf(0.123456789), delta);
        assertNumMatches(numOf(0.12345), numOf(0.12344), delta);
        assertNumNotMatches(numOf(0.12345), numOf(0.123), delta);
        assertNumMatches(numOf(0.12344), numOf(0.12345), delta);
        assertNumNotMatches(numOf(0.123), numOf(0.12345), delta);
    }

    @Test
    public void testIndicatorIndicatorPass() {
        indicatorIndicator(BigDecimalNum.valueOf("0.0001", 64));
    }

    @Test(expected = AssertionError.class)
    public void testIndicatorIndicatorFail() {
        indicatorIndicator(BigDecimalNum.valueOf("0.00001", 64));
    }

    public void indicatorIndicator(Num delta) {
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.1234)),
                new MockIndicator(numFunction, numOf(0.123456789)),
                delta);
        assertIndicatorNotMatches(
                new MockIndicator(numFunction, numOf(0.123)),
                new MockIndicator(numFunction, numOf(0.123456789)),
                delta);
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.123456789)),
                new MockIndicator(numFunction, numOf(0.1234)),
                delta);
        assertIndicatorNotMatches(
                new MockIndicator(numFunction, numOf(0.123456789)),
                new MockIndicator(numFunction, numOf(0.123)),
                delta);
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.123456789)),
                new MockIndicator(numFunction, numOf(0.123456789)),
                delta);
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.12345678)),
                new MockIndicator(numFunction, numOf(0.123456789)),
                delta);
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.12345)),
                new MockIndicator(numFunction, numOf(0.123456789)),
                delta);
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.12345)),
                new MockIndicator(numFunction, numOf(0.12344)),
                delta);
        assertIndicatorNotMatches(
                new MockIndicator(numFunction, numOf(0.12345)),
                new MockIndicator(numFunction, numOf(0.123)),
                delta);
        assertIndicatorMatches(
                new MockIndicator(numFunction, numOf(0.12344)),
                new MockIndicator(numFunction, numOf(0.12345)),
                delta);
        assertIndicatorNotMatches(
                new MockIndicator(numFunction, numOf(0.123)),
                new MockIndicator(numFunction, numOf(0.12345)),
                delta);
    }
}
