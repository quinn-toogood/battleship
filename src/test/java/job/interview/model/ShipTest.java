package job.interview.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShipTest {

    /**
     * Test that a ship is destroyed when all its spaces are marked with 'isFiredUpon'
     */
    @Test
    void testShipDestroyed() {
        // Setup
        var ship = new Ship();
        var shipSpaces = ship.getSpaces();

        var space1 = new Space(0, 0);
        var space2 = new Space(0, 1);
        shipSpaces.add(space1);
        shipSpaces.add(space2);

        // Test
        var isDestroyedNoShot = ship.isDestroyed();
        space1.setFiredUpon(true);
        var isDestroyedOneShot = ship.isDestroyed();
        space2.setFiredUpon(true);
        var isDestroyedTwoShot = ship.isDestroyed();

        // Assert
        Assertions.assertFalse(isDestroyedNoShot, "Ship is incorrectly destroyed without firing a shot.");
        Assertions.assertFalse(isDestroyedOneShot, "Ship is incorrectly destroyed after one shot.");
        Assertions.assertTrue(isDestroyedTwoShot, "Ship is incorrectly not destroyed after two shots.");
    }

    /**
     * Ensure that if a ship has an empty set of spaces, it is not marked as destroyed.
     */
    @Test
    void shipWithNoSpacesNotDestroyed() {
        // Setup
        var ship = new Ship();

        // Test
        var destroyedNoSpaces = ship.isDestroyed();

        // Assert
        Assertions.assertFalse(destroyedNoSpaces, "Ship should not be marked as destroyed if it hasn't been placed yet.");
    }
}
