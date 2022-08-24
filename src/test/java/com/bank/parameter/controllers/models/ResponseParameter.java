package com.bank.parameter.controllers.models;

import lombok.Data;

@Data
public class ResponseParameter<T>
{
    private T data;

    private String message;

    private String status;
}
