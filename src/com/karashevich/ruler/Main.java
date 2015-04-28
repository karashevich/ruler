package com.karashevich.ruler;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;

import javax.swing.*;
import java.awt.*;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

        boolean useSwingEventQueue = false;
        Provider provider = Provider.getCurrentProvider(useSwingEventQueue);
        provider.register(KeyStroke.getKeyStroke("control shift R"), new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                System.out.println("HotKey fired");
                (new RulerMeasurement()).run();
            }
        });

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
                    newWindow(mainStage);
//                    (new RulerMeasurement()).run();
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

        mainStage.setOnCloseRequest(event -> {
            System.err.println("Provider stopped.");
            provider.reset();
            provider.stop();
        });
    }

    public class RulerMeasurement implements Runnable {

        @Override
        public void run() {
            Platform.runLater(() -> {
                final Double MAXSTROKE = 30.0;
                final Integer CROSSLENGTH = 3;
                final Group lineGroup;

                final Map<KeyCode, Boolean> modifiers = new HashMap<KeyCode, Boolean>();

                //OPTIONS
                final boolean magnifierOption = true;
                final boolean resectionOption = true;
                final int resectionStep = 10;


                final ArrayList<Resection> resections = new ArrayList<Resection>(10);
                final Point2D[] point = new Point2D[2];
                final Line myLine = new Line(0,0,0,0);
                lineGroup = new Group();
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

                //Set dopshadow effect
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(2.0);
                dropShadow.setOffsetX(1.0);
                dropShadow.setOffsetY(1.0);
                dropShadow.setColor(Color.color(0.4, 0.4, 0.4));
                coordinateText.setFont(new Font(9));
                coordinateText.setStroke(Color.WHITE);
                coordinateText.setEffect(dropShadow);
                crossGroup1.setEffect(dropShadow);
                crossGroup2.setEffect(dropShadow);
                myLine.setEffect(dropShadow);

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


                crossGroup1.getChildren().add(crossLineH1);
                crossGroup1.getChildren().add(crossLineV1);

                crossGroup2.getChildren().add(crossLineH2);
                crossGroup2.getChildren().add(crossLineV2);


                lineGroup.getChildren().add(myLine);
                lineGroup.getChildren().add(coordinateText);
                lineGroup.getChildren().add(crossGroup1);
                lineGroup.getChildren().add(crossGroup2);

                canvas.setOnMousePressed(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent me) {

                        lineGroup.getChildren().clear();
                        lineGroup.getChildren().add(myLine);
                        lineGroup.getChildren().add(coordinateText);
                        lineGroup.getChildren().add(crossGroup1);
                        lineGroup.getChildren().add(crossGroup2);

                        resections.clear();

                        crossGroup2.setVisible(false);

                        coordinateText.setText("");

                        point[0] = new Point2D(me.getSceneX(), me.getSceneY());

                        crossLineH1.setStartX(point[0].getX() - CROSSLENGTH);
                        crossLineH1.setStartY(point[0].getY());
                        crossLineH1.setEndX(point[0].getX() + CROSSLENGTH);
                        crossLineH1.setEndY(point[0].getY());

                        crossLineV1.setStartX(point[0].getX());
                        crossLineV1.setStartY(point[0].getY() - CROSSLENGTH);
                        crossLineV1.setEndX(point[0].getX());
                        crossLineV1.setEndY(point[0].getY() + CROSSLENGTH);

                        coordinateText.setText("[" + Math.round(me.getX()) + ", " + Math.round(me.getY()) + "]");
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

                            crossGroup2.managedProperty().bind(crossGroup2.visibleProperty());
                            crossGroup2.setVisible(false);
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

                            if (modifiers.get(KeyCode.SHIFT) != null && modifiers.get(KeyCode.SHIFT).booleanValue()) {

                                point[1] = new Point2D(me.getSceneX(), me.getSceneY());

                                if ((Math.abs(point[0].getX() - point[1].getX()) - Math.abs(point[0].getY() - point[1].getY())) >= 0) {

                                    point[1] = new Point2D(me.getSceneX(), point[0].getY());

                                } else {

                                    point[1] = new Point2D(point[0].getX(), me.getSceneY());

                                }


                                crossLineH2.setStartX(point[1].getX() - CROSSLENGTH);
                                crossLineH2.setStartY(point[1].getY());
                                crossLineH2.setEndX(point[1].getX() + CROSSLENGTH);
                                crossLineH2.setEndY(point[1].getY());

                                crossLineV2.setStartX(point[1].getX());
                                crossLineV2.setStartY(point[1].getY() - CROSSLENGTH);
                                crossLineV2.setEndX(point[1].getX());
                                crossLineV2.setEndY(point[1].getY() + CROSSLENGTH);

                                lineGroup.setVisible(true);

                                myLine.setStartX(point[0].getX());
                                myLine.setStartY(point[0].getY());
                                myLine.setEndX(point[1].getX());
                                myLine.setEndY(point[1].getY());
                                if (resectionOption) calcResections(myLine, resections, resectionStep, lineGroup);
                                redrawResections(resections, resectionStep, myLine);

                                coordinateText.setText("[" + Math.round(Math.abs(myLine.getEndX() - myLine.getStartX())) + ", " + Math.round(Math.abs(myLine.getEndY() - myLine.getStartY())) + "]");
                                coordinateText.setX(point[1].getX() + 10);
                                coordinateText.setY(point[1].getY() + 10);

                                crossGroup2.setVisible(true);


                            } else {

                                point[1] = new Point2D(me.getSceneX(), me.getSceneY());

                                crossLineH2.setStartX(point[1].getX() - CROSSLENGTH);
                                crossLineH2.setStartY(point[1].getY());
                                crossLineH2.setEndX(point[1].getX() + CROSSLENGTH);
                                crossLineH2.setEndY(point[1].getY());

                                crossLineV2.setStartX(point[1].getX());
                                crossLineV2.setStartY(point[1].getY() - CROSSLENGTH);
                                crossLineV2.setEndX(point[1].getX());
                                crossLineV2.setEndY(point[1].getY() + CROSSLENGTH);

                                lineGroup.setVisible(true);

                                myLine.setStartX(point[0].getX());
                                myLine.setStartY(point[0].getY());
                                myLine.setEndX(me.getX());
                                myLine.setEndY(me.getY());
                                if (resectionOption) calcResections(myLine, resections, resectionStep, lineGroup);
                                redrawResections(resections, resectionStep, myLine);


                                coordinateText.setText("[" + Math.round(Math.abs(myLine.getEndX() - myLine.getStartX())) + ", " + Math.round(Math.abs(myLine.getEndY() - myLine.getStartY())) + "]");
                                coordinateText.setX(point[1].getX() + 10);
                                coordinateText.setY(point[1].getY() + 10);

                                crossGroup2.setVisible(true);

                            }

                            double x0 = myLine.getStartX();
                            double y0 = myLine.getStartY();

                            double x1 = myLine.getEndX();
                            double y1 = myLine.getEndY();

                            double len_sqr = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);

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

    private void calcResections(Line myLine, ArrayList<Resection> resections, int resectionStep, Group lineGroup){
        double dx = myLine.getEndX() - myLine.getStartX();
        double dy = myLine.getEndY() - myLine.getStartY();

        double length = Math.sqrt(dx * dx + dy * dy);
        if (Math.round(length / resectionStep) != resections.size()) {
            //delete redundant resections
            if (Math.round(length / resectionStep) > resections.size()) {
                int n = resections.size();
                for (int i = (int) n; i < Math.round(length / resectionStep); i++) {
                    final Resection resection = new Resection(myLine, i, 5, resectionStep);
                    lineGroup.getChildren().add(resection.getLine());
                    resections.add(resection);
                }
            } else {
                int n = resections.size();
                for (int i = (int) Math.round(length / resectionStep); i < n; i++) {
                    final Resection resection = resections.get(resections.size() - 1);
                     if (lineGroup.getChildren().contains(resection.getLine()))
                        lineGroup.getChildren().remove(resection.getLine());
                    resections.remove(resections.size() - 1);
                }
            }
        }
    }


    public void newWindow(Stage stage) {
        Platform.runLater(() -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            Scene scene = new Scene(grid, 300, 275);
            stage.setScene(scene);

            // set controllers here
            Label label = new Label("Type shortcut here");
            label.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(label, 0, 0, 2, 1);

            TextField textField = new TextField("Shortcut");
            grid.add(textField, 1, 1);


            dialog.setScene(scene);
            dialog.show();
        });
    }

    private void redrawResections(ArrayList<Resection> resections, int step, Line majorLine){
        for (int i = 0; i < resections.size(); i++) {
            resections.get(i).redraw(step, i, majorLine);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}