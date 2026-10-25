package com.cours.backend.model.dto;

import java.util.List;

public record ProgressionCoursDTO(
        int pourcentage,
        List<Long> chapitresTerminesIds
) {
}
