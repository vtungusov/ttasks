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
    public static final String COMMON_ERR = "common.compute.err";

    private Double operand1;
    private Double operand2;
    private String action;
    private String result;

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (operand1 == null || operand2 == null || action == null) {
            errors.add(COMMON_ERR,
                    new ActionMessage("error.common.html.calculator.required"));
        }
        if (("DIVISION").equals(action) && Double.valueOf(0d).equals(operand2)) {
            errors.add(COMMON_ERR,
                    new ActionMessage("error.common.html.calculator.divisionByZero"));
        }
        return errors;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        operand1 = 0d;
        operand2 = 0d;
        action = "ADDITION";
    }
}
