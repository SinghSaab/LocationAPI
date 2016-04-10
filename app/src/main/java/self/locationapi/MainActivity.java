package self.locationapi;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MainActivity";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;                    //Using GoogleLocationAPI
    private Location mLocation;                                  //Getting the location
    private LocationRequest mLocationRequest;                    //For quality of service parameters
    private String mLastUpdateTime;                              //Show the updated location time

    private static final int REQUEST_COARSE_LOCATION = 1;
    private static int getPermissionRequestResult;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
        mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));

        verifyLocationPermissions(this);

        //Google API builder for Location Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Callbacks for checking connectivity status with Google Play services
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);                                 //Can vary depending upon process running
        mLocationRequest.setFastestInterval(5000);                          //Would not get new location at-least before 3s

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
//        ********************* Requesting Location only when application is first run *******************************
//
//        try {
//            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (mLocation != null) {
//                mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
//                mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
//            } else {
//                mLatitudeTextView.setText("***");
//                mLongitudeTextView.setText("***");
//                Toast.makeText(this, "Location error. Check if location is ON",Toast.LENGTH_SHORT).show();
//            }
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//        ***********************************************************************************************************
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection Suspended:");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed: " + connectionResult.getErrorMessage());
    }

//    This callback if when there is a change in the location
    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = java.text.DateFormat.getTimeInstance().format(new Date());
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        Toast.makeText(this, "Updated: " + mLastUpdateTime, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    //    As of Marshmallow, API 23 we need to explicitly ask for permissions along with mentioning in Manifest
    public static boolean verifyLocationPermissions(Activity activity) {
        // Check if we have permissions
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission
                .ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_COARSE_LOCATION);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MainActivity.this, "LOCATION Allowed", Toast.LENGTH_SHORT)
                            .show();
                    getPermissionRequestResult = 1;
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                    getPermissionRequestResult = 0;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
