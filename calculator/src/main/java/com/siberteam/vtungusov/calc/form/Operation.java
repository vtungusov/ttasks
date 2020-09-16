package com.siberteam.vtungusov.calc.form;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;

public enum Operation {
    ADDITION("+", BigDecimal::add),
    SUBTRACTION("-", BigDecimal::subtract),
    MULTIPLICATION("*", BigDecimal::multiply),
    DIVISION("/", (d1, d2) -> d1.divide(d2, Constants.RESULT_SCALE, RoundingMode.HALF_EVEN));

    private final String value;
    private final BinaryOperator<BigDecimal> biFunction;

    Operation(String value, BinaryOperator<BigDecimal> biFunction) {
        this.value = value;
        this.biFunction = biFunction;
    }

    public String getValue() {
        return value;
    }

    public BigDecimal compute(BigDecimal num1, BigDecimal num2) {
        return biFunction.apply(num1, num2);
    }

    private static class Constants {
        public static final int RESULT_SCALE = 10;
    }
}
