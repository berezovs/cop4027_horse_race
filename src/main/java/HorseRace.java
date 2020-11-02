import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;


public class HorseRace extends Application {
    private static int NUM_HORSES;

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();

        for (int i = 0; i < NUM_HORSES; ++i) {
            root.getChildren().add(getHorseTrack());
        }

        root.getChildren().add(this.getButtonBar());
        Scene scene = new Scene(root);

        String WINDOW_NAME = "Horse Race";
        stage.setTitle(WINDOW_NAME);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

    }

    private HBox getButtonBar() {
        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll(this.getButton("Start"), this.getButton("Reset"), this.getButton("Stop"));
        buttonBar.setSpacing(15);
        return buttonBar;
    }

    private Button getButton(String buttonName) {
        return new Button(buttonName);
    }

    private Canvas getHorseTrack() {
        return new Canvas(500, 50);
    }


    public static void launch(int arg) {
        NUM_HORSES = arg;
        Application.launch();
    }


    private class Animate implements Runnable{
        GraphicsContext gc = null;
        Horse horse = null;
    Animate(Horse horse, GraphicsContext gContext){

    }
        @Override
        public void run() {

        }
    }

}
