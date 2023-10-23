package job.interview.model;

import job.interview.exception.InvalidCoordinateException;
import job.interview.exception.InvalidPlacementException;
import job.interview.exception.InvalidShotException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class BoardTest {

    /**
     * For tests where the ship location does not matter, but a ship must be placed arbitrarily to allow firing.
     *
     * @param board Board to place ship on.
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    private static void placeShip(Board board) throws InvalidPlacementException {
        var startingX = 3;
        var startingY = 4;
        var length = 5;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();
        board.placeShip(placement);
    }

    /**
     * Tests that adding a ship in a horizontal position works correctly.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
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

        // To sanity check that X and Y are correct, test the hard coded values.
        var expectedSpaces = new ArrayList<Space>();
        expectedSpaces.add(boardSpaces.get(3).get(4));
        expectedSpaces.add(boardSpaces.get(4).get(4));
        expectedSpaces.add(boardSpaces.get(5).get(4));
        expectedSpaces.add(boardSpaces.get(6).get(4));
        expectedSpaces.add(boardSpaces.get(7).get(4));

        for (var space : expectedSpaces) {
            Assertions.assertTrue(space.isContainsShip());
        }

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
     * @throws InvalidPlacementException Exception thrown on invalid placement. Exception thrown for invalid placement.
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

        // To sanity check that X and Y are correct, test the values directly.
        var expectedSpaces = new ArrayList<Space>();
        expectedSpaces.add(boardSpaces.get(6).get(5));
        expectedSpaces.add(boardSpaces.get(6).get(7));
        expectedSpaces.add(boardSpaces.get(6).get(6));

        for (var space : expectedSpaces) {
            Assertions.assertTrue(space.isContainsShip());
        }

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

    /**
     * Test that adding a ship with a negative X co-ordinate throws an error.
     */
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
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingX + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship with an X co-ordinate larger than the size of the board throws an error.
     */
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
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingX + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship with zero X co-ordinate does not throw an error.
     */
    @Test
    void testAddShipZeroXCoord() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 0;
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
     * Test that adding a ship with negative Y co-ordinate throws an error.
     */
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
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingY + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test adding a ship with a Y co-ordinate greater than the board size throws an error.
     */
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
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + startingY + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship with a Y co-ordinate of 0 does not throw an error.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testAddShipZeroYCoord() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 2;
        var startingY = 0;
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
     * Test that adding a ship with a negative length throws an error.
     */
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
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship length: " + length;
        Assertions.assertEquals(message, exception.getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship with a length longer than the board throws an error.
     */
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
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship length: " + length;
        Assertions.assertEquals(message, exception.getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship with zero length throws an error.
     */
    @Test
    void testAddShipZeroLength() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 4;
        var startingY = 5;
        var length = 0;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship length: " + length;
        Assertions.assertEquals(message, exception.getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship with null orientation throws an error.
     */
    @Test
    void testAddShipNullOrientation() {
        // Setup
        var startingX = 4;
        var startingY = 5;
        var length = 3;

        // Test
        var exception = Assertions.assertThrows(NullPointerException.class, () -> ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(null)
                .build());

        // Assert
        var message = "orientation is marked non-null but is null";
        Assertions.assertEquals(message, exception.getMessage());
    }

    /**
     * Test that adding a ship that would extend past the edge horizontally throws an error.
     */
    @Test
    void testAddShipFallOffEdgeHorizontal() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 9;
        var startingY = 5;
        var length = 4;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.HORIZONTAL;
        var causeMessage = "Value " + (startingX + length) + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding a ship that would extend past the edge vertically throws an error.
     */
    @Test
    void testAddShipFallOffEdgeVertical() {
        // Setup
        var size = 10;
        var board = new Board(size);
        var startingX = 4;
        var startingY = 9;
        var length = 4;
        var placement = ShipPlacement.builder()
                .startingX(startingX)
                .startingY(startingY)
                .length(length)
                .orientation(Orientation.VERTICAL)
                .build();

        // Test
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(placement));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid ship placement attempted with values - X: " + startingX + " Y: " + startingY + " Length: " + length
                + " Orientation: " + Orientation.VERTICAL;
        var causeMessage = "Value " + (startingY + length) + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());


        // Check that no ship has been placed.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isContainsShip)));
    }

    /**
     * Test that adding two ships that overlap throws an error, and only the first ship is placed.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testOverlappingShipsError() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);

        // Ship 1 details
        var ship1StartingX = 3;
        var ship1StartingY = 4;
        var ship1Length = 3;
        var ship1Placement = ShipPlacement.builder()
                .startingX(ship1StartingX)
                .startingY(ship1StartingY)
                .length(ship1Length)
                .orientation(Orientation.VERTICAL)
                .build();

        var ship2StartingX = 2;
        var ship2StartingY = 5;
        var ship2Length = 4;
        var ship2Placement = ShipPlacement.builder()
                .startingX(ship2StartingX)
                .startingY(ship2StartingY)
                .length(ship2Length)
                .orientation(Orientation.HORIZONTAL)
                .build();

        var overlappingSpaceX = 3;
        var overlappingSpaceY = 5;


        // Test
        // Place ship 1
        board.placeShip(ship1Placement);
        // Placing ship 2 should cause an exception.
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(ship2Placement));

        // Assert
        Assertions.assertEquals("Attempted to place a ship on top of another - ship already exists at space " + overlappingSpaceX + ", " + overlappingSpaceY, exception.getMessage());
        var boardSpaces = board.readBoardSpaces();

        // Iterate through the board, check that only ship 1 has been placed.
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                var currentSpace = boardSpaces.get(x).get(y);
                if (x == ship1StartingX && y >= ship1StartingY && y < ship1StartingY + ship1Length) {
                    Assertions.assertTrue(currentSpace.isContainsShip(), "Ship 1 not found at co-ordinates " + x + ", " + y);
                } else {
                    Assertions.assertFalse(currentSpace.isContainsShip(), "Ship incorrectly found at co-ordinates " + x + ", " + y);
                }
                Assertions.assertFalse(currentSpace.isFiredUpon(), "Ship should not be marked fired upon after creation.");
            }
        }

    }

    /**
     * Test that attempting to place a ship after someone has already fired throws an error.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     * @throws InvalidShotException      Exception thrown on invalid shot.
     */
    @Test
    void testCannotPlaceShipsAfterShooting() throws InvalidPlacementException, InvalidShotException {
        // Setup
        var size = 10;
        var board = new Board(size);

        // Ship 1 details
        var ship1StartingX = 6;
        var ship1StartingY = 5;
        var ship1Length = 3;
        var ship1Placement = ShipPlacement.builder()
                .startingX(ship1StartingX)
                .startingY(ship1StartingY)
                .length(ship1Length)
                .orientation(Orientation.VERTICAL)
                .build();

        var ship2StartingX = 0;
        var ship2StartingY = 0;
        var ship2Length = 2;
        var ship2Placement = ShipPlacement.builder()
                .startingX(ship2StartingX)
                .startingY(ship2StartingY)
                .length(ship2Length)
                .orientation(Orientation.HORIZONTAL)
                .build();


        // Test
        // Place both ships
        board.placeShip(ship1Placement);
        board.attackPosition(1, 1);
        var exception = Assertions.assertThrows(InvalidPlacementException.class, () -> board.placeShip(ship2Placement));

        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Cannot place a ship after a shot has been fired.";
        Assertions.assertEquals(message, exception.getMessage());


        // Iterate through the board, check that only ship 1 has been placed.
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                var currentSpace = boardSpaces.get(x).get(y);
                if (x == ship1StartingX && y >= ship1StartingY && y < ship1StartingY + ship1Length) {
                    Assertions.assertTrue(currentSpace.isContainsShip(), "Ship 1 not found at co-ordinates " + x + ", " + y);
                } else {
                    Assertions.assertFalse(currentSpace.isContainsShip(), "Ship incorrectly found at co-ordinates " + x + ", " + y);
                }
            }
        }
    }

    /**
     * Test that a valid shot is shot successfully, and that a 'hit' is returned.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     * @throws InvalidShotException      Exception thrown on invalid shot.
     */
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

    /**
     * Tests that valid shot is shot successfully, then returns false for a missed shot.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     * @throws InvalidShotException      Exception thrown on invalid shot.
     */
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

    /**
     * Test that a shot with a negative x co-ordinate throws an error.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testInvalidShotNegativeXCoord() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);

        var shotX = -1;
        var shotY = 1;

        placeShip(board);

        // Test
        var exception = Assertions.assertThrows(InvalidShotException.class, () -> board.attackPosition(shotX, shotY));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid shot attempted at co-ordinates " + shotX + ", " + shotY;
        var causeMessage = "Value " + shotX + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());


        // Check that no shot has been fired.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isFiredUpon)));
    }

    /**
     * Test that a shot with an X co-ordinate larger than the board throws an error.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testInvalidShotLargeXCoord() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);

        var shotX = 11;
        var shotY = 1;

        placeShip(board);

        // Test
        var exception = Assertions.assertThrows(InvalidShotException.class, () -> board.attackPosition(shotX, shotY));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid shot attempted at co-ordinates " + shotX + ", " + shotY;
        var causeMessage = "Value " + shotX + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no shot has been fired.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isFiredUpon)));
    }

    /**
     * Tests that a shot with a negative Y co-ordinate throws an error.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testInvalidShotNegativeYCoord() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);

        var shotX = 1;
        var shotY = -1;

        placeShip(board);

        // Test
        var exception = Assertions.assertThrows(InvalidShotException.class, () -> board.attackPosition(shotX, shotY));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid shot attempted at co-ordinates " + shotX + ", " + shotY;
        var causeMessage = "Value " + shotY + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());

        // Check that no shot has been fired.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isFiredUpon)));
    }

    /**
     * Test that a shot with a Y co-ordinate larger than the board throws an error.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testInvalidShotLargeYCoord() throws InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);

        var shotX = 1;
        var shotY = 11;

        placeShip(board);

        // Test
        var exception = Assertions.assertThrows(InvalidShotException.class, () -> board.attackPosition(shotX, shotY));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Invalid shot attempted at co-ordinates " + shotX + ", " + shotY;
        var causeMessage = "Value " + shotY + " does not fit on the board";
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertTrue(exception.getCause() instanceof InvalidCoordinateException, "Expected cause of error to be Invalid Co-ordinate Exception.");
        Assertions.assertEquals(causeMessage, exception.getCause().getMessage());


        // Check that no shot has been fired.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isFiredUpon)));
    }

    /**
     * Test that a shot at a position that has already been fired upon throws an error.
     *
     * @throws InvalidShotException      Exception thrown on invalid shot.
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     */
    @Test
    void testInvalidShotDuplicateError() throws InvalidShotException, InvalidPlacementException {
        // Setup
        var size = 10;
        var board = new Board(size);

        var shotX = 1;
        var shotY = 1;

        placeShip(board);

        // Test
        board.attackPosition(shotX, shotY);
        var exception = Assertions.assertThrows(InvalidShotException.class, () -> board.attackPosition(shotX, shotY));

        // Assert
        var message = "Attempted to shoot a space already fired on at co-ordinates " + shotX + ", " + shotY;
        Assertions.assertEquals(message, exception.getMessage());
    }

    /**
     * Test that a shot cannot be fired until at least one ship is placed.
     */
    @Test
    void testInvalidShotNoShips() {
        // Setup
        var size = 10;
        var board = new Board(size);

        var shotX = 1;
        var shotY = 1;

        // Test
        var exception = Assertions.assertThrows(InvalidShotException.class, () -> board.attackPosition(shotX, shotY));
        var boardSpaces = board.readBoardSpaces();

        // Assert
        var message = "Cannot fire a shot before any ships are placed.";
        Assertions.assertEquals(message, exception.getMessage());

        // Check that no shot has been fired.
        Assertions.assertTrue(boardSpaces.stream().allMatch(row -> row.stream().noneMatch(Space::isFiredUpon)));

    }

    /**
     * Test that game over is marked when two ships exist and have been destroyed.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     * @throws InvalidShotException      Exception thrown on invalid shot.
     */
    @Test
    void testGameOverTwoShips() throws InvalidPlacementException, InvalidShotException {
        // Setup
        var size = 10;
        var board = new Board(size);

        // Ship 1 details
        var ship1StartingX = 6;
        var ship1StartingY = 5;
        var ship1Length = 3;
        var ship1Placement = ShipPlacement.builder()
                .startingX(ship1StartingX)
                .startingY(ship1StartingY)
                .length(ship1Length)
                .orientation(Orientation.VERTICAL)
                .build();

        var ship2StartingX = 0;
        var ship2StartingY = 0;
        var ship2Length = 2;
        var ship2Placement = ShipPlacement.builder()
                .startingX(ship2StartingX)
                .startingY(ship2StartingY)
                .length(ship2Length)
                .orientation(Orientation.HORIZONTAL)
                .build();


        // Test
        // Place both ships
        board.placeShip(ship1Placement);
        board.placeShip(ship2Placement);
        // Attack each ship until destroyed
        for (int y = ship1StartingY; y < ship1StartingY + ship1Length; y++) {
            var shotHit = board.attackPosition(ship1StartingX, y);
            Assertions.assertTrue(shotHit, "Shot intended to hit Ship 1 missed at co-ordinate: " + ship1StartingX + ", " + y);

        }
        for (int x = ship2StartingX; x < ship2StartingX + ship2Length; x++) {
            var shotHit = board.attackPosition(x, ship2StartingY);
            Assertions.assertTrue(shotHit, "Shot intended to hit Ship 2 missed at co-ordinate: " + x + ", " + ship2StartingY);
        }
        var isGameOver = board.isGameOver();

        // Assert
        Assertions.assertTrue(isGameOver, "Game not marked as over after destroying both ships.");
    }

    /**
     * Test that game over is not marked when there are two ships and one is destroyed.
     *
     * @throws InvalidPlacementException Exception thrown on invalid placement.
     * @throws InvalidShotException      Exception thrown on invalid shot.
     */
    @Test
    void testNotGameOverAfterOneShipDestroyed() throws InvalidPlacementException, InvalidShotException {
        // Setup
        var size = 10;
        var board = new Board(size);

        // Ship 1 details
        var ship1StartingX = 2;
        var ship1StartingY = 5;
        var ship1Length = 3;
        var ship1Placement = ShipPlacement.builder()
                .startingX(ship1StartingX)
                .startingY(ship1StartingY)
                .length(ship1Length)
                .orientation(Orientation.VERTICAL)
                .build();

        var ship2StartingX = 0;
        var ship2StartingY = 0;
        var ship2Length = 2;
        var ship2Placement = ShipPlacement.builder()
                .startingX(ship2StartingX)
                .startingY(ship2StartingY)
                .length(ship2Length)
                .orientation(Orientation.HORIZONTAL)
                .build();


        // Test
        // Place both ships
        board.placeShip(ship1Placement);
        board.placeShip(ship2Placement);
        // Attack ship 1 until destroyed
        for (int y = ship1StartingY; y < ship1StartingY + ship1Length; y++) {
            var shotHit = board.attackPosition(ship1StartingX, y);
            Assertions.assertTrue(shotHit, "Shot intended to hit Ship 1 missed at co-ordinate: " + ship1StartingX + ", " + y);

        }
        var isGameOver = board.isGameOver();

        // Assert
        Assertions.assertFalse(isGameOver, "Game incorrectly marked as over after destroying one ship.");
    }

    /**
     * Test game is not marked over unless at least one ship has been placed.
     */
    @Test
    void testNotGameOverIfNoShipsPlaced() {
        // Setup
        var board = new Board();

        // Test
        var isGameOver = board.isGameOver();

        // Assert
        Assertions.assertFalse(isGameOver, "Game over should not be marked if no ships have been placed yet.");
    }
}
