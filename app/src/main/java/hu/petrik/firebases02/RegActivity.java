package hu.petrik.firebases02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegActivity extends AppCompatActivity {

    private Button RegRegButton;
    private EditText ETRegEmail, ETRegJelszo, ETRegYourName;
    private Users users;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        init();
        RegRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ETRegEmail.getText().toString();
                String jelszo = ETRegJelszo.getText().toString();
                String yourName = ETRegYourName.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(RegActivity.this, "Nem lehet üres az email", Toast.LENGTH_SHORT).show();
                    ETRegEmail.setError("Nem lehet üres az email");
                } else if (jelszo.isEmpty()) {
                    Toast.makeText(RegActivity.this, "Nem lehet üres a jelszó", Toast.LENGTH_SHORT).show();
                    ETRegJelszo.setError("Nem lehet üres a jelszó");
                } else if (yourName.isEmpty()) {
                    Toast.makeText(RegActivity.this, "Nem lehet üres a neved", Toast.LENGTH_SHORT).show();
                    ETRegYourName.setError("Nem lehet üres a neved");
                } else {
                    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            firebaseAuth.createUserWithEmailAndPassword(email, jelszo).addOnCompleteListener(task1 -> {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                users = new Users(yourName, email);

                                databaseReference.child(firebaseUser.getUid()).setValue(users);

                                firebaseUser.sendEmailVerification();

                                firebaseAuth.signOut();

                                //Frissítjük az autentikációs profilunkat
                                UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder().setDisplayName(yourName).build();
                                firebaseUser.updateProfile(upcr);

                                //Visszalépés a fő oldalra
                                Intent back2Main = new Intent(RegActivity.this, MainActivity.class);
                                startActivity(back2Main);
                                finish();
                            });
                        }
                        else {
                            Toast.makeText(RegActivity.this, "Ilyen email címmel már regisztráltak!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void init() {
        RegRegButton = findViewById(R.id.RegRegButton);
        ETRegEmail = findViewById(R.id.ETRegEmail);
        ETRegJelszo = findViewById(R.id.ETRegJelszo);
        ETRegYourName = findViewById(R.id.ETRegYourName);
        users = new Users();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Felhasználók");
    }
}