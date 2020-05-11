package com.example.android;

import android.content.Context;
import android.util.Log;

import com.example.android.model.Bishop;
import com.example.android.model.GameSaver;
import com.example.android.model.GameStates;
import com.example.android.model.King;
import com.example.android.model.Knight;
import com.example.android.model.Pawn;
import com.example.android.model.Piece;
import com.example.android.model.Position;
import com.example.android.model.Queen;
import com.example.android.model.Rook;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the Chess game board, which is represented as a 2D array of Cells.
 * @author Krysti Leong, April Dizon
 */
public class Board {

    public static final int BOARD_SIZE = 8;
    private Cell[][] board;
    private int[][] prevMove;
    private List<Piece> pieces;
    private boolean[][] cellsThreatenedByWhite;
    private boolean[][] cellsThreatenedByBlack;
    private boolean whiteInCheck;
    private boolean blackInCheck;
    private boolean whiteInCheckmate;
    private boolean blackInCheckmate;
    private GameStates states;
    private int turn;

    public static Board getNewInstance(){
        return new Board();
    }

    /**
     * File = Column
     * Rank = Row
     * Setup the board. Note that the board is being coded as if it was flipped upside-down
     * so that the indices will correspond exactly to the file and rank of an actual
     * chess board.
     */
    public Board(){

        // Instantiate the variables
        prevMove = new int[2][2];
        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        pieces = new ArrayList<Piece>();
        cellsThreatenedByWhite = new boolean[BOARD_SIZE][BOARD_SIZE];
        cellsThreatenedByBlack = new boolean[BOARD_SIZE][BOARD_SIZE];
        whiteInCheck = false;
        whiteInCheckmate = false;
        blackInCheck = false;
        blackInCheckmate = false;

        // Fill up the board.
        for (int row = 0; row < BOARD_SIZE; row++){ //Note that row = 0 implies 8
            for (int col = 0; col < BOARD_SIZE; col++) {
                Cell newCell = null;
                Boolean toColor = (row + col) % 2 == 1;
                Position position = new Position(row, col);

                if (row == 0 || row == 7) { // rook, knight, bishop, queen, king, bishop, knight, and rook;
                    String color = row == 0 ? "Black" : "White";
                    if (col == 0) newCell = new Cell(position, new Rook(color, position), toColor);
                    else if (col == 1) newCell = new Cell(position, new Knight(color,position), toColor);
                    else if (col == 2) newCell = new Cell(position, new Bishop(color, position), toColor);
                    else if (col == 3) newCell = new Cell(position, new Queen(color, position), toColor);
                    else if (col == 4) newCell = new Cell(position, new King(color, position), toColor);
                    else if (col == 5) newCell = new Cell(position, new Bishop(color, position), toColor);
                    else if (col == 6) newCell = new Cell(position, new Knight(color,position), toColor);
                    else newCell = new Cell(position, new Rook(color,position), toColor);

                    // Add this piece to the list
                    pieces.add(newCell.piece);

                } else if (row == 1 || row == 6) { // pawns
                    String color = row == 1 ? "Black" : "White";
                    newCell = new Cell(position, new Pawn(color,position), toColor);

                    // Add this piece to the list
                    pieces.add(newCell.piece);
                } else {
                    newCell = new Cell(position, toColor);
                }
                board[row][col] = newCell;
            }
        }
        findThreats();
        states = new GameStates();
        states.addState(board, pieces, 0, "Game start!");
        turn = 1;
    }


    //regionGETTING AND SETTING THE CELLS

    public Board.Cell[][] getCells(){
        return board;
    }

    /**
     * Set the game board and the pieces on the game board. Used for debugging and testing.
     * @param board 2D array of cells that represent the board.
     * @param pieces Pieces on the board.
     */
    public void setBoard(Cell[][] board, List<Piece> pieces){

        Board.Cell[][] boardState = new Board.Cell[Board.BOARD_SIZE][Board.BOARD_SIZE];
        List<Piece> piecesState = new ArrayList<Piece>();
        for (int i = 0; i < Board.BOARD_SIZE; i++){
            for (int j = 0; j < Board.BOARD_SIZE; j++){
                Board.Cell srcCell = board[i][j];
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

        this.board = boardState;
        this.pieces = piecesState;
        findThreats();
    }

    /**
     * Get the 2D array of cells that represent the game board.
     * @return The game board.
     */
    public Cell[][] getBoard(){
        return board;
    }

    //endregion

    //region GAMESTATE OPERATIONS

    public void setGameName(String name){
        states.setName(name);
    }

    public void saveGame(Context context) throws IOException, ClassNotFoundException {
        states.saveGameStates(context);
    }

    /**
     * Add a state to the states list, which specifically represent any draws or resigns
     * done by the player.
     * !! Should not be used when making a move!!
     * @param title Title for this state to show on the UI.
     */
    public void addNoMoveState(String title){
        states.addState(board, pieces, turn, title);
        turn++;
    }

    /**
     * Undo the move made by simply setting the board with the previous state
     * @return True if successful, false elsewise, and there were no previous moves made.
     */
    public boolean undoPrevMove(){
        try{
            GameStates.State state = states.undoCurrentState();
            setBoard(state.board, state.pieces);
            turn = state.turn;
            //Log.d("STATES UPON UNDO", states.statesToString());
        } catch (IndexOutOfBoundsException e){
            return false;
        }
        return true;
    }

    //endregion

    /**
     * Checks if the game defined in the board has ended by seeing if there are any kings in checkmate.
     * @return 0 if no kings are in checkmate, 1 if the white king is in checkmate, 2 if the black
     * king is in checkmate.
     */
    public BoardStatus checkGameProgress(){

        if (whiteInCheckmate){
            return BoardStatus.WHITEINCHECKMATE;
        }
        else if (blackInCheckmate){
            return BoardStatus.BLACKINCHECKMATE;
        }
        else if (whiteInCheck){
            return BoardStatus.WHITEINCHECK;
        }
        else if (blackInCheck){
            return BoardStatus.BLACKINCHECK;
        }
        else{
            return BoardStatus.NOCHECKS;
        }

    }


    /**
     * Choose a random move for the player. This move is checked to make sure its valid.
     * There is assumed to be at least one move. If there is none, then the game should have already
     * ended????
     * @param forWhitePlayer True if we're finding the move for the white player, False if we're
     *                       finding it for the black player.
     * @return An array with the random move, where Position[0] is the source and Position[1] is the
     * destination.
     */
    public void makeRandomMove(boolean forWhitePlayer){
        //Proposal to infinite loop question: we can keep a visited set to keep track of repeats
        String currPlayer = forWhitePlayer ? "White" : "Black";
        while (true){
            // Pick a random piece.
            int random = new Random().nextInt(pieces.size());
            Piece randomPiece = pieces.get(random);

            // Check if we can use this piece.
            if (randomPiece == null || !randomPiece.color.equals(currPlayer)) continue;

            int srcRank = randomPiece.getPosition().rank;
            int srcFile = randomPiece.getPosition().file;
            List<Position> moves = randomPiece.getAllMoves(this);

            if(moves == null || moves.size() == 0) continue;

            // Get a random move from this piece.
            random = new Random().nextInt(moves.size());
            Position destination = moves.get(random);
            int destFile = destination.file;
            int destRank = destination.rank;
            if (canMovePiece(srcFile, srcRank, destFile, destRank, forWhitePlayer)){
                movePiece(srcFile, srcRank, destFile, destRank, forWhitePlayer);
                break; //terminating condition
            }
        }
    }


    /**
     * Checks if a move defined by a player is a valid move.
     * @param srcFile Source file
     * @param srcRank Source rank
     * @param destFile Destination file
     * @param destRank Destination rank
     * @param whitesTurn True if the source piece is white, false if it's black.
     * @return True if this move is valid. False elsewise.
     */
    public boolean canMovePiece(int srcFile, int srcRank, int destFile, int destRank, Boolean whitesTurn){

        // Get the piece the user wants to move.
        String playerColor = whitesTurn? "White" : "Black";
        Piece sourcePiece = board[srcRank][srcFile].piece;

        if (sourcePiece == null){
            //System.out.println("no piece to move");
            // Can't move a piece that isn't there!
            Log.d("Illegal", "1");
            return false;
        }
        else if (!sourcePiece.color.equals(playerColor)){
            //System.out.println("Cant move piece of another color");
            // Can't move a piece that isn't the player's color!
            Log.d("Illegal", "2");
            return false;
        }

        // Check if this piece is allowed to move to its destination.
        else if (srcFile == destFile && srcRank == destRank){
            //System.out.println("Cant move piece to same spot");
            // Can't move a piece to its current place!
            Log.d("Illegal", "3");
            return false;
        }
        else if (!sourcePiece.canReachDestination(this, new Position(destRank, destFile))){
            //System.out.println("Can't reach");
            Log.d("Illegal", "4");
            return false;
        }
        else if (canThreatenKing(srcRank,srcFile,destRank,destFile)){
            // The player cannot threaten its own king!
            //System.out.println("threatens king");
            Log.d("Illegal", "5");
            return false;
        }

        // Everything is valid!
        return true;
    }

    /**
     *
     * @param srcFile
     * @param srcRank
     * @param destFile
     * @param destRank
     */
    public void movePiece(int srcFile, int srcRank, int destFile, int destRank, Boolean whitesTurn){
        Cell sourceCell = board[srcRank][srcFile];
        Piece sourcePiece = sourceCell.piece;


        if(sourcePiece.name.equals("Pawn") && Pawn.enPassant(srcRank, destFile, prevMove)){ // Perform en passant:
            //remove the "captured piece"
            board[prevMove[1][1]][prevMove[1][0]].piece = null;
        } else if(sourcePiece.name.equals("King") && King.castling(srcFile, srcRank, destFile, destRank, whitesTurn, this)) { // Perform castling
            // move rook
            int rookSrcFile;
            int rookDestFile;
            if(srcFile - destFile > 0){
                rookSrcFile = 0;
                rookDestFile = 3;
            }else{
                rookSrcFile = 7;
                rookDestFile = 5;
            }
            Cell sourceRookCell = board[srcRank][rookSrcFile];
            Cell destRookCell = board[destRank][rookDestFile];
            Piece rookPiece = sourceRookCell.piece;

            sourceRookCell.piece = null;
            destRookCell.piece = rookPiece;
            rookPiece.setPosition(destRank, rookDestFile);
        }
        // Perform the actual move and capture.
        sourceCell.piece = null;
        pieces.remove(board[destRank][destFile].piece); // Capture?
        board[destRank][destFile].piece = sourcePiece;
        sourcePiece.setPosition(destRank,destFile);
        board[destRank][destFile].piece.setHasMoved(true);
        recordCurrentMove(srcFile, srcRank,destFile,destRank);

        // Update the threats array.
        findThreats();
        Log.d("Threats", threatsToString());

        // Update the status of the king
        if (isKingInCheckmate(whitesTurn) || isKingInCheckmate(!whitesTurn)){
            System.out.println("Checkmate");
            return;
        }
        if (isKingInCheck(whitesTurn) || isKingInCheck(!whitesTurn)){
            System.out.println("Check");
        }


        //Log.d("STATES BEFORE MOVE", states.statesToString());

        // Add the state to the states.
        String title = whitesTurn? "White's turn" : "Black's turn";
        states.addState(board, pieces, turn, title);
        //Log.d("STATES AFTER MOVE", states.statesToString());
        turn++;
    }


    /**
     * Get the previous move done.
     * @return The previous move, or null, if there aren't any previous moves.
     */
    public int[][] getPrevMove(){
        return prevMove;
    }



    /**
     * Iterate through the board and pieces to find all cells that are under threat by another piece.
     * Populates the cellsThreatenedByWhite and the cellsThreatenedByBlack, which are mainly
     * used to see if the king is in check or in checkmate.
     */
    private void findThreats(){

        // Iterate through the board and set all cells to false
        for (int rank = 0; rank < BOARD_SIZE; rank++){
            for (int file = 0; file < BOARD_SIZE; file++){
                cellsThreatenedByWhite[rank][file] = false;
                cellsThreatenedByBlack[rank][file] = false;
            }
        }

        // Look at all the current pieces on the pieces list
        for (Piece piece: pieces){
            if(piece == null) continue;
            // Get all this piece's possible movements
            //System.out.println("Threats for:" + piece);
            List<Position> movements = piece.getAllMoves(this);
            //System.out.println(movements);

            if (movements == null){
                continue;
            }

            // Mark the cellsThreatenedBy according to these positions
            for (Position move : movements){
                //System.out.println(move);
                // test
                if (move.file == 0 && move.rank == 2){
                    //Log.d("This piece is making the threat:", piece + "");
                }
                if (piece.color.equals("White")){
                    if (!cellsThreatenedByWhite[move.rank][move.file]){
                        cellsThreatenedByWhite[move.rank][move.file] = true;
                    }
                }
                else{
                    if (!cellsThreatenedByBlack[move.rank][move.file]){
                        cellsThreatenedByBlack[move.rank][move.file] = true;
                    }
                }
            }
        }


        // Done!
        //System.out.println(threatsToString());
    }


    /**
     * Check if the given move will threaten the king and put it in check. This method assumes that
     * the given move is already valid.
     * @param srcRank Source rank.
     * @param srcFile Source file.
     * @param destRank Destination rank.
     * @param destFile Destination file.
     * @return True if the king will be in check, false elsewise.
     */
    private boolean canThreatenKing(int srcRank, int srcFile, int destRank, int destFile){

        Cell srcCell = board[srcRank][srcFile];
        Cell destCell = board[destRank][destFile];
        Piece srcPiece = srcCell.piece;
        Piece destPiece = destCell.piece;

        // If this piece is a king, see if this move will threaten itself.
        boolean isWhite = srcPiece.color.equals("White");
        if (srcPiece.name.equals("King") && underThreat(isWhite, destRank, destFile)){
            // The king is about to put itself under threat!
            return true;
        }

        // If this isn't a king, pretend to make the move to see if it'll threaten the king.
        destCell.piece = srcPiece;
        srcPiece.setPosition(destRank, destFile);
        pieces.remove(destPiece);
        srcCell.piece = null;
        //System.out.println("IMAGING MOVE");
        findThreats();
        //System.out.println(threatsToString());

        boolean result = isKingInCheck(isWhite);

        // Undo the move.
        srcCell.piece = srcPiece;
        srcPiece.setPosition(srcRank,srcFile);
        destCell.piece = destPiece;
        pieces.add(destPiece);
        findThreats();
        isKingInCheck(isWhite);

        return result;
    }


    /**
     * Check if a certain cell on the board is under threat by a white piece or a black piece.
     * @param isWhite true if the king we're examining is white. False if i's black.
     * @param rank Rank of the cell in question.
     * @param file File of the cell in question.
     * @return True if the cell is under threat, false elsewise.
     */
    public boolean underThreat(boolean isWhite, int rank, int file){

        if (!isWhite){
            return cellsThreatenedByWhite[rank][file];
        }
        else{
            return cellsThreatenedByBlack[rank][file];
        }

    }

    /**
     * Get the king of the specified color in the pieces array.
     * @param isWhite Color of the king we'd like to get.
     * @return the King Piece found, or null if none found.
     */
    private Piece getKing(boolean isWhite){
        String color = isWhite? "White" : "Black";
        // Locate the king of the given color in the pieces array.
        for (Piece piece: pieces){
            if (piece == null) continue;
            if (piece.name.equals("King") && piece.color.equals(color)){
                return piece;
            }
        }
        return null;
    }

    /**
     * Get Piece by file and rank
     * @param file
     * @param rank
     * @return null if there is no piece at file rank
     */
    public Piece getPieceByFileRank(int file, int rank){
        return board[rank][file].piece;
    }

    /**
     * Check if the king of the given color is in check, meaning that it is on a cell that is threatened
     * by a piece of the opposing color.
     * Note that this result will alter what valid moves can be made by the player.
     * @param isWhite Color of the king we'd like to examine.
     * @return true if the king is in check, false elsewise.
     */
    public boolean isKingInCheck(boolean isWhite){

        // Get the king in question and find out if it is on cell that is threatened.
        Piece king = getKing(isWhite);
        if (king == null){
            return false;
        }
        boolean inCheck = underThreat(isWhite, king.getPosition().rank, king.getPosition().file);

        // Set the cooresponding variable.
        if (isWhite){
            whiteInCheck = inCheck;
        }
        else{
            blackInCheck = inCheck;
        }

        return inCheck;
    }

    /**
     * Check if the king of a given color is in checkmate, meaning that it is not only threatened
     * by a piece of the opposing color, but it also cannot move to a cell that is not threatened.
     * Note that checkmate will end the game.
     * @param isWhite True if the king we are looking at is white. Else, false if it's black.
     * @return True if the king is in checkmate, false elsewise.
     */
    private boolean isKingInCheckmate(boolean isWhite){

        // Find out if the king is in check.
        if (!isKingInCheck(isWhite)){
            // The king is still safe!
            return false;
        }
        // The king is threatened! See if it can move to any of its surrounding cells (this is it's valid movements).
        Piece king = getKing(isWhite);
        String color = isWhite? "White" : "Black";
        // Iterate!
        List<Position> moves = king.getAllMoves(this);
        if (moves != null){
            for (Position move: moves){
                if (!underThreat(isWhite, move.rank, move.file)){
                    // Double check if the king can move to this cell.
                    Cell cell = board[move.rank][move.file];
                    if (cell.piece != null){
                        if (!cell.piece.color.equals(color)){
                            // The king can actual move here through capture!
                            return false;
                        }
                        else{
                            continue;
                        }
                    }else{
                        // The king can move here!
                        return false;
                    }
                }
                else{

                }
            }
        }
        // The king cannot move anywhere to be safe!
        if(isWhite){
            whiteInCheckmate = true;
        }
        else{
            blackInCheckmate = true;
        }
        return true;

    }



    /**
     * A valid promotion:
     * 1. Piece is pawn
     * 2. Destination rank is 0 for white and 7 for black
     * 3. Promotion type is either: Queen, Rook, Bishop or Knight
     */
    public boolean canPromote(int srcFile, int srcRank, int destFile, int destRank, boolean whitesTurn, char promotion){
        Piece piece = board[srcRank][srcFile].piece;
        Boolean isPiecePawn = piece.name.equals("Pawn");
        Boolean isRankPromotable = whitesTurn? destRank == 0 : destRank == 7;
        Boolean isValidType = promotion == 'Q' || promotion == 'R' || promotion =='B' || promotion =='N';
        return isPiecePawn && isRankPromotable && isValidType;
    }

    /**
     * Promote a Pawn to another type of piece.
     * @param srcRank Current rank of the pawn.
     * @param srcFile Current file of the pawn.
     * @param whitesTurn If it's currently whitesTurn.
     * @param promotion Char representing the type of piece that this pawn will promote to.
     */
    public void promote(int srcRank, int srcFile, boolean whitesTurn, Character promotion){

        // Setups.
        String playerColor = whitesTurn? "White" : "Black";
        Piece piece = board[srcRank][srcFile].piece;

        // Make the promotion.
        if (promotion == 'Q'){
            piece = new Queen(playerColor, new Position(srcRank, srcFile));
        }
        else if (promotion == 'R'){
            piece = new Rook(playerColor, new Position(srcRank, srcFile));
        }
        else if (promotion == 'B'){
            piece = new Bishop(playerColor, new Position(srcRank, srcFile));
        }
        else if (promotion == 'N'){
            piece = new Knight(playerColor, new Position(srcRank, srcFile));
        }

        // Set this piece to the board.
        board[srcRank][srcFile].piece = piece;
    }


    /**
     * Records the current move for the next turn.
     * @param srcFile Source file of the move.
     * @param srcRank Source rank of the move.
     * @param destFile Destination file of the move.
     * @param destRank Destination rank of the move.
     */
    public void recordCurrentMove(int srcFile, int srcRank, int destFile, int destRank){
        // store the current move for next turn - might need to move this logic to app.
        this.prevMove[0][0] = srcFile;
        prevMove[0][1] = srcRank;
        prevMove[1][0] = destFile;
        prevMove[1][1] = destRank;
    }


    /**
     * Express the board as String with the format provided from the prompt.
     * @return String of the board.
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col++){
                sb.append(board[row][col].toString() + " ");
            }
            sb.append(BOARD_SIZE - row);
            sb.append(System.getProperty("line.separator"));
        }
        sb.append(" a  b  c  d  e  f  g  h");
        return sb.toString();
    }

    /**
     * Express the cellsThreatenedBy arrays as strings to print out to the console.
     * Used for debugging and testing only.
     * @return cellsThreatenedByWhite and cellsThreatenedByBlack as strings.
     */
    public String threatsToString(){
        String s ="";
        s = "Cells threatened by white:" + System.lineSeparator();
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                if (cellsThreatenedByWhite[i][j]){
                    s += " T ";
                }
                else{
                    s += " f ";
                }
            }
            s += System.lineSeparator();
        }

        s += "Cells threatened by black:";
        s += System.lineSeparator();
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                if (cellsThreatenedByBlack[i][j]){
                    s += " T ";
                }
                else{
                    s += " f ";
                }
            }
            s += System.lineSeparator();
        }
        return s;
    }

    public static enum BoardStatus{
        WHITEINCHECK,
        WHITEINCHECKMATE,
        BLACKINCHECK,
        BLACKINCHECKMATE,
        NOCHECKS
    }

    /**
     * Implementation of an individual cell on the board.
     * @author Krysti Leong, April Dizon
     */
    public static class Cell implements Serializable {
        public Position position;
        public Piece piece;
        public boolean isColored;

        /**
         * Constructor for a Cell.
         * @param position Position of this cell in the Board.
         * @param piece Piece on this cell. Null if there is none.
         * @param isColored Color of this cell.
         */
        public Cell(Position position, Piece piece, boolean isColored){
            this.position = position;
            this.piece = piece;
            this.isColored = isColored;
        }

        /**
         * Constructor for a Cell.
         * @param position Position of this cell in the Board.
         * @param isColored Color of this cell.
         */
        public Cell(Position position, boolean isColored){
            this(position,null, isColored);
        }

        @Override
        public String toString() {
            if(piece != null) return piece.toString();
            return isColored? "##" : "  ";
        }
    }
}
