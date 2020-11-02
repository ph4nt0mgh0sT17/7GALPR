package cz.osu.raster.graphics;

import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsOperations {

    public static void fillBrightness(V_RAM vram, int brightness){

        brightness = Math.min(255, Math.max(0, brightness));

        for(int y = 0; y < vram.getHeight(); y++)
            for(int x = 0; x < vram.getWidth(); x++)
                vram.getRawData()[y][x] = brightness;
    }

    public static void drawLine(V_RAM vram, Line2D line, int brightness){
        line(vram, line, brightness);
    }

    public static void drawLine3D(V_RAM vram, Line3D line, int brightness) {
        Point2D pointA = new Point2D(line.pointA.vector[0], line.pointA.vector[1]);
        Point2D pointB = new Point2D(line.pointB.vector[0], line.pointB.vector[1]);

        Line2D convertedLine = new Line2D(pointA, pointB);

        line(vram, convertedLine, brightness);
    }

    public static void drawTriangle(V_RAM vram, Triangle2D triangle, int brightness){
        line(vram, new Line2D(triangle.pointA, triangle.pointB), brightness);
        line(vram, new Line2D(triangle.pointB, triangle.pointC), brightness);
        line(vram, new Line2D(triangle.pointC, triangle.pointA), brightness);
    }

    public static void drawTriangle3D(V_RAM vram, Triangle3D triangle, int brightness){
        drawLine3D(vram, new Line3D(triangle.pointA, triangle.pointB), brightness);
        drawLine3D(vram, new Line3D(triangle.pointB, triangle.pointC), brightness);
        drawLine3D(vram, new Line3D(triangle.pointC, triangle.pointA), brightness);
    }

    public static void drawEllipse(V_RAM vram, Ellipse2D ellipse, int brightness) {
        ellipseBresenham(vram, ellipse, brightness);
    }

    private static void ellipseBresenham(V_RAM vram, Ellipse2D ellipse, int brightness) {

        int radiusX = ellipse.getRadiusX();
        int radiusY = ellipse.getRadiusY();

        int middleX = ellipse.getMiddleX();
        int middleY = ellipse.getMiddleY();

        double d1, d2;
        int x, y;
        x = 0;
        y = radiusY;

        double radiusXPow2 = Math.pow(radiusX, 2);
        double radiusYPow2 = Math.pow(radiusY, 2);

        d1 = radiusXPow2 +
                (4 * radiusYPow2) -
                (4 * radiusXPow2 * radiusY);

        double decisionParameter = radiusXPow2 / (Math.sqrt(radiusXPow2 + radiusYPow2));

        // For region 1
        while (decisionParameter >= x)
        {
            projectEllipsePixel(vram, x, y, middleX, middleY, brightness);

            x++;

            if (d1 <= 0)
            {
                d1 += 4 * radiusYPow2 * (2*x + 3);
            }

            else
            {
                y--;
                d1 += (4 * radiusYPow2 * ((2 * x) + 3)) - (8 * radiusXPow2 * (y-1));
            }
        }

        // Decision parameter of region 2
        d2 = radiusYPow2 +
                (4 * radiusXPow2) -
                (4 * radiusYPow2 * radiusX);


        // Switching axis due to 45° tilt
        while (y >= 0) {
            projectEllipsePixel(vram, x, y, middleX, middleY, brightness);

            y--;
            if (d2 <= 0)
            {
                d2 += 4 * radiusXPow2 * (2*y + 3);
            }

            else
            {
                x++;
                d2 += (4 * radiusXPow2 * ((2 * y) + 3)) - (8 * radiusYPow2 * (x-1));
            }
        }
    }

    /**
     * Projects the pixel from the ellipse to each quadrant.
     * @param vram The {@link V_RAM} object that puts pixels in the {@link BufferedImage}.
     * @param x The value of X axis.
     * @param y The value of Y axis.
     * @param middleX The value where should be the middle of ellipse on X axis.
     * @param middleY The value where should be the middle of ellipse on Y axis.
     * @param brightness The brightness of the pixel.
     */
    private static void projectEllipsePixel(V_RAM vram, int x, int y, int middleX, int middleY, int brightness) {
        vram.setPixel(x + middleX, y + middleY, brightness);
        vram.setPixel(-x + middleX, y +middleY, brightness);
        vram.setPixel(x + middleX, -y +middleY, brightness);
        vram.setPixel(-x + middleX, -y +middleY, brightness);
    }

    private static void line(V_RAM vram, Line2D line, int brightness) {

        Point startPoint = line.pointA.getRoundedPoint();
        Point endPoint = line.pointB.getRoundedPoint();

        LineDelta lineDelta = LineDelta.createLineDelta(startPoint, endPoint);

        if (isOnlyPixel(startPoint, endPoint)) {
            vram.setPixel(startPoint.x,startPoint.y,brightness);

            return;
        }

        if (isVerticalLine(startPoint, endPoint)) {
            if (isAxisYSwitched(startPoint, endPoint)) {
                switchPoints(startPoint,endPoint);
            }

            drawVerticalLine(vram, startPoint, endPoint, brightness);

            return;
        }

        if (isHorizontalLine(startPoint, endPoint)) {
            if (isAxisXSwitched(startPoint, endPoint)) {
                switchPoints(startPoint,endPoint);
            }

            drawHorizontalLine(vram, startPoint, endPoint, brightness);

            return;
        }

        float a = (float)lineDelta.getDeltaY() / lineDelta.getDeltaX();

        // If the slope of a line is lesser or equal to 1 it means that
        // X axis is bigger or equal as Y axis, so for every X there will be every Y
        if (Math.abs(a) <= 1) {
            if (isAxisXSwitched(startPoint, endPoint)) {
                switchPoints(startPoint,endPoint);
            }

            bresenhamLow(startPoint, endPoint, vram, brightness);
        }

        // This is reverted way, because the slope of a line is bigger than 1 it means
        // there is definitively a bigger amount of Y pixels than X's.

        // So we can switch Y with X to draw pixel for each Y instead of X.
        else {
            if (isAxisYSwitched(startPoint, endPoint)) {
                switchPoints(startPoint,endPoint);
            }

            bresenhamHigh(startPoint, endPoint, vram, brightness);
        }
    }



    //<editor-fold desc="Bresenham">

    private static void bresenhamHigh(Point startPoint, Point endPoint, V_RAM vram, int brightness) {

        LineDelta lineDelta = LineDelta.createLineDelta(startPoint,endPoint);

        int d = 1;
        if (startPoint.x > endPoint.x) {
            d = -1;
        }

        int h1 = 2 * Math.abs(lineDelta.getDeltaX());
        int h2 = h1 - 2 * (lineDelta.getDeltaY());

        int h = h1 - lineDelta.getDeltaY(); // It is h0 at this point

        int x = startPoint.x;

        for (int y = startPoint.y; y <= endPoint.y; y++) {
            vram.setPixel(x, y, brightness);

            if (h > 0) {
                x += d;
                h+= h2;
            }

            else {
                h +=h1;
            }
        }
    }

    private static void bresenhamLow(Point startPoint, Point endPoint, V_RAM vram, int brightness) {

        LineDelta lineDelta = LineDelta.createLineDelta(startPoint,endPoint);

        int d = 1;
        if (startPoint.y > endPoint.y) {
            d = -1;
        }

        int h1 = 2 * Math.abs(lineDelta.getDeltaY());
        int h2 = h1 - 2 * (lineDelta.getDeltaX());

        int h = h1 - lineDelta.getDeltaX();

        int y = startPoint.y;

        for (int x = startPoint.x; x <= endPoint.x; x++) {
            vram.setPixel(x, y, brightness);

            if (h > 0) {
                y += d;
                h += h2;
            }

            else {
                h +=h1;
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="Old line algorithms">

    /**
     *
     * @param startPoint The start {@link Point} of the line.
     * @param endPoint The end {@link Point} of the line.
     * @param vram The VRAM image where the line is drawn.
     * @param brightness The brightness (0 - 255) of a pixel.
     */
    private static void ddaLow(Point startPoint, Point endPoint, V_RAM vram, int brightness) {
        LineDelta lineDelta = LineDelta.createLineDelta(startPoint,endPoint);

        float a = (float)lineDelta.getDeltaY() / lineDelta.getDeltaX();
        float y = startPoint.y;

        for (int x = startPoint.x; x <= endPoint.x; x++) {
            y += a;
            vram.setPixel(x, Math.round(y), brightness);
        }
    }

    private static void ddaHigh(Point startPoint, Point endPoint, V_RAM vram, int brightness) {
        LineDelta lineDelta = LineDelta.createLineDelta(startPoint,endPoint);

        float a = (float)lineDelta.getDeltaX() / lineDelta.getDeltaY();
        float x = startPoint.x;

        for (int y = startPoint.y; y <= endPoint.y; y++) {
            x += a;
            vram.setPixel(Math.round(x), y, brightness);
        }
    }

    /**
     * <p>
     *      This algorithm is executed whenever the abs(slope) of a line is greater than 1.
     *</p>
     *
     * <p>
     *      The principle of this algorithm is that we perceive the Y axis as the X axis and vice versa.
     *      Because the Y axis has more pixels than the X axis.
     * </p>
     * @param startPoint The start {@link Point} of the line.
     * @param endPoint The end {@link Point} of the line.
     * @param vram The VRAM image where the line is drawn.
     * @param brightness The brightness (0 - 255) of a pixel.
     */
    private static void naiveLineHigh(Point startPoint, Point endPoint, V_RAM vram, int brightness) {
        LineDelta lineDelta = LineDelta.createLineDelta(startPoint,endPoint);

        float a = (float)lineDelta.getDeltaX() / lineDelta.getDeltaY();
        float b = startPoint.x - (a * startPoint.y);
        int x;


        for (int y = startPoint.y;  y <= endPoint.y; y++) {
            x = Math.round(a * y + b);
            vram.setPixel(x,y,brightness);
        }
    }


    /**
     * <p>
     *      This algorithm is executed whenever the abs(slope) of a line is lesser or equal to 1.
     *</p>
     *
     * <p>
     *      The principle of this algorithm is that we perceive the X and Y axis normally because there is more or equal
     *      amount of X pixels than Y pixels.
     * </p>
     * @param startPoint The start {@link Point} of the line.
     * @param endPoint The end {@link Point} of the line.
     * @param vram The VRAM image where the line is drawn.
     * @param brightness The brightness (0 - 255) of a pixel.
     */
    private static void naiveLineLow(Point startPoint, Point endPoint, V_RAM vram, int brightness) {

        LineDelta lineDelta = LineDelta.createLineDelta(startPoint,endPoint);

        float a = (float)lineDelta.getDeltaY() / lineDelta.getDeltaX(); // deltaY / deltaX
        float b = startPoint.y - (a * startPoint.x); // startY - (slope of a line * startX)
        int y;

        for (int x = startPoint.x;  x <= endPoint.x; x++) {
            y = Math.round(a * x + b);
            vram.setPixel(x,y,brightness);
        }
    }
    //</editor-fold>

    /**
     * Draws the vertical line from startPoint to the endPoint.
     * @param vram The VRAM image where the line is drawn.
     * @param startPoint The start {@link Point} of the line.
     * @param endPoint The end {@link Point} of the line.
     * @param brightness The brightness (0 - 255) of a pixel.
     */
    private static void drawVerticalLine(V_RAM vram, Point startPoint, Point endPoint, int brightness) {
        for (int y = startPoint.y; y <= endPoint.y; y++) {
            vram.setPixel(endPoint.x, y, brightness);
        }
    }

    /**
     * Draws the horizontal line from startPoint to the endPoint.
     * @param vram The VRAM image where the line is drawn.
     * @param startPoint The start {@link Point} of the line.
     * @param endPoint The end {@link Point} of the line.
     * @param brightness The brightness (0 - 255) of a pixel.
     */
    private static void drawHorizontalLine(V_RAM vram, Point startPoint, Point endPoint, int brightness) {
        for (int x = startPoint.x; x <= endPoint.x; x++) {
            vram.setPixel(x, endPoint.y, brightness);
        }
    }

    /**
     * Switches the values of both points.
     * @param startPoint The start {@link Point} of the line.
     * @param endPoint The end {@link Point} of the line.
     */
    private static void switchPoints(Point startPoint, Point endPoint) {
        int x = startPoint.x;
        int y = startPoint.y;

        startPoint.x = endPoint.x;
        startPoint.y = endPoint.y;

        endPoint.x = x;
        endPoint.y = y;
    }

    /**
     * Checks if start X is greater than end X.
     * @param start The start {@linkplain Point} coordination.
     * @param end The end {@linkplain Point} coordination.
     * @return A state if the point are really switched (the X axis is inverted).
     */
    private static boolean isAxisXSwitched(Point start, Point end) {
        return (start.x > end.x);
    }

    /**
     * Checks if start Y is greater than end Y.
     * @param start The start {@linkplain Point} coordination.
     * @param end The end {@linkplain Point} coordination.
     * @return A state if the point are really switched (the Y axis is inverted).
     */
    private static boolean isAxisYSwitched(Point start, Point end) {
        return (start.y > end.y);
    }

    /**
     * Checks if the line according to {@link LineDelta} is horizontal line.
     * @param start The start point of the line.
     * @param end The end of the line.
     * @return The state if the current line is horizontal line.
     */
    private static boolean isHorizontalLine(Point start, Point end) {

        LineDelta lineDelta = LineDelta.createLineDelta(start,end);
        return lineDelta.getDeltaY() == 0;
    }

    /**
     * Checks if the line according to {@link LineDelta} is vertical line.
     * @param start The start point of the line.
     * @param end The end of the line.
     * @return The state if the current line is vertical line.
     */
    private static boolean isVerticalLine(Point start, Point end) {
        LineDelta lineDelta = LineDelta.createLineDelta(start,end);
        return lineDelta.getDeltaX() == 0;
    }

    /**
     * Checks if the line is only 1 pixel.
     * @param start The start point of the line.
     * @param end The end of the line.
     */
    private static boolean isOnlyPixel(Point start, Point end) {
        return start.x == end.x && start.y == end.y;
    }
}
