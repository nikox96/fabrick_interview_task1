package com.example.fabrick_interview_task1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A path segment of an asteroid between two planets")
public class AsteroidPath {

    @Schema(description = "The planet where the asteroid path starts", example = "Earth")
    @JsonProperty("fromPlanet")
    private String fromPlanet;

    @Schema(description = "The planet where the asteroid path ends", example = "Jupiter")
    @JsonProperty("toPlanet")
    private String toPlanet;

    @Schema(description = "The start date of the asteroid path segment", example = "2025-10-26", type = "string", format = "date")
    @JsonProperty("fromDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @Schema(description = "The end date of the asteroid path segment", example = "2025-11-26", type = "string", format = "date")
    @JsonProperty("toDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDate;
}
