package com.example.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.Board;
import com.example.android.R;
import com.example.android.model.GameSaver;
import com.example.android.model.GameStates;

import java.io.IOException;
import java.util.List;

public class ReplayListActivity extends AppCompatActivity {

    // UI elements
    private ListView listView;
    private Spinner spinner;
    private List<GameStates> states;
    private ArrayAdapter<GameStates> statesAdapter;
    private Button deleteButton;

    private GameSaver gameSaver;
    private boolean isDeleting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up UI.
        setContentView(R.layout.activity_replay_list);
        listView = findViewById(R.id.listView);
        View emptyText = findViewById(R.id.emptyText); // Set the empty list text
        listView.setEmptyView(emptyText);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isDeleting = false;
        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isDeleting){
                    isDeleting = false;
                    cancelDelete();
                }
                else{
                    isDeleting = true;
                    promptDelete();
                }
            }
        });


        // Show the saved games, if any.
        this.gameSaver = new GameSaver();
        try {
            states = gameSaver.getAllGameStates(this);
            statesAdapter = new ArrayAdapter<GameStates>(this, R.layout.saved_game, states);
            listView.setAdapter(statesAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add a listener to begin replaying a game when the player clicks on an entry.
        // Or if the player wants to delete a game, allow that game to be deleted.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isDeleting){
                    startReplay(position);
                    return;
                }
                else{
                    // Delete the selected game.
                    // Position should correspond to the gameSaver's list.
                    states = gameSaver.delete(position, ReplayListActivity.this);
                    updateListView();
                    Toast.makeText(ReplayListActivity.this,"You deleted the game!", Toast.LENGTH_LONG).show();
                    isDeleting = false;
                    deleteButton.setText("Delete");
                }
            }
        });


        // Set up the dropdown spinner for sorting.
        String[] entries = {"Sort by name", "Sort by date"};
        spinner = findViewById(R.id.spinner);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.saved_game, entries);
        spinnerAdapter.setDropDownViewResource(R.layout.saved_game);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    // Sort by name.
                    try{
                        states = gameSaver.sortByName();
                    } catch (NullPointerException e){
                        return;
                    }
                }
                if (position == 1){
                    // Sort by date.
                    try{
                        states = gameSaver.sortByDate();
                    } catch (NullPointerException e){
                        return;
                    }
                }
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Start the reply by switching to the reply activity with the chosen
     * game states to view.
     * @param gameState Index of the saved game in the states list, which will be replayed.
     */
    private void startReplay(int gameState){
        GameStates gs = states.get(gameState);
        Intent intent = new Intent(this, ReplayActivity.class);
        intent.putExtra("states", gs);
        startActivity(intent);
    }

    private void promptDelete(){
        Toast.makeText(this,"Choose a game to delete.", Toast.LENGTH_LONG).show();
        deleteButton.setText("Cancel Delete");
    }

    private void cancelDelete(){
        deleteButton.setText("Delete");
    }

    private void updateListView(){
        statesAdapter = new ArrayAdapter<GameStates>(ReplayListActivity.this, R.layout.saved_game, states);
        statesAdapter.notifyDataSetChanged();
        listView.invalidateViews();
        listView.setAdapter(statesAdapter);
    }

}
