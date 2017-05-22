package com.dissertation.findtheclue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String text="my text";
        TextView tv=(TextView)findViewById(R.id.textView1);
        tv.setText(text);
    }
}
