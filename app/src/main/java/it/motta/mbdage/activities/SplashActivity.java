package it.motta.mbdage.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.utils.Utils;
import it.motta.mbdage.worker.LoadVarchiWoker;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        if ((ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this,
                Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.NFC,
                    Manifest.permission.CAMERA,
                },
                1);
        } else
            handler();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if ((ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.NFC)
                        != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)){
                    handler();
                } else {
                    Toast.makeText(this, "Permission denied ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private void handler() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
            startActivity(new Intent(this,LoginActivity.class));
        else {
                new LoadVarchiWoker(this,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                //  new LoadVarchiWoker(this,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);

    }

}