package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jochengehtab.luckwheel.JSON;
import com.jochengehtab.luckwheel.R;

import java.util.ArrayList;
import java.util.List;

public class RegisterGroup extends AppCompatActivity {

    private List<String> classNames;
    private ArrayAdapter<String> adapter;
    private JSON saveFile;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        saveFile = JSON.createInstance(this, "save.json");
        editText = findViewById(R.id.input);
        ListView groupListView = findViewById(R.id.groupListView);
        Button submitButton = findViewById(R.id.submit);
        ImageButton backButton = findViewById(R.id.back);

        // Load existing class names or initialize a new list
        assert saveFile != null;
        classNames = saveFile.readList("names", String.class);
        if (classNames == null) {
            classNames = new ArrayList<>();
        }

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classNames);
        groupListView.setAdapter(adapter);

        backButton.setOnClickListener(v -> startActivity(new Intent(this, Main.class)));

        // Handle adding a new group
        submitButton.setOnClickListener(v -> {
            String newGroupName = editText.getText().toString().trim();
            if (!newGroupName.isEmpty()) {
                if (!classNames.contains(newGroupName)) {
                    classNames.add(newGroupName);
                    saveFile.write("names", classNames.toArray());
                    // Refresh the ListView
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                } else {
                    Toast.makeText(this, "Group already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle clicking on a group to add students
        groupListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGroup = classNames.get(position);
            Intent intent = new Intent(RegisterGroup.this, RegisterPeople.class);
            intent.putExtra("GROUP_NAME", selectedGroup);
            startActivity(intent);
        });
    }
}