package pl.poznan.put.etraction;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import pl.poznan.put.etraction.model.CameraMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 19.04.2017.
 */

public class CamerasFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CameraMsg>>, PlayMediaListener {

    private static final String TAG = CamerasFragment.class.getSimpleName();
    //id of loader
    private static final int CAMERAS_GET_LOADER = 42;

    private RecyclerView mRecyclerView;
    private CamerasAdapter mCamerasAdapter;
    private TextView mErrorView;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CAMERAS_GET_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cameras, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_cameras);
        LinearLayoutManager layoutManager;
        switch(Resources.getSystem().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
            default:
                layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                layoutManager = new GridLayoutManager(this.getContext(), 2);
                break;
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mCamerasAdapter = new CamerasAdapter(this);
        mRecyclerView.setAdapter(mCamerasAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_cameras_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_cameras_error);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void playMedia(Uri file) {
        Intent intent = new Intent(Intent.ACTION_VIEW );
        intent.setDataAndType(file, "video/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(getContext(), R.string.cant_resolve_intent, Toast.LENGTH_SHORT).show();

    }

    @Override
    public Loader<List<CameraMsg>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<CameraMsg>>(this.getContext()) {

            private List<CameraMsg> mCamerasResponse;

            @Override
            protected void onStartLoading() {
                if (mCamerasResponse != null){
                    Log.d(TAG, "DELIVERED CAMERAS");
                    deliverResult(mCamerasResponse);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    Log.d(TAG, "LOADED CAMERAS");
                    forceLoad();
                }
            }

            @Override
            public List<CameraMsg> loadInBackground() {
                try {
                    URL moviesUrl = NetworkUtils.buildUrl(NetworkUtils.CAMERAS_BASE_URL);
                    String moviesGetResults = NetworkUtils.getResponseFromHttpUrl(moviesUrl);
                    Gson gson = new Gson();
                    return gson.fromJson(moviesGetResults, CameraMsg.CamerasMsg.class).getCameras();
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get cameras data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(List<CameraMsg> data) {
                mCamerasResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<CameraMsg>> loader, List<CameraMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mCamerasAdapter.setCamerasData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CameraMsg>> loader) {

    }
}
