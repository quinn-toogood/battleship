package job.interview.service;

import job.interview.exception.InvalidPlacementException;
import job.interview.exception.InvalidShotException;
import job.interview.model.Board;
import job.interview.model.Orientation;
import job.interview.model.ShipPlacement;
import lombok.extern.slf4j.Slf4j;

/**
 * State tracker to wrap the 'Board' entity and handle the interface of making requests to the board.
 */
@Slf4j
public class BattleshipGameTracker {

    /**
     * Board entity - initialised on construction to avoid ever having a null board.
     */
    private Board board = new Board();

    /**
     * Start a new game by replacing our board entity with a new one.
     */
    public void startNewGame() {
        log.info("Starting new game!");
        board = new Board();
    }

    /**
     * Places a ship at position x and y, with length and Horizontal or Vertical orientation
     *
     * @param x           X co-ordinate to place ship.
     * @param y           Y co-ordinate to place ship.
     * @param length      Length of ship.
     * @param orientation Orientation - ship can either hang down vertically or to the right horizontally
     * @throws InvalidPlacementException Error thrown when an invalid placement is provided.
     */
    public void placeShip(int x, int y, int length, Orientation orientation) throws InvalidPlacementException {
        log.info("Placing ship at co-ordinates {},{} with length {} and orientation {}", x, y, length, orientation);
        var placement = ShipPlacement.builder()
                .startingX(x)
                .startingY(y)
                .length(length)
                .orientation(orientation)
                .build();
        board.placeShip(placement);
    }

    /**
     * Attacks a position on the board and returns whether the shot was a hit.
     *
     * @param x X co-ordinate to shoot.
     * @param y Y co-ordinate to shoot.
     * @throws InvalidShotException Error thrown when shot is found to be invalid.
     */
    public boolean attackPosition(int x, int y) throws InvalidShotException {
        log.info("Attacking position {},{}", x, y);
        return board.attackPosition(x, y);
    }

    /**
     * Returns whether the current game board has been cleared of ships.
     *
     * @return whether game is over.
     */
    public boolean isGameOver() {
        return board.isGameOver();
    }
}
