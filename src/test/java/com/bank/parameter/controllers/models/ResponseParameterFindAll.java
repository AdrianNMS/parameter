package com.bank.parameter.controllers.models;

import com.bank.parameter.models.documents.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class ResponseParameterFindAll
{
    private List<Parameter> data;

    private String message;

    private String status;
}
