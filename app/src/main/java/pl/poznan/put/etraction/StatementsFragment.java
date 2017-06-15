package pl.poznan.put.etraction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import pl.poznan.put.etraction.model.StatementMsg;
import pl.poznan.put.etraction.model.StatementsMsg;
import pl.poznan.put.etraction.service.EtractionService;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 11.04.2017.
 */

public class StatementsFragment extends BaseRecyclerViewFragment
        implements LoaderManager.LoaderCallbacks<List<StatementMsg>>,
        EtractionService.EtractionServiceListener{

    private static final String TAG = StatementsFragment.class.getSimpleName();
    //id of loader
    private static final int STATEMENTS_GET_LOADER = 69;

    private StatementsAdapter mStatementsAdapter;
    private ProgressBar mLoadingIndicator;

    EtractionService mService;
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EtractionService.LocalBinder binder = (EtractionService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "SERVICE DISCONNECTED");
            mBound = false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this.getContext(), EtractionService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(STATEMENTS_GET_LOADER, null, this);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);

        mStatementsAdapter = new StatementsAdapter();
        mRecyclerView.setAdapter(mStatementsAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_common_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_common_error);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
//            Intent intent = new Intent(this.getContext(), EtractionService.class);
//            getActivity().stopService(intent);
        }
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

    @Override
    public void newMessage(JsonElement msg) {
        final StatementMsg statementMsg = new Gson().fromJson(msg, StatementMsg.StatementMessageDTO.class).getMessage();
        //some notification sound
        MediaPlayer player = MediaPlayer.create(this.getContext(), R.raw.you_wouldnt_believe);
        final boolean isScrolledBottom = isLastItemDisplaying(mRecyclerView, false);
        player.start();
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatementsAdapter.addStatementMessage(statementMsg);
                // Do not scroll the list when user is watching previous messages
                if (isScrolledBottom) {
                    mRecyclerView.scrollToPosition(0);
                }
            }
        });
    }
}
