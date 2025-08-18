package com.jochengehtab.luckwheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RegisterClassActivity extends AppCompatActivity {
    private final ArrayList<String> students = new ArrayList<>();
    private boolean isRegisteringANewClass = false;
    private boolean hasClassNameEntered = false;
    private String className = null;
    private List<String> classNames = new ArrayList<>();
    private JSON saveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_class);

        String filesDir = getApplicationContext().getFilesDir() + "/";

        //Initialize the textview
        TextView list = findViewById(R.id.output),
                top = findViewById(R.id.top),
                show = findViewById(R.id.show);


        saveFile = JSON.createInstance(this, "save.json");


        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        Button addClass = findViewById(R.id.addClasses);
        Button finish = findViewById(R.id.finsih);
        addClass.setOnClickListener(v -> {

            list.setText(null);
            show.setText(null);
            top.setText(R.string.shown);

            isRegisteringANewClass = true;

            classNames = saveFile.readList("names", String.class);

            addClass.setVisibility(View.GONE);
            finish.setVisibility(View.VISIBLE);
        });


        finish.setOnClickListener(v -> {
            saveFile.write(className, students.toArray());
            saveFile.write("names", classNames.toArray());

            list.setText(null);
            show.setText(null);
            top.setText(R.string.das_sind_die_eigegebenen_namen);
            isRegisteringANewClass = false;
            addClass.setVisibility(View.VISIBLE);
            finish.setVisibility(View.GONE);
            hasClassNameEntered = false;
            className = null;
            students.clear();
        });


        EditText editText = findViewById(R.id.inputadd);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (!(editText.getText().toString().trim().isEmpty())) {
                //TODO
                if (isRegisteringANewClass) {

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

                    top.setText(R.string.bghene);
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