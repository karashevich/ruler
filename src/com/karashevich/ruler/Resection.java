package com.karashevich.ruler;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

/**
 * Created by karashevich on 28/04/15.
 */
public class Resection {

    private Line line;
    private int length;
    private int count;
    private int step;

    private DoubleBinding dpx;
    private DoubleBinding dpy;
    private DoubleBinding dpex;
    private DoubleBinding dpey;


    public Resection(Line majorLine, int count, int length, int step) {
        this.step = step;
        this.count = count;
        this.length = length;

        dpx = new DoubleBinding(){
            {
                super.bind(majorLine.endXProperty(), majorLine.startXProperty());
            }

            @Override
            protected double computeValue() {

                return calc_x0(step, count, majorLine);
            }
        };

        dpy = new DoubleBinding(){
            {
                super.bind(majorLine.endYProperty(), majorLine.startYProperty());
            }

            @Override
            protected double computeValue() {
                return calc_y0(step, count, majorLine);
            }
        };

        dpex = new DoubleBinding(){
            {
                super.bind(majorLine.endXProperty(), majorLine.startXProperty());
            }

            @Override
            protected double computeValue() {
                return calc_x1(step, count, majorLine, length);
            }
        };

        dpey = new DoubleBinding(){
            {
                super.bind(majorLine.endYProperty(), majorLine.startYProperty());
            }

            @Override
            protected double computeValue() {
                return calc_y1(step, count, majorLine, length);

            }
        };

        line = new Line();
        line.startXProperty().bind(dpx);
        line.startYProperty().bind(dpy);

        line.endXProperty().bind(dpex);
        line.endYProperty().bind(dpey);

        initStyle();
    }

    private double calc_y1(int step, int count, Line majorLine, int length) {
        double dx = majorLine.getEndX() - majorLine.getStartX();
        double dy = majorLine.getEndY() - majorLine.getStartY();

        double y0 = calc_y0(step, count, majorLine);

        if (dy == 0) return y0 + length;
        return length/Math.sqrt(1 + (dx/dy)*(dx/dy))*(-dx/dy) + y0;
    }

    private double calc_x1(int step, int count, Line majorLine, int length) {
        double dx = majorLine.getEndX() - majorLine.getStartX();
        double dy = majorLine.getEndY() - majorLine.getStartY();

        double x0 =  calc_x0(step, count, majorLine);

        if (dy == 0) return x0;
        return length/Math.sqrt(1 + (dx/dy)*(dx/dy)) + x0;
    }


    private double calc_x0(int step, int count, Line majorLine) {
        double l = step*count;
        double dx = majorLine.getEndX() - majorLine.getStartX();
        double dy = majorLine.getEndY() - majorLine.getStartY();

        if (dx == 0) return majorLine.getStartX();
        return (l/Math.sqrt(1 + (dy/dx)*(dy/dx)))*Math.signum(dx) + majorLine.getStartX();
    }

    private double calc_y0(int step, int count, Line majorLine) {
        double l = step*count;
        double dx = majorLine.getEndX() - majorLine.getStartX();
        double dy = majorLine.getEndY() - majorLine.getStartY();

        if (dx == 0) return Math.signum(dy)*l + majorLine.getStartY();
        return (dy/dx)* (calc_x0(step, count, majorLine) - majorLine.getStartX()) + majorLine.getStartY();
    }

    public Line getLine() {
        return line;
    }

    private void initStyle(){
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(1);
        line.setVisible(true);
    }

    public void redraw(int step, int count, Line majorLine) {
        line.setStartX(calc_x0(step, count, majorLine));
        line.setStartY(calc_y0(step, count, majorLine));
        line.setEndX(calc_x1(step, count, majorLine, length));
        line.setEndY(calc_y1(step, count, majorLine, length));
    }
}
