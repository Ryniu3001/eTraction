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

    public ChatAdapter(FragmentActivity fragmentActivity){
        mFragmentActivity = fragmentActivity;
    }

    public void setChatData(List<ChatMessageMsg> chatMessagesList){
        mChatMessagesList = chatMessagesList;
        notifyDataSetChanged();
    }

    @Override
    public ChatMessageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_message_card_view, parent, false);
       // RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return new ChatMessageAdapterViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatMessagesList.get(position).getAuthor().equals("Janusz"))
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
        layoutParams.width =  new Double(CHAT_BUBBLE_WIDTH * width).intValue();

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
        }
    }
}
