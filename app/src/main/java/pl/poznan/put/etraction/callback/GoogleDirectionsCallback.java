package pl.poznan.put.etraction.callback;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 15.07.2017.
 */

public class GoogleDirectionsCallback implements  LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = GoogleDirectionsCallback.class.getSimpleName();
    private GoogleDirectionsCallbackListener resultListener;
    private Context context;
    private LatLng from;
    private LatLng to;

    private static final String GOOGLE_DIRECTIONS_API_KEY = "AIzaSyDZhRO6xyzmvSD1LI01nhZq65QmiFKDhmY";
    /* Google Directions API params */
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String TRANSIT_MODE = "transit_mode";
    private static final String MODE = "mode";
    private static final String ROUTING_PREFERENCE = "transit_routing_preference";
    private static final String DEPARTURE_TIME = "departure_time";
    private static final String KEY = "key";

    private static final String ROUTING_PREF_FEWER_TRANSFER = "fewer_transfers";
    private static final String TRANSIT_MODE_TRAIN = "train";
    private static final String MODE_TRANSIT = "transit";


    public GoogleDirectionsCallback(GoogleDirectionsCallbackListener resultListener, Context context, LatLng from, LatLng to){
        this.resultListener = resultListener;
        this.context = context;
        this.from = from;
        this.to = to;
    }

    private Map<String, String> prepareRequestParams(LatLng from, LatLng to){
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(ORIGIN, from.latitude + "," + from.longitude );
        paramsMap.put(DESTINATION, to.latitude + "," + to.longitude);
        paramsMap.put(TRANSIT_MODE, TRANSIT_MODE_TRAIN);
        paramsMap.put(MODE, MODE_TRANSIT);
        paramsMap.put(ROUTING_PREFERENCE, ROUTING_PREF_FEWER_TRANSFER);
        paramsMap.put(KEY, GOOGLE_DIRECTIONS_API_KEY);
        return paramsMap;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this.context) {
            String mRouteResponse;

            @Override
            protected void onStartLoading() {
                if (mRouteResponse != null){
                    Log.d(TAG, "DELIVERED ROUTE");
                    deliverResult(mRouteResponse);
                } else {
                    Log.d(TAG, "LOADED ROUTE");
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                try {
                    URL statementsUrl = NetworkUtils.buildUrlWithParams(NetworkUtils.GOOGLE_DIRECTION_API, prepareRequestParams(from, to));
                    String routeResponse = NetworkUtils.getResponseFromHttpUrl(statementsUrl);
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(routeResponse).getAsJsonObject();
                    String route = null;
                    if (obj.get("routes").getAsJsonArray().size() > 0) {
                        route = obj.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("overview_polyline").getAsJsonObject().get("points").getAsString();
                    }
                    return route;
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get track data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mRouteResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (resultListener != null)
            resultListener.onRouteLoad(data);
        else
            Log.w(TAG, "Listener is null");
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public interface GoogleDirectionsCallbackListener {
        void onRouteLoad(String route);
    }
}
