package com.siberteam.vtungusov.calc.form;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum OperationType {
    ADDITION("+") {
        @Override
        protected Double compute(double num1, double num2) {
            return BigDecimal.valueOf(num1)
                    .add(BigDecimal.valueOf(num2))
                    .doubleValue();
        }
    },
    SUBTRACTION("-") {
        @Override
        protected Double compute(double num1, double num2) {
            return BigDecimal.valueOf(num1)
                    .subtract(BigDecimal.valueOf(num2))
                    .doubleValue();

        }
    },
    MULTIPLICATION("*") {
        @Override
        protected Double compute(double num1, double num2) {
            return BigDecimal.valueOf(num1)
                    .multiply(BigDecimal.valueOf(num2))
                    .doubleValue();
        }
    },
    DIVISION("/") {
        @Override
        protected Double compute(double num1, double num2) {
            return BigDecimal.valueOf(num1)
                    .divide(BigDecimal.valueOf(num2), RESULT_SCALE, RoundingMode.HALF_EVEN)
                    .doubleValue();
        }
    };

    private static final int RESULT_SCALE = 10;

    public void calculate(CalcForm form) {
        final double operand1 = form.getOperand1();
        final double operand2 = form.getOperand2();
        final OperationType action = OperationType.valueOf(form.getAction());
        Double result = compute(operand1, operand2);
        form.setResult(String.format("%s %s %s = %s", operand1, action.getValue(), operand2, result));
    }

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    protected abstract Double compute(double num1, double num2);
}
