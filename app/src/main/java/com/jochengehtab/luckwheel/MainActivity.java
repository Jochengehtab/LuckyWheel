package com.jochengehtab.luckwheel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final String blue = "#00008B";

    private LuckyWheel luckyWheel;

    private List<WheelItem> wheelItems;
    private final List <String> members = new ArrayList< >();

    private Button button;
    private MediaPlayer mediaPlayer;

    //randomColorNumber get initialize in getRandomColor()
    private int max, min, randomColorNumber, winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filesDir = getApplicationContext().getFilesDir() + "/";

        min = 0;

        button = findViewById(R.id.btnSpin);
        Button registerNewClass = findViewById(R.id.registerClass);

        Bundle bundle = getIntent().getExtras();

        luckyWheel = findViewById(R.id.lwv);
        defaultWheel();
        luckyWheel.addWheelItems(wheelItems);
        luckyWheel.setTarget(randomColorNumber);
        if (bundle != null){
            ArrayList<String> namesFromLoadClasses = bundle.getStringArrayList("Names");
            if (namesFromLoadClasses != null){
                try {
                    for (String name : namesFromLoadClasses){
                        if (!name.isEmpty()){
                            addAItem(name);
                            members.add(name);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Random random = new Random();

        EditText editText = findViewById(R.id.input);

        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (editText.getText().toString().trim().length() == 0) {
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
            if (members.size() == 0) {
                showDefaultSubtitle("Bitte gib ein paar Namen ein!");
                return;
            }

            max = members.size();

            button.setEnabled(false);
            winner = random.nextInt(max) + min;

            //Show Subtitle
            showDefaultSubtitle("Ausloshung...");
            mediaPlayer = MediaPlayer.create(this, R.raw.openchest);
            mediaPlayer.start();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            luckyWheel.setTarget(winner);
            luckyWheel.rotateWheelTo(winner);
            button.setEnabled(true);
            luckyWheel.setLuckyWheelReachTheTarget(() -> {
                showDefaultSubtitle("Der Glückliche ist: " + wheelItems.get((winner - 1 == -1) ? winner : winner - 1).text + ".");
                Bundle input = new Bundle();
                input.putString("winner", wheelItems.get((winner - 1 == -1) ? winner : winner - 1).text);
                input.putString("arrayName", (bundle != null) ? bundle.getString("arrayName") : null);
                startActivity(new Intent(this, ShowWinner.class).putExtras(input));
            });
        });

        registerNewClass.setOnClickListener(v -> startActivity(new Intent(this, RegisterClassActivity.class)));

        Button load = findViewById(R.id.load);
        load.setOnClickListener(v -> startActivity(new Intent(this, AllClassesActivity.class)));

    }


    private void defaultWheel() {
        wheelItems = new ArrayList < > ();
        // Create a dummy 1x1 transparent bitmap to satisfy the constructor
        Bitmap dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        wheelItems.add(new WheelItem(Color.parseColor(blue), dummyBitmap, "Klasse laden oder Namen hinzufügen"));
    }


    private void addAItem(String name) {
        if (wheelItems.get(0).text.equalsIgnoreCase("Klasse laden oder Namen hinzufügen")) {
            wheelItems.remove(0);
        }
        // Create a dummy 1x1 transparent bitmap to satisfy the constructor
        Bitmap dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        wheelItems.add(new WheelItem(Color.parseColor(getRandomColor()), dummyBitmap, name));
    }


    /**
     * Gets the random color
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
     * Gets the image to random color
     * @return The Integer of the images
     */
    private int getImageToRandomColor() {

        int result;
        switch (randomColorNumber) {
            case 1:
                result = R.drawable.yellow;
                break;
            case 2:
                result = R.drawable.red;
                break;
            case 3:
                result = R.drawable.green;
                break;
            case 4:
                result = R.drawable.lightgreen;
                break;
            case 5:
                result = R.drawable.purple;
                break;
            case 6:
                result = R.drawable.light_blue;
                break;
            default:
                result = R.drawable.blue;
                break;
        }
        return result;
    }


    /**
     * Show a default subtitle
     * @param msg The message
     */
    private void showDefaultSubtitle(Object msg) {
        Toast.makeText(getApplicationContext(), String.valueOf(msg), Toast.LENGTH_SHORT).show();
    }
}