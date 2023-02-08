package com.uog.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class EntryActivity extends AppCompatActivity {

    public static final String IMAGE_URL ="imageUrl";
    private EditText txtImageUrl;
    private Button btnAddImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        txtImageUrl =findViewById(R.id.txtImageUrl);
        btnAddImageUrl =findViewById(R.id.btnAddImageUrl);

        btnAddImageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( txtImageUrl.getText().toString().trim().isEmpty() ){
                    Toast.makeText(EntryActivity.this, "Please enter the valid Image URL!", Toast.LENGTH_SHORT).show();
                }else{
                    if( txtImageUrl.getText().toString().trim().toLowerCase().startsWith("https://")
                    || txtImageUrl.getText().toString().trim().toLowerCase().startsWith("http://")){
                        String url = txtImageUrl.getText().toString().toLowerCase().trim();
                        //TODO return to parent activity
                        Intent intent = new Intent();
                        intent.putExtra(EntryActivity.IMAGE_URL, url);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        Toast.makeText(EntryActivity.this, "Please enter the valid Image URL!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}