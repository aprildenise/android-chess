package com.example.android.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.android.Board;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class GameStates implements Serializable {

    public static final long serialVersionUID = GameSaver.serialVersionUID;
    private String name;
    private List<State> states;
    private GameSaver gameSaver;
    private Calendar saveDate;

    public GameStates(){
        this.name = "";
        states = new ArrayList<State>();
        gameSaver = new GameSaver();
    }

    /**
     * Set the name of this Game. Because we set the name when the player wants to save a game, we also
     * set the date of the game.
     * @param name Name for this game.
     */
    public void setName(String name){
        this.name = name;
        this.saveDate = new GregorianCalendar();
    }

    public String getName(){
        return this.name;
    }

    public Calendar getSaveDate(){
        return this.saveDate;
    }

    /**
     * Get the previous state of the board, which would be the state in the second to last
     * index. Used when undoing a move.
     * @return The previous state, which becomes a newly added state AND SHOULD NOT BE ADDED AGAIN.
     */
    public State undoCurrentState() throws IndexOutOfBoundsException{

        if (states.size() == 0 || states.size() == 1){
            throw new IndexOutOfBoundsException();
        }

        // Delete the move that was just done and add a copy of the previous state to the list.
        states.remove(states.size() - 1);
//        State previousState = states.get(states.size() - 1);
//        addState(previousState.board, previousState.pieces, previousState.turn + 1, previousState.title);
//
//        // Return this newly added state. Safe for editing.
//        undoMade = true;
        return states.get(states.size() - 1);
    }

    private State getState(int index){
        return states.get(index);
    }

    /**
     * Add a new state to the gameStates.
     * @param state Cell[][] from the Board class.
     * @param pieces List of pieces from the Board class.
     */
    public void addState(Board.Cell[][] state, List<Piece> pieces, int turn, String title){

        // Make sure that this state is not already in the list.
        // The list is kept in order until the most recent item.
//        if (states.size() != 0){
//            if (turn == states.get(states.size() - 1).turn){
//                // Don't add this.
//                return;
//            }
//        }

        Board.Cell[][] boardState = new Board.Cell[Board.BOARD_SIZE][Board.BOARD_SIZE];
        List<Piece> piecesState = new ArrayList<Piece>();
        for (int i = 0; i < Board.BOARD_SIZE; i++){
            for (int j = 0; j < Board.BOARD_SIZE; j++){
                Board.Cell srcCell = state[i][j];
                Piece srcPiece = srcCell.piece;
                Piece piece = null;
                if (srcPiece != null){
                    if (srcPiece instanceof Bishop) piece = new Bishop(srcPiece.color, new Position(srcPiece.getPosition().rank, srcPiece.getPosition().file));
                    else if (srcPiece instanceof King) piece = new King(srcPiece.color, new Position(srcPiece.getPosition().rank, srcPiece.getPosition().file));
                    else if (srcPiece instanceof Knight) piece = new Knight(srcPiece.color, new Position(srcPiece.getPosition().rank, srcPiece.getPosition().file));
                    else if (srcPiece instanceof Pawn) piece = new Pawn(srcPiece.color, new Position(srcPiece.getPosition().rank, srcPiece.getPosition().file));
                    else if (srcPiece instanceof Queen) piece = new Queen(srcPiece.color, new Position(srcPiece.getPosition().rank, srcPiece.getPosition().file));
                    else if (srcPiece instanceof Rook) piece = new Rook(srcPiece.color, new Position(srcPiece.getPosition().rank, srcPiece.getPosition().file));
                    piecesState.add(piece);
                }
                Board.Cell cell = new Board.Cell(srcCell.position, piece, srcCell.isColored);
                boardState[i][j] = cell;
            }
        }
        states.add(new State(boardState, piecesState, turn, title));
    }

    public void addState(State state){
        states.add(state);
    }

    /**
     * To save memory, serialize old states into a file and delete the objects of these states.
     * @return True if we can do the above, false if we cannot, and there aren't enough existing states
     * for it to be safe to save and delete the old ones.
     */
    public boolean saveOldStates(Context context){
        // Check if we can save the old states, which is based on the fact that the user can undo ONLY the
        // previous move/state made.
        if(states.size() % 3 == 0 && states.size() != 0){
            State oldState = states.get(0);
            try {
                // Save the state and delete it.
                states.remove(0);
                gameSaver.storeCurrentGameStates(oldState, context);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void addAllStates(List<State> states) {
        this.states.addAll(states);
    }

    public List<State> getStates(){
        return states;
    }

    public int size(){
        return states.size();
    }

    public void saveGameStates(Context context) throws IOException, ClassNotFoundException {
        gameSaver.addNewSave(this, context);
    }

    /**
     * Iterate through the gameStates. The only legal way to iterate through the states
     * is sequentially forward or backward.
     */
    public static class Iterator{

        private int currentState;
        private GameStates gameStates;

        public Iterator(GameStates gameStates){
            currentState = 0;
            this.gameStates = gameStates;
        }

        public State getNextState() throws IndexOutOfBoundsException{
            currentState++;
            if (currentState < 0 || currentState >= gameStates.size()){
                currentState--;
                throw new IndexOutOfBoundsException();
            }
            State state = gameStates.getState(currentState);
            return state;
        }

        public State getPrevState() throws IndexOutOfBoundsException{
            currentState--;
            if (currentState < 0 || currentState >= gameStates.size()){
                currentState++;
                throw new IndexOutOfBoundsException();
            }
            State state = gameStates.getState(currentState);
            return state;
        }
    }

    public String statesToString() {
        String s = "";
        for (State state: states){
            s += System.lineSeparator();
            s += state.toString();
        }
        return s;
    }

    /**
     * Utility method for converting Calendar to MM/dd/yyyy
     * @param calendar
     * @return
     */
    public static String format(Calendar calendar) {
        if (calendar == null){
            return "NO DATE";
        }
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + " (" + format(this.saveDate) + ")";
    }

    public static class State implements Serializable{
        public Board.Cell[][] board;
        public List<Piece> pieces;
        public int turn;
        public String title;

        public State(Board.Cell[][] board, List<Piece> pieces, int turn, String title){
            this.board = board;
            this.pieces = pieces;
            this.turn = turn;
            this.title = title;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Turn:" + turn + System.lineSeparator());
            for(int row = 0; row < Board.BOARD_SIZE; row++){
                for(int col = 0; col < Board.BOARD_SIZE; col++){
                    sb.append(board[row][col].toString() + " ");
                }
                sb.append(Board.BOARD_SIZE - row);
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(" a  b  c  d  e  f  g  h");
            return sb.toString();
        }
    }


}