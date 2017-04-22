package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import pl.poznan.put.etraction.model.StatementMsg;
import pl.poznan.put.etraction.model.StatementsMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 11.04.2017.
 */

public class StatementsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<StatementMsg>>{

    private static final String TAG = StatementsFragment.class.getSimpleName();
    //id of loader
    private static final int STATEMENTS_GET_LOADER = 69;

    private RecyclerView mRecyclerView;
    private StatementsAdapter mStatementsAdapter;
    private TextView mErrorView;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(STATEMENTS_GET_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statements, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_statements);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);

        mStatementsAdapter = new StatementsAdapter();
        mRecyclerView.setAdapter(mStatementsAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_statement_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_statement_error);
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
    public Loader<List<StatementMsg>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<StatementMsg>>(this.getContext()) {

            //Cache the previous result (useful when user press home button and then came back to application or when rotate the screen)
            List<StatementMsg> mStatementsResponse;

            @Override
            protected void onStartLoading() {
                if (mStatementsResponse != null){
                    Log.d(TAG, "DELIVERED STATEMENTS");
                    deliverResult(mStatementsResponse);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    Log.d(TAG, "LOADED STATEMENTS");
                    forceLoad();
                }
            }

            @Override
            public List<StatementMsg> loadInBackground() {

                try {
                    URL statementsUrl = NetworkUtils.buildUrl(NetworkUtils.STATEMENTS_BASE_URL);
                    String statementsGetResults = NetworkUtils.getResponseFromHttpUrl(statementsUrl);
                    Gson gson = new Gson();
                    return gson.fromJson(statementsGetResults, StatementsMsg.class).getStatements();
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get statements data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(List<StatementMsg> data) {
                mStatementsResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<StatementMsg>> loader, List<StatementMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mStatementsAdapter.setStatementsData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<StatementMsg>> loader) {
    }
}
