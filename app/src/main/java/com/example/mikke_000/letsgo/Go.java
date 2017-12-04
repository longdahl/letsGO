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


    public int width;
    public int height;
    public int player = 1;
    public Map<Integer,Integer> colorDict = new HashMap<Integer,Integer>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int boardsize = getIntent().getExtras().getInt("boardS",0); // send this with intent when you create slider

        //LinearLayout layout = new LinearLayout(this);
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(boardsize);
        gridLayout.setRowCount(boardsize);


        final ImageButton btnArray[] = new ImageButton[boardsize*boardsize];

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        gridLayout.setY((width-height/2));

        final int btwidth = width/boardsize;
        int count = 0;

        /*nested loops that loops over the boardsize in 2 dimensions, creates the imagebuttons and
        sets an onClickListener for each */

        for(int i=0;i<boardsize;i++){
            for (int k = 0; k < boardsize; k++){
                btnArray[count] = new ImageButton(this);
                LinearLayout.LayoutParams linParams = new LinearLayout.LayoutParams(btwidth,btwidth);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(linParams);
                gridLayout.addView(btnArray[count],params);

                final int _i = i*boardsize+k;
                btnArray[count].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeATurn(_i,btnArray[_i]);
                    }
                });
                count += 1;
            }
        }
        setContentView(gridLayout);

    }


    public void takeATurn(Integer i,ImageButton b) {

        try {
            if (colorDict.get(i) == R.drawable.black || colorDict.get(i) == R.drawable.white) {
                Toast.makeText(getApplicationContext(), "This field has already been crossed off", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }catch(java.lang.NullPointerException e){
                e.printStackTrace();
        }
        if(player == 1){
            b.setImageResource(R.drawable.black);
            b.setScaleType(ImageView.ScaleType.CENTER);
            b.setAdjustViewBounds(true);
            player = 2;
            colorDict.put(i,R.drawable.black);
            return;
        }
        else{
            b.setImageResource(R.drawable.white);
            b.setScaleType(ImageView.ScaleType.CENTER);
            b.setAdjustViewBounds(true);
            player = 1;
            colorDict.put(i,R.drawable.white);
        }

    }
}




