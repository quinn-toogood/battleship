package job.interview.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a ship on the board - considered as a reference to a number of spaces.
 */
@Getter
@EqualsAndHashCode
@ToString
public class Ship {

    /**
     * The set of spaces that this ship occupies.
     */
    private final Set<Space> spaces = new HashSet<>();

    /**
     * Check if a ship is destroyed by seeing if all spaces referenced by the ship have been fired on.
     *
     * @return Whether the ship has been destroyed.
     */
    public boolean isDestroyed() {
        return !spaces.isEmpty() && spaces.stream().allMatch(Space::isFiredUpon);
    }

}
