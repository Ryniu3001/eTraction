package pl.poznan.put.etraction;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import pl.poznan.put.etraction.listener.PlayMediaListener;
import pl.poznan.put.etraction.model.MovieMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 14.04.2017.
 */

public class MoviesFragment extends BaseRecyclerViewFragment implements LoaderManager.LoaderCallbacks<List<MovieMsg>> {

    private static final String TAG = MoviesFragment.class.getSimpleName();
    //id of loader
    private static final int MOVIES_GET_LOADER = 21;

    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_GET_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_rv_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_common);
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

        mMoviesAdapter = new MoviesAdapter(new PlayMediaListener(this.getActivity()));
        mRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_common_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_common_error);
    }

    @Override
    public Loader<List<MovieMsg>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<MovieMsg>>(this.getContext()) {

            private List<MovieMsg> mMoviesResponse;

            @Override
            protected void onStartLoading() {
                if (mMoviesResponse != null){
                    Log.d(TAG, "DELIVERED MOVIES");
                    deliverResult(mMoviesResponse);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    Log.d(TAG, "LOADED MOVIES");
                    forceLoad();
                }
            }

            @Override
            public List<MovieMsg> loadInBackground() {
                try {
                    URL camerasUrl = NetworkUtils.buildUrl(NetworkUtils.MOVIES_BASE_URL);
                    String moviesGetResults = NetworkUtils.getResponseFromHttpUrl(camerasUrl);
                    return new Gson().fromJson(moviesGetResults, MovieMsg.MoviesMsg.class).getMovies();
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get common_rv_layout data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(List<MovieMsg> data) {
                mMoviesResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<MovieMsg>> loader, List<MovieMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMoviesAdapter.setMoviesData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MovieMsg>> loader) {
        Log.d(TAG, "RESET LOADER");
    }

}
