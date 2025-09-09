package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.WheelItem;
import com.jochengehtab.luckwheel.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends AppCompatActivity {

    private final String blue = "#00008B";
    private final List<String> members = new ArrayList<>();
    private LuckyWheel luckyWheel;
    private List<WheelItem> wheelItems;
    private Button button;
    private MediaPlayer mediaPlayer;
    private int max, min, randomColorNumber, winner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        min = 0;

        button = findViewById(R.id.btnSpin);
        Button registerNewClass = findViewById(R.id.registerGroup);

        Bundle bundle = getIntent().getExtras();

        luckyWheel = findViewById(R.id.lwv);
        defaultWheel();
        luckyWheel.addWheelItems(wheelItems);
        luckyWheel.setTarget(randomColorNumber);
        if (bundle != null) {
            ArrayList<String> namesFromLoadClasses = bundle.getStringArrayList("Names");
            if (namesFromLoadClasses != null) {
                for (String name : namesFromLoadClasses) {
                    if (!name.isEmpty()) {
                        addAItem(name);
                        members.add(name);
                    }
                }
            }
        }
        Random random = new Random();

        EditText editText = findViewById(R.id.input);

        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (editText.getText().toString().trim().isEmpty()) {
                    showDefaultSubtitle("Bitte gib einen Namen ein.");
                    return false;
                }
                luckyWheel.addWheelItems(wheelItems);
                members.add(editText.getText().toString());
                addAItem(editText.getText().toString());
                showDefaultSubtitle(editText.getText().toString() + " wurde hinzugefügt.");
                editText.setText(null);
            }
            return true;
        });

        button.setOnClickListener(v -> {
            if (members.isEmpty()) {
                showDefaultSubtitle("Bitte gib ein paar Namen ein!");
                return;
            }

            max = members.size();

            button.setEnabled(false);
            winner = random.nextInt(max) + min;

            //Show Subtitle
            showDefaultSubtitle("Draw is underway...");
            mediaPlayer = MediaPlayer.create(this, R.raw.openchest);
            mediaPlayer.start();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            luckyWheel.setTarget(winner);
            luckyWheel.rotateWheelTo(winner);
            button.setEnabled(true);
            luckyWheel.setLuckyWheelReachTheTarget(() -> {
                showDefaultSubtitle("The lucky one is: " + wheelItems.get((winner - 1 == -1) ? winner : winner - 1).text + ".");
                Bundle input = new Bundle();
                input.putString("winner", wheelItems.get((winner - 1 == -1) ? winner : winner - 1).text);
                input.putString("arrayName", (bundle != null) ? bundle.getString("arrayName") : null);
                startActivity(new Intent(this, Result.class).putExtras(input));
            });
        });

        registerNewClass.setOnClickListener(v -> startActivity(new Intent(this, RegisterGroup.class)));

        Button load = findViewById(R.id.load_group);
        load.setOnClickListener(v -> startActivity(new Intent(this, LoadGroup.class)));

    }


    private void defaultWheel() {
        wheelItems = new ArrayList<>();
        // Create a dummy 1x1 transparent bitmap to satisfy the constructor
        Bitmap dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        wheelItems.add(new WheelItem(Color.parseColor(blue), dummyBitmap, ""));
    }


    private void addAItem(String name) {
        if (wheelItems.get(0).text.isBlank()) {
            wheelItems.remove(0);
        }
        // Create a dummy 1x1 transparent bitmap to satisfy the constructor
        Bitmap dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        wheelItems.add(new WheelItem(Color.parseColor(getRandomColor()), dummyBitmap, name));
    }


    /**
     * Gets the random color
     *
     * @return The random color
     */
    private String getRandomColor() {

        Random random = new Random();
        int minRange = 0, maxRange = 6;
        randomColorNumber = random.nextInt(maxRange + 1 - minRange) + minRange;

        String color, yellow = "#ffff00", red = "#FF0000", green = "#008000", purple = "#9400D3", lightGreen = "#32CD32", lightBlue = "#20c4fc";

        switch (randomColorNumber) {
            case 1:
                color = yellow;
                break;
            case 2:
                color = red;
                break;
            case 3:
                color = green;
                break;
            case 4:
                color = lightGreen;
                break;
            case 5:
                color = purple;
                break;
            case 6:
                color = lightBlue;
                break;
            default:
                color = blue;
                break;
        }
        return color;
    }

    /**
     * Show a default subtitle
     *
     * @param msg The message
     */
    private void showDefaultSubtitle(Object msg) {
        Toast.makeText(getApplicationContext(), String.valueOf(msg), Toast.LENGTH_SHORT).show();
    }
}