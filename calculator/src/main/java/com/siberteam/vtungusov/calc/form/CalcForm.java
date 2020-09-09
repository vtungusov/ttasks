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
    private Double num1, num2;
    private String action;
    private String result;

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (num1 == null || num2 == null || action == null) {
            errors.add("common.compute.err",
                    new ActionMessage("error.common.html.calculator.required"));
        } else {
            if (action.equals("/") && num2.equals(0d)) {
                errors.add("common.compute.err",
                        new ActionMessage("error.common.html.calculator.divisionByZero"));
            }
        }
        return errors;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        num1 = .0d;
        num2 = .0d;
        action = "+";
    }
}
