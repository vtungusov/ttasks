package com.siberteam.vtungusov.calc.action;

import com.siberteam.vtungusov.calc.form.CalcForm;
import com.siberteam.vtungusov.calc.form.OperationType;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalcAction extends Action {

    private static final String DIVISION_BY_ZERO = "Division by zero";
    private static final char REDUNDANT_SUFFIX = '0';

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
        CalcForm calcForm = (CalcForm) form;
        calculate(calcForm);
        return mapping.findForward("success");
    }

    private void calculate(CalcForm form) {
        final double operand1 = form.getOperand1();
        final double operand2 = form.getOperand2();
        final OperationType action = form.getAction();
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
        final String res = trimZero(result);
        final String n1 = trimZero(operand1);
        final String n2 = trimZero(operand2);
        form.setResult(String.format("%s %s %s = %s", n1, action.getValue(), n2, res));
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
        if (num2 == 0) {
            throw new ArithmeticException(DIVISION_BY_ZERO);
        }
        return BigDecimal.valueOf(num1)
                .divide(BigDecimal.valueOf(num2), RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private String trimZero(Double num) {
        String s = num.toString();
        boolean trimmed = false;
        int lastIndex = s.length() - 1;
        int tail = -1;
        while (!trimmed) {
            if (s.charAt(lastIndex) == REDUNDANT_SUFFIX) {
                tail++;
                lastIndex--;
            } else {
                trimmed = true;
            }
        }
        return s.substring(0, lastIndex - tail);
    }
}
