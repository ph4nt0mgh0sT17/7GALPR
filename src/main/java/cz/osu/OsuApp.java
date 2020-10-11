package cz.osu;

import cz.osu.raster.graphics.Line2D;
import cz.osu.raster.graphics.MainWindow;
import cz.osu.raster.graphics.Point2D;

import java.awt.*;
import static java.lang.Math.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OsuApp {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("GALPR");

        MainWindow mainWindow = new MainWindow();

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

        mainWindow.drawLines(linesToBeDrawn);



    }
}
