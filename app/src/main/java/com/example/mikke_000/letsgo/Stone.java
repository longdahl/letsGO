package com.example.mikke_000.letsgo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class Stone {
    private HashSet<Cell> cells;
    private HashSet<Cell> liberties;
    private Board board;
    private int player;

    public Stone(Cell[] cells) {
        this.cells = new HashSet<>(Arrays.asList(cells));
        this.liberties = new HashSet<>();
        this.player = cells[0].getPlayer();
        this.board = cells[0].getBoard();
    }
    public Stone(Cell cell) {
        this.cells = new HashSet<>();
        this.liberties = new HashSet<>();
        this.cells.add(cell);
        this.player = cell.getPlayer();
        this.board = cell.getBoard();
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
     *
     * Handles adding/removing liberties from this and other stones.
     * Handles merging with new neighboring stones.
     */
    public void add(Cell cell) {
        if (cell.getPlayer() != this.player) {
            throw new IllegalArgumentException("Cannot add cell to stone - not same player");
        }

        // we need to update liberties and merge with neighboring stones
        Cell[] neighbors = cell.getNeighbors();
        for (int i = 0; i < neighbors.length; ++i) {
            Cell neighbor = neighbors[i];
            // if it's empty, add it as a new liberty
            if (neighbor.isEmpty()) { this.liberties.add(neighbor); }
            // if it's non-empty, that stone might have liberties we need to remove
            // this also handles removing it as a liberty from ourselves
            else {
                Stone neighborStone = board.getStone(neighbor);
                // remove from neighborStones liberties
                if (neighborStone.hasLiberty(neighbor)) {
                    neighborStone.removeLiberty(neighbor);
                }
                // merge with ourselves if needed
                if (neighborStone.getPlayer() == this.player) {
                    this.mergeWith(neighborStone);
                }
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
        
        // merge their stuff into us
        this.cells.addAll(target.getCells());
        this.liberties.addAll(target.getLiberties());
        
        // remove the stone from the board
        this.board.removeStone(target);
    }

    /**
     * Kills ourselves.
     * Also handles removing ourselves from the board.
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