package com.example.mikke_000.letsgo;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * A cell on a Go board.
 * The cell must know where it is placed on the board
 */
public class Cell {
    private Board board;
    private int x, y, player;
    private ImageButton button;

    public Cell(Board board, int x, int y) {
        this.board = board;
        this.player = 0;
        this.x = x;
        this.y = y;
    }
    public Cell(Board board, int x, int y, int player) {
        this.board = board;
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public boolean isEmpty() {
        return this.player == 0;
    }

    public Cell[] getNeighbors() {
        int boardSize = this.board.getSize();
        int[][] offsets = new int[][]{
                {-1,0}, // west
                {0,-1}, // north
                {1,0},  // east
                {0,1}   // south
        };

        // only return the neighbors that aren't outside of the board
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < offsets.length; ++i) {
            int[] offset = offsets[i];
            Cell neighbor = this.board.getCell(this.x + offset[0], this.y + offset[1]);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }

        // https://stackoverflow.com/a/4042464/645768
        return neighbors.toArray(new Cell[0]);
    }

    public void setPlayer(int player) {
        this.player = player;
        if (this.button != null) {
            int newBg = R.drawable.emptyboard;
            if (player == 1){
                newBg = R.drawable.black;
            }
            else if (player == 2){
                newBg = R.drawable.white;
            }
            //int newBg = player == 1 ? R.drawable.black : R.drawable.white;
            this.button.setImageResource(newBg);
        }
    }

    public ImageButton createButton(Context context) {
        ImageButton btn = new ImageButton(context);
        btn.setPadding(0, 0, 0, 0);
        btn.setImageResource(R.drawable.emptyboard);
        btn.setScaleType(ImageView.ScaleType.FIT_XY);
        btn.setAdjustViewBounds(true);

        this.button = btn;

        return btn;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getPlayer() { return player; }
    public ImageButton getButton() { return this.button; }
    public void setButton(ImageButton button) { this.button = button; }
    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
}
