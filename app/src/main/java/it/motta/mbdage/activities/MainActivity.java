package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.adapters.AdapterAccessi;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.interfaces.ILoadPassaggi;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.filter.FilterPassaggi;
import it.motta.mbdage.utils.Utils;
import it.motta.mbdage.worker.LoadPassaggiWorker;

@SuppressLint({"UseCompatLoadingForDrawables"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    private CardView cardProfilo;
    private CardView cardMovimenti;
    private ImageButton btNfc,btQrCode;
    private Utente utente;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView list;
    private FilterPassaggi filterPassaggi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        utente = DBHandler.getIstance(this).getUtente();
        cardProfilo = findViewById(R.id.cardProfilo);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        list = findViewById(R.id.listAccessi);
        cardProfilo.addView(setContentProfilo());
        swipeRefreshLayout.setOnRefreshListener(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        filterPassaggi = new FilterPassaggi(utente.getId());

        //  btNfc.setOnClickListener(this);
    }

    private View setContentProfilo(){
        View view = LayoutInflater.from(this).inflate(R.layout.contet_profile,null);
        ImageView imgProfilo = view.findViewById(R.id.imgProfilo);
        TextView txtDisplay = view.findViewById(R.id.txtDisplayName);

        TextView txtEmail = view.findViewById(R.id.txtEmail);
        txtDisplay.setText(utente.getDisplayName());
        txtEmail.setText(utente.getEmail());
        if(!StringUtils.isEmpty(utente.getImageUrl()))
            Picasso.get().load(Uri.decode(utente.getImageUrl())).resize(80,80).centerInside().into(imgProfilo);

        return view;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
          /*  case R.id.btNfc:
                ScanOptions options = new ScanOptions();
                options.setCaptureActivity(ScreenQrActivity.class);
                options.setCameraId(1);
                options.setPrompt("Scan qr");
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                barcodeLauncher.launch(options);*/
        }
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            new LoadPassaggiWorker(this,filterPassaggi,iLoadPassaggi).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }, 2000);
    }


    private final ILoadPassaggi iLoadPassaggi = new ILoadPassaggi() {
        @Override
        public void OnComplete() {
            list.setAdapter(new AdapterAccessi(MainActivity.this,DBHandler.getIstance(MainActivity.this).getItemPassaggi(utente.getId())));
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void OnError() {
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void OnCompleteWithout() {
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
        result -> {
            if(result.getContents() == null) {
                Intent originalIntent = result.getOriginalIntent();
                if (originalIntent == null) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                    Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                    Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        });
}