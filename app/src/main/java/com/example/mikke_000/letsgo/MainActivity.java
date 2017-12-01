package com.example.mikke_000.letsgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public int boardsize = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        final TextView tw = (TextView)findViewById(R.id.textView2);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                boardsize = i + 7;
                String number = Integer.toString(boardsize);
                final String text = "The board size is " + number;
                tw.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGo();
            }
        });
    }

    private void goToGo(){
        Intent intent = new Intent(MainActivity.this,Go.class);
        intent.putExtra("boardS",boardsize);
        startActivity(intent);
    }
}
