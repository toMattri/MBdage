package it.motta.mbdage.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.interfaces.IAccessOperation;
import it.motta.mbdage.response.ResponseAccess;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.evalue.TypeLogin;
import it.motta.mbdage.models.evalue.TypeUtente;
import it.motta.mbdage.utils.TraduceComunication;
import it.motta.mbdage.worker.LoadVarchiWoker;
import it.motta.mbdage.worker.LoginWorker;
import it.motta.mbdage.worker.UpdateTokenWorker;

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
            startActivity(new Intent(this, LoginActivity.class));
        else {
           Utente utente =  DBHandler.getIstance(this).getUtente();
            if(utente == null){
                String typeLogin;
                switch (currentUser.getProviderId()){
                    case "google.com":
                        typeLogin = TypeLogin.GOOGLE;
                        break;
                    case "github.com":
                        typeLogin = TypeLogin.GIT;
                        break;
                    default:
                        typeLogin = TypeLogin.EMIAL;

                }
                new LoginWorker(this,createUser(currentUser), typeLogin,iAccessOperation).execute();
            }else{
                reloadToken(utente.getId());
                new LoadVarchiWoker(this,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
    }

    private Utente createUser(FirebaseUser firebaseUser){
        return new Utente(" ",firebaseUser.getEmail()," ", TypeUtente.NOCOMPLETED,firebaseUser.getUid(),"");
    }

    private final IAccessOperation iAccessOperation = new IAccessOperation() {
        @Override
        public void OnCompleteOperation(JSONObject response) {
            try {
                int result = response.getInt("result");

                switch (ResponseAccess.fromValue(result)){
                    case SUCCESS:
                        Utente utente = TraduceComunication.getUtente(response.getJSONObject("Utente"));
                        reloadToken(utente.getId());
                        DBHandler.getIstance(SplashActivity.this).logginUser(utente);
                        new LoadVarchiWoker(SplashActivity.this,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                        break;
                    default:
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                        finish();

                }
            }catch (Exception ex){
                ex.printStackTrace();
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }

        }

        @Override
        public void OnError() {
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        }

    };

    private void reloadToken(int idUtente){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }

            String token = task.getResult();
            new UpdateTokenWorker(this,idUtente,token).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }



}