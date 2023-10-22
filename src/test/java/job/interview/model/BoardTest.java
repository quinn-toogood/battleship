package job.interview.model;

import job.interview.exception.InvalidCoordinateException;
import job.interview.exception.InvalidPlacementException;
import job.interview.exception.InvalidShotException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardTest {

    /**
     * Tests that adding a ship in a horizontal position works correctly.
     *
     * @throws InvalidPlacementException Exception thrown for invalid placement.
     */
    @Test
    void testAddHorizontalShip() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 3;
        var startingY = 4;
        var length = 5;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var addedShip = board.placeShip(placement);
        var boardSpaces = board.readBoardSpaces();
        var ships = board.readShipList();
        var shipSpaces = addedShip.getSpaces();

        // Assert
        Assertions.assertEquals(1, ships.size());
        Assertions.assertTrue(ships.contains(addedShip), "Ship not included on the board.");
        Assertions.assertEquals(length, shipSpaces.size());

        // Iterate through the board, check that the ship is present at the co-ordinates it is supposed to be, and is not present at any others
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                var currentSpace = boardSpaces.get(x).get(y);
                if (y == startingY && x >= startingX && x < startingX + length) {
                    Assertions.assertTrue(currentSpace.isContainsShip(), "Ship not found at co-ordinates " + x + ", " + y);
                    Assertions.assertTrue(shipSpaces.contains(currentSpace), "Space missing from Ship declaration at co-ordinates");
                } else {
                    Assertions.assertFalse(currentSpace.isContainsShip(), "Ship incorrectly found at co-ordinates " + x + ", " + y);
                }
                Assertions.assertFalse(currentSpace.isFiredUpon(), "Ship should not be marked fired upon after creation.");
            }
        }
    }

    /**
     * Tests that adding a ship in a vertical position works correctly.
     *
     * @throws InvalidPlacementException Exception thrown for invalid placement.
     */
    @Test
    void testAddVerticalShip() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 6;
        var startingY = 5;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.VERTICAL)
                .build();

        // Test
        var addedShip = board.placeShip(placement);
        var boardSpaces = board.readBoardSpaces();
        var ships = board.readShipList();

        // Assert
        Assertions.assertEquals(1, ships.size());
        Assertions.assertTrue(ships.contains(addedShip), "Ship not included on the board.");
        Assertions.assertEquals(length, addedShip.getSpaces().size());

        // Iterate through the board, check that the ship is present at the co-ordinates it is supposed to be, and is not present at any others
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == startingX && y >= startingY && y < startingY + length) {
                    Assertions.assertTrue(boardSpaces.get(x).get(y).isContainsShip(), "Ship not found at co-ordinates " + x + ", " + y);
                } else {
                    Assertions.assertFalse(boardSpaces.get(x).get(y).isContainsShip(), "Ship incorrectly found at co-ordinates " + x + ", " + y);
                }
                Assertions.assertFalse(boardSpaces.get(x).get(y).isFiredUpon(), "Ship should not be marked fired upon after creation.");
            }
        }
    }

    @Test
    void testShootValidShotHit() throws InvalidPlacementException, InvalidShotException {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 6;
        var startingY = 5;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.VERTICAL)
                .build();

        // Test
        board.placeShip(placement);
        var shotHit = board.attackPosition(startingX, startingY);
        var boardSpaces = board.readBoardSpaces();

        // Assert
        Assertions.assertTrue(shotHit, "Shot incorrectly marked as a miss");
        // Iterate through the board, check that the shot is present at the co-ordinates it is supposed to be, and is not present at any others
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == startingX && y == startingY) {
                    Assertions.assertTrue(boardSpaces.get(x).get(y).isFiredUpon(), "No shot at co-ordinates " + x + ", " + y);
                    Assertions.assertTrue(boardSpaces.get(x).get(y).isContainsShip(), "Ship not found at co-ordinates " + x + ", " + y);
                } else {
                    Assertions.assertFalse(boardSpaces.get(x).get(y).isFiredUpon(), "Incorrect shot at co-ordinates " + x + ", " + y);
                }
            }
        }
    }

    @Test
    void testShootValidShotMiss() throws InvalidPlacementException, InvalidShotException {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 6;
        var startingY = 5;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.VERTICAL)
                .build();

        var shotX = 1;
        var shotY = 1;

        // Test
        board.placeShip(placement);
        var shotHit = board.attackPosition(shotX, shotY);
        var boardSpaces = board.readBoardSpaces();

        // Assert
        Assertions.assertFalse(shotHit, "Shot incorrectly marked as a hit");
        // Iterate through the board, check that the shot is present at the co-ordinates it is supposed to be, and is not present at any others
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == shotX && y == shotY) {
                    Assertions.assertTrue(boardSpaces.get(x).get(y).isFiredUpon(), "No shot at co-ordinates " + x + ", " + y);
                    Assertions.assertFalse(boardSpaces.get(x).get(y).isContainsShip(), "Ship incorrectly found at co-ordinates " + x + ", " + y);
                } else {
                    Assertions.assertFalse(boardSpaces.get(x).get(y).isFiredUpon(), "Incorrect shot at co-ordinates " + x + ", " + y);
                }
            }
        }
    }

    @Test
    void testAddShipNegativeXCoord() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = -3;
        var startingY = 5;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingX + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());
    }

    @Test
    void testAddShipLargeXCoord() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 12;
        var startingY = 4;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingX + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());
    }

    @Test
    void testAddShipNegativeYCoord() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 4;
        var startingY = -5;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingY + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());
    }

    @Test
    void testAddShipLargeYCoord() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 4;
        var startingY = 50;
        var length = 3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingY + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());
    }

    @Test
    void testAddShipNegativeLength() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 4;
        var startingY = 5;
        var length = -3;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + length + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());
    }

    @Test
    void testAddShipLargeLength() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 4;
        var startingY = 5;
        var length = 30;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + length + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());
    }


}
