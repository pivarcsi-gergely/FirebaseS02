package hu.petrik.firebases02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button RegButton, LoginButton;
    private EditText ETLoginEmail, ETLoginPassword;
    private FirebaseAuth frAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegIntent = new Intent(MainActivity.this, RegActivity.class);
                startActivity(RegIntent);
                finish();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                String email = ETLoginEmail.getText().toString();
                String jelszo = ETLoginPassword.getText().toString();
                if (cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){

                    if (email.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Nem lehet üres az email!", Toast.LENGTH_SHORT).show();
                        ETLoginEmail.setError("Nem lehet üres az email!");
                    }
                    else if (jelszo.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Nem lehet üres a jelszó!", Toast.LENGTH_SHORT).show();
                        ETLoginEmail.setError("Nem lehet üres a jelszó!");
                    }
                    else {
                        frAuth.signInWithEmailAndPassword(email, jelszo).addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               FirebaseUser frUser = frAuth.getCurrentUser();
                               if (!frUser.isEmailVerified()) {
                                   Toast.makeText(MainActivity.this, "Erősítsd meg az email-edet!", Toast.LENGTH_SHORT).show();
                               }
                               else {
                                   Toast.makeText(MainActivity.this, "Hoi there, " + frUser.getDisplayName(), Toast.LENGTH_SHORT).show();

                                   Intent intent = new Intent(MainActivity.this, FeltoltesActivity.class);
                                   startActivity(intent);
                                   finish();
                               }
                           }
                        });
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Nincs internet kapcsolat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void init() {
        RegButton = findViewById(R.id.RegButton);
        LoginButton = findViewById(R.id.LoginButton);
        ETLoginEmail = findViewById(R.id.ETLoginEmail);
        ETLoginPassword = findViewById(R.id.ETLoginPassword);
        frAuth = FirebaseAuth.getInstance();
    }
}