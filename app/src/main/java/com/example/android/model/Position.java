package com.example.android.model;


import com.example.android.Board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of coordinates on the chess board, called Position.
 * @author Krysti Leong, April Dizon
 */
public class Position implements Serializable {

    public static final long serialVersionUID = GameSaver.serialVersionUID;
    public int rank;
    public int file;
    public static final int MAXRANK = Board.BOARD_SIZE - 1;
    public static final int MAXFILE = Board.BOARD_SIZE - 1;
    public static final int MINRANK = 0;
    public static final int MINFILE = 0;

    /**
     * Constructor for a Position.
     * @param rank Rank of this position, which is the row.
     * @param file File of this position, which is the file.
     */
    public Position(int rank, int file){
        this.rank = rank;
        this.file = file;
    }

    /**
     * Compare this Position's rank to another's.
     * @param compareTo Position to compare to.
     * @return int representing the comparision. It is 0 if they are equal, less than 0 if this
     * Position has a smaller rank, and greater than 0 if compareTo has a smaller rank.
     */
    public int compareRank(Position compareTo){
        return this.rank - compareTo.rank;
    }

    /**
     * Compare this Position's file to another's.
     * @param compareTo Position to compare to.
     * @return int representing the comparision. It is 0 if they are equal, less than 0 if this
     * Position has a smaller file, and greater than 0 if compareTo has a smaller file.
     */
    public int compareFile(Position compareTo){
        return this.file - compareTo.file;
    }

    /**
     * Get the manhattan distance from position1 to position2.
     * @param position1 Position
     * @param position2 Position
     * @return The manhattan distance.
     */
    public static Position manhattanDistance(Position position1, Position position2){
        int rank = Math.abs(position1.rank - position2.rank);
        int file = Math.abs(position1.file - position2.file);
        return new Position(rank, file);
    }

    /**
     * Get the distance from position1 to position2.
     * @param position1 Position
     * @param position2 Position
     * @return The distance.
     */
    public static Position distance(Position position1, Position position2){
        int rank = (position1.rank - position2.rank);
        int file = (position1.file - position2.file);
        return new Position(rank, file);
    }

    /**
     * Adds two Positions.
     * @param position1 First addend Position.
     * @param position2 Second addend Position.
     * @return Sum Position.
     */
    public static Position add(Position position1, Position position2){
        int rank = position1.rank + position2.rank;
        int file = position1.file + position2.file;
        return new Position(rank, file);
    }

    /**
     * Checks if the given two Positions have the same rank and the same file.
     * @param position1 Position.
     * @param position2 Position.
     * @return True if they are equal, false if they are not.
     */
    public static boolean equals(Position position1, Position position2){
        return (position1.rank == position2.rank) && (position1.file == position2.file);
    }

    /**
     * Checks if the given Position is within the bounds defined by MAXRANK, MINRANK,
     * MAXFILE, MINFILE, which is the bounds of the chess board.
     * @param position Position in question.
     * @return True if the position is within the bounds, false elsewise.
     */
    public static boolean withinBounds(Position position){
        return (position.rank <= MAXRANK && position.rank >= MINRANK) && (position.file <= MAXFILE && position.file >= MINFILE);
    }


    /**
     * Add the given distance vector to this position to find the furthest position
     * from the source without going over the Position's min and max bounds.
     * @param direction Position representing the direction vector.
     * @return Max position.
     */
    public static Position getMaxDistance(Position direction, Position source){

        Position max = source;
        Position iteration = source;

        // Iterate through to find the max position.
        while(withinBounds(iteration)){
            max = iteration;
            iteration = add(iteration, direction);
        }
        return max;

    }

    /**
     * Add the given direction vector to this position to find all the positions between
     * this position and the furthest position it can reach without going over the bounds.
     * @param direction Position representing the direction vector.
     * @return List of positions found.
     */
    public List<Position> getPositionsBetweenMax(Position direction){

        List<Position> positions = new ArrayList<Position>();

        Position iteration = new Position(this.rank, this.file);
        while (withinBounds(iteration)){
            iteration = add(iteration, direction);
            if (withinBounds(iteration)){
                positions.add(iteration);
            }
        }
        return positions;
    }

    /**
     * Add the given direction vector to this position to find all positions
     * between this position and the given bound.
     * @param direction Position representing the direction vector.
     * @param bound Position representing the for this search.
     * @return List of positions found.
     */
    public List<Position> getPositionsBetween(Position direction, Position bound){

        List<Position> positions = new ArrayList<Position>();

        Position iteration = new Position(this.rank, this.file);
        iteration = add(iteration, direction);
        while (withinBounds(iteration) && !equals(bound, iteration)){
            positions.add(iteration);
            iteration = add(iteration, direction);
        }

        return positions;

    }

    public String toString(){
        return "(" + rank + "," + file + ")";
    }


}
