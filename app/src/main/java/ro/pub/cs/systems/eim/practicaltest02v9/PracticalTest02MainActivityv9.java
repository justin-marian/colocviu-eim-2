package ro.pub.cs.systems.eim.practicaltest02v9;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class PracticalTest02MainActivityv9 extends AppCompatActivity {

    private EditText wordInput;
    private EditText minLengthInput;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v9_main);

        wordInput = findViewById(R.id.word_input);
        minLengthInput = findViewById(R.id.min_length_input);
        Button fetchButton = findViewById(R.id.fetch_button);
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

                new Thread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        try {
                            String urlString = "http://www.anagramica.com/all/" + word + "?min=" + minLength;
                            URL url = new URL(urlString);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");

                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;

                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();

                            runOnUiThread(() -> resultText.setText(response.toString()));
                        } catch (Exception e) {
                            Log.e("HTTP_ERROR", Objects.requireNonNull(e.getMessage()));
                            runOnUiThread(() -> resultText.setText("Eroare la conectarea cu API-ul!"));
                        }
                    }
                }).start();
            }
        });
    }
}
