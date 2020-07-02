package de.cyberfacility.alpaykucuk.palaver;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;


public class Gps implements LocationListener {

    Context kontext;

    public Gps(Context kontext) {
        super();
        this.kontext = kontext;
    }

    public Location getCurrentDeviceLocation(){
        if (ContextCompat.checkSelfPermission(kontext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Keine Berechtigung","Der App wurde keine Standortberechtigung erteilt!");
            return null;
        }
        try {
            LocationManager standortManager = (LocationManager) kontext.getSystemService(LOCATION_SERVICE);
            boolean gpsAktiv = standortManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsAktiv){
                standortManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,10,this);
                Location letzterBekannteStandort = standortManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                return letzterBekannteStandort;
            }else{
                Log.e("GPS nicht aktiv","GPS Aktivieren!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}

