package com.example.fabrick_interview_task1.service;

import com.example.fabrick_interview_task1.model.AsteroidPath;
import com.example.fabrick_interview_task1.model.CloseApproachData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AsteroidPathMapper {

    @Mapping(target = "fromPlanet", source = "current.orbitingBody")
    @Mapping(target = "toPlanet", source = "next.orbitingBody")
    @Mapping(target = "fromDate", source = "current.closeApproachDate")
    @Mapping(target = "toDate", source = "next.closeApproachDate")
    AsteroidPath toPath(CloseApproachData current, CloseApproachData next);
}
