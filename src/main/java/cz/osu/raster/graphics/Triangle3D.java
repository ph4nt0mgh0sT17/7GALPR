package cz.osu.raster.graphics;

public class Triangle3D {

    public Point3D pointA;
    public Point3D pointB;
    public Point3D pointC;
    public boolean isVisible;

    public Triangle3D(Point3D pA, Point3D pB, Point3D pC){

        pointA = pA;
        pointB = pB;
        pointC = pC;
    }

    public void applyMatrix(Matrix3D matrix3D){

        pointA.applyMatrix(matrix3D);
        pointB.applyMatrix(matrix3D);
        pointC.applyMatrix(matrix3D);
    }

    public void checkVisibility(Point3D viewVector) {
        Point3D u = pointB.vector(pointA);
        Point3D v = pointC.vector(pointB);

        Point3D normalVector = RasterMath.crossProduct(u, v);

        double cosinus = RasterMath.dotProduct(normalVector, viewVector);

        isVisible = (cosinus < 0);
    }
}
