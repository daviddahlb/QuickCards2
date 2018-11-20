package com.david.quickcards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class addQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        findViewById(R.id.ic_cancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              finish();
            }
        });

        findViewById(R.id.ic_accept).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent data = new Intent();
                data.putExtra("question", ((EditText) findViewById(R.id.editQuestion)).getText().toString());
                data.putExtra("answer", ((EditText) findViewById(R.id.editAnswer)).getText().toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }

}
