package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.adapters.AdapterAccessi;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.CustomDialog;
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
    private boolean loading = false;
    private Double longitudine = null,latitudine = null;

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
        swipeRefreshLayout.setRefreshing(true);
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
        if (!StringUtils.isEmpty(utente.getImageUrl()))
            Picasso.get().load(Uri.decode(utente.getImageUrl())).resize(80, 80).centerInside().into(imgProfilo);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btQr:
                ScanOptions options = new ScanOptions();
                options.setCaptureActivity(ScreenQrActivity.class);
                options.setCameraId(0);
                options.setPrompt("Scan qr");
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                barcodeLauncher.launch(options);
                break;
            case R.id.btImpostazioni:
                PopupMenu menu = new PopupMenu(this, view);
                menu.getMenu().add(getResources().getString(R.string.settingsActivity)).setNumericShortcut('0');
                menu.getMenu().add("Logout").setNumericShortcut('1');
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getNumericShortcut()) {
                        case '0':
                            Intent intent = new Intent(this, SettingsActivity.class);
                            intent.putExtra(Parameters.INTENT_UTENTE, utente);
                            startActivity(intent);
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
        }
    }

    @Override
    public void onRefresh() {
        loading = false;
        new Handler().postDelayed(() -> {
            filterPassaggi.resetPager();
            new LoadPassaggiWorker(this, filterPassaggi, iLoadPassaggi).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }, 2000);
    }

    public void onLoadMore() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(() -> {
            filterPassaggi.updatePager();
            new LoadPassaggiWorker(this, filterPassaggi, iLoadPassaggi).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }, 2000);
    }

    private final ILoadPassaggi iLoadPassaggi = new ILoadPassaggi() {
        @Override
        public void OnComplete() {
            if (loading) {
                DBHandler.getIstance(MainActivity.this).getItemPassaggiWhitLoaded(utente.getId(), ((AdapterAccessi) list.getAdapter()).getItems());
                list.getAdapter().notifyDataSetChanged();
            } else {
                list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
                list.addItemDecoration(dividerItemDecoration);
                list.setAdapter(new AdapterAccessi(MainActivity.this, DBHandler.getIstance(MainActivity.this).getItemPassaggi(utente.getId())));
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)list.getLayoutManager();
                list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0 && linearLayoutManager != null) {
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();
                            int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                            if (loading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    onLoadMore();
                                }
                            }
                        }
                    }
                });
            }
            swipeRefreshLayout.setRefreshing(false);
            loading = true;
        }

        @Override
        public void OnError() {
            swipeRefreshLayout.setRefreshing(false);
            loading = true;
        }

        @Override
        public void OnCompleteWithout() {
            swipeRefreshLayout.setRefreshing(false);
            loading = true;
        }
    };

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),result -> {
        if (result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
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
            }else
                new CustomDialog(MainActivity.this,"Attenzione","Sei troppo distante dall punto di accesso", TypeDialog.WARING).show();
        }else
            new CustomDialog(MainActivity.this,"Errore","Verifica se hai attivo la localizazzione").show();
    }

    private final IOpenVarco iOpenVarco = new IOpenVarco() {
        @Override
        public void OnSuccess(boolean completeUtente) {
            if(completeUtente){
                DBHandler.getIstance(MainActivity.this).completeUtente(utente);
                setContentProfilo();
            }
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }

        @Override
        public void ErrorParam() {
            new CustomDialog(MainActivity.this,"Errore","Verifica se hai attivo la localizazzione").show();
        }

        @Override
        public void ErrorApertura() {
            new CustomDialog(MainActivity.this,"Errore","Verifica se hai attivo la localizazzione").show();
        }

        @Override
        public void ErroGeneric() {
            new CustomDialog(MainActivity.this,"Errore","Verifica se hai attivo la localizazzione").show();
        }

        @Override
        public void ErrorConnection() {
            new CustomDialog(MainActivity.this,"Errore","Verifica se hai attivo la localizazzione").show();
        }

        @Override
        public void ErrToken() {
            new CustomDialog(MainActivity.this,"Errore","Verifica se hai attivo la localizazzione").show();
        }
    };

}