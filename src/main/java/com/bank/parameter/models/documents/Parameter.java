package com.bank.parameter.models.documents;

import com.bank.parameter.models.enums.ClientType;
import com.bank.parameter.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "parameters")
public class Parameter extends Audit {
    @Id
    private String id;
    @NotNull(message = "code must not be null")
    private Integer code;
    @NotNull(message = "name must not be null")
    private String name;
    private ClientType clientType;
    @NotNull(message = "comissionPercentage must not be null")
    private Float comissionPercentage;
    @NotNull(message = "transactionDay must not be null")
    private String transactionDay;
    @NotNull(message = "maxMovementPerMonth must not be null")
    private String maxMovementPerMonth;
    @NotNull(message = "maxMovement must not be null")
    private Integer maxMovement;
    @NotNull(message = "percentageMaxMovement must not be null")
    private Float percentageMaxMovement;

    public boolean checkClientType(Integer clientType)
    {
        return getClientType().getValue() == clientType;
    }

}
