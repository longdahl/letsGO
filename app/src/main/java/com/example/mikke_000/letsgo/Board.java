package com.example.mikke_000.letsgo;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import java.util.ArrayList;

public class Board {
    private Go game;
    private Cell[][] cells;
    private ArrayList<Stone> stones;
    private int size;

    public Board(Go game, int size) {
        this.game = game;
        this.size = size;
        this.cells = new Cell[size][size];
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
     * Removes a stone from the board.
     * Does not actually kill the stone - call `stone.kill()` for that.
     */
    public void removeStone(Stone stone) {
        this.stones.remove(stone);
    }

    public int getSize() { return size; }

    public GridLayout createLayout(final Context context, int boardWidth) {
        GridLayout gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(this.size);
        gridLayout.setRowCount(this.size);

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
        /* the following adds the test count button. can be deleted in the finale version */
        Button countB = new Button(context);
        countB.setX(boardWidth/2);
        countB.setY(0);
        countB.setText("C");
        gridLayout.addView(countB,btnSize,btnSize);
        countB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.countBoardScore();
            }
        });
        /* end of test button code */
        return gridLayout;

    }

}
