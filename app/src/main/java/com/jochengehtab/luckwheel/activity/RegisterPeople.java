package com.jochengehtab.luckwheel.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jochengehtab.luckwheel.JSON;
import com.jochengehtab.luckwheel.R;
import java.util.ArrayList;
import java.util.List;

public class RegisterPeople extends AppCompatActivity {

    private String groupName;
    private List<String> students;
    private ArrayAdapter<String> adapter;
    private JSON saveFile;
    private EditText studentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_people);

        // Get the group name passed from the previous activity
        groupName = getIntent().getStringExtra("GROUP_NAME");

        TextView groupNameTitle = findViewById(R.id.groupNameTitle);
        ListView studentListView = findViewById(R.id.studentListView);
        studentInput = findViewById(R.id.studentInput);
        Button addStudentButton = findViewById(R.id.addStudentButton);
        ImageButton backButton = findViewById(R.id.backButton); // Initialize the back button

        groupNameTitle.setText(groupName);

        saveFile = JSON.createInstance(this, "save.json");

        // Load existing students for this group
        students = saveFile.readList(groupName, String.class);
        if (students == null) {
            students = new ArrayList<>();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, students);
        studentListView.setAdapter(adapter);

        addStudentButton.setOnClickListener(v -> {
            String newStudent = studentInput.getText().toString().trim();
            if (!newStudent.isEmpty()) {
                students.add(newStudent);
                saveFile.write(groupName, students.toArray());
                adapter.notifyDataSetChanged();
                studentInput.setText("");
            }
        });

        // Set the OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            // Finish the current activity and go back to the previous one
            finish();
        });
    }
}