package job.interview.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Representation of a Space on the game board
 */
@Getter
@RequiredArgsConstructor
public class Space {

    /**
     * X co-ordinate of space.
     */
    private final int x;

    /**
     * Y co-ordinate of space.
     */
    private final int y;

    /**
     * Boolean value to represent whether this space contains a ship.
     */
    @Setter(AccessLevel.PROTECTED)
    private boolean containsShip;

    /**
     * Boolean value to represent whether this space has already been fired on.
     */
    @Setter(AccessLevel.PROTECTED)
    private boolean firedUpon;
}
