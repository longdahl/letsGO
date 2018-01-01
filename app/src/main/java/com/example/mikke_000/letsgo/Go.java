package com.example.mikke_000.letsgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Go extends AppCompatActivity {
    private int width;
    private int height;
    private int player = 1;
    private Board board;
    private Cell koBlacklist;
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
        View layout = board.createLayout(this, width);
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
        if (this.koBlacklist != null
                && x == this.koBlacklist.getX()
                && y == this.koBlacklist.getY()) {
            throw new IllegalArgumentException("Ko rule");
        }
        if (checkSuicide(target)){
            // throw new IllegalArgumentException("Suicide move!");
        }

        this.koBlacklist = null;

        target.setPlayer(player);

        // check surrounding stones
        Stone containingStone = null;
        int ownLiberties = 0;
        ArrayList<Stone> killedStones = new ArrayList<>();
        Cell[] neighbors = target.getNeighbors();
        for (int i = 0; i < neighbors.length; ++i) {
            Cell neighbor = neighbors[i];

            if (neighbor.isEmpty()) {
                ++ownLiberties;
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

                // kill the stone if it has no liberties left
                if (stone.getLiberties().size() == 0) {
                    // TODO: add stone.getCells().size() to score
                    killedStones.add(stone);
                    this.board.killStone(stone);
                }
            }
        }

        // if we haven't added the target to a stone, it should become a new stone
        if (containingStone == null) {
            containingStone = new Stone(target);
            this.board.addStone(containingStone);
        }

        // if we are at risk of encountering ko rule, block it for next move
        if (killedStones.size() == 1
                && killedStones.get(0).getCells().size() == 1
                && killedStones.get(0).getLiberties().size() == 0
                && ownLiberties == 0) {
            Iterator itr = killedStones.get(0).getCells().iterator();
            Cell killedCell = (Cell) itr.next();
            this.koBlacklist = killedCell;
        }

        this.board.applyDebugColors();
    }
    public boolean checkSuicide(Cell target){
        Cell[] neighbors = target.getNeighbors(); // Get array of neighbor cells
        int liberty = 0; // Count liberties
        for (int i=0; i< neighbors.length; ++i){ // Loop Liberties
            // TODO: implement using board.getStone(Cell c) and Stone.getLiberties().size()
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
