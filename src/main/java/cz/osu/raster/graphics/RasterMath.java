package cz.osu.raster.graphics;

public class RasterMath {

    public static Point3D crossProduct(Point3D u, Point3D v) {

        double x = (u.vector[1] * v.vector[2]) - (v.vector[1] * u.vector[2]);
        double y = (u.vector[2] * v.vector[0]) - (v.vector[2] * u.vector[0]);
        double z = (u.vector[0] * v.vector[1]) - (v.vector[0] * u.vector[1]);

        Point3D export = new Point3D(x, y, z);

        return export;
    }

    public static double dotProduct(Point3D normalVector, Point3D viewVector) {

        double result =
                        (normalVector.vector[0] * viewVector.vector[0]) +
                        (normalVector.vector[1] * viewVector.vector[1]) +
                        (normalVector.vector[2] * viewVector.vector[2]);

        return result;
    }
}
