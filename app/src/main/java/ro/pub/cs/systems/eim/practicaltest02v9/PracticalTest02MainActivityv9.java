package ro.pub.cs.systems.eim.practicaltest02v9;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PracticalTest02MainActivityv9 extends AppCompatActivity {

    // Tags and constants
    private static final String TAG_HTTP_RESPONSE = "HTTP_RESPONSE";
    private static final String TAG_HTTP_ERROR = "HTTP_ERROR";
    private static final String BROADCAST_ACTION = "ro.pub.cs.systems.eim.practicaltest02v9.ANAGRAMS_BROADCAST";

    private EditText wordInput;
    private EditText minLengthInput;
    private TextView resultText;

    // Define BroadcastReceiver to handle results
    private final BroadcastReceiver anagramsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Receive data from the broadcast
            String anagrams = intent.getStringExtra("anagrams");
            resultText.setText(anagrams);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v9_main);

        wordInput = findViewById(R.id.word_input);
        minLengthInput = findViewById(R.id.min_length_input);
        Button fetchButton = findViewById(R.id.fetch_button);
        Button openMapButton = findViewById(R.id.open_map_button);
        resultText = findViewById(R.id.result_text);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String word = wordInput.getText().toString();
                String minLength = minLengthInput.getText().toString();

                if (word.isEmpty() || minLength.isEmpty()) {
                    resultText.setText("Introduceți toate câmpurile!");
                    return;
                }

                new Thread(() -> {
                    sendHttpRequest(word, minLength);
                }).start();
            }
        });

        openMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(PracticalTest02MainActivityv9.this, MapsActivityV9.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        // Register BroadcastReceiver
        registerReceiver(anagramsReceiver, new IntentFilter(BROADCAST_ACTION), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister BroadcastReceiver
        unregisterReceiver(anagramsReceiver);
    }

    @SuppressLint("SetTextI18n")
    private void sendHttpRequest(String word, String minLength) {
        try {
            String urlString = "http://www.anagramica.com/all/:" + word;
            Log.d(TAG_HTTP_RESPONSE, "Connecting to: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            Log.d(TAG_HTTP_RESPONSE, "Response Code: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP error code: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Log.d(TAG_HTTP_RESPONSE, "Response: " + response);

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray anagrams = jsonResponse.getJSONArray("all");

            Log.d(TAG_HTTP_RESPONSE, "JSON Array (Parsed): " + anagrams);

            StringBuilder filteredResult = new StringBuilder();
            for (int i = 0; i < anagrams.length(); i++) {
                String anagram = anagrams.getString(i);
                Log.d(TAG_HTTP_RESPONSE, "Anagram [" + i + "]: " + anagram);

                if (anagram.length() >= Integer.parseInt(minLength)) {
                    filteredResult.append(anagram).append(", ");
                }
            }

            Intent intent = new Intent(BROADCAST_ACTION).setPackage(getPackageName());
            intent.putExtra("anagrams", filteredResult.toString());
            sendBroadcast(intent);

            runOnUiThread(() -> resultText.setText(filteredResult.toString()));
        } catch (Exception e) {
            Log.e(TAG_HTTP_ERROR, "Error: ", e);
            runOnUiThread(() -> resultText.setText("Eroare la conectarea cu API-ul: " + e.getMessage()));
        }
    }
}
