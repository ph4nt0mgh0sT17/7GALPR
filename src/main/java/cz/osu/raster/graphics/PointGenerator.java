package cz.osu.raster.graphics;

import java.util.ArrayList;
import java.util.List;

public class PointGenerator {
    
    public static Point2D[] generateStripPoints2D(int count) {
        
        Point2D[] export = new Point2D[2 * count];

        for (int i = 0; i < count; i++) {
            export[i] = new Point2D(10 * i + 5, 20);
            export[i + count] = new Point2D(10 * i + 5, 30);
        }

        return export;
    }

    public static Triangle2D[] generateTriangles(Point2D[] points) {
        ArrayList<Triangle2D> export = new ArrayList<>();

        int lineLength = points.length / 2;

        for (int i = 0; i < lineLength - 1; i++) {
            export.add(new Triangle2D(points[i], points[i + lineLength], points[i + lineLength + 1]));
            export.add(new Triangle2D(points[i], points[i + lineLength + 1], points[i + 1]));
        }

        return export.toArray(new Triangle2D[0]);
    }

    public static Point2D[] generateFanPoints(int count) {
        Point2D[] export = new Point2D[count];

        for (int i = 0; i < count; i++) {
            double t = 2 * Math.PI * i / count;

            export[i] = new Point2D((int)(200 +50 * Math.cos(t)), (int)(200 +50 * Math.sin(t)));
        }

        return export;
    }

    public static Triangle2D[] generateFanTriangles(Point2D[] points) {
        ArrayList<Triangle2D> export = new ArrayList<>();

        for (int i = 0; i < points.length - 1; i++) {
            export.add(new Triangle2D(points[0], points[i], points[i + 1]));
        }

        return export.toArray(new Triangle2D[0]);
    }

    public static Point3D[] generateFanPoints3D(int count) {
        Point3D[] export = new Point3D[count];

        for (int i = 0; i < count; i++) {
            double t = 2 * Math.PI * i / count;

            export[i] = new Point3D((int)(200 +50 * Math.cos(t)), (int)(200 +50 * Math.sin(t)), (int)(200 +50 * Math.cos(t)));
        }

        return export;
    }

    public static Triangle3D[] generateFanTriangles3D(Point3D[] points) {
        ArrayList<Triangle3D> export = new ArrayList<>();

        for (int i = 0; i < points.length - 1; i++) {
            export.add(new Triangle3D(points[0], points[i], points[i + 1]));
        }

        return export.toArray(new Triangle3D[0]);
    }

    public static Triangle3D[] generateCubeTriangles(Point3D[] points) {
        Triangle3D[] export = new Triangle3D[] {

                // horni stena
                new Triangle3D(points[3], points[2], points[6]),
                new Triangle3D(points[3], points[6], points[7]),

                // dolni stena
                new Triangle3D(points[0], points[4], points[5]),
                new Triangle3D(points[0], points[5], points[1]),

                // predni stena
                new Triangle3D(points[0], points[1], points[2]),
                new Triangle3D(points[0], points[2], points[3]),

                // zadni stena
                new Triangle3D(points[5], points[4], points[7]),
                new Triangle3D(points[5], points[7], points[6]),

                // leva stena
                new Triangle3D(points[3], points[7], points[4]),
                new Triangle3D(points[3], points[4], points[0]),

                // prava stena
                new Triangle3D(points[1], points[5], points[6]),
                new Triangle3D(points[1], points[6], points[2])

        };

        return export;
    }
}
