package cz.osu.raster.graphics;

public class Line3D {

    public Point3D pointA;
    public Point3D pointB;

    public Line3D(Point3D pA, Point3D pB)  {
        pointA = pA;
        pointB = pB;
    }

    public void applyMatrix(Matrix3D matrix3D){

        pointA.applyMatrix(matrix3D);
        pointB.applyMatrix(matrix3D);
    }
}
