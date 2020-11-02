package cz.osu.raster.graphics;

public class Point3D {

    public double[] vector;

    public Point3D() {
        vector = new double[4];
    }

    public Point3D(double x, double y, double z) {
        vector = new double[] { x, y, z, 1};
    }

    public Point3D applyMatrix(Matrix3D matrix) {

        Point3D export = new Point3D();

        for (int i = 0; i < 4; i++) {
            export.vector[0] += matrix.matrix[0][i] * vector[i];
            export.vector[1] += matrix.matrix[1][i] * vector[i];
            export.vector[2] += matrix.matrix[2][i] * vector[i];
            export.vector[3] += matrix.matrix[3][i] * vector[i];
        }

        return export;
    }

    public Point3D vector(Point3D anotherPoint) {
        return new Point3D(vector[0] - anotherPoint.vector[0], vector[1] - anotherPoint.vector[1], vector[2] - anotherPoint.vector[2]);
    }
}
