package job.interview.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
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
