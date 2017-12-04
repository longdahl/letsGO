package com.example.mikke_000.letsgo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Board {
    private Go game;
    private Cell[][] cells;
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

        return gridLayout;
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
