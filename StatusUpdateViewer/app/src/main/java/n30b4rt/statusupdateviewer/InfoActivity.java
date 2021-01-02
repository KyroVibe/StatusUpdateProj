package n30b4rt.statusupdateviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import n30b4rt.Status;

public class InfoActivity extends AppCompatActivity {

    private TextView playerCount;
    private Button disconnectButton, refreshButton;

    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        playerCount = (TextView) findViewById(R.id.playercount_text);
        disconnectButton = (Button) findViewById(R.id.disconnect_button);
        refreshButton = (Button) findViewById(R.id.refresh_button);

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client.getInstance().stop(InfoActivity.this::OnDisconnectResult);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Display();
            }
        });

        Display();
    }

    private void toast(String s) {
        this.runOnUiThread(() -> {
            String s2 = s;
            Toast.makeText(this, s2, Toast.LENGTH_LONG).show();
        });
    }

    public void Display() {
        Thread t = new Thread(() -> {
            Pair<Status, Exception> result = Client.getInstance().getStatus();
            if (result.first == null) {
                OnDisconnectResult(result.second);
            } else {
                playerCount.setText("Player Count\n" + result.first.PlayerCount);
            }
        });
        t.start();
    }

    public void OnDisconnectResult(Exception e) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}