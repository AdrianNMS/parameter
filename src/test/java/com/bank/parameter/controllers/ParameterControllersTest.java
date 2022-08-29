package com.bank.parameter.controllers;

import com.bank.parameter.controllers.models.ResponseParameter;
import com.bank.parameter.models.dao.ParameterDao;
import com.bank.parameter.models.documents.Parameter;
import com.bank.parameter.models.enums.ClientType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParameterControllersTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    @Autowired
    private ParameterDao dao;

    @MockBean
    private Parameter parameter;

    @BeforeEach
    public void setupMock()
    {
        parameter = Parameter.builder()
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

        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);

        var parameterFlux = Flux.just(parameter);
        var parameterMono = Mono.just(parameter);

        Mockito.when(dao.findAll())
                .thenReturn(parameterFlux);
        Mockito.when(dao.findById("1"))
                .thenReturn(parameterMono);
        Mockito.when(dao.save(parameter))
                .thenReturn(parameterMono);
        Mockito.when(dao.saveAll(parameterList))
                .thenReturn(parameterFlux);
        Mockito.when(dao.existsById("1"))
                .thenReturn(Mono.just(true));
        Mono<Void> empty  = Mono.empty();
        Mockito.when(dao.deleteById("1"))
                .thenReturn(empty);
    }

    @Test
    void create() {
        webTestClient.post()
                .uri("/api/parameter")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(parameter), Parameter.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseParameter<Parameter>>(){})
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getId()).isEqualTo("1");
                });
    }

    @Test
    void findAll() {
        webTestClient.get().uri("/api/parameter")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseParameter<List<Parameter>>>(){})
                .value(responseParameterFindAll -> {
                    var parameterList = responseParameterFindAll.getData();
                    parameterList.forEach(parameter1 -> {
                        Assertions.assertThat(parameter1.getId()).isEqualTo("1");
                    });
                });
    }

    @Test
    void find() {
        webTestClient.get().uri("/api/parameter/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseParameter<Parameter>>(){})
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getId()).isEqualTo("1");
                });
    }

    @Test
    void update() {
        webTestClient.put()
                .uri("/api/parameter/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(parameter), Parameter.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseParameter<Parameter>>(){})
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getId()).isEqualTo("1");
                    Assertions.assertThat(parameterR.getName()).isEqualTo("Cuenta ahorro - normal");
                });
    }

    @Test
    void delete() {
        webTestClient.delete().uri("/api/parameter/{id}","1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseParameter<Parameter>>(){})
                .value(responseParameter -> {
                    Assertions.assertThat(responseParameter.getStatus()).isEqualTo("OK");
                });
    }

    @Test
    void findByCode() {
        webTestClient.get().uri("/api/parameter/catalogue/{clientType}/{code}",0,1000)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ResponseParameter<Parameter>>(){})
                .value(responseParameter -> {
                    var parameterR = responseParameter.getData();
                    Assertions.assertThat(parameterR.getCode()).isEqualTo(1000);
                });
    }

    @Test
    void initParams() {

    }
}