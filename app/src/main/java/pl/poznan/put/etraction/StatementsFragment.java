package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private RecyclerView mRecyclerView;
    private StatementsAdapter mStatementsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        URL statementsGetUrl = NetworkUtils.buildUrl();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, statementsGetUrl.toString());
        getLoaderManager().initLoader(STATEMENTS_GET_LOADER, queryBundle, this);
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
        /*
        Ustawienie na true moze polepszyc wydajnosc. Warto zastanowic sie nad ograniczeniem liczby znakow komunikatu i ustawić flagę na true.
         */
        mRecyclerView.setHasFixedSize(false);

        mStatementsAdapter = new StatementsAdapter();
        mRecyclerView.setAdapter(mStatementsAdapter);

        //TODO: Progrss bar os sth indicating loading time
    }

    @Override
    public Loader<List<StatementMsg>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<StatementMsg>>(this.getContext()) {

            //Cache the previous result (useful when user press home button and then came back to application or when rotate the screen)
            //TODO: aplikacja nie cachuje odpowiedzi po pierwszej rotacji ekranu... Przy następnych już tak
            List<StatementMsg> mStatementsResponse;

            @Override
            protected void onStartLoading() {

                if (args == null)
                   return;

                if (mStatementsResponse != null){
                    Log.d(TAG, "DELIVERED STATEMENTS");
                    deliverResult(mStatementsResponse);
                } else {
                    Log.d(TAG, "LOADED STATEMENTS");
                    forceLoad();
                }
            }

            @Override
            public List<StatementMsg> loadInBackground() {

                String statementsQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (TextUtils.isEmpty(statementsQueryUrlString))
                    return null;

                try {
                    URL statementsUrl = new URL(statementsQueryUrlString);
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
        mStatementsAdapter.setStatementsData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<StatementMsg>> loader) {

    }
}
