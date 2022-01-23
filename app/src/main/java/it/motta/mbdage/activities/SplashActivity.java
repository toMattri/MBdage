package it.motta.mbdage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);
        DBHandler dbHandler = new DBHandler(this);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }

            String token = task.getResult();
            if(currentUser != null){

            }
            // Get new FCM registration token
            dbHandler.updateToken(token);
            // Log and toast
            //Toast.makeText(MainActivity.this, token, Toast.LENGTH_LONG).show();
        });
        startActivity(new Intent(this,LoginActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
         currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
            startActivity(new Intent(this,LoginActivity.class));
    }

}