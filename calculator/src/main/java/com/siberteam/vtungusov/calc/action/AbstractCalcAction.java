package com.siberteam.vtungusov.calc.action;

import com.siberteam.vtungusov.calc.form.CalcForm;
import com.siberteam.vtungusov.calc.form.OperationType;
import org.apache.struts.action.Action;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class AbstractCalcAction extends Action {

    private static final int RESULT_SCALE = 10;

    public void calculate(CalcForm form) {
        final double operand1 = form.getOperand1();
        final double operand2 = form.getOperand2();
        final OperationType action = OperationType.valueOf(form.getAction());
        Double result;
        switch (action) {
            case SUBTRACTION:
                result = subtract(operand1, operand2);
                break;
            case MULTIPLICATION:
                result = multiply(operand1, operand2);
                break;
            case DIVISION:
                result = divide(operand1, operand2);
                break;
            default:
                result = sum(operand1, operand2);
                break;
        }
        form.setResult(String.format("%s %s %s = %s", operand1, action.getValue(), operand2, result));
    }

    private Double sum(double num1, double num2) {
        return BigDecimal.valueOf(num1)
                .add(BigDecimal.valueOf(num2))
                .doubleValue();
    }

    private Double subtract(double num1, double num2) {
        return BigDecimal.valueOf(num1)
                .subtract(BigDecimal.valueOf(num2))
                .doubleValue();
    }

    private Double multiply(double num1, double num2) {
        return BigDecimal.valueOf(num1)
                .multiply(BigDecimal.valueOf(num2))
                .doubleValue();
    }

    private Double divide(double num1, double num2) {
        return BigDecimal.valueOf(num1)
                .divide(BigDecimal.valueOf(num2), RESULT_SCALE, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}
