package job.interview.model;

import lombok.*;

/**
 * Object representing the details needed to place a ship on the board.
 */
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ShipPlacement {

    /**
     * Starting X position of placement.
     */
    private final int startingX;

    /**
     * Starting Y position of placement.
     */
    private final int startingY;

    /**
     * Length of placement.
     */
    private final int length;

    /**
     * Orientation to place ship.
     */
    @NonNull
    private final Orientation orientation;
}
