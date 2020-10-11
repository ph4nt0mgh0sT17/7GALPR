package cz.osu.raster.graphics;

public class Ellipse2D {

    private int radiusX;
    private int radiusY;
    private int middleX;
    private int middleY;

    public Ellipse2D(int radiusX, int radiusY, int middleX, int middleY) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.middleX = middleX;
        this.middleY = middleY;
    }

    public int getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
    }

    public int getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(int radiusY) {
        this.radiusY = radiusY;
    }

    public int getMiddleX() {
        return middleX;
    }

    public void setMiddleX(int middleX) {
        this.middleX = middleX;
    }

    public int getMiddleY() {
        return middleY;
    }

    public void setMiddleY(int middleY) {
        this.middleY = middleY;
    }
}
