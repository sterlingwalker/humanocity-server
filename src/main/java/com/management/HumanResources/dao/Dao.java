package com.management.HumanResources.dao;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class Dao {

  public WebClient webClient;

  public <T> T getSingleObject(Class<T> classReturnType, String path, Object... args) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(path).build(args))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(classReturnType)
        .block();
  }

  public <T> List<T> getListOfObjects(Class<T> listClassReturnType, String path, Object... args) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(path).build(args))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(listClassReturnType)
        .collectList()
        .block();
  }

  public <T> T putSingleObject(
      Class<T> classReturnType, String path, Object body, Object... args) {
    return webClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(path).build(args))
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(classReturnType)
        .block();
  }

  public <T> List<T> postListOfObjects(
      Class<T> listClassReturnType, String path, Object body, Object... args) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(path).build(args))
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToFlux(listClassReturnType)
        .collectList()
        .block();
  }

  public <T> T deleteObjectWithParams(
    Class<T> classReturnType, String path, String params, Object... args) {
  return webClient
      .delete()
      .uri(uriBuilder -> uriBuilder.path(path).queryParam(params).build(args))
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(classReturnType)
      .block();
}

  public <T> T patchSingleObject(
      Class<T> classReturnType, String path, Object body, Object... args) {
    return webClient
        .patch()
        .uri(uriBuilder -> uriBuilder.path(path).build(args))
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(classReturnType)
        .block();
  }
}
