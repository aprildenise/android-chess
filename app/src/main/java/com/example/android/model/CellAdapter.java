package com.example.android.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.android.Board;
import com.example.android.R;

public class CellAdapter extends BaseAdapter {

    private final Context mContext;
    private final Board.Cell[] cells;

    public CellAdapter(Context context, Board.Cell[] cells){
        this.mContext = context;
        this.cells = cells;
    }

    @Override
    public int getCount() {
        return Board.BOARD_SIZE * Board.BOARD_SIZE;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Board.Cell cell = cells[position];

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_cell, null);
        }
        final ImageView cellColor = (ImageView)convertView.findViewById(R.id.imageview_cell_color);
        final ImageView cellPiece = (ImageView)convertView.findViewById(R.id.imageview_cell_piece);

        // Match the fields of this View to the properties of the Cell
        if (cell.isColored){
            cellColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        }
        else{
            cellColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }

        // Set the image for the piece, if there's a piece on this cell.
        if (cell.piece != null){
            cellPiece.setImageResource(cell.piece.getImageResource());
        }
        else{
            cellPiece.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    /**
     * Take the cells array from the Board and "flatten" it so that it's a 1D array
     * instead of a 2D array.
     * @param board Board that contains the cells to flatten.
     * @return 1D Array representation of the board.
     */
    public static Board.Cell[] flattenCellArray(Board board){

        Board.Cell[] flattenedCells = new Board.Cell[Board.BOARD_SIZE * Board.BOARD_SIZE];
        int k = 0;
        Board.Cell[][] cells = board.getCells();
        for (int i = 0; i < Board.BOARD_SIZE; i++){
            for (int j = 0; j < Board.BOARD_SIZE; j++){
                flattenedCells[k] = cells[i][j];
                k++;
            }
        }

        return flattenedCells;
    }

    @Override
    public String toString(){
        String s = "";
        for (int i = 0; i < cells.length; i++){
            s += " " + cells[i].toString() + " ";
        }
        return s;
    }

}
