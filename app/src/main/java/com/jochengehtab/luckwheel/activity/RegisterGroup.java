package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jochengehtab.luckwheel.JSON;
import com.jochengehtab.luckwheel.R;

import java.util.ArrayList;
import java.util.List;

public class RegisterGroup extends AppCompatActivity {
    private final ArrayList<String> students = new ArrayList<>();
    private boolean isRegisteringGroup = false;
    private boolean hasClassNameEntered = false;
    private String className = null;
    private List<String> classNames = new ArrayList<>();
    private JSON saveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Initialize the textview
        TextView list = findViewById(R.id.output),
                top = findViewById(R.id.top),
                show = findViewById(R.id.show);


        saveFile = JSON.createInstance(this, "save.json");


        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> startActivity(new Intent(this, Main.class)));

        Button addClass = findViewById(R.id.submit);
        Button finish = findViewById(R.id.finish);
        addClass.setOnClickListener(v -> {

            list.setText(null);
            show.setText(null);
            top.setText(R.string.shown);

            isRegisteringGroup = true;

            classNames = saveFile.readList("names", String.class);

            addClass.setVisibility(View.GONE);
            finish.setVisibility(View.VISIBLE);
        });


        finish.setOnClickListener(v -> {
            saveFile.write(className, students.toArray());
            saveFile.write("names", classNames.toArray());

            list.setText(null);
            show.setText(null);
            top.setText(R.string.entered_names);
            isRegisteringGroup = false;
            addClass.setVisibility(View.VISIBLE);
            finish.setVisibility(View.GONE);
            hasClassNameEntered = false;
            className = null;
            students.clear();
        });


        EditText editText = findViewById(R.id.input);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (!(editText.getText().toString().trim().isEmpty())) {
                if (isRegisteringGroup) {

                    if (!hasClassNameEntered) {
                        // TODO
                        show.setText("Bitte gib den Namen der Klasse ein:");
                        className = editText.getText().toString().trim();
                        hasClassNameEntered = true;
                        classNames.add(className);
                        list.append("Der Name der Klasse lautet: '" + editText.getText().toString().trim() + "'.\n\n");
                        editText.setText(null);
                    }

                    students.add(editText.getText().toString().trim());

                    top.setText(R.string.add_student);
                    // TODO
                    show.setText("Bitte gib die Namen der Schüler ein.");
                    if (!(editText.getText().toString().trim().isEmpty())) {
                        list.append("Schüler: " + editText.getText().toString().trim() + "\n\n");
                    }

                    editText.setText(null);
                    return true;
                }
                editText.setText(null);
            }
            return true;
        });
    }

}