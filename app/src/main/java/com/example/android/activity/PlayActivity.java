package com.example.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.Board;
import com.example.android.model.CellAdapter;
import com.example.android.R;

import java.io.IOException;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    // UI elements in the app
    private GridView gridView;
    private CellAdapter cellAdapter;
    private TextView playTitle;

    // TEST ONLY
    //private Button testButton;

    // Game computations
    private Board board;
    private Boolean whitesTurn;
    private String playerColor;
    private Board.Cell[] cells;
    private Board.Cell srcCell;
    private View srcCellView;
    private boolean madeUndo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Find all the UI components in the scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        gridView = findViewById(R.id.gridView);
        playTitle = findViewById(R.id.play_title);

        // Set up the board
        board = Board.getNewInstance();
        whitesTurn = true;
        playerColor = "White";
        madeUndo = false;

        // Show the board
        cells = CellAdapter.flattenCellArray(board);
        cellAdapter = new CellAdapter(this, cells);
        gridView.setAdapter(cellAdapter);
        gridView.setOnItemClickListener(this);

        playTitle.setText("Game start!" + System.lineSeparator() + "White's turn");

        //test only
//        testButton = findViewById(R.id.test_button);
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                displayConfirmSave();
//            }
//        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Adapter's list on item click", cellAdapter.toString());

        playerColor = whitesTurn? "White" : "Black";
        Board.Cell cell = cells[position];
        playTitle.setText(playerColor + "'s turn. Position: " + cell.position.toString());

        if(srcCellView == null){
            srcCell = this.cells[position];
            // Identify if selected source is of type piece and of user's piece.
            if(srcCell.piece != null && srcCell.piece.color.equals(playerColor)){
                // Color the cell to warn the user that this box is selected
                srcCellView = view;
                view.setBackgroundColor(Color.CYAN);
            } else {
                srcCell = null; //Reset
            }
        } else {
            //Setting destination
            Board.Cell destCell = cells[position];
            //checking the validness of "move" through our game logic
            if(board.canMovePiece(srcCell.position.file, srcCell.position.rank,
                    destCell.position.file, destCell.position.rank, whitesTurn)){
                //Detect if move is promotion -> suggest promotion type
                if(srcCell.piece.name.equals("Pawn") && (whitesTurn? destCell.position.rank == 0 : destCell.position.rank == 7)){
                    displayGetPromotionType(destCell); //Need this finish executing before the end of the flow
                }else{
                    makeMove(destCell);
                }

            } else {
                Toast.makeText(this,"Illegal Move!", Toast.LENGTH_LONG).show();
            }
            //Reset background
            srcCellView.setBackgroundColor(0);
            //Reset both source and destination views
            srcCellView = null;
        }
    }


    /**
     * Update the gridView. Mainly used when undoing, since the gridView's adapter needs a new
     * reference to the board's cells.
     */
    private void changeState(){
        this.cells = CellAdapter.flattenCellArray(board);
        this.cellAdapter = new CellAdapter(this, cells);
        this.cellAdapter.notifyDataSetChanged();
        this.gridView.invalidateViews();
        this.gridView.setAdapter(cellAdapter);

        Log.d("Cell adapter's list on changestate", cellAdapter.toString());
        //gridView.setOnItemClickListener(this);
    }

    // Set up the buttons //
    public void handleUndoButton(View v){
        displayConfirmUndo();
    }

    public void handleResignButton(View v){
        displayConfirmResign();
    }

    public void handleDrawButton(View v){
        displayConfirmDraw();
    }

    public void handleRandomButton(View v){
        board.makeRandomMove(whitesTurn);
        changeState();
        whitesTurn = !whitesTurn;
        String title = whitesTurn? "White's turn" : "Black's turn";
        playTitle.setText(title);
        madeUndo = false;
    }

    /**
     * When the user hits the undo button show a prompt to confirm if they really want to undo their
     * last move. Then, confirm with the Board that it is possible to undo the move.
     * Update the displayed Board as needed.
     */
    protected void displayConfirmUndo(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Undo");
        alertDialog.setMessage("Are you sure you want to undo your previous move?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Can't undo a move if undo already been made or if the board
                        // is in its starting state.
                        if (madeUndo){
                            Toast.makeText(PlayActivity.this,"You can't undo this move", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!board.undoPrevMove()){
                            Toast.makeText(PlayActivity.this,"You can't undo this move", Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Update the board, and change the turn back to the other player's
                        changeState();
                        whitesTurn = !whitesTurn;
                        madeUndo = true;
                        playTitle.setText(playerColor + "'s turn");
                    }
                });
        alertDialog.show();
    }

    /**
     * When the user hits the resign button. Screen will prompt to confirmation for resigning.
     * Update the displayed Board as needed.
     */
    protected void displayConfirmResign(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Resign Request");
        alertDialog.setMessage("Do you want to resign this game?");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String winner = whitesTurn? "Black" : "White";
                        String resignee = whitesTurn? "White" : "Black";
                        board.addNoMoveState(resignee + " resigns. " + winner + " wins!");
                        displayWinner(winner);
                        displayConfirmSave();
                    }
                });
        alertDialog.show();
    }

    /**
     * Draw is handled by the follow logic:
     * When player1 clicks on the draw button, a request is made to player2.
     * Player has the option to accept or decline the request.
     * Accepted: the game state is declared draw and the user is prompted for saving the game
     * Decline: player1 gains control of the board again for his/her turn
     */
    protected void displayConfirmDraw(){
        String currentPlayer = whitesTurn? "White": "Black";
        String opponentPlayer = whitesTurn? "Black": "White";
        //Build draw request dialog
        final AlertDialog drawRequestDialog = new AlertDialog.Builder(this).create();
        drawRequestDialog.setTitle("Draw Request");
        drawRequestDialog.setMessage(currentPlayer + ": Do you want to request a draw?");

        //Build drawPrompt dialog
        final AlertDialog drawPromptDialog = new AlertDialog.Builder(this).create();
        drawPromptDialog.setTitle("Draw Requested by opponent");
        drawPromptDialog.setMessage(opponentPlayer + ": Do you want to accept the draw?");

        drawPromptDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        backToHome(); // TODO: end, or resume the game?
                    }
                });
        drawPromptDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        displayWinner("No one");
                        board.addNoMoveState("Draw. No one wins.");
                        displayConfirmSave();
                    }
                });

        //Displaying dialog based on sequence mentioned above.
        drawRequestDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        drawRequestDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawPromptDialog.show(); //prompt draw request to opponent
                    }
                });

        drawRequestDialog.show();

    }


    /**
     * Display a dialog to ask the players if they want to save their game.
     */
    protected void displayConfirmSave(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Do you want to save this game?");

        //final EditText edittext = new EditText(this);
        //alertDialog.setView(edittext);


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        backToHome();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //String gameName = edittext.getText().toString();
                        displayNameGame();
                    }
                });
        alertDialog.show();
    }

    /**
     * A toast will pop up to inform user the winning of the current game.
     * @param winner winner of the current game
     */
    protected void displayWinner(String winner){
        Toast.makeText(this, winner + " has won the game!", Toast.LENGTH_LONG).show();
    }

    /**
     * Display a dialog for the user to name their game to save it to the database.
     */
    protected void displayNameGame(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Name this game.");

        final EditText edittext = new EditText(this);
        alertDialog.setView(edittext);


        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        displayConfirmSave();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String gameName = edittext.getText().toString();
                        board.setGameName(gameName);
                        try {
                            board.saveGame(PlayActivity.this);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            displayError();
                        }
                        backToHome();
                    }
                });
        alertDialog.show();
    }

    /**
     * This display happens when the user tries to make a promotion move.
     * Prompts the user with options for what to promote their pawn to.
     */
    protected void displayGetPromotionType(final Board.Cell destCell){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Promotion");
        //TODO: add static Mapping for promotion -> {Queen: Q} -> to make it look prettier
        final String[] types = {"Q", "R", "B", "N"};
        builder.setItems(types, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PlayActivity.this, "Position: " + which + " Value: " + types[which], Toast.LENGTH_LONG).show();
                char promotion = types[which].charAt(0);
                if(board.canPromote(srcCell.position.file, srcCell.position.rank,
                        destCell.position.file, destCell.position.rank, whitesTurn, promotion)) {
                    board.promote(srcCell.position.rank, srcCell.position.file, whitesTurn, promotion);
                }
                makeMove(destCell);
                dialog.dismiss();
            }
        }).create().show();
    }

    protected void makeMove(Board.Cell destCell){
        //Actual move of the piece
        board.movePiece(srcCell.position.file, srcCell.position.rank, destCell.position.file,
                destCell.position.rank, whitesTurn);
        cellAdapter.notifyDataSetChanged();
        gridView.setAdapter(cellAdapter);

        //Switch turn
        whitesTurn = !whitesTurn;
        madeUndo = false;

        // Check if this is a checkmate/check
        Board.BoardStatus status = board.checkGameProgress();
        if (status != Board.BoardStatus.NOCHECKS){
            if (status == Board.BoardStatus.WHITEINCHECK || status == Board.BoardStatus.BLACKINCHECK){
                Toast.makeText(this, "Check", Toast.LENGTH_LONG).show();
            }
            else if (status == Board.BoardStatus.BLACKINCHECKMATE){
                playTitle.setText("Checkmate!");
                displayWinner("White");
                displayConfirmSave();
                return;
            }
            else if (status == Board.BoardStatus.WHITEINCHECKMATE){
                playTitle.setText("Checkmate!");
                displayWinner("Black");
                displayConfirmSave();
                return;
            }
        }
        String title = whitesTurn? "White's turn" : "Black's turn";
        playTitle.setText(title);


    }

    /**
     * Change the activity and go back to the home screen.
     */
    private void backToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    /**
     * Display a dialog letting the player know that there was an error when
     * saving the game.
     */
    private void displayError(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Could not reach the database to save this game :(");

        final EditText edittext = new EditText(this);
        alertDialog.setView(edittext);


        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        backToHome();
                    }
                });
        alertDialog.show();
    }

}
