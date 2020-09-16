package com.siberteam.vtungusov.calc.action;

import com.siberteam.vtungusov.calc.form.CalcForm;
import com.siberteam.vtungusov.calc.form.Operation;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public class CalcAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
        CalcForm calcForm = (CalcForm) form;
        calculate(calcForm);
        return mapping.findForward("success");
    }

    public void calculate(CalcForm form) {
        final BigDecimal operand1 = BigDecimal.valueOf(form.getOperand1());
        final BigDecimal operand2 = BigDecimal.valueOf(form.getOperand2());
        final Operation operation = Operation.valueOf(form.getAction());
        double result = operation.compute(operand1, operand2).doubleValue();
        form.setResult(String.format("%s %s %s = %s", operand1, operation.getValue(), operand2, result));
    }
}
