package com.example.android.model;

import com.example.android.Board;
import com.example.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the King chess piece.
 * @author Krysti Leong, April Dizon
 */
public class Bishop extends Piece {

    /**
     * Constructor for the Bishop. Sets the color, position, and direction vectors, which are
     * vectors that represent all the diagonals on the board.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public Bishop(String color, Position position){
        super("Bishop", color, position);

        List<Position> directions = new ArrayList<Position>();
        directions.add(new Position(-1, 1));
        directions.add(new Position(-1, -1));
        directions.add(new Position(1,1));
        directions.add(new Position(1, -1));

        if (color.equals("White")){
            setImageResource(R.mipmap.white_bishop_foreground);
        }
        else{
            setImageResource(R.mipmap.black_bishop_foreground);
        }
        setDirectionVectors(directions);
    }


    /**
     * Use the isContinuousPathClear for the Bishop to check if the path is clear.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    public Position isPathClear(Board chessBoard, Position destination){
        return super.isContinuousPathClear(chessBoard, destination);
    }

    /**
     * Use the getAllContinuousMoves for the Bishop to get all its moves.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public List<Position> getAllMoves(Board chessBoard){
        return getAllContinuousMoves(chessBoard);
    }


    /**
     * Check if this destination defines valid movement, which is on the diagonal.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean isValidMovement(Board chessBoard, Position destination){
        Position source = getPosition();
        return Math.abs(source.compareRank(destination)) == Math.abs(source.compareFile(destination));
    }

}
