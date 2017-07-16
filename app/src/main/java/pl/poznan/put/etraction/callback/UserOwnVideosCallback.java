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
import java.util.List;

import pl.poznan.put.etraction.MainActivity;
import pl.poznan.put.etraction.model.HttpUrlResponse;
import pl.poznan.put.etraction.model.UserVideoMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 16.07.2017.
 */

public class UserOwnVideosCallback implements LoaderManager.LoaderCallbacks<List<UserVideoMsg>> {

    private static final String TAG = UserOwnVideosCallback.class.getSimpleName();
    private Context context;
    private UserOwnVideosCallbackListener listener;

    public UserOwnVideosCallback(Context context, UserOwnVideosCallbackListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Loader<List<UserVideoMsg>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<UserVideoMsg>>(context) {

            private List<UserVideoMsg> mUsersVideosResponse;

            @Override
            protected void onStartLoading() {
                if (mUsersVideosResponse != null){
                    Log.d(TAG, "DELIVERED USERS VIDEOS");
                    deliverResult(mUsersVideosResponse);
                } else {
                    listener.beforeOwnVideosRequest();
                    Log.d(TAG, "LOADED  USERS VIDEOS");
                    forceLoad();
                }
            }

            @Override
            public List<UserVideoMsg> loadInBackground() {
                try {
                    URL usersVideosUrl = NetworkUtils.buildUrl(NetworkUtils.USER_OWN_VIDEOS_BASE_URL);
                    HttpUrlResponse userOwnResponse = NetworkUtils.getResponseFromHttpUrl(usersVideosUrl, MainActivity.deviceId);
                    return new Gson().fromJson(userOwnResponse.getJsonResponse(), UserVideoMsg.UserVideosMsg.class).getVideoMsgList();
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get user own videos data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(List<UserVideoMsg> data) {
                mUsersVideosResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<UserVideoMsg>> loader, List<UserVideoMsg> data) {
        listener.onOwnVideosLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<List<UserVideoMsg>> loader) {
        Log.d(TAG, "RESET LOADER");
    }


    public interface UserOwnVideosCallbackListener {
        void beforeOwnVideosRequest();
        void onOwnVideosLoad(List<UserVideoMsg> data);
    }
}
