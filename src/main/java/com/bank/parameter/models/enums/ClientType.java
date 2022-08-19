package com.bank.parameter.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClientType
{
    PERSONAL(0),
    COMPANY(1);

    private final int value;
}
