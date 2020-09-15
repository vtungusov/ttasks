package com.siberteam.vtungusov.calc.action;

import com.siberteam.vtungusov.calc.form.CalcForm;
import com.siberteam.vtungusov.calc.form.OperationType;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CalcAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
        CalcForm calcForm = (CalcForm) form;
        OperationType.valueOf(calcForm.getAction()).calculate(calcForm);
        return mapping.findForward("success");
    }
}
