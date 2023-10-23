package job.interview.service;

import job.interview.exception.InvalidPlacementException;
import job.interview.exception.InvalidShotException;
import job.interview.model.Board;
import job.interview.model.Orientation;
import job.interview.model.Ship;
import job.interview.model.ShipPlacement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

/**
 * Tests for the {@link BattleshipGameTracker}.
 */
public class BattleshipGameTrackerTest {

    /**
     * Test that a new board is constructed when a new game is started.
     *
     * @throws NoSuchFieldException   Thrown when reflection fails to find the board field.
     * @throws IllegalAccessException Thrown when the board field is not accessible.
     */
    @Test
    void testStartNewGame() throws NoSuchFieldException, IllegalAccessException {
        // Setup
        var battleshipTracker = new BattleshipGameTracker();

        try (var mockBoardConstructor = Mockito.mockConstruction(Board.class)) {
            // Test
            battleshipTracker.startNewGame();

            // Assert
            Assertions.assertEquals(1, mockBoardConstructor.constructed().size());
            // By reflection, check that the board field on the battleship tracker object has been replaced with the mock object.
            Field field = battleshipTracker.getClass().getDeclaredField("board");
            field.setAccessible(true);
            var actualBoard = field.get(battleshipTracker);
            var expectedBoard = mockBoardConstructor.constructed().get(0);
            Assertions.assertEquals(expectedBoard, actualBoard);

            // Verify
            Mockito.verifyNoInteractions(expectedBoard);
        }
    }

    /**
     * Test that placing a ship calls the correct function on the board with the correct values.
     *
     * @throws InvalidPlacementException Exception when placement is invalid.
     */
    @Test
    void testPlaceShip() throws InvalidPlacementException {
        // Setup
        var x = 1;
        var y = 2;
        var length = 3;
        var orientation = Orientation.VERTICAL;

        var expectedPlacement = ShipPlacement.builder()
                .startingX(x)
                .startingY(y)
                .length(length)
                .orientation(orientation)
                .build();

        var expectedShip = new Ship();

        try (var mockBoardConstructor = Mockito.mockConstruction(Board.class, (board, context) -> {
            // Mock
            Mockito.when(board.placeShip(expectedPlacement)).thenReturn(expectedShip);
        })) {
            var battleshipTracker = new BattleshipGameTracker();

            // Test
            battleshipTracker.placeShip(x, y, length, orientation);
            Assertions.assertEquals(1, mockBoardConstructor.constructed().size());
            var board = mockBoardConstructor.constructed().get(0);


            // Verify
            Mockito.verify(board).placeShip(expectedPlacement);
            Mockito.verifyNoMoreInteractions(board);
        }
    }

    /**
     * Test that when an InvalidPlacementException is thrown by the board, it is rethrown by the tracker.
     *
     * @throws InvalidPlacementException Exception when placement is invalid.
     */
    @Test
    void testPlaceShipError() throws InvalidPlacementException {
        // Setup
        var x = 10;
        var y = 2;
        var length = 3;
        var orientation = Orientation.VERTICAL;

        var expectedPlacement = ShipPlacement.builder()
                .startingX(x)
                .startingY(y)
                .length(length)
                .orientation(orientation)
                .build();

        var expectedException = new InvalidPlacementException("Bad Placement");

        try (var mockBoardConstructor = Mockito.mockConstruction(Board.class, (board, context) -> {
            // Mock
            Mockito.when(board.placeShip(expectedPlacement)).thenThrow(expectedException);
        })) {
            var battleshipTracker = new BattleshipGameTracker();

            // Test
            var actualException = Assertions.assertThrows(InvalidPlacementException.class, () -> battleshipTracker.placeShip(x, y, length, orientation));
            Assertions.assertEquals(1, mockBoardConstructor.constructed().size());
            var board = mockBoardConstructor.constructed().get(0);

            // Assert
            Assertions.assertEquals(expectedException, actualException);

            // Verify
            Mockito.verify(board).placeShip(expectedPlacement);
            Mockito.verifyNoMoreInteractions(board);
        }
    }

    /**
     * Test that the attack position function passes the values to the board and returns the correct result.
     *
     * @throws InvalidShotException Exception when shot is invalid.
     */
    @Test
    void testAttackPosition() throws InvalidShotException {
        // Setup
        var x = 1;
        var y = 2;

        var expectedShotResult = false;

        try (var mockBoardConstructor = Mockito.mockConstruction(Board.class, (board, context) -> {
            // Mock
            Mockito.when(board.attackPosition(x, y)).thenReturn(expectedShotResult);
        })) {
            var battleshipTracker = new BattleshipGameTracker();

            // Test
            var actualShotResult = battleshipTracker.attackPosition(x, y);
            Assertions.assertEquals(1, mockBoardConstructor.constructed().size());
            var board = mockBoardConstructor.constructed().get(0);

            // Assert
            Assertions.assertEquals(expectedShotResult, actualShotResult);

            // Verify
            Mockito.verify(board).attackPosition(x, y);
            Mockito.verifyNoMoreInteractions(board);
        }
    }

    /**
     * Test that when an InvalidShotException is thrown by the board, it is rethrown by the tracker.
     *
     * @throws InvalidShotException Exception when shot is invalid.
     */
    @Test
    void testAttackPositionError() throws InvalidShotException {
        // Setup
        var x = 10;
        var y = 2;

        var expectedException = new InvalidShotException("Invalid Shot.");

        try (var mockBoardConstructor = Mockito.mockConstruction(Board.class, (board, context) -> {
            // Mock
            Mockito.when(board.attackPosition(x, y)).thenThrow(expectedException);
        })) {
            var battleshipTracker = new BattleshipGameTracker();

            // Test
            var actualException = Assertions.assertThrows(InvalidShotException.class, () -> battleshipTracker.attackPosition(x, y));
            Assertions.assertEquals(1, mockBoardConstructor.constructed().size());
            var board = mockBoardConstructor.constructed().get(0);

            // Assert
            Assertions.assertEquals(expectedException, actualException);

            // Verify
            Mockito.verify(board).attackPosition(x, y);
            Mockito.verifyNoMoreInteractions(board);
        }
    }


    /**
     * Test that isGameOver returns the boards game over value.
     */
    @Test
    void testIsGameOver() {
        // Setup
        var expectedGameOverValue = true;

        try (var mockBoardConstructor = Mockito.mockConstruction(Board.class, (board, context) -> {
            // Mock
            Mockito.when(board.isGameOver()).thenReturn(expectedGameOverValue);
        })) {
            var battleshipTracker = new BattleshipGameTracker();

            // Test
            var actualGameOverValue = battleshipTracker.isGameOver();
            Assertions.assertEquals(1, mockBoardConstructor.constructed().size());
            var board = mockBoardConstructor.constructed().get(0);

            // Assert
            Assertions.assertEquals(expectedGameOverValue, actualGameOverValue);

            // Verify
            Mockito.verify(board).isGameOver();
            Mockito.verifyNoMoreInteractions(board);
        }
    }
}
