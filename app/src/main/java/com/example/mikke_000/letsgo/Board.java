package com.example.mikke_000.letsgo;

public class Board {
    private Cell[][] cells;
    private int size;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                this.cells[x][y] = new Cell(this, x, y);
            }
        }
    }

    /**
     * Throws an IllegalArgumentException if the move is illegal
     * The exception contains a user-friendly error message
     */
    public void makeMove(int x, int y, int player) {
        Cell target = this.getCell(x, y);
        if (target.getPlayer() != 0) { // TODO: replace `0` with constant defined somewhere (e.g. Go.PLAYERS.empty)
            throw new IllegalArgumentException("This cell has already been played on");
        }
        if (!coordinateIsOnBoard(x, y)) {
            throw new IllegalArgumentException("Cell must be on board");
        }

        target.setPlayer(player);

        // TODO: implement Go logic
    }

    public boolean coordinateIsOnBoard(int x, int y) {
        return (x >= 0 && x < this.size && y >= 0 && y < this.size);
    }

    public int getSize() { return size; }
    public Cell getCell(int x, int y) {
        if (!coordinateIsOnBoard(x, y)) {
            return null;
        }
        return this.cells[x][y];
    }
}
