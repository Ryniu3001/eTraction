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

import pl.poznan.put.etraction.model.RestaurantMenuItemMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 22.04.2017.
 */

public class RestaurantMenuFragment extends BaseRecyclerViewFragment implements LoaderManager.LoaderCallbacks<List<RestaurantMenuItemMsg>> {

    private static final String TAG = MoviesFragment.class.getSimpleName();
    //id of loader
    private static final int RESTAURANT_GET_LOADER = 666;

    private RestaurantMenuAdapter mRestaurantMenuAdapter;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(RESTAURANT_GET_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movies, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_movies);
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

        mRestaurantMenuAdapter = new RestaurantMenuAdapter();
        mRecyclerView.setAdapter(mRestaurantMenuAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_movies_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_movies_error);
    }

    @Override
    public Loader<List<RestaurantMenuItemMsg>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<RestaurantMenuItemMsg>>(this.getContext()) {

            private List<RestaurantMenuItemMsg> mRestaurantResult;

            @Override
            protected void onStartLoading() {
                if (mRestaurantResult != null){
                    Log.d(TAG, "DELIVERED MENU");
                    deliverResult(mRestaurantResult);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    Log.d(TAG, "LOADED MENU");
                    forceLoad();
                }
            }

            @Override
            public List<RestaurantMenuItemMsg> loadInBackground() {
                try {
                    URL camerasUrl = NetworkUtils.buildUrl(NetworkUtils.RESTAURANT_MENU_BASE_URL);
                    String moviesGetResults = NetworkUtils.getResponseFromHttpUrl(camerasUrl);
                    return new Gson().fromJson(moviesGetResults, RestaurantMenuItemMsg.RestaurantMenuItemsMsg.class).getItems();
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get movies data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(List<RestaurantMenuItemMsg> data) {
                mRestaurantResult = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<RestaurantMenuItemMsg>> loader, List<RestaurantMenuItemMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRestaurantMenuAdapter.setMenuData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<RestaurantMenuItemMsg>> loader) {
        Log.d(TAG, "RESET LOADER");
    }

}
