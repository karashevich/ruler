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


    public Resection(Line majorLine, int count, int length, int step) {
        this.step = step;
        this.count = count;
        this.length = length;

        line = new Line();
        redraw(step, count, majorLine, length);

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

    public void redraw(int step, int count, Line majorLine, int _length) {
        length = _length;
        line.setStartX(calc_x0(step, count, majorLine));
        line.setStartY(calc_y0(step, count, majorLine));
        if (count%5 == 0) length *= 1.5;
        if (count%10 == 0) length *= 1.5;

        line.setEndX(calc_x1(step, count, majorLine, length));
        line.setEndY(calc_y1(step, count, majorLine, length));
    }
}
