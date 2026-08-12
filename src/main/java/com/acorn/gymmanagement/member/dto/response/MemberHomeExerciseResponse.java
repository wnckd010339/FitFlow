package com.acorn.gymmanagement.member.dto.response;

import java.math.BigDecimal;

public record MemberHomeExerciseResponse(
        String exerciseName,
        int targetSets,
        Integer targetRepsMin,
        Integer targetRepsMax,
        BigDecimal targetWeight
) {
    public String targetDescription() {
        StringBuilder description = new StringBuilder();
        if (targetWeight != null) {
            description.append(targetWeight.stripTrailingZeros().toPlainString()).append("kg · ");
        }
        description.append(targetSets).append("세트");
        if (targetRepsMin != null) {
            description.append(" · ").append(targetRepsMin);
            if (targetRepsMax != null && !targetRepsMax.equals(targetRepsMin)) {
                description.append('–').append(targetRepsMax);
            }
            description.append("회");
        }
        return description.toString();
    }
}
