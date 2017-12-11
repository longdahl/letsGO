package com.example.mikke_000.letsgo;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Stone {
    private HashSet<Cell> cells;
    private HashSet<Cell> liberties;
    private Board board;
    private int player;

    public int debugColor;

    public Stone(Cell cell) {
        this.cells = new HashSet<>();
        this.liberties = new HashSet<>();
        this.cells.add(cell);
        this.player = cell.getPlayer();
        this.board = cell.getBoard();

        this.addLibertiesFromCell(cell);

        this.debugColor = Color.HSVToColor(new float[]{
                (float) (Math.random()*360), // hue
                (float) (Math.random() * 0.5 + 0.5), // saturation
                (float) (Math.random() * 0.5 + 0.5), // value
        }) & 0x88ffffff; // reduce opacity to 50%
    }

    public boolean contains(Cell cell) {
        return this.cells.contains(cell);
    }
    public boolean hasLiberty(Cell cell) {
        return this.liberties.contains(cell);
    }
    public void removeLiberty(Cell cell) {
        this.liberties.remove(cell);
    }

    /**
     * Adds a cell to the stone.
     * The cell must be owned by the player that owns the stone.
     * *Doesn't* handle interacting with other stones (removing liberties, merging)
     */
    public void add(Cell cell) {
        if (cell.getPlayer() != this.player) {
            throw new IllegalArgumentException("Cannot add cell to stone - not same player");
        }

        // if it was a liberty, remove it
        if (this.liberties.contains(cell)) {
            this.liberties.remove(cell);
        }

        // also add it to ourselves
        this.cells.add(cell);
        this.addLibertiesFromCell(cell);
    }

    public void addLiberty(Cell cell) {
        this.liberties.add(cell);
    }
    public void addLibertiesFromCell(Cell cell) {
        Cell[] neighbors = cell.getNeighbors();
        for (int i = 0; i < neighbors.length; ++i) {
            if (neighbors[i].isEmpty()) {
                this.liberties.add(neighbors[i]);
            }
        }
    }

    /**
     * Merges a stone into ourselves.
     * Target stone must have the same player as us.
     * Assumes target is valid (i.e. no liberties on filled cells)
     */
    public void mergeWith(Stone target) {
        if (target.getPlayer() != this.player) {
            throw new IllegalArgumentException("Cannot merge stones - not same player");
        }

        if (target == this) { return; }
        
        // merge their stuff into us
        this.cells.addAll(target.getCells());
        this.liberties.addAll(target.getLiberties());
        
        // remove the stone from the board
        this.board.removeStone(target);
    }

    /**
     * Sets all cells to 0. Also removes self from board.
     * *Doesn't* handle adding cells as liberties to surrounding stones (see Board.killStone)
     */
    public void kill() {
        Iterator<Cell> cellIterator = this.cells.iterator();
        while (cellIterator.hasNext()) {
            cellIterator.next().setPlayer(0);
        }
        this.board.removeStone(this);
    }

    public int getPlayer() { return player; }
    public HashSet<Cell> getCells() { return cells; }
    public HashSet<Cell> getLiberties() { return liberties; }
}
