package n30b4rt.statusupdateviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText ipField;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // I hate android perms
        if (checkSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_DENIED) {
            String[] perms = {"android.permission.INTERNET"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        } else {
            toast("Perms already granted");
        }

        ipField = (EditText) findViewById(R.id.ip_input);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = ipField.getText().toString();
                connectButton.setText(address);
                // toast("\"" + address + "\"");
                Client.getInstance().start(address, MainActivity.this::onConnectResult);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            toast("Goodie");
        }
    }

    public void onConnectResult(Exception e) {
        if (e != null) {
            toast(e.toString());
        } else {
            toast("All good");
            OpenInfoActivity();
        }
    }

    private void toast(String s) {
        this.runOnUiThread(() -> {
            String s2 = s;
            Toast.makeText(this, s2, Toast.LENGTH_LONG).show();
        });
    }

    private void OpenInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}