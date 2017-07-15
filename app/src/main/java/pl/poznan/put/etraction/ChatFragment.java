package pl.poznan.put.etraction;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.poznan.put.etraction.listener.EndlessRecyclerViewScrollListener;
import pl.poznan.put.etraction.model.ChatMessageMsg;
import pl.poznan.put.etraction.model.HttpUrlResponse;
import pl.poznan.put.etraction.service.EtractionService;
import pl.poznan.put.etraction.utilities.NetworkUtils;
import pl.poznan.put.etraction.utilities.PrefUtility;

/**
 * Created by Marcin on 01.05.2017.
 */

public class ChatFragment extends BaseRecyclerViewFragment implements
        LoaderManager.LoaderCallbacks<List<ChatMessageMsg>>,
        EtractionService.EtractionServiceListener{

    private static final String TAG = ChatFragment.class.getSimpleName();
    //id of loader
    private static final int CHAT_LOADER = 71;

    private ChatAdapter mChatAdapter;
    private ProgressBar mLoadingIndicator;
    private ImageButton mSendButton;
    private EditText mEnteredMessage;
    private Toast mToast;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putInt(NetworkUtils.CHAT_PAGE_PARAM, 1);
        getLoaderManager().initLoader(CHAT_LOADER, bundle, this);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_rv_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_common);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(false);

        String nickname = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getString(getString(R.string.pref_nickname_key), "");
        mChatAdapter = new ChatAdapter(this.getActivity(), nickname);
        mRecyclerView.setAdapter(mChatAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_common_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_common_error);
        mSendButton = (ImageButton) view.findViewById(R.id.iv_chat_send);
        mEnteredMessage = (EditText) view.findViewById(R.id.et_chat_message_edit);

        final LoaderManager.LoaderCallbacks callback = this; // May be moved to Listener constructor
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Bundle bundle = new Bundle();
                bundle.putInt(NetworkUtils.CHAT_PAGE_PARAM, page);
                getLoaderManager().restartLoader(CHAT_LOADER, bundle, callback);

            }
        });

        setMessageSendAction();
    }

    private void setMessageSendAction() {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEnteredMessage.getText().toString();
                if (!message.isEmpty()) {
                    new SendMessageTask().execute(message);
                }
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

    /**
     * New message from notification (WebSockets)
     * @param msg
     */
    @Override
    public void newMessage(JsonElement msg) {
        final ChatMessageMsg chatMessage = new Gson().fromJson(msg, ChatMessageMsg.ChatMessageDTO.class).getMessage();
        if (chatMessage.getAuthor().equals(PreferenceManager.getDefaultSharedPreferences(this.getContext()).getString(getString(R.string.pref_nickname_key), ""))){
            return;
        }
        //some notification sound
        MediaPlayer player = MediaPlayer.create(this.getContext(), R.raw.you_wouldnt_believe);
        final boolean isScrolledBottom = isLastItemDisplaying(mRecyclerView, true);
        player.start();
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChatAdapter.addChatMessage(chatMessage);
                // Do not scroll the list when user is watching previous messages
                if (isScrolledBottom) {
                    mRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
                }
            }
        });
    }

    public class SendMessageTask extends AsyncTask<String, Void, HttpUrlResponse<ChatMessageMsg> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSendButton.setEnabled(false);
            mEnteredMessage.setText("");
        }

        @Override
        protected HttpUrlResponse<ChatMessageMsg>  doInBackground(String... params) {

            String message = params[0];
            ChatMessageMsg msg = new ChatMessageMsg();
            msg.setContent(message);
            ChatMessageMsg.ChatMessageDTO chatMessageDTO = new ChatMessageMsg.ChatMessageDTO();
            chatMessageDTO.setMessage(msg);
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(chatMessageDTO);
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
            URL userUrl = NetworkUtils.buildUrl(NetworkUtils.CHAT_MESSAGES_BASE_URL);

            HttpUrlResponse<ChatMessageMsg> response = NetworkUtils.saveDataToServer(userUrl, "POST", jsonObject);
            if (response.isOk()) {
                ChatMessageMsg chatMessage = new Gson().fromJson(response.getJsonResponse(), ChatMessageMsg.ChatMessageDTO.class).getMessage();
                response.setObjectResponse(chatMessage);
            }
            return response;
        }

        @Override
        protected void onPostExecute(HttpUrlResponse<ChatMessageMsg>  response) {
            super.onPostExecute(response);
            mSendButton.setEnabled(true);
            if (response.getObjectResponse() == null && response.getResponseCode() != HttpURLConnection.HTTP_UNAUTHORIZED){
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(getContext(), getString(R.string.generic_error_msg), Toast.LENGTH_SHORT);
                mToast.show();
            } else if (response.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                //if any nickname is set - clear
                PrefUtility.changeNickName(getContext(), null);
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(getContext(), getString(R.string.nickname_empty), Toast.LENGTH_LONG);
                mToast.show();
                Intent startSettingsActivity = new Intent(getContext(), SettingsActivity.class);
                startSettingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startSettingsActivity);

            } else {
                final boolean isScrolledBottom = isLastItemDisplaying(mRecyclerView, true);
                mChatAdapter.addChatMessage(response.getObjectResponse());
                if (isScrolledBottom) {
                    mRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
                }
            }
        }
    }
}
