package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.adapters.AdapterAccessi;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.CustomDialog;
import it.motta.mbdage.dialog.DialogOpenVarco;
import it.motta.mbdage.dialog.FilterPassaggiDialog;
import it.motta.mbdage.dialog.SettingsDialog;
import it.motta.mbdage.interfaces.ILoadPassaggi;
import it.motta.mbdage.interfaces.IOpenVarco;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.models.evalue.TypeDialog;
import it.motta.mbdage.models.evalue.TypeUtente;
import it.motta.mbdage.models.filter.FilterPassaggi;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.Precision;
import it.motta.mbdage.utils.Utils;
import it.motta.mbdage.worker.LoadPassaggiWorker;
import it.motta.mbdage.worker.OpenVarcoWorker;

@SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId", "NotifyDataSetChanged","MissingPermission"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private CardView cardProfilo;
    private Utente utente;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView list;
    private FilterPassaggi filterPassaggi;
    private boolean relaod = false;
    private Double longitudine = null,latitudine = null;
    private SettingsDialog settingsDialog;
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
        filterPassaggi = new FilterPassaggi(utente.getId());
        ImageButton btFiltro = findViewById(R.id.btFiltro);
        btFiltro.setOnClickListener(this);
        swipeRefreshLayout.setRefreshing(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);
        this.onRefresh();
        checkForLocation();
    }

    private void checkForLocation(){
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Location lastKnownLocation;
                lastKnownLocation = task.getResult();
                if (lastKnownLocation != null) {
                    longitudine = lastKnownLocation.getLongitude();
                    latitudine = lastKnownLocation.getLatitude();
                    Log.e("Check POS: ",longitudine + " " + latitudine);
                }
            }
        });
    }

    private View setContentProfilo() {
        View view = LayoutInflater.from(this).inflate(R.layout.contet_profile, null);
        ImageView imgProfilo = view.findViewById(R.id.imgProfilo);
        TextView txtDisplay = view.findViewById(R.id.txtDisplayName);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        txtDisplay.setText(utente.getDisplayName());
        txtEmail.setText(utente.getEmail());
        if (!StringUtils.isEmpty(utente.getImageUrl())) {
            if(utente.getImageUrl().contains("https"))
                Picasso.get().load(Uri.decode(utente.getImageUrl())).resize(80, 80).centerInside().into(imgProfilo);
            else{
                new Handler().postDelayed(() -> {
                    FirebaseStorage storage = FirebaseStorage.getInstance(Parameters.PATH_STORAGE);
                    StorageReference storageRef = storage.getReference();
                    runOnUiThread(()->{
                        storageRef.child(utente.getImageUrl()).getDownloadUrl().addOnSuccessListener( uri -> Picasso.get().load(uri).into(imgProfilo)).
                            addOnFailureListener(Throwable::printStackTrace);
                    });
                },500);
            }
        }else {
            imgProfilo.setImageDrawable(getResources().getDrawable(R.drawable.ic_default_user,null));
        }

        ImageButton btQr = view.findViewById(R.id.btQr);
        ImageButton btImpostazioni = view.findViewById(R.id.btImpostazioni);
        Button btMaps = view.findViewById(R.id.btMaps);
        if (utente.getTipoUtente().equals(TypeUtente.NOCOMPLETED))
            btImpostazioni.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_err, null));

        btQr.setOnClickListener(this);
        btImpostazioni.setOnClickListener(this);
        btMaps.setOnClickListener(this);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLocation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btQr:
                ScanOptions options = new ScanOptions();
                options.setCaptureActivity(ScreenQrActivity.class);
                options.setCameraId(0);
                options.setPrompt("Scan qr");
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                qrcodeLauncher.launch(options);
                break;
            case R.id.btImpostazioni:
                PopupMenu menu = new PopupMenu(this, view);
                menu.getMenu().add(getResources().getString(R.string.settingsActivity)).setNumericShortcut('0');
                menu.getMenu().add("Logout").setNumericShortcut('1');
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getNumericShortcut()) {
                        case '0':
                            settingsDialog = new SettingsDialog(getSupportFragmentManager(),utente, v-> {
                                cardProfilo.addView(setContentProfilo());
                            });
                            settingsDialog.show();
                            break;
                        case '1':
                            Utils.logOut(this);
                    }
                    return true;
                });
                menu.show();
                break;
            case R.id.btMaps:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra(Parameters.INTENT_UTENTE, utente);
                startActivity(intent);
                break;
            case R.id.btFiltro:
                FilterPassaggiDialog filterPassaggiDialog = new FilterPassaggiDialog(this,filterPassaggi,utente);
                filterPassaggiDialog.setOnDismissListener(di -> {
                    if(filterPassaggiDialog.getFilter() != filterPassaggi){
                        filterPassaggi = filterPassaggiDialog.getFilter();
                        swipeRefreshLayout.setRefreshing(true);
                        relaod = true;
                        this.onRefresh();
                    }
                });
                filterPassaggiDialog.show();
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            new LoadPassaggiWorker(this, filterPassaggi, iLoadPassaggi,relaod).execute();
        }, 2000);
        relaod = false;
    }


    private final ILoadPassaggi iLoadPassaggi = new ILoadPassaggi() {
        @Override
        public void OnComplete() {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
            list.setLayoutManager(linearLayoutManager);
            list.setAdapter(new AdapterAccessi(MainActivity.this,DBHandler.getIstance(MainActivity.this).getItemPassaggi(utente.getId())));
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void OnError() {
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void OnCompleteWithout() {
            list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
            list.addItemDecoration(dividerItemDecoration);
            list.setAdapter(new AdapterAccessi(MainActivity.this, new ArrayList<>()));
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private final ActivityResultLauncher<ScanOptions> qrcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Toast.makeText(MainActivity.this,getResources().getString(R.string.op_cancellata), Toast.LENGTH_LONG).show();
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                new CustomDialog(MainActivity.this, getResources().getString(R.string.errore),getResources().getString(R.string.err_perm_cam),TypeDialog.ERROR).show();
            }
        } else {
            try {
                Varco varco = new Varco(new JSONObject(result.getContents()));
                openVarco(varco);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });

    private void openVarco(Varco varco){
        if (longitudine != null && latitudine != null) {
            BigDecimal res= BigDecimal.valueOf(Precision.round(Utils.calculateDist(varco.getLatitudine(),latitudine,varco.getLongitudine(),longitudine),3,BigDecimal.ROUND_HALF_UP));
            long dist = res.multiply(new BigDecimal(1000)).longValue();
            Log.e("DIST " ,""+ dist);
            if(dist < 10){
                new OpenVarcoWorker(MainActivity.this,utente,varco,iOpenVarco).execute();
            }else {
                new CustomDialog(MainActivity.this, getResources().getString(R.string.attenzione), getResources().getString(R.string.lontano), TypeDialog.WARING).show();
                checkForLocation();
            }
        }else {
            new CustomDialog(MainActivity.this, getResources().getString(R.string.errore), getResources().getString(R.string.err_perm_geo)).show();
            checkForLocation();
        }
    }

    private final IOpenVarco iOpenVarco = new IOpenVarco() {
        @Override
        public void OnSuccess(boolean completeUtente) {
            new DialogOpenVarco(getSupportFragmentManager()).show();
            if(completeUtente){
                DBHandler.getIstance(MainActivity.this).completeUtente(utente);
                cardProfilo.addView(setContentProfilo());
            }
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }

        @Override
        public void ErrorParam() {
            new CustomDialog(MainActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_param)).show();
        }

        @Override
        public void ErrorApertura() {
            new CustomDialog(MainActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_open)).show();
        }

        @Override
        public void ErroGeneric() {
            new CustomDialog(MainActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_generico)).show();
        }

        @Override
        public void ErrorConnection() {
            new CustomDialog(MainActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_server)).show();
        }

        @Override
        public void ErrToken() {
            new CustomDialog(MainActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_token)).show();
        }
    };

}