package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jochengehtab.luckwheel.JSON;
import com.jochengehtab.luckwheel.R;

public class Result extends AppCompatActivity {

    private final Bundle sendBundle = new Bundle();
    private String name;

    private JSON saveFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_winner);

        Bundle bundle = getIntent().getExtras();

        String filesDir = getApplicationContext().getFilesDir() + "/";

        saveFile = JSON.createInstance(this, "save.json");

        if (bundle != null) {
            name = bundle.getString("winner");
            TextView textView = findViewById(R.id.out);
            textView.append(name + ".");
        }

        Button remove = findViewById(R.id.remove);
        remove.setOnClickListener(v -> {
            saveFile.remove(name);

            //sendBundle.putStringArrayList("Names", jsonArray);
            Intent intent = new Intent(this, Main.class);
            //intent.putExtras(sendBundle);
            startActivity(intent);
        });
    }
}