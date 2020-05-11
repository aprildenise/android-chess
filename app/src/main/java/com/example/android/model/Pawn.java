package com.example.android.model;

import com.example.android.Board;
import com.example.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Pawn chess piece.
 * @author Krysti Leong, April Dizon
 */
public class Pawn extends Piece {

    /**
     * Constructor for the Knight. Sets the color, position, and direction vectors, which are 1
     * space forward, 2 spaces forward, and 1 space diagonal. The sign of the vectors depend on its color.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public Pawn(String color, Position position){
        super("Pawn", color, position);

        List<Position> directions = new ArrayList<Position>();

        if (color.equals("White")){
            directions.add(new Position(-2,0));
            directions.add(new Position(-1, 0));
            directions.add(new Position(-1, 1));
            directions.add(new Position(-1, -1));
            setImageResource(R.mipmap.white_pawn_foreground);
        }
        else{
            directions.add(new Position(2,0));
            directions.add(new Position(1, 0));
            directions.add(new Position(1, -1));
            directions.add(new Position(1,1));
            setImageResource(R.mipmap.black_pawn_foreground);
        }
        setDirectionVectors(directions);

    }

    /**
     * Check if this destination defines valid movement, which can be 2 steps forward if it
     * hasn't moved yet, 1 step forward, or diagonal through en passant.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean isValidMovement(Board chessBoard, Position destination){
        int srcRank = getPosition().rank;
        int srcFile = getPosition().file;
        int destRank = destination.rank;
        int destFile = destination.file;
        int[][] prevMove = chessBoard.getPrevMove();

        int multiplier = getColor().equals("White")? 1 : -1;
        int verticalDistance = multiplier * (srcRank - destRank);
        int horizontalDistance =  multiplier * (srcFile - destFile);
        if(verticalDistance > 2){
            //Not capable of moving more than two ranks
            return false;
        }
        if(verticalDistance == 2){
            //Not capable of moving two ranks if pawn is not at its original rank
            if(getColor().equals("White") && srcRank != 6){
                return false;
            }
            if(getColor().equals("Black") && srcRank != 1){
                return false;
            }
        }
        if(horizontalDistance > 1){
            //Not capable of moving more than one file (only applies with en passant)
            return false;
        }
        Piece destPiece = chessBoard.getPieceByFileRank(destFile, destRank);
        if(Math.abs(horizontalDistance) == 1 && verticalDistance == 1){
            //Moving diagonally
            if(destPiece != null && !destPiece.color.equals(getColor())){
                //Check if destination is an opponent's piece
                return true;
            }
            if(!enPassant(srcRank, destFile, prevMove)){
                return false;
            }
        }
        //Check if destination cell contains a piece
        if(destPiece != null){
            return false;
        }

        return true;
    }

    /**
     * Check if the path is clear by checking if the move is valid through
     * isValidMovement().
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public Position isPathClear(Board chessBoard, Position destination){

        // Check if the pawn can move to its destination, using the isValidMove().
        if (isValidMovement(chessBoard, destination)){
            return null;
        }
        else{
            return destination;
        }

    }

    /**
     * Use the getAllDiscreteMoves for the Pawn to get all its moves.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public List<Position> getAllMoves(Board chessBoard){
        return getAllDiscreteMoves(chessBoard);
    }

    /**
     *
     * @param srcRank Rank of the moving piece.
     * @param destFile File of the destination.
     * @param prevMove A record of the previous move from the other player.
     *                 2D int array
     *                 [0][0] = source file
     *                 [0][1] = source rank
     *                 [1][0] = destination file
     *                 [1][1] = destination rank
     * @return true if valid en passant move
     */
    public static boolean enPassant(int srcRank, int destFile, int[][] prevMove) {
        Boolean fromSameRank = srcRank == prevMove[1][1];
        Boolean toSameFile = destFile == prevMove[1][0];
        Boolean prevMovedTwoRanks = Math.abs(prevMove[0][1] - prevMove[1][1]) == 2;
        return fromSameRank && toSameFile && prevMovedTwoRanks;
    }

}
