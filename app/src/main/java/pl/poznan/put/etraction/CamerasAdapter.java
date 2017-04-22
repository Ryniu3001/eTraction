package pl.poznan.put.etraction;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.poznan.put.etraction.model.CameraMsg;

/**
 * Created by Marcin on 19.04.2017.
 */

public class CamerasAdapter extends RecyclerView.Adapter<CamerasAdapter.CamerasAdapterViewHolder> {


    private List<CameraMsg> mCamerasList;
    private PlayMediaListener mPlayMediaListener;

    public CamerasAdapter(PlayMediaListener listener) {
        mPlayMediaListener = listener;
    }

    public void setCamerasData(List<CameraMsg> camerasList){
        mCamerasList = camerasList;
        notifyDataSetChanged();
    }

    @Override
    public CamerasAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.camera_card_view, parent, false);
        return new CamerasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CamerasAdapterViewHolder holder, int position) {
        CameraMsg movieMsg = mCamerasList.get(position);
        holder.mName.setText(movieMsg.getName());
        holder.url = movieMsg.getUrl();
    }

    @Override
    public int getItemCount() {
        if (mCamerasList == null) return 0;
        return mCamerasList.size();
    }

    public class CamerasAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mName;
        String url;

        public CamerasAdapterViewHolder(final View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_camera_name);
            itemView.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mPlayMediaListener.playMedia(Uri.parse(url));
            }
        };
    }
}
