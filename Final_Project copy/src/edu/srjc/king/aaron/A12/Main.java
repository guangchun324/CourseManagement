package edu.srjc.king.aaron.A12;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application
{

    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage primaryStage)
    {

        SnakeController game = new SnakeController();
        game.start(new Stage());

    }
}
