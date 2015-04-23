package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private Path path;
    private Group lineGroup;
    private static final Double DEFAULTSTROKE = 3.0;
    private static final Double MAXSTROKE = 30.0;
    private static final Double MINSTROKE = 1.0;
    private static final Integer DEFAULTRED = 0;
    private static final Integer DEFAULTGREEN = 0;
    private static final Integer DEFAULTBLUE = 255;
    private static final Integer MAXRGB = 255;
    private static final Integer MINRGB = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {

        final Map<KeyCode, Boolean> modifiers = new HashMap<KeyCode, Boolean>();
        final Point2D[] point = new Point2D[1];
        final Line myLine = new Line();
        final Text coordinateText = new Text();

        coordinateText.setFont(new Font(12));
        coordinateText.setStroke(Color.WHITE);

        myLine.setStrokeWidth(1);
        myLine.setStroke(Color.WHITE);

        primaryStage.setTitle("Drawing Tool");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        final Group root = new Group();

        final Scene scene = new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        scene.setFill(new Color(0.0, 0, 0, 0.0));

        // A group to hold all the drawn path elements
        lineGroup = new Group();


        // Build the slider, label, and button and their VBox layout container
        Button btnClear = new Button();
        btnClear.setText("Clear");
        btnClear.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                lineGroup.getChildren().removeAll(lineGroup.getChildren());
            }
        });



        // Build the RGB sliders, labels, and HBox containers

        // Build the VBox container for all the slider containers


        // Build the sample line and its layout container
        final Line sampleLine = new Line(0, 0, 140, 0);
        sampleLine.setStrokeWidth(1);
        sampleLine.setStroke(Color.WHITE);

        StackPane stackpane = new StackPane();
        stackpane.setPrefHeight(MAXSTROKE);
        stackpane.getChildren().add(sampleLine);
        // Bind to the Paint Binding object


        // Build the canvas
        final Rectangle canvas = new Rectangle(scene.getWidth(), scene.getHeight());
        canvas.setCursor(Cursor.CROSSHAIR);
        canvas.setFill(new Color(0, 0, 0, 0.2));
        lineGroup.getChildren().add(myLine);
        lineGroup.getChildren().add(coordinateText);

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {

                path = new Path();
                path.setMouseTransparent(true);
                path.setStrokeWidth(sampleLine.getStrokeWidth());
                path.setStroke(sampleLine.getStroke());
                point[0] = new Point2D(me.getSceneX(), me.getSceneY());

                myLine.setStartX(point[0].getX());
                myLine.setStartY(point[0].getY());
                myLine.setEndX(point[0].getX());
                myLine.setEndY(point[0].getY());
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                modifiers.put(event.getCode(), true);
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
                path = null;
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {

                // keep lines within rectangle

                if (canvas.getBoundsInLocal().contains(me.getX(), me.getY()) && modifiers.get(KeyCode.SHIFT) != null && modifiers.get(KeyCode.SHIFT).booleanValue()) {
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
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
            }
        });
    }
}