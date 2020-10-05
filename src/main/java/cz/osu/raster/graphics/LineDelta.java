package cz.osu.raster.graphics;

import java.awt.*;

public class LineDelta {

    private final int deltaX;
    private final int deltaY;

    public LineDelta(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public static LineDelta createLineDelta(Point beginPoint, Point endPoint) {
        return new LineDelta(endPoint.x - beginPoint.x, endPoint.y - beginPoint.y);
    }
}
