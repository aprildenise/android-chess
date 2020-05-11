package com.example.android.model;

import com.example.android.Board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a chess Piece.
 * @author Krysti Leong, April Dizon
 */
public abstract class Piece implements Serializable {


    public static final long serialVersionUID = GameSaver.serialVersionUID;
    public String name;
    public String color;
    public boolean hasMoved;
    private Position position;
    private List<Position> directionVectors = null;
    private int imageResource;

    /**
     * Constructor for Piece
     * @param name Type of the piece, ie. "King", "Queen", etc.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public Piece(String name, String color, Position position){
        this.name = name;
        this.color = color;
        this.hasMoved = false;
        this.position = position;
    }

    protected void setImageResource(int imageResourceID){
        this.imageResource = imageResourceID;
    }

    public int getImageResource(){
        return imageResource;
    }

    /**
     * @return This piece's current position.
     */
    public Position getPosition(){
        return position;
    }

    /**
     * set hasMoved to the boolean value
     * @param hasMoved
     */
    public void setHasMoved(boolean hasMoved){
        this.hasMoved = hasMoved;
    }
    /**
     * Set this piece's current position.
     * @param p Position representing the current position.
     */
    public void setPosition(Position p){
        this.position = p;
    }

    /**
     * Set this piece's current position.
     * @param rank Rank of the current position.
     * @param file File of the current position.
     */
    public void setPosition(int rank, int file){
        setPosition(new Position(rank,file));
    }

    /**
     * @return This piece's color.
     */
    public String getColor(){
        return color;
    }

    /**
     * Get this piece's direction vectors, which are vectors representing this piece's movement paths.
     * These vectors are added to the piece through the children's constructors.
     * @return List of the vectors, or null if there are no vectors.
     */
    public List<Position> getDirectionVectors(){
        return directionVectors;
    }

    /**
     * Set this piece's direction vectors.
     * @param directions List of the vectors.
     */
    public void setDirectionVectors(List<Position> directions){
        directionVectors = directions;
    }

    /**
     * See if this piece can reach its destination from its current position by checking the following positions:
     * 1. The path to the destination from its current position is in its valid movements.
     * 2. The path is clear of any other pieces.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean canReachDestination(Board chessBoard, Position destination){
        if (!isValidMovement(chessBoard, destination)){
            return false;
        }
        Position obstacle = isPathClear(chessBoard, destination);
        if (obstacle != null){
            Board.Cell[][] cells = chessBoard.getCells();
            // This piece can reach the destination if the there's an obstacle, ONLY
            // if the obstacle is at the destination, and its the opposing color
            if (destination.equals(obstacle)){
                if (cells[obstacle.rank][obstacle.file].piece.color.equals(getColor())){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the path to the destination from its current position is in its valid movements.
     * Each movement will depend on the piece implementing it.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public abstract boolean isValidMovement(Board chessBoard, Position destination);


    /**
     * Check if the path to the destination is clear of any obstacles (other chess pieces).
     * Note that this also checks if the destination is clear or capturable.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    public abstract Position isPathClear(Board chessBoard, Position destination);

    /**
     * Get all the Positions this piece can move to, given its current position.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public abstract List<Position> getAllMoves(Board chessBoard);

    /**
     * Used by a piece's getAllMoves(). Use the piece's vectors, and find all the moves it can move to
     * by simply checking if the position at (current position + vector) has an obstacle.
     * All moves that are free of obstacles are added to the returned list.
     * @param chessBoard Board of the game.
     * @return List of Positions that this piece can move to.
     */
    protected List<Position> getAllDiscreteMoves(Board chessBoard){

        List<Position> moves = new ArrayList<Position>();
        List<Position> directions = getDirectionVectors();

        // Iterate through this piece's directions.
        for (Position direction : directions){
            Position destination = Position.add(getPosition(), direction);
            // Make sure not to go beyond the board.
            if (!Position.withinBounds(destination)){
                continue;
            }

            // If there are no obstacles, add this to the moves!
            Position obstacle = isPathClear(chessBoard, destination);
            if (obstacle == null){
                moves.add(destination);
            }
            else{
                moves.add(destination);
                // If there's an obstacle, then you can move to this position
                // only if capture is possible
//                Board.Cell[][] cells = chessBoard.getCells();
//                Piece obstaclePiece = cells[obstacle.rank][obstacle.file].piece;
//                if (obstaclePiece != null){
//                    if (!obstaclePiece.color.equals(getColor())){
//                        moves.add(destination);
//                    }
//                }
            }
        }

        // We're done!
        if (moves.size() == 0){
            return null;
        }
        else{
            return moves;
        }
    }

    /**
     * Used by a piece's getAllMoves. Use the piece's vectors, and find all the positions it can
     * move to by first finding the furthest positions on the board this piece can get to, depending
     * on its current position, then checking if there are any obstacles in between the current position
     * and the furthest position. All positions free of obstacles are returned in this list.
     * @param chessBoard Board of the game.
     * @return List of Positions that this piece can move to.
     */
    protected List<Position> getAllContinuousMoves(Board chessBoard){

        // Setup
        Position source = getPosition();
        Board.Cell[][] board = chessBoard.getBoard();
        List<Position> movements = new ArrayList<Position>();
        List<Position> directions = getDirectionVectors();

        // Find the furthest cells that this piece can travel to and add them to a list.
        List<Position> bounds = new ArrayList<Position>();
        for (Position direction : directions){
            bounds.add(Position.getMaxDistance(direction, source));
        }

        // Attempt to travel to these positions.
        for (int i = 0; i < bounds.size(); i++){

            // Check if the path is clear between this max position.
            Position obstacle = isPathClear(chessBoard, bounds.get(i));
            List<Position> newMovements;

            // If it's not clear, then add the positions that are between the source, up to this obstacle.
            if (obstacle != null){
                newMovements = source.getPositionsBetween(directions.get(i), obstacle);
                movements.add(obstacle);
                // Depending on the color of the obstacle, this piece can still travel to the obstacle's position
                // It cannot move any further though.
//                if (!board[obstacle.rank][obstacle.file].piece.color.equals(getColor())){
//                    movements.add(obstacle);
//                }
            }
            // Else, add all the movements between the source and the max position.
            else{
                newMovements = source.getPositionsBetweenMax(directions.get(i));
            }
            movements.addAll(newMovements);
        }

        // If no movements are found, return null.
        if (movements.size() == 0){
            return null;
        }
        else{
            return movements;
        }
    }

    /**
     * Used by a piece's isPathClear(). Find if the path between this piece's current position and the
     * destination by checking each position between the current and the destination, as well as the
     * destination. Note that a piece at the destination is not an obstacle if it has the opposing color.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    protected Position isContinuousPathClear(Board chessBoard, Position destination){

        // Create a vector to express the direction from the starting position to the destination
        Board.Cell[][] board = chessBoard.getBoard();
        Position source = getPosition();
        Position distance = Position.distance(source, destination);
        int rank = 0;
        int file = 0;
        if (distance.rank < 0){
            rank = 1;
        }
        else if (distance.rank > 0){
            rank = -1;
        }

        if (distance.file < 0){
            file = 1;
        }
        else if (distance.file > 0){
            file = -1;
        }

        // Use the vector to iterate to the destination
        int obstacles = 0;
        Position direction = new Position(rank, file);
        Position bounds = Position.add(destination, direction);
        for (Position i = Position.add(direction, source); (!Position.equals(i, bounds) && Position.withinBounds(i)); i = Position.add(i, direction)){
            Board.Cell cell = board[i.rank][i.file];
            if (cell.piece != null){
                // There is something in the way!
                return i;
//                if (!cell.piece.color.equals(getColor()) && obstacles != 1){
//                    // If there's an obstacle of the opposing color, and it's the first obstacle, this piece
//                    // can still travel to this spot, because this spot can be captured.
//                    obstacles++;
//                    continue;
//                }
//                else{
//                    return i;
//                }
            }
        }
        return null;
    }

    /**
     * Used by a piece's isPathClear(). Find if this piece can reach its destination by simply checking if the
     * destination is clear of any pieces of the same color.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    protected Position isDiscretePathClear(Board chessBoard, Position destination){
        Board.Cell[][] board = chessBoard.getBoard();
        Piece piece = board[destination.rank][destination.file].piece;
        if (piece != null){
            return destination;
            // If this piece has the same color, then it is an obstacle.
//            if (piece.color.equals(getColor())){
//                return destination;
//            }
        }
        return null;
    }



    @Override
    public String toString() {
        return "" + Character.toLowerCase(this.color.charAt(0)) + (this.name=="Knight"? "N" : this.name.charAt(0));
    }

}
