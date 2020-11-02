package cz.osu.raster.graphics;

public class Matrix2D {

    public double[][] Values;

    public Matrix2D(){

        Values = new double[3][3];

    }

    public static Matrix2D createIdentityMatrix(){

        //TODO

        return null;
    }

    /**
     * Creates the translation matrix with given spaces.
     * @param translationX
     * @param translationY
     * @return The {@link Matrix2D}.
     */
    public static Matrix2D createTranslationMatrix(double translationX, double translationY) {

        Matrix2D translationMatrix = new Matrix2D();

        translationMatrix.Values = new double[][]{
                { 1, 0, translationX },
                { 0, 1, translationY },
                { 0, 0, 1            }
        };

        return translationMatrix;
    }

    public static Matrix2D createRotationMatrix(double angle) {

        double radianAngle = Math.toRadians(angle);

        Matrix2D rotationMatrix = new Matrix2D();

        rotationMatrix.Values = new double[][]{
                { Math.cos(radianAngle), -Math.sin(radianAngle), 0 },
                { Math.sin(radianAngle), Math.cos(radianAngle),  0 },
                { 0,                     0,                      1 }
        };

        return rotationMatrix;
    }

    public static Matrix2D createScalingMatrix(double scalingX, double scalingY) {

        Matrix2D scalingMatrix = new Matrix2D();

        scalingMatrix.Values = new double[][]{
                { scalingX, 0,          0 },
                { 0,        scalingY,   0 },
                { 0,        0,          1 }
        };

        return scalingMatrix;
    }

    public static Matrix2D multiplyMatrix(Matrix2D m1, Matrix2D m2) {

        Matrix2D export = new Matrix2D();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    export.Values[i][j] += m1.Values[i][k] * m2.Values[k][j];
                }
            }
        }

        return export;
    }
}
