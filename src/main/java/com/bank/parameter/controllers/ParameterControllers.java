package com.bank.parameter.controllers;

import com.bank.parameter.handler.ResponseHandler;
import com.bank.parameter.models.dao.ParameterDao;
import com.bank.parameter.models.documents.Parameter;
import com.bank.parameter.models.enums.ClientType;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parameter")
public class ParameterControllers {

    @Autowired
    private ParameterDao dao;

    private static final Logger log = LoggerFactory.getLogger(ParameterControllers.class);

    @PostMapping
    public Mono<ResponseEntity<Object>> Create(@Valid @RequestBody Parameter p) {
        log.info("[INI] Create Parameter");
        p.setDateRegister(LocalDateTime.now());
        return dao.save(p)
                .doOnNext(parameter -> log.info(parameter.toString()))
                .map(parameter -> ResponseHandler.response("Done", HttpStatus.OK, parameter))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] Create Parameter"));
    }


    @GetMapping
    public Mono<ResponseEntity<Object>> FindAll() {
        log.info("[INI] FindAll Parameter");
        return dao.findAll()
                .doOnNext(parameter -> log.info(parameter.toString()))
                .collectList().map(parameters -> ResponseHandler.response("Done", HttpStatus.OK, parameters))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] FindAll Parameter"));

    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> Find(@PathVariable String id) {
        log.info("[INI] Find Parameter");
        return dao.findById(id)
                .doOnNext(parameter -> log.info(parameter.toString()))
                .map(parameter -> ResponseHandler.response("Done", HttpStatus.OK, parameter))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] Find Parameter"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> Update(@PathVariable("id") String id,@Valid @RequestBody Parameter p) {
        log.info("[INI] Update Parameter");
        return dao.existsById(id).flatMap(check -> {
                    if (check){
                        p.setDateUpdate(LocalDateTime.now());
                        return dao.save(p)
                                .doOnNext(parameter -> log.info(parameter.toString()))
                                .map(parameter -> ResponseHandler.response("Done", HttpStatus.OK, parameter))
                                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
                    }
                    else
                        return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));

                })
                .doFinally(fin -> log.info("[END] Update Parameter"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> Delete(@PathVariable("id") String id) {
        log.info("[INI] Delete Parameter");
        return dao.existsById(id).flatMap(check -> {
                    if (check)
                        return dao.deleteById(id).then(Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)));
                    else
                        return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));
                })
                .doFinally(fin -> log.info("[END] Delete Parameter"));
    }

    @GetMapping(value ={"/catalogue/{clientType}/{code}"})
    public Mono<ResponseEntity<Object>> FindByCode(@PathVariable Integer clientType, @PathVariable Integer code) {
        log.info("[INI] FindByCode Parameter");

        Flux<Parameter> parameters = dao.findAll();

        return parameters
                .filter(p -> p.getCode().equals(code) && p.getClientType().getValue() == clientType)
                .collectList().flatMap(p ->
                {
                    if(!p.isEmpty())
                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, p.get(0)));
                    else
                        return Mono.just(ResponseHandler.response("Not Found", HttpStatus.BAD_REQUEST, null));
                })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] FindByCode Parameter"));
    }

    @PostMapping("/init")
    public Flux<Parameter> initParams()
    {
        List<Parameter> parameterList = new ArrayList<>();


        parameterList.add(Parameter.builder()
                .code(1000)
                .clientType(ClientType.STANDARD)
                .name("Cuenta ahorro - normal")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("10")
                .maxMovement(20)
                .percentageMaxMovement(0.1f)
                .build());
        parameterList.add(Parameter.builder()
                .code(1001)
                .clientType(ClientType.STANDARD)
                .name("Cuenta corriente - normal")
                .comissionPercentage(0.1f)
                .transactionDay("false")
                .maxMovementPerMonth("INFINITY")
                .maxMovement(20)
                .percentageMaxMovement(0.1f)
                .build());
        parameterList.add(Parameter.builder()
                .clientType(ClientType.STANDARD)
                .code(1002)
                .name("Cuenta plazo fijo - normal")
                .comissionPercentage(0.0f)
                .transactionDay("true")
                .maxMovementPerMonth("1")
                .maxMovement(2)
                .percentageMaxMovement(0.1f)
                .build());
        parameterList.add(Parameter.builder()
                .clientType(ClientType.SPECIAL)
                .code(1002)
                .name("Cuenta plazo fijo - normal")
                .comissionPercentage(0.0f)
                .transactionDay("true")
                .maxMovementPerMonth("1")
                .maxMovement(2)
                .percentageMaxMovement(0.1f)
                .build());
        parameterList.add(Parameter.builder()
                .clientType(ClientType.SPECIAL)
                .code(1000)
                .name("Cuenta ahorro - vip")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("100")
                .maxMovement(100)
                .percentageMaxMovement(0.0f)
                .build());
        parameterList.add(Parameter.builder()
                .clientType(ClientType.SPECIAL)
                .code(1001)
                .name("Cuenta corriente - Pyme")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("INFINITY")
                .maxMovement(100)
                .percentageMaxMovement(0.0f)
                .build());



        return dao.saveAll(parameterList);



    }


}
