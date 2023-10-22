package job.interview.model;

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
        Arrays.stream(boardSpaces).forEach(row -> {
            boardSpaceList.add(Collections.unmodifiableList(Arrays.asList(row)));
        });
        return Collections.unmodifiableList(boardSpaceList);
    }

}
