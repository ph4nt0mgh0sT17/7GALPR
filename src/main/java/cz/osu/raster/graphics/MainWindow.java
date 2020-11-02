package cz.osu.raster.graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MainWindow extends JPanel{

    private ImagePanel imagePanel;
    private JLabel infoLabel;

    private V_RAM vram;

    Matrix3D k;
    Matrix3D q;

    Point3D[] points;

    Matrix3D rotationXZ;
    Matrix3D rotationXY;
    Matrix3D rotationYZ;

    Matrix3D scaleUp;
    Matrix3D scaleDown;

    Matrix3D moveUp;
    Matrix3D moveDown;
    Matrix3D moveRight;
    Matrix3D moveLeft;

    Point3D viewVector;

    private String currentModel;

    public MainWindow() {

        initialize();

        vram = new V_RAM(500, 500);

        GraphicsOperations.fillBrightness(vram, 255);

        viewVector = new Point3D(0, 0, 5);

        rotationXZ = Matrix3D.createRotationMatrixXZ(10);
        rotationXY = Matrix3D.createRotationMatrixXY(10);
        rotationYZ = Matrix3D.createRotationMatrixYZ(10);

        scaleUp = Matrix3D.createScalingMatrix(1.05);
        scaleDown = Matrix3D.createScalingMatrix(0.95);

        moveUp = Matrix3D.createTranslationMatrix(0,-10,-10);
        moveDown = Matrix3D.createTranslationMatrix(0,10,0);
        moveLeft = Matrix3D.createTranslationMatrix(-10,0,0);
        moveRight = Matrix3D.createTranslationMatrix(10,0,0);

        refresh();
        setRenderForCube("XY");
    }

    private void drawCube() {
        List<Point3D> drawPoints = new ArrayList<>();

        Matrix3D kq = Matrix3D.multiplyMatrix(k,q);

        for (Point3D point : points) {
            Point3D toDrawPoint = point.applyMatrix(kq);
            drawPoints.add(toDrawPoint);
        }

        Triangle3D[] triangles = PointGenerator.generateCubeTriangles(drawPoints.toArray(new Point3D[0]));

        for (Triangle3D triangle : triangles) {
            triangle.checkVisibility(viewVector);

            if (!triangle.isVisible)
                GraphicsOperations.drawTriangle3D(vram, triangle,200);
        }

        for (Triangle3D triangle : triangles) {
            if (triangle.isVisible)
                GraphicsOperations.drawTriangle3D(vram, triangle,0);
        }

        imagePanel.setImage(vram.getImage());
    }

    private void drawFanTriangles() {
        List<Point3D> drawPoints = new ArrayList<>();

        Matrix3D kq = Matrix3D.multiplyMatrix(k,q);

        for (int i = 0; i < points.length; i++) {
            Point3D point = points[i];

            Point3D toDrawPoint = point.applyMatrix(kq);
            drawPoints.add(toDrawPoint);
        }

        List<Triangle3D> triangles = Arrays.asList(PointGenerator.generateFanTriangles3D(drawPoints.toArray(new Point3D[0])));

        for (Triangle3D triangle : triangles) {
            triangle.checkVisibility(viewVector);

            if (!triangle.isVisible)
                GraphicsOperations.drawTriangle3D(vram, triangle,200);
        }

        for (Triangle3D triangle : triangles) {
            if (triangle.isVisible)
                GraphicsOperations.drawTriangle3D(vram, triangle,0);
        }

        imagePanel.setImage(vram.getImage());
    }



    private void refresh() {
        GraphicsOperations.fillBrightness(vram, 255);
        imagePanel.setImage(vram.getImage());
    }

    private void initialize(){

        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        JMenuBar menuBar = new JMenuBar();

        JMenu optionsMenu = new JMenu("Options");

        menuBar.add(optionsMenu);

        JMenuItem drawBresenhamConfigurationItem = new JMenuItem("Draw Bresenham configuration",
                KeyEvent.VK_T);
        drawBresenhamConfigurationItem.addActionListener(e -> drawBresenhamConfiguration());

        optionsMenu.add(drawBresenhamConfigurationItem);

        JMenuItem drawBresenhamEllipse = new JMenuItem("Draw Bresenham ellipse",
                KeyEvent.VK_T);
        drawBresenhamEllipse.addActionListener(e -> drawBresenhamEllipse());

        optionsMenu.add(drawBresenhamEllipse);

        JMenuItem clearScreen = new JMenuItem("Clear screen",
                KeyEvent.VK_T);
        clearScreen.addActionListener(e -> refresh());

        optionsMenu.add(clearScreen);

        JMenu renderCube = new JMenu("Render cube");

        JMenuItem renderCubeXY = new JMenuItem("Render cube in XY projection",
                KeyEvent.VK_T);
        renderCubeXY.addActionListener(e -> {
            setRenderForCube("XY");
        });

        renderCube.add(renderCubeXY);

        JMenuItem renderCubeYZ = new JMenuItem("Render cube in YZ projection",
                KeyEvent.VK_T);
        renderCubeYZ.addActionListener(e -> {
            setRenderForCube("YZ");
        });

        renderCube.add(renderCubeYZ);

        JMenuItem renderCubeXZ = new JMenuItem("Render cube in XZ projection",
                KeyEvent.VK_T);
        renderCubeXZ.addActionListener(e -> {
            setRenderForCube("XZ");
        });

        renderCube.add(renderCubeXZ);

        optionsMenu.add(renderCube);

        JMenuItem renderTriangles = new JMenuItem("Render fan triangle",
                KeyEvent.VK_T);
        renderTriangles.addActionListener(e -> {
            setRenderForFanTriangles();
        });

        optionsMenu.add(renderTriangles);


        imagePanel = new ImagePanel();
        imagePanel.setBounds(10,60, 970, 600);
        this.add(imagePanel);


        //open image
        JButton button = new JButton();
        button.setBounds(150,10,120,30);
        button.setText("Load Image");

        button.addActionListener(e -> openImage());

        this.add(button);


        //save image as PNG
        JButton button4 = new JButton();
        button4.setBounds(10,10,120,30);
        button4.setText("Save as PNG");
        button4.addActionListener(e -> saveImageAsPNG());

        this.add(button4);

        infoLabel = new JLabel();
        infoLabel.setBounds(850,10,120,30);
        infoLabel.setText("Rotation");
        infoLabel.setFont(new Font(infoLabel.getName(), Font.BOLD, 20));

        this.add(infoLabel);

        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {

                if(e.getKeyChar() == 'r'){

                    infoLabel.setText("Rotation");
                }

                if(e.getKeyChar() == 't'){

                    infoLabel.setText("Translation");
                }

                if(e.getKeyChar() == 's'){

                    infoLabel.setText("Scale");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                GraphicsOperations.fillBrightness(vram, 255);

                if(infoLabel.getText().equals("Rotation")) {

                    if (key == KeyEvent.VK_LEFT) {
                        q = Matrix3D.multiplyMatrix(rotationXY, q);
                    }

                    if (key == KeyEvent.VK_RIGHT) {
                        q = Matrix3D.multiplyMatrix(rotationXZ, q);
                    }

                    if (key == KeyEvent.VK_UP) {
                        q = Matrix3D.multiplyMatrix(rotationYZ, q);
                    }
                }

                if(infoLabel.getText().equals("Scale")) {

                    if (key == KeyEvent.VK_UP) {
                        q = Matrix3D.multiplyMatrix(scaleUp, q);
                    }

                    if (key == KeyEvent.VK_DOWN) {
                        q = Matrix3D.multiplyMatrix(scaleDown, q);
                    }
                }

                if(infoLabel.getText().equals("Translation")) {

                    /*if (key == KeyEvent.VK_LEFT) {
                        q = Matrix3D.multiplyMatrix(moveLeft, q);
                    }

                    if (key == KeyEvent.VK_RIGHT) {
                        q = Matrix3D.multiplyMatrix(moveRight, q);
                    }

                    if (key == KeyEvent.VK_DOWN) {
                        q = Matrix3D.multiplyMatrix(moveDown, q);
                    }

                    if (key == KeyEvent.VK_UP) {
                        q = Matrix3D.multiplyMatrix(moveUp, q);
                    }*/
                }

                if (currentModel.equals("Cube")) {
                    drawCube();
                }

                if (currentModel.equals("Fan Triangles")) {
                    drawFanTriangles();
                }

                imagePanel.setImage(vram.getImage());
            }
        });

        JFrame frame = new JFrame("Raster Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(this);
        frame.setSize(1004, 705);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void drawBresenhamEllipse() {
        GraphicsOperations.drawEllipse(vram,new Ellipse2D(150,150,300,300),60);
        imagePanel.setImage(vram.getImage());
    }

    private void drawBresenhamConfiguration() {
        List<Line2D> linesToBeDrawn = new ArrayList<>();

        Collections.addAll(linesToBeDrawn,
                new Line2D(new Point2D(50,50), new Point2D(30,70)),
                new Line2D(new Point2D(50,50), new Point2D(30,60)),
                new Line2D(new Point2D(50,50), new Point2D(30,50)),
                new Line2D(new Point2D(50,50), new Point2D(30,40)),
                new Line2D(new Point2D(50,50), new Point2D(30,30)),
                new Line2D(new Point2D(50,50), new Point2D(40,30)),
                new Line2D(new Point2D(50,50), new Point2D(50,30)),
                new Line2D(new Point2D(50,50), new Point2D(60,30)),
                new Line2D(new Point2D(50,50), new Point2D(70,30)),
                new Line2D(new Point2D(50,50), new Point2D(70,40)),
                new Line2D(new Point2D(50,50), new Point2D(70,50)),
                new Line2D(new Point2D(50,50), new Point2D(70,60)),
                new Line2D(new Point2D(50,50), new Point2D(70,70)),
                new Line2D(new Point2D(50,50), new Point2D(60,70)),
                new Line2D(new Point2D(50,50), new Point2D(50,70)),
                new Line2D(new Point2D(50,50), new Point2D(40,70))
        );

        drawLines(linesToBeDrawn);
    }

    private void openImage(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Load Image");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            try {

                BufferedImage temp = ImageIO.read(file);

                if(temp != null){

                    imagePanel.setImage(temp);

                }

                else {

                    JOptionPane.showMessageDialog(null, "Unable to load image", "Open image: ", JOptionPane.ERROR_MESSAGE);
                }

            }catch (IOException e){

                e.printStackTrace();
            }
        }
    }

    private void saveImageAsPNG(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Save Image as PNG");

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            String fname = file.getAbsolutePath();

            if(!fname.endsWith(".png") ) file = new File(fname + ".png");

            try {

                ImageIO.write(imagePanel.getImage(), "png", file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setRenderForCube(String projectionSettings) {
        refresh();

        points = new Point3D[] {
                new Point3D(10, 80, 10), //A
                new Point3D(80, 80, 10), // B
                new Point3D(80, 10, 10), // F
                new Point3D(10, 10, 10), // E

                new Point3D(10, 80, 80), //A
                new Point3D(80, 80, 80), // B
                new Point3D(80, 10, 80), // F
                new Point3D(10, 10, 80), // E
        };

        //points = PointGenerator.generateFanPoints3D(20);


        // Process of perspective


        double xMin = points[0].vector[0];
        double xMax = xMin;

        double yMin = points[0].vector[1];
        double yMax = yMin;

        double zMin = points[0].vector[2];
        double zMax = zMin;

        for (int i = 1; i < points.length; i++) {
            Point3D currentPoint = points[i];

            xMin = Math.min(xMin, currentPoint.vector[0]);
            xMax = Math.max(xMax, currentPoint.vector[0]);

            yMin = Math.min(yMin, currentPoint.vector[1]);
            yMax = Math.max(yMax, currentPoint.vector[1]);

            zMin = Math.min(zMin, currentPoint.vector[2]);
            zMax = Math.max(zMax, currentPoint.vector[2]);
        }


        // The transition into normalized space
        Matrix3D translationToNormalizedSpace = Matrix3D.createTranslationMatrix(
                -(xMin + xMax) / 2.0,
                -(yMin + yMax) / 2.0,
                -(zMin + zMax) / 2.0
        );

        Matrix3D scaleToNormalizedSpace = Matrix3D.createScalingMatrix(
                2.0 / Math.max(
                        Math.max(xMax - xMin, yMax - yMin), zMax - zMin)
        );

        q = Matrix3D.multiplyMatrix(scaleToNormalizedSpace, translationToNormalizedSpace);
        Matrix3D scaleToOutput = Matrix3D.createScalingMatrix(Math.min(vram.getWidth(), vram.getHeight()) / 2.0 - 50); // -50 is due to scaling in final window
        Matrix3D translationToOutput = Matrix3D.createTranslationMatrix(vram.getWidth() / 2.0, vram.getHeight() / 2.0, 0);

        Matrix3D projection = null;

        if (projectionSettings.equals("XY"))
            projection = Matrix3D.createOrthogonalMatrix();

        if (projectionSettings.equals("YZ"))
            projection = Matrix3D.createOrthogonalMatrixYZ();

        if (projectionSettings.equals("XZ"))
            projection = Matrix3D.createOrthogonalMatrixXZ();

        k = Matrix3D.multiplyMatrix(translationToOutput, scaleToOutput);
        k = Matrix3D.multiplyMatrix(k, projection);

        currentModel = "Cube";

        drawCube();
    }

    public void setRenderForFanTriangles() {

        points = PointGenerator.generateFanPoints3D(20);


        // Process of perspective


        double xMin = points[0].vector[0];
        double xMax = xMin;

        double yMin = points[0].vector[1];
        double yMax = yMin;

        double zMin = points[0].vector[2];
        double zMax = zMin;

        for (int i = 1; i < points.length; i++) {
            Point3D currentPoint = points[i];

            xMin = Math.min(xMin, currentPoint.vector[0]);
            xMax = Math.max(xMax, currentPoint.vector[0]);

            yMin = Math.min(yMin, currentPoint.vector[1]);
            yMax = Math.max(yMax, currentPoint.vector[1]);

            zMin = Math.min(zMin, currentPoint.vector[2]);
            zMax = Math.max(zMax, currentPoint.vector[2]);
        }


        // The transition into normalized space
        Matrix3D translationToNormalizedSpace = Matrix3D.createTranslationMatrix(
                -(xMin + xMax) / 2.0,
                -(yMin + yMax) / 2.0,
                -(zMin + zMax) / 2.0
        );

        Matrix3D scaleToNormalizedSpace = Matrix3D.createScalingMatrix(
                2.0 / Math.max(
                        Math.max(xMax - xMin, yMax - yMin), zMax - zMin)
        );

        q = Matrix3D.multiplyMatrix(scaleToNormalizedSpace, translationToNormalizedSpace);

        Matrix3D scaleToOutput = Matrix3D.createScalingMatrix(Math.min(vram.getWidth(), vram.getHeight()) / 2.0);
        Matrix3D translationToOutput = Matrix3D.createTranslationMatrix(vram.getWidth() / 2.0, vram.getHeight() / 2.0, 0);

        Matrix3D projection = Matrix3D.createOrthogonalMatrix();

        k = Matrix3D.multiplyMatrix(translationToOutput, scaleToOutput);
        k = Matrix3D.multiplyMatrix(k, projection);

        currentModel = "Fan Triangles";

        drawFanTriangles();
    }

    public static void main(String[] args) {
        new MainWindow();
    }

    public void drawLine(Line2D lineToBeWritten) {
        GraphicsOperations.drawLine(vram,lineToBeWritten,60);
        imagePanel.setImage(vram.getImage());
    }

    public void drawLines(Collection<Line2D> linesToBeDrawn) {
        for (Line2D line2D : linesToBeDrawn) {
            drawLine(line2D);
        }
    }

    public void drawPixel(int x, int y) {
        vram.setPixel(x,y,60);
        imagePanel.setImage(vram.getImage());
    }
}
