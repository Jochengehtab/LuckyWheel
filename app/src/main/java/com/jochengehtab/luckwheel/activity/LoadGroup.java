package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.jochengehtab.luckwheel.JSON;
import com.jochengehtab.luckwheel.R;

public class LoadGroup extends AppCompatActivity {

    private long index;
    private String jsonArray;
    private String arrayName;
    private JSON saveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_group);

        String filesDir = getApplicationContext().getFilesDir() + "/";

        ListView listView = findViewById(R.id.listView);

        saveFile = JSON.createInstance(this, "save.json");

        //listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jsonArray));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            index = listView.getItemIdAtPosition(position);

            //this.jsonArray = saveFile.readArray(name, String[].class);
            Bundle bundle = new Bundle();
            //bundle.putStringArrayList("Names", this.jsonArray);
            bundle.putString("arrayName", arrayName);

            Intent intent = new Intent(this, Main.class);

            intent.putExtras(bundle);

            startActivity(intent);
        });

        ImageButton imageButton = findViewById(R.id.back);
        imageButton.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            //bundle.putStringArrayList("Names", this.jsonArray);
            bundle.putString("arrayName", arrayName);
            Intent intent = new Intent(this, Main.class);
            intent.putExtras(bundle);

            startActivity(intent);
        });
    }
}