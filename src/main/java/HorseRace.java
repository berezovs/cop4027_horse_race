/**
 Student Name:Serghei Berezovschi
 File Name:HorseRace.java
 Project 3

 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class HorseRace extends Application {
    private static int NUM_HORSES;
    boolean isRaceInProgress = false;
    long startTime, endTime;
    Lock lock = new ReentrantLock();
    List<Canvas> horseTracks;
    List<Thread> threads;
    Horse winner;

    //Launches the application
    public static void launch(int arg) {
        NUM_HORSES = arg;
        Application.launch();
    }


    //builds the UI
    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        horseTracks = new ArrayList<>();
        threads = new ArrayList<>();

        for (int i = 0; i < NUM_HORSES; ++i) {
            Canvas c = getHorseTrack();
            root.getChildren().add(c);
            horseTracks.add(c);
        }

        root.getChildren().add(this.getButtonBar());
        Scene scene = new Scene(root);


        String WINDOW_NAME = "Horse Race";
        stage.setTitle(WINDOW_NAME);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

    }


    //creates and returns a set of buttons
    private HBox getButtonBar() {
        HBox buttonBar = new HBox();
        Button start = this.getButton("Start Race");
        Button reset = this.getButton("Reset Race");
        Button stop = this.getButton("Exit");

        start.setOnAction(new StartHandler());
        stop.setOnAction(new ExitHandler());
        reset.setOnAction(new ResetHandler());

        buttonBar.getChildren().addAll(start, reset, stop);
        buttonBar.setSpacing(15);
        return buttonBar;
    }

    //creates and returns a single button
    private Button getButton(String buttonName) {
        return new Button(buttonName);
    }


    //creates and returns a 500x50 Canvas
    private Canvas getHorseTrack() {
        return new Canvas(500, 50);
    }


    //creates all the runnables and threads, places all the threads into an array list and starts all the threads
    private void startRace(List<Canvas> horseTracks) {
        this.threads.clear();
        for (int i = 0; i < NUM_HORSES; i++) {
            Runnable h = new Animate(new Horse("Horse " + (i + 1)), horseTracks.get(i).getGraphicsContext2D());
            Thread t = new Thread(h);
            threads.add(t);
        }

        for (Thread t : threads) {
            t.setDaemon(true);
            t.start();
        }
        this.startTime = new Date().getTime();
    }

    //interrupts all horse threads
    private void interruptAllThreads() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    //sets the winner of the race, class the method that displays all the results in a pop-up dialog
    private void setWinner(Horse horse) {
        lock.lock();
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try {
            this.endTime = new Date().getTime();
            this.winner = horse;
            interruptAllThreads();
            isRaceInProgress = false;
            this.showWinner();

        } finally {
            lock.unlock();
        }
    }

    //displays the results and statistics of race in a javafx dialog
    private void showWinner() {
        Runnable dialog = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Winner: " + this.winner.getName() + "\n" + this.getFormattedTimeInSeconds(this.startTime, this.endTime));
            alert.showAndWait();
        };
        Platform.runLater(dialog);
    }

    //returns the time of the winning horse as a string
    public String getFormattedTimeInSeconds(long start, long end) {
        long timeMilisTotal = end - start;
        long timeInSec = timeMilisTotal / 1000;
        long timeMilis = timeMilisTotal % timeInSec;

        String timeStr = "Time: " + timeInSec + " seconds, " + timeMilis + " milliseconds.";

        return timeStr;
    }

    //handler for the start race button
    private class StartHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (isRaceInProgress)
                return;
            startRace(horseTracks);
            isRaceInProgress = true;
        }
    }

    //handler for the reset race button
    private class ResetHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            interruptAllThreads();
            startRace(horseTracks);
        }
    }

    //handler for the exit application button
    private class ExitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            Platform.exit();
        }
    }


    //utility function that return a random number in a range specified by the function parameters
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    //animation class. draws and animates the horse
    private class Animate implements Runnable {
        int LENGTH_OF_TRACK = 450;
        GraphicsContext gc;
        Horse horse;

        Animate(Horse horse, GraphicsContext gContext) {
            this.horse = horse;
            horse.setPositionX(0);
            horse.setPositionY(0);
            this.gc = gContext;
        }

        @Override
        public void run() {
            this.drawHorse();

            while (this.horse.getPositionX() < LENGTH_OF_TRACK && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                    this.moveHorse();
                } catch (InterruptedException e) {
                    return;
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            setWinner(this.horse);
        }

        //clears the canvas and draws the horse in a new location as specified by the x-axis parameter in the Horse class.
        //These operations are dispatched to run on the main thread through Platform.runLater().
        public void drawHorse() {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName());
                return;
            }
            Runnable draw = () -> {
                gc.clearRect(0, 0, 500, 50);
                gc.strokeRect(horse.getPositionX(), 5, 40, 25);
            };
            Platform.runLater(draw);
        }

        //updates the horses position along the x-axis
        public void moveHorse() throws InterruptedException {
            this.drawHorse();
            int distance = getRandomNumber(10, 30);
            int currentPosition = this.horse.getPositionX();

            //condition to ensure the horse doesn't move past the finish line
            if (currentPosition + distance >= LENGTH_OF_TRACK)
                distance = LENGTH_OF_TRACK - currentPosition;
            this.horse.setPositionX(this.horse.getPositionX() + distance);

        }


    }

}
