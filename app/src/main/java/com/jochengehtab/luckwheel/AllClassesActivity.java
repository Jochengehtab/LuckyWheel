package com.jochengehtab.luckwheel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.simple.JSONArray;

import java.util.Objects;

public class AllClassesActivity extends AppCompatActivity {

    private long index;
    private JSONArray jsonArray;
    private String arrayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_classes);

        String filesDir = getApplicationContext().getFilesDir() + "/";

        ListView listView = findViewById(R.id.listView);

        JSONArray jsonArray = Manager.getArray(filesDir + "Classes.json", "Names");


        assert jsonArray != null;
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jsonArray));


        listView.setOnItemClickListener((parent, view, position, id) -> {
            index = listView.getItemIdAtPosition(position);

            String name = String.valueOf(Objects.requireNonNull(jsonArray).get(Math.toIntExact(index)));
            arrayName = name;
            Manager.set(filesDir + "Classes.json", "CurrentName", name);
            this.jsonArray = Manager.getArray(filesDir + "Classes.json", name);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("Names", this.jsonArray);
            bundle.putString("arrayName", arrayName);

            Intent intent = new Intent(this, MainActivity.class);

            intent.putExtras(bundle);

            startActivity(intent);
        });

        ImageButton imageButton = findViewById(R.id.back);
        imageButton.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putStringArrayList("Names", this.jsonArray);
            bundle.putString("arrayName", arrayName);
            Intent intent = new Intent(this, MainActivity.class);

            intent.putExtras(bundle);

            startActivity(intent);
        });
    }
}