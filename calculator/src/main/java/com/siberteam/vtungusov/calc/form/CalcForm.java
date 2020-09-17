package com.siberteam.vtungusov.calc.form;

import lombok.Getter;
import lombok.Setter;
import org.apache.struts.validator.ValidatorForm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CalcForm extends ValidatorForm {
    private String operand1;
    private String operand2;
    private String action;
    private String result;
    private final List<Operation> operations = Arrays.stream(Operation.values())
            .collect(Collectors.toList());
}
