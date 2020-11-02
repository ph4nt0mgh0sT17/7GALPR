package cz.osu.raster.graphics;

import java.awt.*;

public class Point2D {

    public double[] Values;

    public Point2D(){

        Values = new double[3];
    }

    public Point2D(double x, double y){

        Values = new double[3];

        Values[0] = x;
        Values[1] = y;
        Values[2] = 1;
    }

    public void applyMatrix(Matrix2D matrix) {

        Point2D export = new Point2D();

        for (int i = 0; i < 3; i++) {
            export.Values[0] += matrix.Values[0][i] * Values[i];
            export.Values[1] += matrix.Values[1][i] * Values[i];
            export.Values[2] += matrix.Values[2][i] * Values[i];
        }

        Values = export.Values;
    }

    public Point2D clone(){

        return new Point2D(Values[0], Values[1]);
    }

    public Point getRoundedPoint(){

        return new Point((int)Math.round(Values[0]), (int)Math.round(Values[1]));
    }
}
