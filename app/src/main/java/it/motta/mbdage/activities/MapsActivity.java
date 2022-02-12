package it.motta.mbdage.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.CreaVarcoDialog;
import it.motta.mbdage.dialog.CustomDialog;
import it.motta.mbdage.dialog.VarcoFrameDialog;
import it.motta.mbdage.interfaces.ICreateVarco;
import it.motta.mbdage.interfaces.ILoadVarchi;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.worker.LoadVarchiWoker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

  private GoogleMap mMap;
  private CreaVarcoDialog creaVarcoDialog;
  private Utente utente;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    utente = (Utente) getIntent().getSerializableExtra(Parameters.INTENT_UTENTE);

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setOnMapLongClickListener(this);
    mMap.setOnMarkerClickListener(  marker ->{
      Varco varco = (Varco) marker.getTag();
      VarcoFrameDialog varcoFrameDialog = new VarcoFrameDialog(getSupportFragmentManager(),varco);
      varcoFrameDialog.show();
      return true;
    });

    reloadMap();
  }

  private void reloadMap(){
    for(Varco varco: DBHandler.getIstance(MapsActivity.this).getVarchi()) {
      mMap.addMarker(new MarkerOptions().position(new LatLng(varco.getLatitudine(),varco.getLongitudine())).title(varco.getDescrizione())).setTag(varco);
    }
    getDeviceLocation();
  }

  private final ICreateVarco iCreateVarco = new ICreateVarco() {
    @Override
    public void OnSuccess(Varco varco) {
      VarcoFrameDialog varcoFrameDialog = new VarcoFrameDialog(getSupportFragmentManager(),varco);
      varcoFrameDialog.show();
      new LoadVarchiWoker(MapsActivity.this, 0, new ILoadVarchi() {
        @Override
        public void OnSuccess() {
          reloadMap();
        }

        @Override
        public void ErroGeneric() {
          new CustomDialog(MapsActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_load_varchi)).show();
        }
      }).execute();
    }

    @Override
    public void ErrorParam() {
      new CustomDialog(MapsActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_param)).show();
    }

    @Override
    public void ErroGeneric() {
      new CustomDialog(MapsActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_generico)).show();
    }

    @Override
    public void ErrorConnection() {
      new CustomDialog(MapsActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_server)).show();
    }

    @Override
    public void ErroOnLoadImageg() {
      new CustomDialog(MapsActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_load_img_varco)).show();
    }

    @Override
    public void AlreadyCreated() {
      new CustomDialog(MapsActivity.this,getResources().getString(R.string.errore),getResources().getString(R.string.err_already_varco)).show();
    }
  };

  private void getDeviceLocation() {
    try {
      mMap.setMyLocationEnabled(true);
      FusedLocationProviderClient fusedLocationClient;
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      Task<Location> locationResult = fusedLocationClient.getLastLocation();
      locationResult.addOnCompleteListener(this, task -> {
        if (task.isSuccessful()) {
          Location lastKnownLocation;
          // Set the map's camera position to the current location of the device.
          lastKnownLocation = task.getResult();
          if (lastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude()),50));
          }
        }
      });
    } catch (SecurityException e)  {
      e.printStackTrace();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(creaVarcoDialog != null)
      creaVarcoDialog.onTakePhoto(requestCode,resultCode,data);
  }

  @Override
  public void onMapLongClick(@NonNull LatLng latLng) {
    creaVarcoDialog = new CreaVarcoDialog(this,latLng,iCreateVarco);
    creaVarcoDialog.show();
  }

}