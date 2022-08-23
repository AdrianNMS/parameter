package com.bank.parameter.controllers.models;

import com.bank.parameter.models.documents.Parameter;
import lombok.Data;

@Data
public class ResponseParameter
{
    private Parameter data;

    private String message;

    private String status;
}
