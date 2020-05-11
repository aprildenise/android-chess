package com.example.android.model;

import com.example.android.Board;
import com.example.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Knight chess piece.
 * @author Krysti Leong, April Dizon
 */
public class Knight extends Piece {

    /**
     * Constructor for the Knight. Sets the color, position, and direction vectors, which are
     * vectors that represent all the "L" shaped moves on the board.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public Knight(String color, Position position){
        super("Knight", color, position);

        List<Position> directions = new ArrayList<Position>();
        directions.add(new Position(1,-2));
        directions.add(new Position(2,-1));
        directions.add(new Position(2,1));
        directions.add(new Position(1,2));
        directions.add(new Position(-1,-2));
        directions.add(new Position(-2,-1));
        directions.add(new Position(-1,2));
        directions.add(new Position(-2,1));
        setDirectionVectors(directions);

        if (color.equals("White")){
            setImageResource(R.mipmap.white_knight_foreground);
        }
        else{
            setImageResource(R.mipmap.black_knight_foreground);
        }

    }

    /**
     * Check if this destination defines valid movement, which are on the "L" shaped slopes.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean isValidMovement(Board chessBoard, Position destination){
        Position distance = Position.manhattanDistance(getPosition(), destination);
        Boolean slope1 = distance.file == 2 && distance.rank == 1;
        Boolean slope2 = distance.file == 1 && distance.rank == 2;
        return slope1 || slope2;
    }

    /**
     * Use the isDiscretePathClear for the Knight to check if the path is clear.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    public Position isPathClear(Board chessBoard, Position destination){
        return isDiscretePathClear(chessBoard, destination);
    }

    /**
     * Use the getAllDiscreteMoves for the Queen to get all its moves.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public List<Position> getAllMoves(Board chessBoard) {
        return getAllDiscreteMoves(chessBoard);
    }


}
