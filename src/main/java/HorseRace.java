import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class HorseRace extends Application {
    private static int NUM_HORSES;
    private List<Canvas> horseTracks = null;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        horseTracks = new ArrayList<>();
        for (int i = 0; i < NUM_HORSES; ++i) {

            Canvas c = getHorseTrack();
            root.getChildren().add(c);
            horseTracks.add(c);
        }

        this.startRace(horseTracks);
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


    private void startRace(List<Canvas> horseTracks) {
        Thread t;
        Runnable contestant;
        for (int i = 0; i < horseTracks.size(); ++i) {
            Horse h = new Horse("Horse" + i);
            contestant = new Animate(h, horseTracks.get(i).getGraphicsContext2D());
            t = new Thread(contestant, "Thread "+ i);
            t.start();
        }
    }

    private class Animate implements Runnable {
        GraphicsContext gc = null;
        Horse horse = null;

        Animate(Horse horse, GraphicsContext gContext) {
            this.horse = horse;
            this.gc = gContext;
        }

        @Override
        public void run() {

            while (this.horse.getPositionX() < 450){
                try {
                    gc.clearRect(0,0, 500, 50);
                    this.drawHorse();
                    this.moveHorse();
                    System.out.println(Thread.currentThread().getName());
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        }

        public void drawHorse() {
            this.gc.strokeRect(this.horse.getPositionX(), 5, 40, 25);
        }

        public void moveHorse() throws InterruptedException {
            this.horse.setPositionX(this.horse.getPositionX() + getRandomNumber(20, 40));

        }

        public int getRandomNumber(int min, int max) {
            return (int) ((Math.random() * (max - min)) + min);
        }

    }

}
