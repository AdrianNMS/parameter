package com.bank.parameter.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClientType
{
    STANDARD(0),
    SPECIAL(1);

    private final int value;
}
