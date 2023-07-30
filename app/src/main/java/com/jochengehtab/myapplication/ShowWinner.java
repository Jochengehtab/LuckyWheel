package com.jochengehtab.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Objects;

public class ShowWinner extends AppCompatActivity {

    private String name;
    private String arrayName;
    private final Bundle sendBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_winner);

        Bundle bundle = getIntent().getExtras();

        String filesDir = getApplicationContext().getFilesDir() + "/";

        if (bundle != null){
            name = bundle.getString("winner");
            TextView textView = findViewById(R.id.out);
            textView.append(name + ".");
            arrayName = bundle.getString("arrayName");
        }

        Button remove = findViewById(R.id.remove);
        remove.setOnClickListener(v -> {
            JSONObject jsonObject = Manager.getHoleFile(filesDir + "Classes.json");
            Manager.removeFromArray(filesDir + "Classes.json", Objects.requireNonNull(jsonObject.get("CurrentName")).toString(), name);
            JSONArray jsonArray = Manager.getArray(filesDir + "Classes.json", Objects.requireNonNull(jsonObject.get("CurrentName")).toString());
            sendBundle.putStringArrayList("Names", jsonArray);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(sendBundle);
            startActivity(intent);
        });
    }
}