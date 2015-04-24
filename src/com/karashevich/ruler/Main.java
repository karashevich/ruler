package com.karashevich.ruler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private Label cowerInFear = new Label();
    private Stage mainStage;

    @Override
    public void start(final Stage stage) {
        TrayIcon trayIcon = null;
        mainStage = stage;
        mainStage.setAlwaysOnTop(true);
        mainStage.setTitle("Drawing Tool");
        mainStage.initStyle(StageStyle.TRANSPARENT);

        // the wumpus doesn't leave when the last stage is hidden.
        Platform.setImplicitExit(false);



        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            Image image = new ImageIcon(this.getClass().getResource("tray-icon.gif")).getImage();
            // create a action listener to listen for default action executed on the tray icon
            // create a popup menu
            PopupMenu popup = new PopupMenu();
            // create menu item for the default action
            MenuItem defaultItem = new MenuItem("Run Measure");

            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    (new RulerMeasurement()).run();
                }
            };
            defaultItem.addActionListener(listener);
            popup.add(defaultItem);
            /// ... add other items
            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "Tray Demo", popup);
            // set the TrayIcon properties
            trayIcon.addActionListener(listener);
            // ...
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
            // ...
        } else {
            // disable tray option in your application or
            // perform other actions
        }
        // ...
        // some time later
        //  the application state has changed - update the image
        if (trayIcon != null) {
            //       trayIcon.setImage(updatedImage);
        }
    }

    public class RulerMeasurement implements Runnable {

        @Override
        public void run() {
            Platform.runLater(() -> {
                final Double MAXSTROKE = 30.0;
                final Integer CROSSLENGTH = 3;
                final Group lineGroup;

                final Map<KeyCode, Boolean> modifiers = new HashMap<KeyCode, Boolean>();
                final Point2D[] point = new Point2D[1];
                final Line myLine = new Line();
                final Text coordinateText = new Text();

                final Group crossGroup1 = new Group();
                final Line crossLineV1 = new Line();
                final Line crossLineH1 = new Line();

                final Group crossGroup2 = new Group();
                final Line crossLineV2 = new Line();
                final Line crossLineH2 = new Line();

                crossLineV1.setStroke(Color.WHITE);
                crossLineH1.setStroke(Color.WHITE);

                crossLineV2.setStroke(Color.WHITE);
                crossLineH2.setStroke(Color.WHITE);

                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(2.0);
                dropShadow.setOffsetX(1.0);
                dropShadow.setOffsetY(1.0);
                dropShadow.setColor(Color.color(0.4, 0.4, 0.4));
                coordinateText.setFont(new Font(9));
                coordinateText.setStroke(Color.WHITE);
                coordinateText.setEffect(dropShadow);

                myLine.setStrokeWidth(1);
                myLine.setStroke(Color.WHITE);

                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

                mainStage.setX(primaryScreenBounds.getMinX());
                mainStage.setY(primaryScreenBounds.getMinY());
                mainStage.setWidth(primaryScreenBounds.getWidth());
                mainStage.setHeight(primaryScreenBounds.getHeight());

                final Group root = new Group();

                final Scene scene = new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
                scene.setFill(new Color(0.0, 0, 0, 0.0));
                lineGroup = new Group();

                Button btnClear = new Button();
                btnClear.setText("Clear");
                btnClear.setOnAction(new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent event) {
                        lineGroup.getChildren().removeAll(lineGroup.getChildren());
                    }
                });

                final Line sampleLine = new Line(0, 0, 140, 0);
                sampleLine.setStrokeWidth(1);
                sampleLine.setStroke(Color.WHITE);

                StackPane stackpane = new StackPane();
                stackpane.setPrefHeight(MAXSTROKE);
                stackpane.getChildren().add(sampleLine);

                final Rectangle canvas = new Rectangle(scene.getWidth(), scene.getHeight());
                canvas.setCursor(Cursor.CROSSHAIR);
                canvas.setFill(new Color(0, 0, 0, 0.05));
                lineGroup.getChildren().add(myLine);
                lineGroup.getChildren().add(coordinateText);

                crossGroup1.getChildren().add(crossLineH1);
                crossGroup1.getChildren().add(crossLineV1);

                lineGroup.getChildren().add(crossGroup1);

                canvas.setOnMousePressed(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent me) {

                        coordinateText.setText("");

                        point[0] = new Point2D(me.getSceneX(), me.getSceneY());

                        myLine.setStartX(point[0].getX());
                        myLine.setStartY(point[0].getY());
                        myLine.setEndX(point[0].getX());
                        myLine.setEndY(point[0].getY());

                        crossLineH1.setStartX(point[0].getX() - CROSSLENGTH);
                        crossLineH1.setStartY(point[0].getY());
                        crossLineH1.setEndX(point[0].getX() + CROSSLENGTH);
                        crossLineH1.setEndY(point[0].getY());

                        crossLineV1.setStartX(point[0].getX());
                        crossLineV1.setStartY(point[0].getY() - CROSSLENGTH);
                        crossLineV1.setEndX(point[0].getX());
                        crossLineV1.setEndY(point[0].getY() + CROSSLENGTH);

                        coordinateText.setText(Math.round(Math.abs(myLine.getEndX() - myLine.getStartX())) + ", " + Math.round(Math.abs(myLine.getEndY() - myLine.getStartY())));
                        coordinateText.setX(me.getX() + 10);
                        coordinateText.setY(me.getY() + 10);
                    }
                });

                scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        modifiers.put(event.getCode(), true);
                        if (event.getCode() == KeyCode.ESCAPE) {
                            lineGroup.getChildren().removeAll();
                            lineGroup.managedProperty().bind(lineGroup.visibleProperty());
                            lineGroup.setVisible(false);
                            System.out.println("Escape pressed!");
                            mainStage.close();
                        }
                    }

                });

                scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (modifiers.containsKey(event.getCode())) {
                            modifiers.put(event.getCode(), false);
                        }
                    }
                });

                canvas.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ((Node) event.getSource()).setCursor(Cursor.CROSSHAIR);
                    }
                });

                canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent me) {
                    }
                });

                canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent me) {

                        // keep lines within rectangle

                        if (canvas.getBoundsInLocal().contains(me.getX(), me.getY())) {
                            lineGroup.setVisible(true);
                            myLine.setEndX(me.getX());
                            myLine.setEndY(me.getY());
                            coordinateText.setText(Math.round(Math.abs(myLine.getEndX() - myLine.getStartX())) + ", " + Math.round(Math.abs(myLine.getEndY() - myLine.getStartY())));
                            coordinateText.setX(me.getX() + 10);
                            coordinateText.setY(me.getY() + 10);
                        }

                    }
                });

                // Build the VBox container for the toolBox, sampleline, and canvas
                VBox vb = new VBox(20);
                vb.setPrefWidth(scene.getWidth());
                vb.setLayoutY(0);
                vb.setLayoutX(0);
                vb.getChildren().addAll(canvas);
                root.getChildren().addAll(vb, lineGroup);
                mainStage.setScene(scene);

                mainStage.show();
                mainStage.sizeToScene();
                mainStage.show();
            });
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}