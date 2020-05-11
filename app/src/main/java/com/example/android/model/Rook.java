package com.example.android.model;

import com.example.android.Board;
import com.example.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Rook chess piece.
 * @author Krysti Leong, April Dizon
 */
public class Rook extends Piece {

    /**
     * Constructor for the Rook. Sets the color, position, and direction vectors, which are
     * vectors that represent all the verticals and horizontals on the board.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public Rook(String color, Position position){
        super("Rook", color, position);

        List<Position> directions = new ArrayList<Position>();
        directions.add(new Position(0,1));
        directions.add(new Position(1, 0));
        directions.add(new Position(0, -1));
        directions.add(new Position(-1, 0));
        setDirectionVectors(directions);

        if (color.equals("White")){
            setImageResource(R.mipmap.white_rook_foreground);
        }
        else{
            setImageResource(R.mipmap.black_rook_foreground);
        }
    }

    /**
     * Check if this destination defines valid movement, which is on the vertical or horizontal
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean isValidMovement(Board chessBoard, Position destination){
        return (destination.file == getPosition().file || destination.rank == getPosition().rank);
    }


    /**
     * Use the isContinuousPathClear for the Rook to check if the path is clear.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    public Position isPathClear(Board chessBoard, Position destination){
        return isContinuousPathClear(chessBoard, destination);
    }

    /**
     * Use the getAllContinuousMoves for the Rook to get all its moves.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public List<Position> getAllMoves(Board chessBoard){
        return getAllContinuousMoves(chessBoard);
    }

}
