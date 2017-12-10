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
import java.util.HashSet;
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
    public int prisWhite = 0;
    public int prisBlack = 0;
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
        if (!target.isEmpty()) {
            throw new IllegalArgumentException("This cell has already been played on");
        }
        if (!this.board.coordinateIsOnBoard(x, y)) {
            throw new IllegalArgumentException("Cell must be on board");
        }
        if (checkSuicide(target)) {
            throw new IllegalArgumentException("Suicide move!");
        }

        target.setPlayer(player);


        Stone containingStone = null;
        Cell[] neighbors = target.getNeighbors();
        for (int i = 0; i < neighbors.length; ++i) {
            Cell neighbor = neighbors[i];

            if (neighbor.isEmpty()) {
                continue;
            } else if (neighbor.getPlayer() == player) {
                Stone stone = this.board.getStone(neighbor);
                if (containingStone == null) {
                    // if we found a neighboring stone, we should add the target to that rock
                    containingStone = stone;
                    containingStone.add(target);
                } else {
                    // if we've already added the target, and we find another friendly stone,
                    // we should merge them
                    stone.removeLiberty(target);
                    containingStone.mergeWith(stone);
                }
            } else {
                // if we found a neighboring opponent, remove target as a liberty
                Stone stone = this.board.getStone(neighbor);
                stone.removeLiberty(target);

                int x_ = neighbor.getX();
                int y_ = neighbor.getY();
                uncheckMap();
                int returnval = inception(x_, y_, player);
                if (returnval == 1) {
                    int score = stone.getCells().size();
                    stone.kill();
                    /* count prisoners for scoring */
                    if (player == 1){
                        prisBlack += score;
                    }
                    else{
                        prisWhite +=score;
                    }
                }
            }
        }

            // if we haven't added the target to a stone, it should become a new stone
        if (containingStone == null) {
            containingStone = new Stone(target);
            this.board.addStone(containingStone);
        }

        this.board.applyDebugColors();

    }

    public boolean checkSuicide(Cell target){
        Cell[] neighbors = target.getNeighbors(); // Get array of neighbor cells
        int liberty = 0; // Count liberties
        for (int i=0; i< neighbors.length; ++i){ // Loop Liberties
            if (neighbors[i].isEmpty()) { // Check for open liberty
                ++liberty;
            }
        }
        if (liberty == 0){
            return true; // zero liberties -> suicide move
        }
        return false; // liberties > 0 -> legal move
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
                fieldSize += 1; // if the 2 above conditions hold, increment the field size by 1
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

    public int inception(int x, int y, int player){
        if (checkMap.get(y * boardSize + x) == 1){
            return 2; // 2 will denote a previously checked field
        }
        checkMap.put(y*boardSize+x,1);
        Cell target = this.board.getCell(x, y);
        Cell[] Inceptionneighbors = target.getNeighbors();
        for (int k = 0; k < Inceptionneighbors.length; ++k) {
            Cell inceptionneighbor = Inceptionneighbors[k];

            if (inceptionneighbor.isEmpty()) {
                return 0;
            }
            if (inceptionneighbor.getPlayer() == player){
                // do nothing
            }
            else{
                int x_ = inceptionneighbor.getX();
                int y_ = inceptionneighbor.getY();
                int returnval = inception(x_,y_,player);
                if (returnval == 0) {
                    return 0;
                }
                if (returnval == 2){
                    continue;
                }
            }
        }
        return 1;
    }

}
