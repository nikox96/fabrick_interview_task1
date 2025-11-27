package com.example.fabrick_interview_task1.controller;

import com.example.fabrick_interview_task1.constant.Status;
import com.example.fabrick_interview_task1.model.AsteroidPath;
import com.example.fabrick_interview_task1.model.ErrorResponse;
import com.example.fabrick_interview_task1.model.GenericResponse;
import com.example.fabrick_interview_task1.service.AsteroidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fabrick/v1.0")
@Tag(name = "Asteroid API", description = "API to retrieve asteroid path across the Solar System by NASA system")
public class AsteroidController {

    private final AsteroidService asteroidService;

    private static class SuccessfulResponse extends GenericResponse<List<AsteroidPath>> {}

    @Autowired
    public AsteroidController(AsteroidService asteroidService) {
        this.asteroidService = asteroidService;
    }

    @Operation(
            summary = "Get asteroid paths",
            description = "Given an asteroid id, retrieves paths between two dates. " +
                    "If fromDate is not provided, default value is 100 years ago. " +
                    "If toDate is not provided, default value is today."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Asteroid paths successfully retrieved.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SuccessfulResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid parameters (e.g.: fromDate is greater than toDate, date format is not yyyy-MM-dd)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Asteroid doesn't exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Bad Gateway - Error communicating with NASA API",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/asteroids/{asteroidId}/paths")
    public ResponseEntity<GenericResponse<List<AsteroidPath>>> getAsteroidPaths(
            @Parameter(description = "Asteroid ID", required = true, example = "1234567")
            @PathVariable int asteroidId,
            @Parameter(description = "From date for path search (format: yyyy-MM-dd). Default is 100 years ago if not provided.", example = "1925-11-26")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "To date for path search (format: yyyy-MM-dd). Default is today if not provided.", example = "2025-11-26")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        log.info("Fetching asteroid paths for asteroidId: {}, fromDate: {}, toDate: {}",
                asteroidId, fromDate, toDate);

        // Apply default values as per requirements
        LocalDate actualFromDate = (fromDate != null) ? fromDate : LocalDate.now().minusYears(100);
        LocalDate actualToDate = (toDate != null) ? toDate : LocalDate.now();

        //Validate input
        validateDateRange(actualFromDate, actualToDate);

        log.info("Using date range - From: {}, To: {}", actualFromDate, actualToDate);

        List<AsteroidPath> result = asteroidService.getAsteroidPaths(asteroidId, actualFromDate, actualToDate);

        log.info("Result size: {}", result.size());

        GenericResponse<List<AsteroidPath>> response = new GenericResponse<>();
        response.setData(result);
        response.setErrorCode(0);
        response.setStatus(Status.SUCCESS);
        return ResponseEntity.ok().body(response);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate cannot be greater than toDate");
        }
    }
}
