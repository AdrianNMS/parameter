package com.bank.parameter.models.dao;

import com.bank.parameter.models.documents.Parameter;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ParameterDao extends ReactiveMongoRepository<Parameter, String> {

}
