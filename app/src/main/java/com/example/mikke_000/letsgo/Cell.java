package com.example.mikke_000.letsgo;

import java.util.ArrayList;

/**
 * A cell on a Go board.
 * The cell must know where it is placed on the board
 */
public class Cell {
    private Board board;
    private int x, y, player;

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

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getPlayer() { return player; }
    public void setPlayer(int player) { this.player = player; }
    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
}
