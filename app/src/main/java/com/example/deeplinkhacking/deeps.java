package com.example.deeplinkhacking;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class deeps extends AppCompatActivity {

    TextView tokenView, infoView;
    Button closeButton;
    private static final String SERVER_URL = "http://84.247.132.220:8000/profile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deeps);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenView = findViewById(R.id.token_view);
        infoView = findViewById(R.id.info_view);
        closeButton = findViewById(R.id.close_button);

        Uri data = getIntent().getData();
        if (data != null && "koko".equals(data.getScheme()) && "auth".equals(data.getHost())) {
            String usertoken = data.getQueryParameter("token");
            tokenView.setText("Token: " + usertoken);
            UserData(usertoken);
        }

        closeButton.setOnClickListener(v -> finish());
    }

    private void UserData(String token) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject json = new JSONObject();
            json.put("token", token);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(deeps.this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (response.isSuccessful()) {
                                JSONObject user = jsonResponse.getJSONObject("user");
                                String id = user.getString("id");
                                String username = user.getString("username");
                                String email = user.getString("email");

                                String info = "ID: " + id + "\nUsername: " + username + "\nEmail: " + email;
                                infoView.setText(info);
                            }
                        } catch (Exception e) {
                            infoView.setText("error");
                        }
                    });
                }

            });

        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }
}
