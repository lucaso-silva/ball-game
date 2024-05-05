import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * BallGame extends Application Class and sets the Game Panes
 */
public class BallGame extends Application
{
    private Pane gameScreen;
    private ArrayList<Ball> ballsInGame;
    private BallMovement animation;
    private int hits = 0, misses = 0;
    private Text nHits;
    private Text nMisses;
    private Text gameOverMsg;
    private boolean gamePaused = false;
    private boolean gameOverMode = false;
    private double yPos;

    /**
     * Runs the JavaFx Application
     * @param args not used
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * Defines the user interface container
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * @throws IOException if the stream file for the background image cannot be created
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        BorderPane root = new BorderPane();
        gameScreen = new Pane();

        setGameArea();

        Button pause = new Button("Pause");
        Button reset = new Button("Reset");

        HBox controls = new HBox(20, pause, reset);
        controls.setPadding(new Insets(5, 10,10,0));
        controls.setAlignment(Pos.BOTTOM_RIGHT);

        root.setCenter(gameScreen);
        root.setBottom(controls);

        animation = new BallMovement();
        animation.start();

        reset.setOnAction(new ResetButtonHandler());
        pause.setOnAction(new PauseButtonHandler());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Assignment 8 - Game Ball");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Sets the elements of the game screen
     * @throws IOException if the stream file for the background image cannot be created
     */
    private void setGameArea() throws IOException {
        ballsInGame = new ArrayList<Ball>();

        try {
            FileInputStream inputImg =  new FileInputStream("background_img.jpg");
            Image image = new Image(inputImg);
            BackgroundImage backgroundImg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

            Background background = new Background(backgroundImg);
            gameScreen.setPrefSize(700,500);
            gameScreen.setBackground(background);

        } catch (FileNotFoundException e) {
            setBackgroundColor();
        }

        String hitsMsg = "Hits: " + hits;
        String missesMsg = "Misses: " + misses;

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setColor(Color.GRAY);

        nHits = new Text(245, 30, hitsMsg);
        nHits.setFill(Color.NAVY);
        nHits.setEffect(shadow);
        nHits.setFont(Font.font("Verdana", FontWeight.BLACK,20));

        nMisses = new Text(370,30, missesMsg);
        nMisses.setFill(Color.DARKRED);
        nMisses.setEffect(shadow);
        nMisses.setFont(Font.font("Verdana", FontWeight.BLACK,20));

        gameScreen.getChildren().addAll(nHits, nMisses);

        Random r = new Random();
        yPos = r.nextInt(390) + 60;
        Ball ball = new Ball(25, yPos, 25);
        ball.setFill(Color.WHITE);
        ball.setStroke(Color.BLACK);
        ball.setStrokeWidth(1.5);
        ballsInGame.add(ball);

        addBallIntoScreen(gameScreen, ballsInGame);
    }

    /**
     * Adds the balls to the game screen
     * @param gamePane the game user interface
     * @param listOfBalls the set of balls
     */
    private static void addBallIntoScreen(Pane gamePane, ArrayList<Ball> listOfBalls)
    {
        for(Ball ball : listOfBalls) {
            gamePane.getChildren().add(ball);
        }
    }

    /**
     * Sets the position of the ball missed, as well as the point lost
     * @param ball the ball object that "moved outside" the screen
     */
    private void setMisses(Circle ball) {
        ball.setCenterX(0);
        misses += 1;
        nMisses.setText("Misses: " + misses);

        if(misses == 5) {
            setGameOver();
        }
    }

    /**
     * Sets the game over mode (Stops animation, displays game over message, sets all balls' position)
     */
    private void setGameOver()
    {
        animation.stop();
        gameOverMsg = new Text(185, 250, "Game Over");
        gameOverMsg.setFont(Font.font("Verdana",FontWeight.BOLD, 55));
        gameOverMsg.setFill(Color.FIREBRICK);
        gameOverMsg.setStroke(Color.DARKRED);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setColor(Color.WHITE);
        gameOverMsg.setEffect(shadow);

        gameScreen.getChildren().add(gameOverMsg);

        for(Ball ball : ballsInGame) {
            ball.setCenterX(-40);
        }
        gameOverMode = true;
    }

    /**
     * Sets the background color, in case of error with the background image
     */
    private void setBackgroundColor() {
        BackgroundFill backgroundFill = new BackgroundFill(Color.SLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        gameScreen.setPrefSize(700,500);
        gameScreen.setBackground(background);
    }

    /**
     * BallMovement class extends AnimationTimer
     */
    private class BallMovement extends AnimationTimer
    {
        /**
         * Creates the movement of each ball by changing its position X according to the value of its velocity
         * @param now not used
         */
        @Override
        public void handle(long now)
        {
            if(ballsInGame != null) {
                for(Ball ball : ballsInGame) {
                    ball.setOnMouseClicked(new BallClickedHandler());

                    if(ball.getCenterX() > 725) {
                        setMisses(ball);
                    }

                    double x = ball.getCenterX();
                    x += ball.getBallVelocity();

                    ball.setCenterX(x);
                }
            }
        }
    }

    /**
     * Represents the event handler associated with the mouse click on each ball
     */
    private class BallClickedHandler implements EventHandler<MouseEvent>
    {
        /**
         * Adds 1 point to hit, increases the velocity of the ball clicked by 1, and inserts a new ball in the game
         * @param e the event which occurred
         */
        @Override
        public void handle(MouseEvent e)
        {
            if(!gamePaused) {
                hits += 1;
                nHits.setText("Hits: " + hits);

                Ball ballClicked = (Ball)e.getSource();
                ballClicked.setCenterX(-50);
                ballClicked.setBallVelocity(1);

                Random r = new Random();
                yPos = r.nextInt(390) + 60;
                Ball newBall = new Ball(5, yPos,25);
                newBall.setFill(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
                newBall.setStroke(Color.BLACK);
                newBall.setStrokeWidth(1.5);
                ballsInGame.add(newBall);
                gameScreen.getChildren().add(newBall);
            }
        }
    }

    /**
     * Represents the event handler associated with the Reset button
     */
    private class ResetButtonHandler implements EventHandler<ActionEvent>
    {
        /**
         * Sets the variables hits and misses to their initial value, and resets the game
         * @param e the event which occurred
         */
        @Override
        public void handle(ActionEvent e)
        {
            animation.stop();
            hits = 0;
            misses = 0;

            nHits.setText("Hits: ");
            nMisses.setText("Misses: ");
            for(Ball ball : ballsInGame){
                ball.setCenterX(-35);
            }

            if(gameOverMsg != null) {
                gameOverMsg.setVisible(false);
            }

            ballsInGame = null;

            try {
                setGameArea();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            gamePaused = false;
            gameOverMode = false;
            animation.start();
        }
    }

    /**
     * Represents the event handler associated with the Pause button
     */
    private class PauseButtonHandler implements EventHandler<ActionEvent>
    {
        /**
         * Alternates the game between paused and running
         * @param e the event which occurred
         */
        @Override
        public void handle(ActionEvent e)
        {
            if(!gameOverMode) {
                if(!gamePaused) {
                    animation.stop();
                    gamePaused = true;

                } else {
                    gamePaused = false;
                    animation.start();
                }
            }
        }
    }
}
