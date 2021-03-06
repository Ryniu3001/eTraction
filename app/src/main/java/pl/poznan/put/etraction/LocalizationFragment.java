package pl.poznan.put.etraction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.etraction.callback.GoogleDirectionsCallback;
import pl.poznan.put.etraction.callback.TrackLoaderCallback;
import pl.poznan.put.etraction.helper.TouchableWrapper;
import pl.poznan.put.etraction.model.TrackItemMsg;
import pl.poznan.put.etraction.model.TrackMsg;
import pl.poznan.put.etraction.permission.PermissionUtils;

import static pl.poznan.put.etraction.R.id.map;


//TODO: Pierwsze wejście czasami chrupie. Sprawdzić czy da się to jakoś zoptymalizować.
/**
 * Shows the Google Maps.
 * Created by Marcin on 07.04.2017.
 */

public class LocalizationFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        TouchableWrapper.UpdateMapAfterUserInterection,
        TrackLoaderCallback.TrackLoaderResultListener,
        GoogleDirectionsCallback.GoogleDirectionsCallbackListener {

    private static final String TAG = LocalizationFragment.class.getSimpleName();

    private GoogleMap mMap;
    /**
     * Allows to control Google Maps (e.g. zoom, camera position) and update localization
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Allows to configure parameters of localization requests and updates
     */
    private LocationRequest mLocationRequest;

    /**
     * Determine if camera should follow user location
     */
    private boolean mFollowUser = true;

    private static final int TRACK_LOADER_ID = 5;
    private static final int GOOGLE_DIRECTIONS_LOADER_ID = 4;
    private TrackLoaderCallback trackLoaderCallback;
    private GoogleDirectionsCallback driectionsLoaderCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMapView = inflater.inflate(R.layout.localization, container, false);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000);
        return mMapView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TRACK_LOADER_ID, null, new TrackLoaderCallback(this, getContext()));
    }

    private TrackMsg getDummyTrack(){
        TrackMsg trackMsg = new TrackMsg();
        TrackItemMsg item1 = new TrackItemMsg(1, "Poznań Główny", 52.4021415, 16.9117642);
        TrackItemMsg item2 = new TrackItemMsg(2, "Poznań Garbary", 52.416220, 16.938339);
        TrackItemMsg item3 = new TrackItemMsg(3, "Poznań Wschód", 52.4192801, 16.9740941);
        TrackItemMsg item4 = new TrackItemMsg(4, "Ligowiec", 52.1955529, 21.0303803);
        TrackItemMsg item5 = new TrackItemMsg(5, "Kobylnica", 54.4405652, 16.9983887);
        TrackItemMsg item6 = new TrackItemMsg(6, "Biskupice Wielkopolskie", 52.462578, 17.177662);
        TrackItemMsg item7 = new TrackItemMsg(7, "Promno", 52.4503995, 17.2481292);
        List<TrackItemMsg> trackItemMsgList = new ArrayList<>();
        trackItemMsgList.add(item1);
        trackItemMsgList.add(item2);
        trackItemMsgList.add(item3);
        trackItemMsgList.add(item4);
        trackItemMsgList.add(item5);
        trackItemMsgList.add(item6);
        trackItemMsgList.add(item7);
        trackMsg.setTrackItems(trackItemMsgList);
        return trackMsg;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.

        //TrackMsg trackMsg = getDummyTrack();

        /*        Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(52.416220, 16.938339))
                        .radius(20)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLACK));*/



    }

    private void drawStations(TrackMsg trackMsg) {
        if (mMap == null){
            throw new NullPointerException("Map is null! Can't draw stations");
        }
        int height = 20;
        int width = 20;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_marker, null);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        for (TrackItemMsg station : trackMsg.getTrackItems()) {
            String snippet = getString(R.string.localizatoin_arrival) + ": " + DateFormat.getTimeFormat(getContext()).format(station.getArrivalTime()) +
                    " | " + getString(R.string.localizatoin_departure) + ": " +
                    DateFormat.getTimeFormat(getContext()).format(station.getDepartureTime());
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(station.getLat(), station.getLon()))
                    .title(station.getStopName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
    }

    private void drawRoute(String routeEncoded){
        List<LatLng> list = PolyUtil.decode(routeEncoded);
        PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
        for (LatLng item : list){
            polylineOptions.add(item).clickable(true);
        }
        Polyline polyline1 = mMap.addPolyline(polylineOptions);
        polyline1.setColor(Color.BLUE);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        Log.d(TAG, "FOLLOW USER = TRUE");
        this.mFollowUser = true;
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            showMissingPermissionError();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        Toast.makeText(getActivity(), R.string.permission_required_toast, Toast.LENGTH_LONG).show();
        getActivity().finish();
        //PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getFragmentManager(), "dialog"); //causes errors on android 6
    }

    /* Location and Google Play Services methods */

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
  //      Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        if (lastLocation != null) {
//            handleNewLocation(lastLocation);
//        }
    }

    /**
     * Move camera to the user new location
     * @param location
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        float zoomLevel = 13.0f;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Play Services Connection Failure!");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mFollowUser) {
            handleNewLocation(location);
        }
    }


    /**
     * Calls when user touch te map
     */
    @Override
    public void onUpdateMapAfterUserInterection() {
        Log.d(TAG, "USER TOUCHED THE MAP!");
        this.mFollowUser = false;
    }

    @Override
    public void onTrackLoad(TrackMsg data) {
        if (data != null && data.getTrackItems() != null){
            drawStations(data);
            int size = data.getTrackItems().size();
            if (size > 1) {
                LatLng from = new LatLng(data.getTrackItems().get(0).getLat(), data.getTrackItems().get(0).getLon());
                LatLng to = new LatLng(data.getTrackItems().get(size - 1).getLat(), data.getTrackItems().get(size - 1).getLon());
                driectionsLoaderCallback = new GoogleDirectionsCallback(this, getContext(), from, to);
                getLoaderManager().initLoader(GOOGLE_DIRECTIONS_LOADER_ID, null, driectionsLoaderCallback);
            }
        } else {
            Log.w(TAG, "Track data is null!");
        }

    }

    @Override
    public void onRouteLoad(String route) {
        if (route != null) {
            drawRoute(route);
        } else {
            Log.w(TAG, "Route is null!");
        }
    }
}
