package pl.poznan.put.etraction;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.etraction.model.ChatMessageMsg;

/**
 * Created by Marcin on 01.05.2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatMessageAdapterViewHolder> {

    private final static int TYPE_ME = 0;
    private final static int TYPE_OTHERS = 2;

    private final static double CHAT_BUBBLE_WIDTH = 0.7;

    private List<ChatMessageMsg> mChatMessagesList;
    private FragmentActivity mFragmentActivity;
    private String mNickname;

    public ChatAdapter(FragmentActivity fragmentActivity, String nickname){
        mFragmentActivity = fragmentActivity;
        mNickname = nickname;
    }

    public void setChatData(List<ChatMessageMsg> chatMessagesList){
        mChatMessagesList = chatMessagesList;
        notifyItemRangeInserted(0, chatMessagesList.size());
    }

    public void addChatMeesagesData(List<ChatMessageMsg> chatMessagesList){
        if (mChatMessagesList == null)
            mChatMessagesList = new ArrayList<>();
        int position = mChatMessagesList.size();
        if (chatMessagesList != null) {
            mChatMessagesList.addAll(chatMessagesList);
            notifyItemRangeInserted(position, chatMessagesList.size());
        }
    }

    public void addChatMessage(ChatMessageMsg message){
        // Problem with order may occurs? Sort or hide seconds in message time to resolve problem
        if (mChatMessagesList == null)
            mChatMessagesList = new ArrayList<>();

        mChatMessagesList.add(message);
        notifyItemInserted(mChatMessagesList.size() - 1);
    }

    @Override
    public ChatMessageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_message_card_view, parent, false);
        return new ChatMessageAdapterViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatMessagesList.get(position).getAuthor().equals(mNickname))
            return TYPE_ME;
         else
            return TYPE_OTHERS;
    }

    @Override
    public void onBindViewHolder(ChatMessageAdapterViewHolder holder, int position) {
        ChatMessageMsg chatMsg = mChatMessagesList.get(position);
        holder.mAuthor.setText(chatMsg.getAuthor());
        String dateTime = DateFormat.getDateTimeInstance().format(chatMsg.getDate());
        holder.mDateTime.setText(dateTime);
        holder.mContent.setText(chatMsg.getContent());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels; //Screen width in pixels

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.bubble.getLayoutParams();
        layoutParams.width =  Double.valueOf(CHAT_BUBBLE_WIDTH * width).intValue();

        int type = getItemViewType(position);
        if (type == TYPE_ME) {
            layoutParams.gravity = Gravity.RIGHT;
            holder.bubble.setBackground(mFragmentActivity.getDrawable(R.drawable.bubble_green));
        } else {
            layoutParams.gravity = Gravity.LEFT;
            holder.bubble.setBackground(mFragmentActivity.getDrawable(R.drawable.bubble_yellow));
        }
    }

    @Override
    public int getItemCount() {
        if (mChatMessagesList == null) return 0;
        return mChatMessagesList.size();
    }

    public class ChatMessageAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mAuthor;
        final TextView mDateTime;
        final TextView mContent;
        final LinearLayout bubble;

        public ChatMessageAdapterViewHolder(View itemView) {
            super(itemView);
            bubble = (LinearLayout) itemView.findViewById(R.id.bubble);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_chat_author);
            mDateTime = (TextView) itemView.findViewById(R.id.tv_chat_time);
            mContent = (TextView) itemView.findViewById(R.id.tv_chat_content);

            bubble.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDateTime.setVisibility(mDateTime.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        };
    }
}
