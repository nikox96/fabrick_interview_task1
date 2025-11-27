package com.example.fabrick_interview_task1.client;

import com.example.fabrick_interview_task1.model.NasaAsteroidResponse;

public interface NasaApiClient {
    NasaAsteroidResponse getAsteroidData(int asteroidId);
}
