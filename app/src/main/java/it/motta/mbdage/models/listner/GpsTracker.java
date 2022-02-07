package it.motta.mbdage.models.listner;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import it.motta.mbdage.activities.MBadgeApplication;

public class GpsTracker implements LocationListener {

  @Override
  public void onLocationChanged(Location loc) {

  }

  @Override
  public void onProviderDisabled(String provider) {}

  @Override
  public void onProviderEnabled(String provider) {}

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {}
}
