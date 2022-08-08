package com.bank.parameter.controllers;

import com.bank.parameter.handler.ResponseHandler;
import com.bank.parameter.models.dao.ParameterDao;
import com.bank.parameter.models.documents.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/parameter")
public class ParameterControllers {

    @Autowired
    private ParameterDao dao;

    private static final Logger log = LoggerFactory.getLogger(ParameterControllers.class);

    @PostMapping
    public Mono<ResponseEntity<Object>> Create(@RequestBody Parameter p) {
        log.info("[INI] Create Parameter");
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
    public Mono<ResponseEntity<Object>> Update(@PathVariable("id") String id, @RequestBody Parameter p) {
        log.info("[INI] Update Parameter");
        return dao.existsById(id).flatMap(check -> {
                    if (check)
                        return dao.save(p)
                                .doOnNext(parameter -> log.info(parameter.toString()))
                                .map(parameter -> ResponseHandler.response("Done", HttpStatus.OK, parameter))
                                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
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

    @GetMapping("/code/{code}")
    public Mono<ResponseEntity<Object>> FindByCode(@PathVariable String code) {
        log.info("[INI] FindByCode Parameter");

        Flux<Parameter> parameters = dao.findAll();

        return parameters
                .filter(p -> p.getCode().toString().equals(code))
                .next()
                .doOnNext(p -> log.info(p.toString()))
                .map(p -> ResponseHandler.response("Done", HttpStatus.OK, p))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] FindByCode Parameter"));

    }
}
