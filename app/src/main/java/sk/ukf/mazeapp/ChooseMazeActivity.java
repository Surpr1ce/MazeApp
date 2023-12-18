package sk.ukf.mazeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseMazeActivity extends AppCompatActivity {

    public Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_maze);
    }

    public void startGame(int level) {
        game = new Game(this, level);
        setContentView(game);
        game.startScreening();
    }

    public void goToMainActivity(View v) {
        Intent intent = new Intent(ChooseMazeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void Maze1(View v) {
        startGame(1);
    }

    public void Maze2(View v) {
        startGame(2);
    }

    public void Maze3(View v) {
        startGame(3);
    }

    public void Maze4(View v) {
        startGame(4);
    }

}