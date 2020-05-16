package com.example.revalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.revalapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        EditText editText = (EditText) findViewById(R.id.autoCompleteTextView);
        if(editText.length()!=12)
        {
            MaterialAlertDialogBuilder builderApply = new MaterialAlertDialogBuilder(this);
            //
            // Toast.makeText(this, "Enter valid roll no", Toast.LENGTH_SHORT).show();
            builderApply.setTitle("Invalid Roll no");
            builderApply.setMessage("Please enter valid roll no");
            builderApply.show();
        }
        else {
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            ((MyApplication) this.getApplication()).setSomeVariable(message);
            startActivity(intent);
        }
    }
}
