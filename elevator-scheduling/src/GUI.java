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

public class GUI extends Application {
    int index = 0;
    Elevator elevator;

    @Override
    public void start(Stage stage) throws Exception {
        int[] out = {0};
        int[] in = {1, 2};
        elevator = new Elevator(3, 15, out, in); // An elevator instance

        Rectangle elevatorShape = new Rectangle(25, 50, Color.BLACK); // Shape that used for elevator
        Image image = new Image(new FileInputStream("Imgs/ElevShape.png")); // Elevator image
        elevatorShape.setFill(new ImagePattern(image)); // Set the elevator shape

        VBox floorVBox = new VBox();
        floorVBox.setLayoutX(292.5);

        for (int i = 0; i < 15; i++) {
            Rectangle rectangle = new Rectangle(25, 48, Color.WHITE);
            rectangle.setStrokeWidth(1);
            rectangle.setStroke(Color.BLACK);
            floorVBox.getChildren().add(rectangle);
        }

        Text inText = new Text();
        inText.setText("In requests: " + elevator.inRequest.toString());
        inText.setX(650);
        inText.setY(200);
        Text outText = new Text();
        outText.setText("Out requests: " + elevator.outRequest.toString());
        outText.setX(700);
        outText.setY(200);

        Pane pane = new Pane(elevatorShape); // Put the elevator on a pane
        elevatorShape.setLayoutX(292.5); // Elevator first position
        elevatorShape.setLayoutY(700 - (elevator.currentFloor) * 50);

        VBox vBox = new VBox(); // VBox for elevator buttons
        vBox.setSpacing(20 - 1.0 / 15);
        vBox.setPrefWidth(25);

        for (int i = 0; i < 15; i++) { // Loop that creates elevator buttons
            Button btn1 = new Button(); // Inside elevator button
            btn1.setText(String.format("In %2d", 14 - i));
            int floor = 14 - i;

            btn1.setOnAction((actionEvent) -> elevator.addRequest("in " + floor)); // Creates a request when button is pressed

            Button btn2 = new Button(); // Outside elevator button
            btn2.setText(String.format("Out %2d", 14 - i));

            btn2.setOnAction((actionEvent) -> elevator.addRequest("out " + floor));

            btn1.setMinWidth(vBox.getPrefWidth() * 3);
            btn2.setMinWidth(vBox.getPrefWidth() * 3);
            HBox hBox = new HBox(btn1, btn2); // HBox for every floor button
            hBox.setSpacing(10);
            vBox.getChildren().add(hBox);
        }
        vBox.setLayoutX(1);
        vBox.setLayoutY(1);

        vBox.getChildren().addAll(inText, outText);

        Group root = new Group(floorVBox, pane, vBox); // Group for elevator and buttons
        Scene scene = new Scene(root, 450, 750); // Add group to the scene
        stage.setScene(scene); // Add scene to stage
        stage.setTitle("Elevator scheduling");
        stage.getIcons().add(new Image(new File("Imgs/ElevIcon.jpg").toURI().toString())); // Title bar icon
        stage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), (ActionEvent event) -> { // Moves elevator to currentFloor value
            index = elevator.currentFloor;
            elevatorShape.setLayoutY(700 - (index * 50));
            inText.setText("In requests: " + elevator.inRequest.toString());
            outText.setText("Out requests: " + elevator.outRequest.toString());
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Timeline cycles forever
        timeline.play(); // Start the timeline
        elevator.action(); // Start the elevator
    }

    public static void main(String[] args) {
        launch(args);
    }

}
