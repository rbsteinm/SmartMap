package ch.epfl.smartmap.test;

import org.junit.Test;

import android.test.AndroidTestCase;

import ch.epfl.smartmap.cache.Point;

public class PointTest extends AndroidTestCase {

    private final double x = 1.23;
    private final double y = 3.21;
    private final double newCoord = 4.56;
    
    @Test
    public void testSetX() {
        Point point = new Point(x, y);
        point.setX(newCoord);
        assertTrue(point.getX() == newCoord);
    }

    @Test
    public void testSetY() {
        Point point = new Point(x, y);
        point.setY(newCoord);
        assertTrue(point.getY() == newCoord);
    }
}