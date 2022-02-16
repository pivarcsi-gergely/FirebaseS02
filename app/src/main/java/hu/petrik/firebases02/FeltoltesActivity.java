package hu.petrik.firebases02;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class FeltoltesActivity extends AppCompatActivity {
    private Button buttonKepFeltoltes, buttonLogout, buttonFeltoltes;
    private EditText ETFajlNev;
    private ImageView IVKep;
    private ProgressBar progressBar;
    private DatabaseReference dbReference;
    private StorageReference stReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feltoltes);
        init();
        buttonFeltoltes.setOnClickListener(view -> {
            feltoltes();
        });
        buttonKepFeltoltes.setOnClickListener(view -> {
            kepKivalasztasa();
        });
    }

    public void feltoltes() {
        if (imageUri != null) {
            StorageReference fileReference = stReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            StorageTask sTask = fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                Handler handler = new Handler();
                handler.postDelayed((Runnable) () -> {
                    progressBar.setProgress(0);
                }, 500);
                Toast.makeText(this, "Sikeres fájl feltöltés", Toast.LENGTH_SHORT).show();
                String fajlNev = ETFajlNev.getText().toString();
                File file = new File(fajlNev, imageUri.toString());
                String egyediAzonosito = dbReference.push().getKey();
                dbReference.child(egyediAzonosito).setValue(file);

            }).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()
                        / snapshot.getTotalByteCount());
                progressBar.setProgress((int)progress);
            });
        } else {
            Toast.makeText(this, "Nincs fájl kijelölve", Toast.LENGTH_SHORT).show();
        }
    }

    public void kepKivalasztasa() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            IVKep.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cResolver.getType(uri));
    }

    public void init() {
        buttonKepFeltoltes = findViewById(R.id.buttonKepValasztasa);
        buttonFeltoltes = findViewById(R.id.buttonFajlFeltoltes);
        buttonLogout = findViewById(R.id.buttonLogout);
        IVKep = findViewById(R.id.IVKep);
        progressBar = findViewById(R.id.progressBar);

        dbReference = FirebaseDatabase.getInstance().getReference("Feltöltések");
        stReference = FirebaseStorage.getInstance().getReference("Feltöltések");
    }
}