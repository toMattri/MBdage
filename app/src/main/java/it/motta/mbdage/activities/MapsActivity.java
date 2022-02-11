package it.motta.mbdage.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.CreaVarcoDialog;
import it.motta.mbdage.models.Varco;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

  private GoogleMap mMap;
  private CreaVarcoDialog creaVarcoDialog;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setOnMapLongClickListener(this);
    for(Varco varco: DBHandler.getIstance(MapsActivity.this).getVarchi()) {
      mMap.addMarker(new MarkerOptions().position(new LatLng(varco.getLatitudine(),varco.getLongitudine())).title(varco.getDescrizione()));
    }

    getDeviceLocation();
  }

  private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    try {
      FusedLocationProviderClient fusedLocationClient;
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      Task<Location> locationResult = fusedLocationClient.getLastLocation();
      locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
          if (task.isSuccessful()) {
            Location lastKnownLocation;
            // Set the map's camera position to the current location of the device.
            lastKnownLocation = task.getResult();
            if (lastKnownLocation != null) {
              mMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(),
                  lastKnownLocation.getLongitude())).title("Tu"));

              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                  new LatLng(lastKnownLocation.getLatitude(),
                      lastKnownLocation.getLongitude()),50));
            }
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

    creaVarcoDialog = new CreaVarcoDialog(this,latLng);
    creaVarcoDialog.show();

  }

}