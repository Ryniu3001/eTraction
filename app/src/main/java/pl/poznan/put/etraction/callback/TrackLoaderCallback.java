package pl.poznan.put.etraction.callback;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;

import pl.poznan.put.etraction.model.TrackMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Callback for loader that loads the actual track of the train.
 * Created by Marcin on 15.07.2017.
 */

public class TrackLoaderCallback implements  LoaderManager.LoaderCallbacks<TrackMsg> {

    private static final String TAG = TrackLoaderCallback.class.getSimpleName();
    private TrackLoaderResultListener resultListener;
    private Context context;

    public TrackLoaderCallback(TrackLoaderResultListener resultListener, Context context){
        this.resultListener = resultListener;
        this.context = context;
    }

    @Override
    public Loader<TrackMsg> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<TrackMsg>(this.context) {
            TrackMsg mTrackResponse;

            @Override
            protected void onStartLoading() {
                if (mTrackResponse != null){
                    Log.d(TAG, "DELIVERED TRACK");
                    deliverResult(mTrackResponse);
                } else {
                    Log.d(TAG, "LOADED TRACK");
                    forceLoad();
                }
            }

            @Override
            public TrackMsg loadInBackground() {

                try {
                    URL statementsUrl = NetworkUtils.buildUrl(NetworkUtils.TRACK_BASE_URL);
                    String trackJsonResults = NetworkUtils.getResponseFromHttpUrl(statementsUrl);
                    TrackMsg trackMsg = new Gson().fromJson(trackJsonResults, TrackMsg.Track.class).getTrack();
                    return trackMsg;
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get track data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(TrackMsg data) {
                mTrackResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<TrackMsg> loader, TrackMsg data) {
        if (resultListener != null)
            resultListener.onTrackLoad(data);
        else
            Log.w(TAG, "Listener is null");
    }

    @Override
    public void onLoaderReset(Loader<TrackMsg> loader) {

    }

    public interface TrackLoaderResultListener{
        void onTrackLoad(TrackMsg data);
    }
}
