package com.example.mikke_000.letsgo;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Go extends AppCompatActivity {
    private int width;
    private int height;
    private int player = 1;
    private Board board;
    public Map<Integer, Integer> colorDict = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        int boardSize = getIntent().getExtras().getInt("boardS", 0); // send this with intent when you create slider

        board = new Board(this, boardSize);
        GridLayout layout = board.createLayout(this, width);
        layout.setY((width - height / 2));

        setContentView(layout);
    }

    public void onButtonClick(int x, int y) {
        try {
            makeMove(x, y, player);
            player = 3 - player; // toggle between 1 and 2
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Throws an IllegalArgumentException if the move is illegal
     * The exception contains a user-friendly error message
     */
    public void makeMove(int x, int y, int player) {
        Cell target = this.board.getCell(x, y);
        if (target.getPlayer() != 0) { // TODO: replace `0` with constant defined somewhere (e.g. Go.PLAYERS.empty)
            throw new IllegalArgumentException("This cell has already been played on");
        }
        if (!this.board.coordinateIsOnBoard(x, y)) {
            throw new IllegalArgumentException("Cell must be on board");
        }

        target.setPlayer(player);

        // TODO: implement Go logic
    }
}
