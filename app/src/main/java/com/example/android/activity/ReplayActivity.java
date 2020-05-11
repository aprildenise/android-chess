package com.example.android.activity;

import android.os.Bundle;

import com.example.android.Board;
import com.example.android.R;
import com.example.android.model.CellAdapter;
import com.example.android.model.GameStates;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ReplayActivity extends AppCompatActivity {

    private GridView gridView;
    private Button nextButton, prevButton;
    private TextView title;

    private Board board;
    private Board.Cell[] cells;
    private CellAdapter cellAdapter;

    private GameStates states;
    private GameStates.Iterator iterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        gridView = findViewById(R.id.gridViewReplay);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        title = findViewById(R.id.replay_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the board
        board = Board.getNewInstance();
        states = (GameStates) getIntent().getExtras().get("states");
        iterator = new GameStates.Iterator(states);

        // Show the board
        updateBoard();
        title.setText("Game start!");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                GameStates.State state = null;
                try{
                   state = iterator.getNextState();
                } catch (IndexOutOfBoundsException e){
                    Toast.makeText(ReplayActivity.this,"You're at the end of the game.", Toast.LENGTH_LONG).show();
                    return;
                }
                board.setBoard(state.board, state.pieces);
                updateBoard();
                title.setText(state.title);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                GameStates.State state = null;
                try{
                    state = iterator.getPrevState();
                } catch (IndexOutOfBoundsException e){
                    Toast.makeText(ReplayActivity.this,"You're at the start of the game.", Toast.LENGTH_LONG).show();
                    return;
                }
                board.setBoard(state.board, state.pieces);
                updateBoard();
                title.setText(state.title);
            }
        });

    }

    private void updateBoard(){
        cells = CellAdapter.flattenCellArray(board);
        cellAdapter = new CellAdapter(this, cells);
        gridView.setAdapter(cellAdapter);
    }

}
