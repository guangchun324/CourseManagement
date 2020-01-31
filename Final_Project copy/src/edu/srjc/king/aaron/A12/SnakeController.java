package edu.srjc.king.aaron.A12;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeController extends Application
{
    // variable
    static int speed = 5;
    static int foodcolor = 0;
    static int width = 50;
    static int height = 25;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static List<SnakeController.Corner> snake = new ArrayList<>();
    static SnakeController.Dir direction = SnakeController.Dir.left;
    static boolean gameOver = false;
    static boolean pause = false;
    static Random rand = new Random();

    // tick
    public static void tick(GraphicsContext gc)
    {

        if(pause)
        {
            gc.setFill(Color.ALICEBLUE);
            gc.setFont(new Font("", 100));
            gc.fillText("PAUSED", 400, 350);
            return;

        }
        if (gameOver)
        {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 100));
            gc.fillText("GAME OVER", 100, 250);
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--)
        {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction)
        {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0)
                {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y >= height)
                {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0)
                {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x >= width)
                {
                    gameOver = true;
                }
                break;

        }

        // eat food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y)
        {
            snake.add(new SnakeController.Corner(-1, -1));
            if (foodcolor == 0)
            {
                speed += 20;
            }
            else if (foodcolor == 1)
            {
                speed -= 2;
            }
            else if (foodcolor == 2)
            {
                speed += 5;
            }
            newFood();
        }

        // self destroy
        for (int i = 1; i < snake.size(); i++)
        {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y)
            {
                gameOver = true;
            }
        }

        // fill
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        //System.out.println(speed);
        gc.fillText("Score: " + (speed - 6), 10, 30);


        // random foodcolor
        Color cc = Color.WHITE;

        switch (foodcolor)
        {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        // snake
        for (SnakeController.Corner c : snake)
        {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);

        }

    }

    // food
    public static void newFood()
    {
        start:
        while (true)
        {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (SnakeController.Corner c : snake)
            {
                if (c.x == foodX && c.y == foodY)
                {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            speed++;
            break;

        }
    }
    
    public void start(Stage primaryStage)
    {
        try
        {
            newFood();
            VBox root = new VBox();
            Canvas c = new Canvas(width * cornersize, height * cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer()
            {
                long lastTick = 0;

                public void handle(long now)
                {
                    if (lastTick == 0)
                    {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed)
                    {
                        lastTick = now;
                        tick(gc);
                    }
                }

            }.start();

            Scene scene = new Scene(root, width * cornersize, height * cornersize);

            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key ->
            {
                if (key.getCode() == KeyCode.W)
                {
                    if (direction != SnakeController.Dir.down)
                    {
                        direction = SnakeController.Dir.up;
                    }
                }
                if (key.getCode() == KeyCode.A)
                {
                    if (direction != SnakeController.Dir.right)
                    {
                        direction = SnakeController.Dir.left;
                    }
                }
                if (key.getCode() == KeyCode.S)
                {
                    if (direction != SnakeController.Dir.up)
                    {
                        direction = SnakeController.Dir.down;
                    }
                }
                if (key.getCode() == KeyCode.D)
                {
                    if (direction != SnakeController.Dir.left)
                    {
                        direction = SnakeController.Dir.right;
                    }
                }

                if(key.getCode() == KeyCode.P)
                {
                    pause = !pause;
                }

                if(gameOver)
                {
                    if(key.getCode() == KeyCode.K)
                    {
                        resetGame();
                    }
                }

            });

            // add start snake parts
            snake.add(new SnakeController.Corner(width / 2, height / 2));
            snake.add(new SnakeController.Corner(width / 2+1, height / 2));
            snake.add(new SnakeController.Corner(width / 2+2, height / 2));

            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME");
            primaryStage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public enum Dir
    {
        left, right, up, down
    }

    public static class Corner
    {
        int x;
        int y;

        public Corner(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

    }

    private void resetGame()
    {
        snake.clear();
        gameOver = false;
        snake.add(new SnakeController.Corner(width / 2, height / 2));
        snake.add(new SnakeController.Corner(width / 2, height / 2));
        snake.add(new SnakeController.Corner(width / 2, height / 2));
        newFood();
        speed = 6;
        direction = SnakeController.Dir.left;
    }
}
