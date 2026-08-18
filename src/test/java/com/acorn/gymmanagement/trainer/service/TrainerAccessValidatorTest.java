package com.acorn.gymmanagement.trainer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.trainer.mapper.TrainerMemberMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerAccessValidatorTest {
    @Mock TrainerMemberMapper mapper;

    @Test
    void assignedMemberPassesValidation() {
        TrainerAccessValidator validator = new TrainerAccessValidator(mapper);
        when(mapper.findTrainerId(10L)).thenReturn(Optional.of(20L));
        when(mapper.existsAssignedMember(20L, 30L)).thenReturn(true);

        validator.requireAssignedMember(10L, 30L);

        assertEquals(20L, validator.requireTrainerId(10L));
    }

    @Test
    void memberAssignedToAnotherTrainerIsRejected() {
        TrainerAccessValidator validator = new TrainerAccessValidator(mapper);
        when(mapper.findTrainerId(10L)).thenReturn(Optional.of(20L));
        when(mapper.existsAssignedMember(20L, 99L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> validator.requireAssignedMember(10L, 99L));
    }
}
