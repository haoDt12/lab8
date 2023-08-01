package com.example.btlab8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameMain extends AppCompatActivity {
    private EditText inputName;
    private Button btName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_main);
        inputName = (EditText) findViewById(R.id.inputName);
        btName = (Button) findViewById(R.id.bt_name);

        btName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(NameMain.this, MainActivity.class);
                    intent.putExtra("username", name);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(NameMain.this, "Please enter your name.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}