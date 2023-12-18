package sk.ukf.mazeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Game extends View implements SensorEventListener {

    public Paint paint;
    public Context context;
    public float displayX, displayY, sensorX, sensorY, xHero, yHero;
    public int center, startPoint, level;
    public ArrayList<MazeCell> maze;
    public SensorManager sm;
    public Sensor accelerometer;
    public Point distances;
    public MazeCell finalCell;
    public Handler handler;
    public HandlerThread handlerThread;

    public Game(Context context, int level) {
        super(context);
        this.context = context;
        this.level = level;
        paint = new Paint();

        CreateHandler();
        handlerThread = new HandlerThread(handler);
        handlerThread.start();

        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        distances = new Point();
        display.getSize(distances);

        displayX = distances.x;
        displayY = distances.y;

        center = Math.round(distances.x /2);

        startPoint = center-480;

        createMaze();
        setMazeBackground();
    }

    public void CreateHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                invalidate();
                update();
                super.handleMessage(msg);
            }
        };
    }

    public void setMazeBackground() {
        switch (level) {
            case 1:
                setBackgroundResource(R.drawable.level1);
                break;
            case 2:
                setBackgroundResource(R.drawable.level2);
                break;
            case 3:
                setBackgroundResource(R.drawable.level3);
                break;
            case 4:
                setBackgroundResource(R.drawable.level4);
                break;
        }
    }

    public void createMaze() {
        maze = new ArrayList<>();

        String string = "";
        int y = 300;
        InputStream is = null;

        switch (level) {
            case 1:
                is = this.getResources().openRawResource(R.raw.maze1);
                break;
            case 2:
                is = this.getResources().openRawResource(R.raw.maze2);
                break;
            case 3:
                is = this.getResources().openRawResource(R.raw.maze3);
                break;
            case 4:
                is = this.getResources().openRawResource(R.raw.maze4);
                break;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            for(int i=0; i<string.length(); i++) {
                if(string.charAt(i) == 'X') {
                    maze.add(new MazeCell((i*40)+startPoint, y));
                } else if(string.charAt(i) == 'A'){
                    finalCell = new MazeCell((i*40)+startPoint, y);
                } else if(string.charAt(i) == 'H') {
                    xHero = i*40+80;
                    yHero = y+20;
                }
            }
            y+=40;
        }
    }

    public void update() {
        if(!isColliding(xHero-sensorX, yHero+sensorY)) {
            xHero = xHero - sensorX;
            yHero = yHero + sensorY;
        }

        if(xHero>=finalCell.getX() && xHero<=finalCell.getX()+40 && yHero>=finalCell.getY() && yHero<=finalCell.getY()+40) {
            handlerThread.setInterrupt();
            stopScreening();
            ((Activity) getContext()).finish();
            Intent intent = new Intent(Game.this.getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().getApplicationContext().startActivity(intent);
        }
    }

    public boolean isColliding(float xH, float yH) {
        for(int i=0; i<maze.size(); i++) {
            MazeCell mazeCell = (MazeCell) maze.get(i);

            float Xn = Math.max(mazeCell.getX(), Math.min(xH, mazeCell.getX()+40));
            float Yn = Math.max(mazeCell.getY(), Math.min(yH, mazeCell.getY()+40));

            float Dx = Xn - xH;
            float Dy = Yn - yH;

            if((Dx*Dx + Dy*Dy) <= 10*10) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX = event.values[0];
            sensorY = event.values[1];

        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMaze(canvas);

        paint.setTextSize(50);
        paint.setTypeface(Typeface.SERIF);
        switch (level) {
            case 1:
                paint.setColor(Color.WHITE);
                canvas.drawText("Level 1", 30, 100, paint);
                break;
            case 2:
                paint.setColor(Color.WHITE);
                canvas.drawText("Level 2", 30, 100, paint);
                break;
            case 3:
                paint.setColor(Color.WHITE);
                canvas.drawText("Level 3", 30, 100, paint);
                break;
            case 4:
                paint.setColor(Color.WHITE);
                canvas.drawText("Level 4", 30, 100, paint);
                break;
        }

        paint.setColor(Color.BLUE);
        canvas.drawCircle(xHero, yHero, 10, paint);
    }
    public void drawMaze(Canvas canvas) {
        MazeCell mazeCell;

        for(int i=0; i<maze.size(); i++) {
            mazeCell = (MazeCell) maze.get(i);
            paint.setColor(Color.WHITE);
            canvas.drawRect(mazeCell.getX(), mazeCell.getY(), mazeCell.getX()+40, mazeCell.getY()+40, paint);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void stopScreening() { sm.unregisterListener(this);}

    public void startScreening() {
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
}
