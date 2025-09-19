package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.jochengehtab.luckwheel.JSON;
import com.jochengehtab.luckwheel.R;

import java.util.ArrayList;
import java.util.List;

public class LoadGroup extends AppCompatActivity {

    private List<String> classNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_group);

        ImageButton backButton = findViewById(R.id.back);
        ListView listView = findViewById(R.id.listView);
        JSON saveFile = JSON.createInstance(this, "save.json");

        classNames = saveFile.readList("names", String.class);
        if (classNames == null) {
            classNames = new ArrayList<>();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classNames);
        listView.setAdapter(adapter);

        backButton.setOnClickListener(v -> startActivity(new Intent(this, Main.class)));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGroup = classNames.get(position);
            Intent intent = new Intent(LoadGroup.this, Main.class);
            intent.putExtra("GROUP_NAME", selectedGroup);
            Log.i("Group", selectedGroup);
            startActivity(intent);
        });
    }
}