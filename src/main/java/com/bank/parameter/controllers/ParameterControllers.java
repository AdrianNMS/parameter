package com.bank.parameter.controllers;

import com.bank.parameter.handler.ResponseHandler;
import com.bank.parameter.models.dao.ParameterDao;
import com.bank.parameter.models.documents.Parameter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/parameter")
public class ParameterControllers {

    @Autowired
    private ParameterDao dao;

    private static final Logger log = LoggerFactory.getLogger(ParameterControllers.class);
    private static final String RESILENCE_SERVICE = "defaultConfig";

    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedCreate")
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

    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedUpdate")
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

    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedDelete")
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

    @TimeLimiter(name = RESILENCE_SERVICE)
    @CircuitBreaker(name = RESILENCE_SERVICE,fallbackMethod ="failedFindByCode")
    @GetMapping(value ={"/catalogue/{code}"})
    public Mono<ResponseEntity<Object>> FindByCode(@PathVariable String code) {
        log.info("[INI] FindByCode Parameter");

        Flux<Parameter> parameters = dao.findAll();

        return parameters
                .filter(p -> p.getCode().toString().equals(code))
                .doOnNext(p -> log.info(p.toString()))
                .collectList().flatMap(p -> {
                            if(!p.isEmpty())
                                return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, p.get(0)));
                            else
                                return Mono.just(ResponseHandler.response("Not Found", HttpStatus.BAD_REQUEST, null));
                        })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] FindByCode Parameter"));

    }

    public ResponseEntity<Object> failedCreate(Parameter p, Exception e)
    {
        log.error("[INIT] Failed Create");
        log.error(e.getMessage());
        log.error(p.toString());
        log.error("[END] Failed Create");
        return ResponseHandler.response("Overcharged method", HttpStatus.OK, null);
    }

    public ResponseEntity<Object> failedUpdate(String id, Parameter p, Exception e)
    {
        log.error("[INIT] Failed Update");
        log.error(e.getMessage());
        log.error(id);
        log.error(p.toString());
        log.error("[END] Failed Update");
        return ResponseHandler.response("Overcharged method", HttpStatus.OK, null);
    }

    public ResponseEntity<Object> failedDelete(String id, Exception e)
    {
        log.error("[INIT] Failed Delete");
        log.error(e.getMessage());
        log.error(id);
        log.error("[END] Failed Delete");
        return ResponseHandler.response("Overcharged method", HttpStatus.OK, null);
    }

    public ResponseEntity<Object> failedFindByCode(String code,Exception e)
    {
        log.error("[INIT] Failed FindCode");
        log.error(e.getMessage());
        log.error(code);
        log.error("[END] Failed FindCode");
        return ResponseHandler.response("Overcharged method", HttpStatus.OK, null);
    }
}
