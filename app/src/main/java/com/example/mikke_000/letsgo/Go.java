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

import static java.util.Objects.isNull;

public class Go extends AppCompatActivity {
    private int width;
    private int height;
    private int player = 1;
    private Board board;
    public int boardSize;
    public int fieldSize;
    public int fieldPlayer;
    public Map<Integer, Integer> colorDict = new HashMap<Integer, Integer>();
    public HashMap<Integer, Integer> checkMap = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        boardSize = getIntent().getExtras().getInt("boardS", 0); // send this with intent when you create slider

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
        if (checkSuicide(target, player)){
            throw new IllegalArgumentException("Suicide move!");
        }

        target.setPlayer(player);
    }

    /* Prevent suicide move*/
    public boolean checkSuicide(Cell target, int player){
        /* True = Suicide move
           False = Legal move */
        Cell[] neighbors = target.getNeighbors(); // Get array of neighbor cells
        int liberty = 0; // Count liberties
        int enemy = 0;
        int own = 0;
        for (int i=0; i< neighbors.length; ++i) { // Loop Liberties
            if (neighbors[i].getPlayer() == 0) { // Check for free liberty
                ++liberty;
            }
            if (neighbors[i].getPlayer() == neighbors[i].getPlayer()) { // Check for black rock
                ++own;
            }
            if (neighbors[i].getPlayer() != neighbors[i].getPlayer()) { // Check for white rock
                ++enemy;
            }
        }
        if ((liberty > 1) || (neighbors.length == own)){ // liberties < 2 or libirties = own stone
            return false;                                // -> Legal move
        }
        return false;
    }

    public void countBoardScore() {
        uncheckMap();
        int SP1 = 0;
        int SP2 = 0;
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                Cell target = this.board.getCell(x, y);
                int player = target.getPlayer();
                if (player == 0) {
                    fieldSize = 0;
                    fieldPlayer = 0;
                    searchEmptyField(x, y);
                    if (fieldPlayer == 1){
                        SP1 += fieldSize;
                    }
                    if (fieldPlayer == 2){
                        SP2 += fieldSize;
                    }
                }
                else{
                    /* the following code counts the black and white dots as we traverse the board
                    it is current commented out while the searchEmptyField function is verified.

                    if (player == 1){
                        SP1 ´+= 1;
                    }
                    if (player == 2){
                        SP2 += 1;
                    }
                     */
                }
            }
        }
        Toast.makeText(this, "Black: " + Integer.toString(SP1) + " White: " + Integer.toString(SP2), Toast.LENGTH_SHORT)
                .show();
    }
    public void searchEmptyField(int x, int y) {
        Cell target = this.board.getCell(x, y);
        int player = target.getPlayer();
        if (player == 0) { // checks if the field is empty
            if (checkMap.get(y * boardSize + x) == 0) { // checks if we have seen this field before
                fieldSize += 1; // the 2 above conditions hold, increment the field size by 1
                checkMap.put(y * boardSize + x, 1); // declare that we have seen this field
                /* check fields recursively that have borders touching the current field
                given that such a field exists within the current board size */
                if (x + 1 < boardSize) {
                    searchEmptyField(x + 1, y);
                }
                if (x - 1 >= 0) {
                    searchEmptyField(x - 1, y);
                }
                if (y + 1 < boardSize) {
                    searchEmptyField(x, y + 1);
                }
                if (y - 1 >= 0) {
                    searchEmptyField(x, y - 1);
                }
            }
        }
        else {
                /* the following if statements determines which player the fieldsize belongs to*/
            if (player == fieldPlayer) {
                // do nothing. reached an edge of the player whom the empty field currently belongs to
            }
            if (fieldPlayer == 0) {
                fieldPlayer = player; // assigns the field to a player
            }
            if (fieldPlayer != 0 & player != fieldPlayer) { // this determines that both players have edges that touch the empty space.
                fieldPlayer = -1; // -1 is assigned to a field that has no value irrespective of its size.
            }
        }
    }
    public void uncheckMap() {
        for (int i = 0; i < boardSize * boardSize; i++) {
            checkMap.put(i, 0);
        }
        return;
    }
}
