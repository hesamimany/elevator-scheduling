import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class GUI extends Application {
    int index = 0;
    ArrayList<Elevator> elevators = new ArrayList<>();
    Elevator elevator;

    @Override
    public void start(Stage stage) throws Exception {
        int[] out = {2};
        int[] in = {0, 4};
        int[] emp = {};
        elevator = new Elevator(2, 15, out, in);// An elevator instance
        elevators.add(elevator);
        elevators.add(new Elevator(6, 15, emp, emp));
        elevators.add(new Elevator(9, 15, emp, emp));


        Rectangle elevatorShape = new Rectangle(25, 50, Color.BLACK); // Shape that used for elevator
        Rectangle elevatorShape1 = new Rectangle(25, 50, Color.BLACK); // Shape that used for elevator
        Rectangle elevatorShape2 = new Rectangle(25, 50, Color.BLACK); // Shape that used for elevator
        Image image = new Image(new FileInputStream("Imgs/ElevShape.png")); // Elevator image
        elevatorShape.setFill(new ImagePattern(image)); // Set the elevator shape
        elevatorShape1.setFill(new ImagePattern(image)); // Set the elevator shape
        elevatorShape2.setFill(new ImagePattern(image)); // Set the elevator shape


        VBox floorVBox = new VBox();
        floorVBox.setLayoutX(241.5);

        for (int i = 0; i < 15; i++) {
            Text floorText = new Text();
            floorText.setText(String.valueOf(14 - i));
            floorText.setFill(Color.PURPLE);
            HBox floors = new HBox();
            floors.setSpacing(25);
            Rectangle rectangle = new Rectangle(25, 48, Color.WHITE);
            Rectangle rectangle1 = new Rectangle(25, 48, Color.WHITE);
            Rectangle rectangle2 = new Rectangle(25, 48, Color.WHITE);
            rectangle.setStrokeWidth(1);
            rectangle.setStroke(Color.PURPLE);
            rectangle1.setStrokeWidth(1);
            rectangle1.setStroke(Color.PURPLE);
            rectangle2.setStrokeWidth(1);
            rectangle2.setStroke(Color.PURPLE);
            floors.getChildren().addAll(rectangle, rectangle1, rectangle2, floorText);
            floorVBox.getChildren().add(floors);
        }


        Text inText = new Text();
        Text outText = new Text();
        inText.setText("In requests: ");
        outText.setText("Out requests: ");
        for (Elevator e : elevators) {
            inText.setText(inText.getText() + "\n" + e.inRequest.toString());
            outText.setText(outText.getText() + "\n" + e.outRequest.toString());
        }

        inText.setX(650);
        inText.setY(200);

        outText.setX(700);
        outText.setY(200);

        Pane pane = new Pane(elevatorShape); // Put the elevator on a pane
        elevatorShape.setLayoutX(242.5); // Elevator first position
        elevatorShape.setLayoutY(700 - (elevators.get(0).currentFloor) * 50);
        Pane pane1 = new Pane(elevatorShape1); // Put the elevator on a pane
        elevatorShape1.setLayoutX(292.5); // Elevator first position
        elevatorShape1.setLayoutY(700 - (elevators.get(1).currentFloor) * 50);
        Pane pane2 = new Pane(elevatorShape2); // Put the elevator on a pane
        elevatorShape2.setLayoutX(342.5); // Elevator first position
        elevatorShape2.setLayoutY(700 - (elevators.get(2).currentFloor) * 50);

        VBox vBox = new VBox(); // VBox for elevator buttons
        vBox.setSpacing(14 - 1.0 / 15);
        vBox.setPrefWidth(25);

        for (int i = 0; i < 15; i++) { // Loop that creates elevator buttons
            Button btn1 = new Button(); // Inside elevator button
            btn1.setText(String.format("In %2d", 14 - i));
            int floor = 14 - i;

            btn1.setOnAction((actionEvent) -> {
                int dis = Integer.MAX_VALUE;
                Elevator target = elevators.get(0);
                for (Elevator e : elevators) {
                    if (Math.abs(e.currentFloor - floor) < dis) {
                        dis = Math.abs(e.currentFloor - floor);
                        target = e;
                    }
                }
                target.addRequest("in " + floor);

            }); // Creates a request when button is pressed

            Button btn2 = new Button(); // Outside elevator button
            btn2.setText(String.format("Out %2d", 14 - i));

            btn2.setOnAction((actionEvent) -> {
                int dis = Integer.MAX_VALUE;
                Elevator target = elevators.get(0);
                for (Elevator e : elevators) {
                    if (Math.abs(e.currentFloor - floor) < dis) {
                        dis = Math.abs(e.currentFloor - floor);
                        target = e;
                    }
                }
                target.addRequest("out " + floor);
            });

            btn1.setMinWidth(vBox.getPrefWidth() * 3);
            btn2.setMinWidth(vBox.getPrefWidth() * 3);
            HBox hBox = new HBox(btn1, btn2); // HBox for every floor button
            hBox.setSpacing(10);
            vBox.getChildren().add(hBox);
        }
        vBox.setLayoutX(1);
        vBox.setLayoutY(1);

        vBox.getChildren().addAll(inText, outText);

        Group root = new Group(floorVBox, pane, pane1, pane2, vBox); // Group for elevator and buttons
        Scene scene = new Scene(root, 450, 750); // Add group to the scene
        stage.setScene(scene); // Add scene to stage
        stage.setTitle("Elevator scheduling");
        stage.getIcons().add(new Image(new File("Imgs/ElevIcon.jpg").toURI().toString())); // Title bar icon
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), (ActionEvent event) -> { // Moves elevator to currentFloor value
            index = elevator.currentFloor;
            elevatorShape.setLayoutY(700 - (elevators.get(0).currentFloor * 50));
            elevatorShape1.setLayoutY(700 - (elevators.get(1).currentFloor * 50));
            elevatorShape2.setLayoutY(700 - (elevators.get(2).currentFloor * 50));


            inText.setText("In requests: ");
            outText.setText("Out requests: ");
            for (Elevator e : elevators) {
                inText.setText(inText.getText() + "\n" + e.inRequest.toString());
                outText.setText(outText.getText() + "\n" + e.outRequest.toString());
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Timeline cycles forever
        timeline.play(); // Start the timeline
        elevator.action(); // Start the elevator
        elevators.get(1).action();
        elevators.get(2).action();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
