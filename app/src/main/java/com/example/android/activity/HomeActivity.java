package com.example.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.R;

public class HomeActivity extends AppCompatActivity {

    Button startButton;
    Button replayButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //calls super class's onCreate -> seems to initiate the ability to save game instance state
        super.onCreate(savedInstanceState);
        //sets the view using the setContentView method
        setContentView(R.layout.activity_home);

        startButton = findViewById(R.id.startButton);
        replayButton = findViewById(R.id.replayButton);

        startButton.setOnClickListener((new View.OnClickListener() {
            @Override
            // Get the appropriate input from the user.
            public void onClick(View v) {
                playGame();
            }
        }));
        replayButton.setOnClickListener((new View.OnClickListener() {
            @Override
            // Get the appropriate input from the user.
            public void onClick(View v) {
                showReplayGames();
            }
        }));

    }

    protected void showReplayGames(){
        Intent intent = new Intent(this, ReplayListActivity.class);
        startActivity(intent);
    }

    protected void playGame(){
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }


}
