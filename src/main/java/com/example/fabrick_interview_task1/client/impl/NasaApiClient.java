package com.example.fabrick_interview_task1.client.impl;

import com.example.fabrick_interview_task1.config.NasaApiProperties;
import com.example.fabrick_interview_task1.constant.ApplicationError;
import com.example.fabrick_interview_task1.exception.NasaApiException;
import com.example.fabrick_interview_task1.model.NasaAsteroidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class NasaApiClient implements com.example.fabrick_interview_task1.client.NasaApiClient {

    static final String ASTEROID_ID_PLACE_HOLDER = "{asteroidId}";
    private final RestClient restClient;
    private final NasaApiProperties nasaApiProperties;

    @Cacheable(value = "asteroid", key = "#asteroidId")
    public NasaAsteroidResponse getAsteroidData(int asteroidId) {
        log.info("Retrieving asteroid data from NASA API for asteroidId: {}", asteroidId);

        String url = UriComponentsBuilder.fromUriString(nasaApiProperties.getBaseUrl()).pathSegment(ASTEROID_ID_PLACE_HOLDER)
                .queryParam("api_key", nasaApiProperties.getApiKey())
                .buildAndExpand(asteroidId)
                .toUriString();

        log.debug("NASA API URL: {}", url);

        try {
            NasaAsteroidResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(NasaAsteroidResponse.class);
            log.info("Successfully fetched data from NASA API for asteroidId: {}", asteroidId);
            return response;

        } catch (HttpClientErrorException e) {
            log.error("NASA API client error for asteroidId {}: {} - {}", asteroidId, e.getStatusCode(), e.getMessage(), e);

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NasaApiException(e, HttpStatus.NOT_FOUND, ApplicationError.NASA_ASTEROID_NOT_FOUND);
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new NasaApiException(e, HttpStatus.TOO_MANY_REQUESTS, ApplicationError.NASA_RATE_LIMITER_EXCEEDED);
            } else {
                throw new NasaApiException(e, HttpStatus.BAD_GATEWAY, ApplicationError.NASA_UNABLE_TO_RETRIEVE_DATA);
            }
        } catch (HttpServerErrorException e) {
            log.error("NASA API server error for asteroidId {}: {} - {}", asteroidId, e.getStatusCode(), e.getMessage(), e);
            throw new NasaApiException(e, HttpStatus.INTERNAL_SERVER_ERROR, ApplicationError.GENERIC_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error calling NASA API for asteroidId: {}", asteroidId, e);
            throw new NasaApiException(e, HttpStatus.INTERNAL_SERVER_ERROR, ApplicationError.GENERIC_ERROR);
        }
    }
}