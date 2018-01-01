package com.example.mikke_000.letsgo;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.Iterator;

public class Board {
    private Go game;
    private Cell[][] cells;
    private ArrayList<Stone> stones;
    private int size;

    public boolean debugging = true; // TODO: disable

    public Board(Go game, int size) {
        this.game = game;
        this.size = size;
        this.cells = new Cell[size][size];
        this.stones = new ArrayList<>();
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                this.cells[x][y] = new Cell(this, x, y);
            }
        }
    }

    public boolean coordinateIsOnBoard(int x, int y) {
        return (x >= 0 && x < this.size && y >= 0 && y < this.size);
    }

    public Cell getCell(int x, int y) {
        if (!coordinateIsOnBoard(x, y)) {
            return null;
        }
        return this.cells[x][y];
    }

    /**
     * Gets the stone that contains a given cell.
     */
    public Stone getStone(Cell cell) {
        for (int i = 0; i < this.stones.size(); ++i) {
            Stone stone = this.stones.get(i);
            if (stone.contains(cell)) {
                return stone;
            }
        }
        return null;
    }

    /**
     * Adds a stone to the board
     */
    public void addStone(Stone stone) {
        this.stones.add(stone);
    }

    /**
     * Removes a stone from the board.
     * Handles adding the newly empty fields as liberties to surrounding stones
     */
    public void killStone(Stone stone) {
        // add cells as liberties to neighboring stones
        Iterator<Cell> cellIterator = stone.getCells().iterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            cell.setPlayer(0);
            Cell[] neighbors = cell.getNeighbors();
            for (int i = 0; i < neighbors.length; ++i) {
                if (neighbors[i].isEmpty()) { continue; }
                Stone neighborStone = this.getStone(neighbors[i]);
                if (neighborStone != stone && neighborStone != null) {
                    neighborStone.addLiberty(cell);
                }
            }
        }

        this.stones.remove(stone);
    }

    public void removeStone(Stone stone) {
        this.stones.remove(stone);
    }

    public ArrayList<Stone> getStones() { return stones; }
    public int getSize() { return size; }

    public void applyDebugColors() {
        if (this.debugging) {
            for (int x = 0; x < this.size; ++x) {
                for (int y = 0; y < this.size; ++y) {
                    Cell cell = this.getCell(x, y);
                    Stone stone = this.getStone(cell);
                    if (stone == null) {
                        cell.getButton().clearColorFilter();
                    } else {
                        cell.getButton().setColorFilter(stone.debugColor);
                    }
                }
            }
        }
    }

    public ViewGroup createLayout(Context context, ViewGroup parent, int boardWidth) {
        GridLayout gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(this.size);
        gridLayout.setRowCount(this.size);
        parent.addView(gridLayout);

        int btnSize = boardWidth / this.size;

        // create all the buttons
        for (int x = 0; x < this.size; ++x) {
            for (int y = 0; y < this.size; ++y) {
                final Cell c = this.getCell(x, y);
                ImageButton btn = c.createButton(context);
                c.setButton(btn);

                // set the size of the button
                gridLayout.addView(btn, btnSize, btnSize);

                // add onclick listener
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        game.onButtonClick(c.getX(), c.getY());
                    }
                });
            }
        }

        return parent;
    }
    public ZoomLayout createLayout(final Context context, int boardWidth) {
        ZoomLayout zoomLayout = new ZoomLayout(context);
        zoomLayout.minZoom = 1.0f;
        zoomLayout.maxZoom = 2.0f;

        zoomLayout = (ZoomLayout) this.createLayout(context, zoomLayout, boardWidth);
        return zoomLayout;
    }

}
