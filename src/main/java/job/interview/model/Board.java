package job.interview.model;

import job.interview.exception.InvalidCoordinateException;
import job.interview.exception.InvalidPlacementException;
import job.interview.exception.InvalidShotException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a battleship game board
 */
@Slf4j
public class Board {

    /**
     * Multidimensional array representing the game board.
     */
    private final Space[][] boardSpaces;

    /**
     * size of the board;
     */
    private final int size;

    /**
     * The ships on the game board
     */
    private final List<Ship> ships = new ArrayList<>();

    private boolean gameBegun = false;

    /**
     * Creates a default Board of size 10.
     */
    public Board() {
        this(10);
    }

    /**
     * Creates a board of arbitrary size, and initialises all values with a new Space.
     *
     * @param size Size of square board.
     */
    public Board(int size) {
        this.size = size;
        boardSpaces = new Space[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                boardSpaces[x][y] = new Space(x, y);
            }
        }
    }

    /**
     * Read ship list as an immutable list of spaces.
     *
     * @return Immutable list of current ships.
     */
    public List<Ship> readShipList() {
        return Collections.unmodifiableList(ships);
    }

    /**
     * Read Board spaces as an immutable list of spaces.
     *
     * @return Immutable list of board spaces.
     */
    public List<List<Space>> readBoardSpaces() {
        var boardSpaceList = new ArrayList<List<Space>>();
        Arrays.stream(boardSpaces).forEach(row -> boardSpaceList.add(Collections.unmodifiableList(Arrays.asList(row))));
        return Collections.unmodifiableList(boardSpaceList);
    }

    /**
     * Returns true if all ships have been destroyed.
     *
     * @return whether all ships on the board have been destroyed.
     */
    public boolean isGameOver() {
        return ships.stream().allMatch(Ship::isDestroyed);
    }


    /**
     * Adds a new battleship to the board. We do this by choosing a 'start space'
     * and then hanging the ship either horizontally or vertically from it.
     * <p>
     * Ships will always hang down when vertical, or to the right when horizontal.
     *
     * @param placement - Object containing ship placement details.
     * @throws InvalidPlacementException - exception thrown when invalid placement details are provided.
     */
    public Ship placeShip(ShipPlacement placement) throws InvalidPlacementException {
        if (gameBegun) {
            var message = "Cannot place a ship after a shot has been fired.";
            log.error(message);
            throw new InvalidPlacementException(message);
        }
        // First validate the placement is physically possible on the board.
        validatePlacement(placement);
        var startingX = placement.getStartingX();
        var startingY = placement.getStartingY();
        // Create our new ship.
        var ship = new Ship();
        var shipSpaces = ship.getSpaces();
        // iterate through the spaces occupied by the ship and validate no other ship is there.
        if (placement.getOrientation() == Orientation.HORIZONTAL) {
            for (int x = startingX; x < startingX + placement.getLength(); x++) {
                var currentSpace = boardSpaces[x][startingY];
                if (currentSpace.isContainsShip()) {
                    var message = "Attempted to place a ship on top of another - ship already exists at space " + x + ", " + startingY;
                    log.error(message);
                    throw new InvalidPlacementException(message);
                }
                shipSpaces.add(currentSpace);
            }
        } else if (placement.getOrientation() == Orientation.VERTICAL) {
            for (int y = startingY; y < startingY + placement.getLength(); y++) {
                var currentSpace = boardSpaces[startingX][y];
                if (currentSpace.isContainsShip()) {
                    var message = "Attempted to place a ship on top of another - ship already exists at space " + startingX + ", " + y;
                    log.error(message);
                    throw new InvalidPlacementException(message);
                }
                shipSpaces.add(currentSpace);
            }
        } else {
            var message = "Invalid orientation supplied " + placement.getOrientation();
            log.error(message);
            throw new InvalidPlacementException(message);
        }
        // Once we have validated that all spaces are valid, we can update the spaces to mark the ship as placed.
        for (var space : shipSpaces) {
            space.setContainsShip(true);
        }
        // Add newly created ship to the set of ships in the game.
        ships.add(ship);
        return ship;
    }

    /**
     * Function to handle checking whether a ship placement is valid.
     *
     * @param placement Object containing details of ship placement.
     * @throws InvalidPlacementException - Exception to be thrown in invalid placement
     */
    private void validatePlacement(ShipPlacement placement) throws InvalidPlacementException {
        var x = placement.getStartingX();
        var y = placement.getStartingY();
        var length = placement.getLength();
        var orientation = placement.getOrientation();

        if (length <= 0 || length > size) {
            var message = "Invalid ship length: " + length;
            log.error(message);
            throw new InvalidPlacementException(message);
        }
        try {
            validateBoardValue(x);
            validateBoardValue(y);
            if (orientation == Orientation.HORIZONTAL) {
                validateBoardValue(x + length);
            } else if (orientation == Orientation.VERTICAL) {
                validateBoardValue(y + length);
            } else {
                throw new InvalidPlacementException("Invalid orientation value provided");
            }
        } catch (InvalidCoordinateException e) {
            var message = "Invalid ship placement attempted with values - X: " + x + " Y: " + y + " Length: " + length
                    + " Orientation: " + orientation;
            log.error(message);
            throw new InvalidPlacementException(message, e);
        }
    }

    /**
     * Given a set of co-ordinates, attempt an attack at this position.
     *
     * @param x X co-ordinate to attack.
     * @param y Y co-ordinate to attack.
     * @return Whether the shot was a hit.
     * @throws InvalidShotException When the shot is pointed at invalid co-ordinates, or an already shot space.
     */
    public boolean attackPosition(int x, int y) throws InvalidShotException {
        if (ships.isEmpty()) {
            var message = "Cannot fire a shot before any ships are placed.";
            log.error(message);
            throw new InvalidShotException(message);
        }

        // Check the co-ordinates provided are valid.
        try {
            validateBoardValue(x);
            validateBoardValue(y);
        } catch (InvalidCoordinateException e) {
            var message = "Invalid shot attempted at co-ordinates " + x + ", " + y;
            log.error(message);
            throw new InvalidShotException(message, e);
        }

        var attackedSpace = boardSpaces[x][y];

        // If we've already shot at this space, throw an exception.
        if (attackedSpace.isFiredUpon()) {
            var message = "Attempted to shoot a space already fired on at co-ordinates " + x + ", " + y;
            log.warn(message);
            throw new InvalidShotException(message);
        }

        // Otherwise, mark the board as fired upon.
        attackedSpace.setFiredUpon(true);

        // If this is the first valid shot, mark the game as begun.
        if (!gameBegun) {
            gameBegun = true;
        }

        // Return whether the shot was a hit.
        return attackedSpace.isContainsShip();
    }

    /**
     * Checks whether a given value fits on the board
     *
     * @param value Value to be checked.
     */
    private void validateBoardValue(int value) throws InvalidCoordinateException {
        if (value < 0 || value > size) {
            var message = "Value " + value + " does not fit on the board";
            log.error(message);
            throw new InvalidCoordinateException(message);
        }
    }

    /**
     * Made for fun - builds the board in ascii.
     *
     * @param ownsBoard - whether this is your own board - if true, show hidden ships.
     * @return Assembled string of the board state.
     */
    public String buildBoardString(boolean ownsBoard) {
        StringBuilder boardStringBuilder = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                var currentSpace = boardSpaces[x][y];
                if (currentSpace.isContainsShip()) {
                    if (currentSpace.isFiredUpon()) {
                        boardStringBuilder.append('x');
                    } else if (ownsBoard) {
                        boardStringBuilder.append('o');
                    } else {
                        boardStringBuilder.append('-');
                    }
                } else {
                    boardStringBuilder.append('-');
                }
            }
            boardStringBuilder.append('\n');
        }
        log.info('\n' + boardStringBuilder.toString());
        return boardStringBuilder.toString();
    }

}
