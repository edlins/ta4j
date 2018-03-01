package org.ta4j.core.indicators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;
import org.ta4j.core.ExternalIndicatorTest;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.BigDecimalNum;
import org.ta4j.core.num.BigDecimalPrecNum;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import static org.ta4j.core.TestUtils.assertIndicatorMatches;

public class NumTypesIndicatorTest {

    ExternalIndicatorTest xls;
    Indicator<Num> doubleClose;
    Indicator<Num> bigDecimalClose;
    Indicator<Num> superBigDecimalClose;
    List<Function<Indicator<Num>, Indicator<Num>>> fs;

    public NumTypesIndicatorTest() throws Exception {
        // isolate the Exception to the constructor
        xls = new XLSIndicatorTest(this.getClass(), "GSPC_1970_2017.xls", 6, BigDecimalNum::valueOf);
        doubleClose = new ClosePriceIndicator(xls.getSeries(DoubleNum::valueOf));
        bigDecimalClose = new ClosePriceIndicator(xls.getSeries(BigDecimalNum::valueOf));
        superBigDecimalClose = new ClosePriceIndicator(xls.getSeries(number -> BigDecimalPrecNum.valueOf(number.toString(), 128)));

        fs = new ArrayList<Function<Indicator<Num>, Indicator<Num>>>();
        fs.add(indicator -> new RSIIndicator((Indicator<Num>) indicator, 14));
        fs.add(indicator -> new SMAIndicator((Indicator<Num>) indicator, 200));
        fs.add(indicator -> new StochasticRSIIndicator((Indicator<Num>) indicator, 200)); // problems with precision!!
    }

    @Test
    public void testFunctions() {
        for (Function func : fs) {
            Indicator<Num> highPrecision = (Indicator<Num>) func.apply(superBigDecimalClose);
            Indicator<Num> mediumPrecision = (Indicator<Num>) func.apply(bigDecimalClose);
            Indicator<Num> lowPrecision = (Indicator<Num>) func.apply(doubleClose);
            testMaxPrecision(highPrecision, mediumPrecision);
            testMaxPrecision(highPrecision, lowPrecision);
            testMaxOffset(highPrecision, mediumPrecision);
            testMaxOffset(highPrecision, lowPrecision);
        }
    }

    public void testMaxPrecision(Indicator<Num> expected, Indicator<Num> actual) {
        Num num = BigDecimalNum.valueOf(actual.numOf(BigDecimalNum.valueOf("68.47467140686891745891139277307765935855946901587159762304918549", 64).getDelegate()).toString());
        int precision = ((BigDecimal) num.getDelegate()).precision();
        int i = 1;
        try {
            for (i = 1; i <= 100; i++) {
                assertIndicatorMatches(expected, actual, i);
            }
        } catch (AssertionError error) {
            System.out.println(actual.getClass().getSimpleName() + " " + precision + ": max precision " + --i);
            return;
        }
        System.out.println(actual.getClass().getSimpleName() + " " + precision + ": no max precision up to " + --i);
    }

    public void testMaxOffset(Indicator<Num> expected, Indicator<Num> actual) {
        Num num = BigDecimalNum.valueOf(actual.numOf(BigDecimalNum.valueOf("68.47467140686891745891139277307765935855946901587159762304918549", 64).getDelegate()).toString());
        int precision = ((BigDecimal) num.getDelegate()).precision();
        int i = 1;
        try {
            for (i = 1; i <= 100; i++) {
                assertIndicatorMatches(expected, actual, BigDecimalNum.valueOf(Math.pow(10, -i)));
            }
        } catch (AssertionError error) {
            System.out.println(actual.getClass().getSimpleName() + " " + precision + ": min offset " + Math.pow(10, -(--i)));
            return;
        }
        System.out.println(actual.getClass().getSimpleName() + " " + precision + ": no min offset up to " + --i);
    }


}
