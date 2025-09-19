package com.jochengehtab.luckwheel.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.WheelItem;
import com.jochengehtab.luckwheel.JSON;
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
    private int max;
    private int winner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btnSpin);
        Button registerNewClass = findViewById(R.id.registerGroup);
        luckyWheel = findViewById(R.id.lwv);
        JSON saveFile = JSON.createInstance(this, "save.json");

        defaultWheel();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String groupName = bundle.getString("GROUP_NAME");
            if (groupName != null) {
                List<String> groupMembers = saveFile.readList(groupName, String.class);
                if (groupMembers != null) {
                    for (String name : groupMembers) {
                        if (name != null && !name.trim().isEmpty()) {
                            Log.i("Name", name);
                            addAItem(name);
                            members.add(name);
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Group is empty!", Toast.LENGTH_SHORT).show();
            }
        }

        luckyWheel.addWheelItems(wheelItems);

        Random random = new Random();
        EditText editText = findViewById(R.id.input);

        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String inputText = editText.getText().toString().trim();
                if (inputText.isEmpty()) {
                    showDefaultSubtitle("Please enter some names.");
                    return false;
                }
                // When adding a new item, you must update the wheel again
                addAItem(inputText);
                members.add(inputText);
                luckyWheel.addWheelItems(wheelItems); // Refresh the wheel with the new item
                showDefaultSubtitle(inputText + " was added.");
                editText.setText(null);
            }
            return true;
        });

        button.setOnClickListener(v -> {
            if (members.isEmpty()) {
                showDefaultSubtitle("Please enter some names!");
                return;
            }

            max = members.size();
            button.setEnabled(false);
            winner = random.nextInt(max); // Generates a 0-based index

            showDefaultSubtitle("Draw is underway...");
            mediaPlayer = MediaPlayer.create(this, R.raw.openchest);
            mediaPlayer.start();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            // The target index must be between 1 and list.size() for this library
            luckyWheel.rotateWheelTo(winner + 1);

            luckyWheel.setLuckyWheelReachTheTarget(() -> {
                // FIX: Correctly get the winner using the 0-based index
                String winnerName = wheelItems.get(winner).text;
                showDefaultSubtitle("The lucky one is: " + winnerName + ".");

                Bundle input = new Bundle();
                input.putString("winner", winnerName);
                input.putString("arrayName", (bundle != null) ? bundle.getString("GROUP_NAME") : null);
                startActivity(new Intent(this, Result.class).putExtras(input));

                button.setEnabled(true);
            });
        });

        registerNewClass.setOnClickListener(v -> startActivity(new Intent(this, RegisterGroup.class)));

        Button load = findViewById(R.id.load_group);
        load.setOnClickListener(v -> startActivity(new Intent(this, LoadGroup.class)));

    }


    private void defaultWheel() {
        wheelItems = new ArrayList<>();
        Bitmap dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        wheelItems.add(new WheelItem(Color.parseColor(blue), dummyBitmap, ""));
    }


    private void addAItem(String name) {
        if (!wheelItems.isEmpty() && wheelItems.get(0).text.isEmpty()) {
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
        int randomColorNumber = random.nextInt(maxRange + 1 - minRange) + minRange;

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