package pl.poznan.put.etraction;

import android.os.Bundle;
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
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.poznan.put.etraction.listener.EndlessRecyclerViewScrollListener;
import pl.poznan.put.etraction.model.ChatMessageMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 01.05.2017.
 */

public class ChatFragment extends BaseRecyclerViewFragment implements LoaderManager.LoaderCallbacks<List<ChatMessageMsg>>{

    private static final String TAG = ChatFragment.class.getSimpleName();
    //id of loader
    private static final int CHAT_LOADER = 71;

    private ChatAdapter mChatAdapter;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putInt(NetworkUtils.CHAT_PAGE_PARAM, 1);
        getLoaderManager().initLoader(CHAT_LOADER, bundle, this);
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

        mChatAdapter = new ChatAdapter(this.getActivity());
        mRecyclerView.setAdapter(mChatAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_common_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_common_error);

        final LoaderManager.LoaderCallbacks callback = this; // May be moved to Listener constructor
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Bundle bundle = new Bundle();
                bundle.putInt(NetworkUtils.CHAT_PAGE_PARAM, page);
                getLoaderManager().restartLoader(CHAT_LOADER, bundle, callback);

            }
        });
    }


    private List<ChatMessageMsg> createDumbData(){
        List<ChatMessageMsg> chatMessageList = new ArrayList<>();
        ChatMessageMsg msg = new ChatMessageMsg();
        msg.setAuthor("Janusz");
        msg.setContent("dupa dupa dupa dupa dupa dupa dupa dupa accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum");
        msg.setDate(new Date());
        chatMessageList.add(msg);

        ChatMessageMsg msg2 = new ChatMessageMsg();
        msg2.setAuthor("Zbych");
        msg2.setContent("dupa dupa dupa dupa dupa dupa dupa dupa accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum");
        msg2.setDate(new Date());
        chatMessageList.add(msg2);
        return chatMessageList;
    }

    @Override
    public Loader<List<ChatMessageMsg>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<ChatMessageMsg>>(this.getContext()) {

            List<ChatMessageMsg> mChatResponse;

            @Override
            protected void onStartLoading() {
                if (mChatResponse != null){
                    Log.d(TAG, "DELIVERED CHAT MESSAGES");
                    deliverResult(mChatResponse);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    Log.d(TAG, "LOADED CHAT MESSAGES");
                    forceLoad();
                }
            }

            @Override
            public List<ChatMessageMsg> loadInBackground() {

                try {
                    Map<String, String> params = new HashMap<>();
                    if (args != null && args.containsKey(NetworkUtils.CHAT_PAGE_PARAM))
                        params.put(NetworkUtils.CHAT_PAGE_PARAM, String.valueOf(args.getInt(NetworkUtils.CHAT_PAGE_PARAM)));
                    else
                        params.put(NetworkUtils.CHAT_PAGE_PARAM, "1");

                    Log.d(TAG, "LOADING CHAT PAGE NR: " + params.get(NetworkUtils.CHAT_PAGE_PARAM));
                    params.put(NetworkUtils.CHAT_MESSAGES_PER_PAGE_PARAM, NetworkUtils.CHAT_MESSAGES_PER_PAGE_VALUE);
                    URL statementsUrl = NetworkUtils.buildUrlWithParams(NetworkUtils.CHAT_MESSAGES_BASE_URL, params);
                    String statementsGetResults = NetworkUtils.getResponseFromHttpUrl(statementsUrl);
                    Gson gson = new Gson();
                    return gson.fromJson(statementsGetResults, ChatMessageMsg.ChatMessagesMsg.class).getChatMessages();
                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Can not get chat data!", e);
                    return null;
                }
            }

            @Override
            public void deliverResult(List<ChatMessageMsg> data) {
                mChatResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<ChatMessageMsg>> loader, List<ChatMessageMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mChatAdapter.addChatMeesagesData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ChatMessageMsg>> loader) {

    }
}
