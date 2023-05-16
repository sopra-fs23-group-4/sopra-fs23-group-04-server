package ch.uzh.ifi.hase.soprafs23.helper;

import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RoundHelperTest {
    @Mock
    Round round;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkIfRoundExists_RoundExists_NoExceptionThrown() {
        RoundHelper.checkIfRoundExists(round);
    }

    @Test
    public void checkIfRoundExists_RoundDoesNotExist_ExceptionThrown() {
        assertThrows(ResponseStatusException.class, () -> RoundHelper.checkIfRoundExists(null));
    }

    @Test
    public void checkIfRoundIsFinished_RoundIsFinished_NoExceptionThrown() {
        when(round.getStatus()).thenReturn(RoundStatus.FINISHED);
        RoundHelper.checkIfRoundIsFinished(round);
    }

    @Test
    public void checkIfRoundIsFinished_RoundIsNotFinished_ExceptionThrown() {
        when(round.getStatus()).thenReturn(RoundStatus.RUNNING);
        assertThrows(ResponseStatusException.class, () -> RoundHelper.checkIfRoundIsFinished(round));
    }

    @Test
    public void checkIfRoundIsRunning_RoundIsRunning_NoExceptionThrown() {
        when(round.getStatus()).thenReturn(RoundStatus.RUNNING);
        RoundHelper.checkIfRoundIsRunning(round);
    }

    @Test
    public void checkIfRoundIsRunning_RoundIsNotRunning_ExceptionThrown() {
        when(round.getStatus()).thenReturn(RoundStatus.FINISHED);
        assertThrows(ResponseStatusException.class, () -> RoundHelper.checkIfRoundIsRunning(round));
    }
}
