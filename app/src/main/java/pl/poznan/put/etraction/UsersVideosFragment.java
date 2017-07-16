package pl.poznan.put.etraction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import pl.poznan.put.etraction.callback.UserOwnVideosCallback;
import pl.poznan.put.etraction.callback.UsersAllVideosCallback;
import pl.poznan.put.etraction.listener.PlayMediaListener;
import pl.poznan.put.etraction.model.UserVideoMsg;
import pl.poznan.put.etraction.permission.PermissionUtils;
import pl.poznan.put.etraction.utilities.MultipartUtility;
import pl.poznan.put.etraction.utilities.NetworkUtils;

/**
 * Created by Marcin on 16.07.2017.
 */

public class UsersVideosFragment extends BaseRecyclerViewFragment implements
        UsersAllVideosCallback.UsersAllVideosCallbackListener,
        UserOwnVideosCallback.UserOwnVideosCallbackListener {

    private static final String TAG = UsersVideosFragment.class.getSimpleName();
    //id of loader
    private static final int USERS_VIDEOS_LOADER = 55;
    private static final int USER_OWN_VIDEOS_LOADER = 56;

    static  final int REQUEST_VIDEO_CAPTURE = 1;

    private UsersVideosAdapter mVideosAdapter;
    private ProgressBar mLoadingIndicator;
    private UsersAllVideosCallback allVideosLoaderCallback;
    private UserOwnVideosCallback ownVideosLoaderCallback;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (allVideos()) {
            if (allVideosLoaderCallback == null)
                allVideosLoaderCallback = new UsersAllVideosCallback(this.getContext(), this);
            getLoaderManager().initLoader(USERS_VIDEOS_LOADER, null, allVideosLoaderCallback);
        } else {
            if (ownVideosLoaderCallback == null)
                ownVideosLoaderCallback = new UserOwnVideosCallback(this.getContext(), this);
            getLoaderManager().initLoader(USER_OWN_VIDEOS_LOADER, null, ownVideosLoaderCallback);
        }
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
        LinearLayoutManager layoutManager;
        switch(Resources.getSystem().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
            default:
                layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                layoutManager = new GridLayoutManager(this.getContext(), 2);
                break;
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mVideosAdapter = new UsersVideosAdapter(new PlayMediaListener(this.getActivity()));
        mRecyclerView.setAdapter(mVideosAdapter);

        if (!allVideos()){
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVideo();
                }
            });
            mVideosAdapter.setRemovableVideo(true);
        }



        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_common_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_common_error);
    }

    private boolean allVideos(){
        return getArguments() != null && getArguments().containsKey(UserVideosTabsFragment.ALL_MOVIES)
                && getArguments().getBoolean(UserVideosTabsFragment.ALL_MOVIES);
    }

    public void addVideo(){
        if (checkPermissions()) {
            dispatchTakeVideoIntent();
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getContext(), String.valueOf(resultCode), Toast.LENGTH_LONG).show();
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK){
            Uri videoUri = data.getData();
            //byte [] fileBytesArray = fileToByteArray(videoUri);
            SendVideoTask task = new SendVideoTask();
            task.execute(getRealPathFromUri(videoUri));
        }
    }

    private byte [] fileToByteArray(Uri fileUri){
        File file = new File(getRealPathFromUri(fileUri));
        int size = (int)file.length();
        byte [] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return bytes;
    }

    public String getRealPathFromUri(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        String yourRealPath = null;
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            yourRealPath = cursor.getString(columnIndex);
        } else {
            // cursor doesn't have rows ...
        }
        cursor.close();
        return yourRealPath;
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PermissionUtils.READ_STOREAGE_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PermissionUtils.READ_STOREAGE_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            dispatchTakeVideoIntent();
        } else {
            showMissingPermissionError();
        }
    }

    private void showMissingPermissionError() {
        Toast.makeText(getActivity(), R.string.permission_required_read_storage_toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void beforeAllVideosRequest() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAllVideosLoad(List<UserVideoMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mVideosAdapter.setVideosData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }

    @Override
    public void beforeOwnVideosRequest() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOwnVideosLoad(List<UserVideoMsg> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mVideosAdapter.setVideosData(data);
        if (null == data){
            showErrorMessage();
        } else {
            showDataView();
        }
    }


    public class SendVideoTask extends AsyncTask<String, Void, UserVideoMsg> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected UserVideoMsg doInBackground(String... params) {

            String path = params[0];

            String charset = "UTF-8";
            String requestURL = NetworkUtils.USERS_VIDEOS_BASE_URL;

            MultipartUtility multipart = null;
            try {
                multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("user_video[title]", "dupaaaaaa");
                multipart.addFilePart("user_video[video]", new File(path));
                String response = multipart.finish(); // response from server.
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(response).getAsJsonObject();
                String userVideoJson = obj.get("user_video").getAsJsonObject().toString();
                UserVideoMsg userVideo = new Gson().fromJson(userVideoJson, UserVideoMsg.class);
                return userVideo;
            } catch (IOException e) {
                Log.e(TAG, "Can't send video because: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserVideoMsg response) {
            super.onPostExecute(response);
            mVideosAdapter.addVideo(response);
        }
    }
}
