package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.poznan.put.etraction.model.ChatMessageMsg;

/**
 * Created by Marcin on 01.05.2017.
 */

public class ChatFragment extends BaseRecyclerViewFragment {

    private static final String TAG = ChatFragment.class.getSimpleName();
    //id of loader
    private static final int CHAT_LOADER = 71;

    private ChatAdapter mChatAdapter;
    private ProgressBar mLoadingIndicator;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getLoaderManager().initLoader(CHAT_LOADER, null, this);
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

        mChatAdapter.setChatData(createDumbData());
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
}
