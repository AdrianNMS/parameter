package com.bank.parameter.models.documents;

import com.bank.parameter.models.utils.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "parameters")
public class Parameter extends Audit {
    @Id
    private String id;

    private Integer code;

    private String name;

    private String argument;

}
