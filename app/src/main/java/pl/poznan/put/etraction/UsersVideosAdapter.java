package pl.poznan.put.etraction;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.poznan.put.etraction.listener.IPlayMediaListener;
import pl.poznan.put.etraction.model.HttpUrlResponse;
import pl.poznan.put.etraction.model.UserVideoMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 14.04.2017.
 */

public class UsersVideosAdapter extends RecyclerView.Adapter<UsersVideosAdapter.MoviesAdapterViewHolder>{

    private List<UserVideoMsg> mUsersVideosList;
    private IPlayMediaListener playVideoListener;
    private boolean removableVideo = false;


    public UsersVideosAdapter(IPlayMediaListener listener){
        playVideoListener = listener;
    }

    public void setVideosData(List<UserVideoMsg> videoList){
        mUsersVideosList = videoList;
        notifyDataSetChanged();
    }

    public void addVideo(UserVideoMsg videoMsg){
        if (mUsersVideosList == null){
            mUsersVideosList = new ArrayList<>();
        }
        mUsersVideosList.add(videoMsg);
        notifyItemInserted(mUsersVideosList.size() - 1);
    }

    public void removeVideo(int id){
        if (mUsersVideosList != null){
            for (int i = 0; i < mUsersVideosList.size(); i++){
                if (mUsersVideosList.get(i).getId() == id){
                    mUsersVideosList.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_videos_card_view, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        UserVideoMsg videoMsg = mUsersVideosList.get(position);
        holder.mTitle.setText(videoMsg.getTitle());
        holder.mAuthor.setText(videoMsg.getAuthor());
        holder.mCreatedAt.setText(formatDate(videoMsg.getCreated()));
        holder.uri = videoMsg.getVideoUrl();
        holder.id = videoMsg.getId();
    }

    @Override
    public int getItemCount() {
        if (null == mUsersVideosList) return 0;
        return mUsersVideosList.size();
    }

    private String formatDate(Date date){
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return f.format(date);
    }

    public void setRemovableVideo(boolean removableVideo) {
        this.removableVideo = removableVideo;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private static final int CONTEXT_MENU_DELETE_ID = 69;
        int id;
        final TextView mTitle;
        final TextView mCreatedAt;
        final TextView mAuthor;
        String uri;

        public MoviesAdapterViewHolder(final View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
            mCreatedAt = (TextView) itemView.findViewById(R.id.tv_video_created);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_video_author);
            itemView.setOnClickListener(listener);
            if (removableVideo) {
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Wybierz akcję:");
            MenuItem menuItem = menu.add(ContextMenu.NONE, CONTEXT_MENU_DELETE_ID, ContextMenu.NONE, v.getResources().getString(R.string.menu_delete));//groupId, itemId, order, title
            menuItem.setOnMenuItemClickListener(this);

        }

        private View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playVideoListener.playMedia(Uri.parse(uri));
            }
        };

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == CONTEXT_MENU_DELETE_ID){
                RemoveVideoTask task = new RemoveVideoTask(this.itemView.getContext());
                task.execute(String.valueOf(this.id));
            }
            return false;
        }
    }

    public class RemoveVideoTask extends AsyncTask<String, Void, HttpUrlResponse<String>> {

        private Context context;

        RemoveVideoTask(Context context){
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpUrlResponse doInBackground(String... params) {
            String id = params[0];
            URL requestUrl = NetworkUtils.buildUrl(NetworkUtils.USERS_VIDEOS_BASE_URL, id);
            HttpUrlResponse<String> response = NetworkUtils.saveDataToServer(requestUrl, "DELETE", null);
            return response;
        }

        @Override
        protected void onPostExecute(HttpUrlResponse<String> response) {
            if (response.isOk()) {
                if (context != null)
                    Toast.makeText(this.context, context.getString(R.string.user_video_remove_confirmation), Toast.LENGTH_SHORT).show();
                removeVideo(Integer.parseInt(response.getObjectResponse()));
            }
        }
    }
}
