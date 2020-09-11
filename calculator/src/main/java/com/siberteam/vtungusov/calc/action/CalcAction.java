package com.siberteam.vtungusov.calc.action;

import com.siberteam.vtungusov.calc.form.CalcForm;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CalcAction extends Action {

    public static final String DIVISION_BY_ZERO = "Division by zero";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
        CalcForm calcForm = (CalcForm) form;
        calculate(calcForm);
        return mapping.findForward("success");
    }

    private void calculate(CalcForm form) {
        final double num1 = form.getNum1();
        final double num2 = form.getNum2();
        final String action = form.getAction();
        Double result;
        switch (action) {
            case "-":
                result = subtract(num1, num2);
                break;
            case "*":
                result = multiply(num1, num2);
                break;
            case "/":
                result = divide(num1, num2);
                break;
            default:
                result = sum(num1, num2);
                break;
        }
        final String res = trimZero(result);
        final String n1 = trimZero(num1);
        final String n2 = trimZero(num2);
        form.setResult(String.format("%s %s %s = %s", n1, action, n2, res));
    }

    private Double sum(double num1, double num2) {
        return num1 + num2;
    }

    private Double subtract(double num1, double num2) {
        return num1 - num2;
    }

    private Double multiply(double num1, double num2) {
        return num1 * num2;
    }

    private Double divide(double num1, double num2) {
        if (num2 == 0) {
            throw new ArithmeticException(DIVISION_BY_ZERO);
        }
        return num1 / num2;
    }

    private String trimZero(Double num) {
        String s = num.toString();
        boolean trimmed = false;
        while (!trimmed) {
            if (s.charAt(s.length() - 1) == '0') {
                s = s.substring(0, s.length() - 2);
            } else {
                trimmed = true;
            }
        }
        return s;
    }
}
