package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import it.motta.mbdage.R;


@SuppressLint({"UseCompatLoadingForDrawables"})
public class MainActivity extends AppCompatActivity {

    private CardView cardProfilo;
    private CardView cardMovimenti;
    private ImageButton btLingua,btNfc,btQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        cardProfilo = findViewById(R.id.cardProfilo);
        cardProfilo.addView(setContentProfilo());

    }

    private View setContentProfilo(){
        View view = LayoutInflater.from(this).inflate(R.layout.contet_profile,null);
        ImageView imgProfilo = view.findViewById(R.id.imgProfilo);
        imgProfilo.setImageDrawable(getDrawable(R.drawable.ic_launcher_background));
        return view;
    }



}