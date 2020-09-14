package com.siberteam.vtungusov.calc.form;

import lombok.Getter;
import lombok.Setter;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class CalcForm extends ActionForm {
    private Double operand1;
    private Double operand2;
    private OperationType action;
    private String result;

    public void setAction(String string) {
        this.action = OperationType.valueOf(string);
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (operand1 == null || operand2 == null || action == null) {
            errors.add("common.compute.err",
                    new ActionMessage("error.common.html.calculator.required"));
        }
        if (action.equals(OperationType.DIVISION) && operand2.equals(0d)) {
            errors.add("common.compute.err",
                    new ActionMessage("error.common.html.calculator.divisionByZero"));
        }
        return errors;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        operand1 = 0d;
        operand2 = 0d;
        action = OperationType.ADDITION;
    }
}
