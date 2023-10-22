package job.interview.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a ship on the board - considered as a reference to a number of spaces.
 */
@Getter
public class Ship {
    private final Set<Space> spaces = new HashSet<>();
    
    /**
     * Check if a ship is destroyed by seeing if all spaces referenced by the ship have been fired on.
     *
     * @return Whether the ship has been destroyed.
     */
    public boolean isDestroyed() {
        return spaces.stream().allMatch(Space::isFiredUpon);
    }

}
