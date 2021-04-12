package com.example.dhruv.pg_accomodation.map_oprations;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;


public class LocationProvider implements LocationListener {

    LocationManager locationManager;
    Location currentlocation;

    public Location getLocation(Context mContext) {
        try {

            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationProvider.this);

        }catch (Exception e){
            e.printStackTrace();
        }
        return currentlocation;
    }//get location

    @Override
    public void onLocationChanged(Location location) {
        try{
            currentlocation = location;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}//end of class
