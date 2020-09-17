package com.siberteam.vtungusov.calc.form;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;

public enum Operation {
    ADDITION("+", BigDecimal::add),
    SUBTRACTION("-", BigDecimal::subtract),
    MULTIPLICATION("*", BigDecimal::multiply),
    DIVISION("/", (d1, d2) -> d1.divide(d2, Operation.RESULT_SCALE, RoundingMode.HALF_EVEN));

    private static final int RESULT_SCALE = 10;
    private final String value;
    private final BinaryOperator<BigDecimal> biFunction;

    public String getName() {
        return name;
    }

    private final String name = this.name();

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
}
