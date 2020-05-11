package com.example.android.model;

import com.example.android.Board;
import com.example.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Queen chess piece.
 * @author Krysti Leong, April Dizon
 */
public class Queen extends Piece {

    /**
     * Constructor for the Queen. Sets the color, position, and direction vectors, which are
     * vectors that represent all the diagonals, verticals, and horizontals on the board.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public Queen(String color, Position position){
        super("Queen", color, position);

        List<Position> directions = new ArrayList<Position>();
        directions.add(new Position(0,1));
        directions.add(new Position(1, 0));
        directions.add(new Position(0, -1));
        directions.add(new Position(-1, 0));
        directions.add(new Position(-1, 1));
        directions.add(new Position(-1, -1));
        directions.add(new Position(1,1));
        directions.add(new Position(1, -1));
        setDirectionVectors(directions);

        if (color.equals("White")){
            setImageResource(R.mipmap.white_queen_foreground);
        }
        else {
            setImageResource(R.mipmap.black_queen_foreground);
        }
    }

    /**
     * Check if this destination defines valid movement, which are on the diagonals,
     * verticals and horizontals.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean isValidMovement(Board chessBoard, Position destination){
        Position distance = Position.manhattanDistance(getPosition(), destination);
        int compareRank = getPosition().compareRank(destination);
        int compareFile = getPosition().compareFile(destination);
        return (distance.rank == distance.file) || compareRank == 0 || compareFile == 0;
    }

    /**
     * Use the isContinuousPathClear for the Queen to check if the path is clear.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    public Position isPathClear(Board chessBoard, Position destination){
        return super.isContinuousPathClear(chessBoard, destination);
    }

    /**
     * Use the getAllContinuousMoves for the Queen to get all its moves.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public List<Position> getAllMoves(Board chessBoard){
        return super.getAllContinuousMoves(chessBoard);
    }

}
