package com.student.simongame;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LEVEL = "level_info";
    private Button startBtn, yellowBtn, blueBtn, greenBtn, redBtn;
    private Button[] btnArray = new Button[4];
    ArrayList<Integer> gamePattern = new ArrayList<Integer>();
    ArrayList<Integer> userClickedPattern = new ArrayList<Integer>();
    private TextView level, usernameText, prevlvlText;
    private int levelNo = 0;    private String currUsername;
    private MediaPlayer red, blue, green, yellow, wrong;
    private AlertDialog.Builder welcomeAlert, nameAlert, scoreAlert;
    private AlertDialog alert1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        yellowBtn = findViewById(R.id.yellowBtn);
        greenBtn = findViewById(R.id.greenBtn);
        redBtn = findViewById(R.id.redBtn);
        blueBtn = findViewById(R.id.blueBtn);
        level = findViewById(R.id.level);
        usernameText = findViewById(R.id.usernameText);
        prevlvlText = findViewById(R.id.prevlvlText);

        btnArray[0] = yellowBtn;
        btnArray[1] = greenBtn;
        btnArray[2] = redBtn;
        btnArray[3] = blueBtn;

        final SharedPreferences getSharedData = getSharedPreferences(LEVEL,MODE_PRIVATE);
        int levelNo = getSharedData.getInt("lvl",0);
        prevlvlText.setText("Previous Level: "+levelNo);

        yellowBtn.setOnClickListener(this);
        blueBtn.setOnClickListener(this);
        redBtn.setOnClickListener(this);
        greenBtn.setOnClickListener(this);
        yellowBtn.setEnabled(false);
        blueBtn.setEnabled(false);
        redBtn.setEnabled(false);
        greenBtn.setEnabled(false);

        final String username = getSharedData.getString("username","default user");
        currUsername = username;
        startAlert();
    }

    public void startAlert(){
        welcomeAlert = new AlertDialog.Builder(this);
        welcomeAlert.setMessage("Welcome");
        welcomeAlert.setCancelable(true);
        welcomeAlert.setPositiveButton("Continue as "+currUsername,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startGame();
                    }
                });
        welcomeAlert.setNegativeButton("New User",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        nameAlert = new AlertDialog.Builder(MainActivity.this);
                        nameAlert.setMessage("Enter your name: ");
                        nameAlert.setCancelable(true);
                        final EditText nameEdit = new EditText(MainActivity.this);
                        nameEdit.setTextColor(Color.BLACK);
                        nameAlert.setView(nameEdit);
                        nameAlert.setPositiveButton("Submit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String newname = nameEdit.getText().toString().trim();
                                        SharedPreferences sharedPreferences = getSharedPreferences(LEVEL,MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username",newname);
                                        editor.apply();
                                        currUsername = newname;
                                        startGame();
                                    }
                                });
                        alert1 = nameAlert.create();
                        alert1.show();
                    }
                });
        alert1 = welcomeAlert.create();
        alert1.show();
    }

    public void startGame(){
        startBtn.setEnabled(false);
        yellowBtn.setEnabled(true);
        blueBtn.setEnabled(true);
        redBtn.setEnabled(true);
        greenBtn.setEnabled(true);
        randomTile();
        level.setText("Level: 0");
        usernameText.setText("Hello "+currUsername);
    }

    public void randomTile(){
        final int random = new Random().nextInt(4);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                btnAnimate(random);
                playSound(random);
            }
        },500);
        gamePattern.add(random);
        userClickedPattern.clear();
    }

    private void btnAnimate(final int random){
        final AlphaAnimation animationG = new AlphaAnimation(0.2f, 1.0f);
        final ScaleAnimation animationSl = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        animationG.setDuration(500);
        animationSl.setDuration(400);
        btnArray[random].setAlpha(1.0f);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnArray[random].startAnimation(animationG);
                btnArray[random].startAnimation(animationSl);
            }
        });
    }

    private void playSound(int val){
        final MediaPlayer red = MediaPlayer.create(this, R.raw.red);
        final MediaPlayer green = MediaPlayer.create(this, R.raw.green);
        final MediaPlayer yellow = MediaPlayer.create(this, R.raw.yellow);
        final MediaPlayer blue = MediaPlayer.create(this, R.raw.blue);
        final MediaPlayer wrong = MediaPlayer.create(this, R.raw.wrong);

        switch (val){
            case 0:
                yellow.start();
                break;
            case 1:
                green.start();
                break;
            case 2:
                red.start();
                break;
            case 3:
                blue.start();
                break;
        }
    }

    private void checkAns(int val){
        if(gamePattern.get(val) == userClickedPattern.get(val)){
            if(gamePattern.size() == userClickedPattern.size()){
                levelNo++;
                level.setText("Level: "+levelNo);
                SharedPreferences sharedPreferences = getSharedPreferences(LEVEL,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("lvl",levelNo);
                editor.apply();
                randomTile();
            }
        }
        else{
            level.setText("Game Over!");    int maxlvl = levelNo;
            levelNo = 0;
            gamePattern.clear();
            userClickedPattern.clear();
            startBtn.setEnabled(true);
            yellowBtn.setEnabled(false);
            blueBtn.setEnabled(false);
            redBtn.setEnabled(false);
            greenBtn.setEnabled(false);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            final TextView unText = new TextView(MainActivity.this);
            final TextView lvlText = new TextView(MainActivity.this);
            scoreAlert = new AlertDialog.Builder(this);
            scoreAlert.setMessage("Game Over");
            scoreAlert.setCancelable(true);
            unText.setText("Username: "+currUsername);
            unText.setPadding(40,10,0,0);
            unText.setTextSize(16);
            layout.addView(unText);
            lvlText.setText("Level reached: "+maxlvl);
            lvlText.setPadding(40,10,0,0);
            lvlText.setTextSize(16);
            layout.addView(lvlText);
            scoreAlert.setView(layout);
            scoreAlert.setPositiveButton("Start Game",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startAlert();
                        }
                    });
            alert1 = scoreAlert.create();
            alert1.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.yellowBtn:
                btnAnimate(0);
                playSound(0);
                userClickedPattern.add(0);
                checkAns(userClickedPattern.size()-1);
                break;
            case R.id.greenBtn:
                btnAnimate(1);
                playSound(1);
                userClickedPattern.add(1);
                checkAns(userClickedPattern.size()-1);
                break;
            case R.id.redBtn:
                btnAnimate(2);
                playSound(2);
                userClickedPattern.add(2);
                checkAns(userClickedPattern.size()-1);
                break;
            case R.id.blueBtn:
                btnAnimate(3);
                playSound(3);
                userClickedPattern.add(3);
                checkAns(userClickedPattern.size()-1);
                break;
        }
    }
}
