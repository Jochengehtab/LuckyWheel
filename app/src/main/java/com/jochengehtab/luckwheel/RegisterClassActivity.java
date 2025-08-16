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

public class RegisterClassActivity extends AppCompatActivity {
    private boolean isRegisteringANewClass = false;

    private boolean hasClassNameEntered = false;
    private String className = null;
    private final ArrayList<String> students = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_class);

        String filesDir = getApplicationContext().getFilesDir() + "/";
        Manager.set(filesDir + "Classes.json", "Path", filesDir);

        //Initialize the textview
        TextView list = findViewById(R.id.output),
                top = findViewById(R.id.top),
                show = findViewById(R.id.show);


        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        Button addClass = findViewById(R.id.addClasses);
        Button finish = findViewById(R.id.finsih);
        addClass.setOnClickListener(v -> {

            list.setText(null);
            show.setText(null);
            top.setText(R.string.shown);

            isRegisteringANewClass = true;

            if (!(Manager.getArray(filesDir + "Classes.json", "Names") == null)){
                classNames = Manager.getArray(filesDir + "Classes.json", "Names");
            }

            addClass.setVisibility(View.GONE);
            finish.setVisibility(View.VISIBLE);
        });


        finish.setOnClickListener(v -> {
            Manager.set(filesDir + "Classes.json", className, students.toArray());
            Manager.set(filesDir + "Classes.json", "Names", classNames.toArray());

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
                if (isRegisteringANewClass){

                    if (!hasClassNameEntered){
                        show.setText("Bitte gib den Namen der Klasse ein:");
                        className = editText.getText().toString().trim();
                        hasClassNameEntered = true;
                        classNames.add(className);
                        list.append("Der Name der Klasse lautet: '" + editText.getText().toString().trim() + "'.\n\n");
                        editText.setText(null);
                    }

                    students.add(editText.getText().toString().trim());

                    top.setText(R.string.bghene);
                    show.setText("Bitte gib die Namen der Schüler ein.");
                    if (!(editText.getText().toString().trim().isEmpty())){
                        list.append("Schüler: " + editText.getText().toString().trim()  + "\n\n");
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