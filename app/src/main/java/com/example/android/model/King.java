package com.example.android.model;

import com.example.android.Board;
import com.example.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the King chess piece.
 * @author Krysti Leong, April Dizon
 */
public class King extends Piece {

    /**
     * Constructor for the Knight. Sets the color, position, and direction vectors, which are one
     * space forward and backward on the diagonals, verticals, and horizontals.
     * @param color Color of the piece, either "White" or "Black."
     * @param position Current position of this piece.
     */
    public King(String color, Position position){
        super("King", color, position);

        List<Position> directions = new ArrayList<Position>();
        directions.add(new Position(0,1));
        directions.add(new Position(1, 0));
        directions.add(new Position(0, -1));
        directions.add(new Position(-1, 0));
        directions.add(new Position(-1, 1));
        directions.add(new Position(-1, -1));
        directions.add(new Position(1,1));
        directions.add(new Position(1, -1));

        if (color.equals("White")){
            setImageResource(R.mipmap.white_king_foreground);
        }
        else{
            setImageResource(R.mipmap.black_king_foreground);
        }

        setDirectionVectors(directions);
    }

    /**
     * Check if this destination defines valid movement, which are one
     * space forward and backward on the diagonals, verticals, and horizontals.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return True if this piece can reach the destination, false if it cannot.
     */
    public boolean isValidMovement(Board chessBoard, Position destination){
        Position source = getPosition();
        Position distance = Position.manhattanDistance(source, destination);

        Boolean diagonal = (distance.file == 1) && (distance.rank == 1);
        Boolean vertical = (distance.file == 0) && (distance.rank == 1);
        Boolean horizontal = (distance.file == 1) && (distance.rank) == 0;

        return diagonal || vertical || horizontal || castling(source.file, source.rank, destination.file, destination.rank, getColor().equals("White"), chessBoard);
    }


    /**
     * Method checks if we are performing a valid castling move.
     * Our logic for castling is as follows:
     * 1. A valid castling is when the current player's king has not been moved nor did the rook that the king is heading towards.
     * 2. no pieces between king and rook
     * 3. king cannot be in check. the two squares in the direction the king is moving to cannot in check
     * @param srcFile
     * @param srcRank
     * @param destFile
     * @param destRank
     * @param whitesTurn
     * @param boardInstance
     * @return
     */
    public static boolean castling(int srcFile, int srcRank, int destFile, int destRank, Boolean whitesTurn, Board boardInstance) {
        int dx = srcFile - destFile;
        int dy = srcRank - destRank;

        //Castling happens when we are moving king two ranks left or right
        if(dy != 0 || Math.abs(dx) != 2) return false;

        // Checking condition 1
        int rookFile = srcFile - destFile > 0 ? 0 : 7; //Left Rook has file = 0 and Right Rook has file = 7
        int playerRank = whitesTurn? 7: 0;
        String color = whitesTurn? "White": "Black";
        Piece currRook = boardInstance.getPieceByFileRank(rookFile, playerRank); //expected Rook's location
        Piece currKing = boardInstance.getPieceByFileRank(4, playerRank); //expected King's location

        Boolean currKingIsValid = currKing != null && currKing.name == "King" && currKing.color.equals(color) && !currKing.hasMoved;
        Boolean currRookIsValid = currRook != null && currRook.name == "Rook" && currRook.color.equals(color) && !currRook.hasMoved;

        // Checking condition 2 && 3
        if(boardInstance.isKingInCheck(whitesTurn)) return false; //TODO: this should work after merging with April's latest change
        int dir = srcFile - destFile > 0 ? -1 : 1; // Left when srcFile is greater than destFile and vice versa
        for(int x = 1; x < dx; x++){

            // TODO: check castling test case 2 for underThreat after April's latest change
            if( (boardInstance.getPieceByFileRank(srcFile+(dir*x), srcRank) != null) || (boardInstance.underThreat(whitesTurn, srcRank, srcFile+(dir*x))))
                return false;
        }
        return currKingIsValid && currRookIsValid;
    }

    /**
     * Use the isDiscretePathClear for the King to check if the path is clear.
     * @param chessBoard Board of the game.
     * @param destination Position of the destination.
     * @return Null if the path is clear. Else, return the Position of an obstacle in the way.
     */
    public Position isPathClear(Board chessBoard, Position destination){
        return isDiscretePathClear(chessBoard, destination);
    }

    /**
     * Use the getAllDiscreteMoves for the King to get all its moves.
     * @param chessBoard Board of the game.
     * @return List of positions this piece can move to.
     */
    public List<Position> getAllMoves(Board chessBoard){
        return super.getAllDiscreteMoves(chessBoard);
    }


}
