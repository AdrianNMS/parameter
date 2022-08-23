package com.bank.parameter.controllers;

import com.bank.parameter.controllers.models.ResponseParameter;
import com.bank.parameter.controllers.models.ResponseParameterFindAll;
import com.bank.parameter.models.dao.ParameterDao;
import com.bank.parameter.models.documents.Parameter;
import com.bank.parameter.models.enums.ClientType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@WebFluxTest
public class ParameterRestControllersTest
{
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    ParameterDao dao;

    @Test
    void create()
    {
        var parameter = Parameter.builder()
                .id("1")
                .code(1000)
                .clientType(ClientType.STANDARD)
                .name("Cuenta ahorro - normal")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("10")
                .maxMovement(20)
                .percentageMaxMovement(0.1f)
                .build();

        var parameterMono = Mono.just(parameter);
        Mockito.when(dao.save(parameter)).thenReturn(parameterMono);

        webTestClient.post()
                .uri("/api/parameter")
                .contentType(MediaType.APPLICATION_JSON)
                .body(parameterMono, Parameter.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResponseParameter.class)
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getId()).isEqualTo("1");
                });
    }

    @Test
    void find()
    {
        var parameter = Parameter.builder()
                .id("1")
                .code(1000)
                .clientType(ClientType.STANDARD)
                .name("Cuenta ahorro - normal")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("10")
                .maxMovement(20)
                .percentageMaxMovement(0.1f)
                .build();

        var parameterMono = Mono.just(parameter);
        Mockito.when(dao.findById("1")).thenReturn(parameterMono);

        webTestClient.get().uri("/api/parameter/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResponseParameter.class)
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getId()).isEqualTo("1");
                });
    }

    @Test
    void findAll()
    {
        var parameter = Parameter.builder()
                .id("1")
                .code(1000)
                .clientType(ClientType.STANDARD)
                .name("Cuenta ahorro - normal")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("10")
                .maxMovement(20)
                .percentageMaxMovement(0.1f)
                .build();

        var list = new ArrayList<Parameter>();
        list.add(parameter);

        var parameterFlux = Flux.fromIterable(list);

        Mockito.when(dao.findAll()).thenReturn(parameterFlux);

        webTestClient.get().uri("/api/parameter")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResponseParameterFindAll.class)
                .value(responseParameterFindAll -> {
                    var parameterList = responseParameterFindAll.getData();
                    parameterList.forEach(parameter1 -> {
                        Assertions.assertThat(parameter1.getId()).isEqualTo("1");
                    });
                });

    }

    @Test
    void update()
    {
        var parameter = Parameter.builder()
                .id("1")
                .code(1000)
                .clientType(ClientType.STANDARD)
                .name("Cuenta ahorro - normal")
                .comissionPercentage(0.0f)
                .transactionDay("false")
                .maxMovementPerMonth("10")
                .maxMovement(20)
                .percentageMaxMovement(0.1f)
                .build();

        Mono<Boolean> bol = Mono.just(true);
        Mockito.when(dao.existsById("1")).thenReturn(bol);

        Mono<Parameter> parameterMono = Mono.just(parameter);
        Mockito.when(dao.save(parameter)).thenReturn(parameterMono);

        webTestClient.put()
                .uri("/api/parameter/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(parameter), Parameter.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResponseParameter.class)
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getId()).isEqualTo("1");
                    Assertions.assertThat(parameterR.getName()).isEqualTo("Cuenta ahorro - normal");
                });
    }

    @Test
    void delete()
    {
        Mono<Boolean> bol = Mono.just(true);
        Mockito.when(dao.existsById("1")).thenReturn(bol);

        Mono<Void> empty  = Mono.empty();
        Mockito.when(dao.deleteById("1")).thenReturn(empty);

        webTestClient.delete().uri("/api/parameter/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResponseParameter.class)
                .value(responseParameter -> {
                    Assertions.assertThat(responseParameter.getStatus()).isEqualTo("OK");
                });
    }
}