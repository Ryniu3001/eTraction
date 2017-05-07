package pl.poznan.put.etraction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

import pl.poznan.put.etraction.model.UserMsg;
import pl.poznan.put.etraction.utilities.NetworkUtils;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    ProgressBar mSaveIndicator;
    TextView mMessage;
    String mActualNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        mSaveIndicator = (ProgressBar) findViewById(R.id.pb_settings_save_indicator);
        mMessage = (TextView) findViewById(R.id.tv_settings_msg);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
        final String  prefKey = key;
        final String newNickname = sharedPreferences.getString(key, null);
        if (key.equals(getString(R.string.pref_nickname_key)) && !newNickname.equals(mActualNickname)) {
            new UserAsyncTask(this).execute(newNickname);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getResources().getString(R.string.pref_nickname_key))){
            mActualNickname = PreferenceManager.getDefaultSharedPreferences(this).getString(preference.getKey(), null);
        }
        return true;
    }

    public class UserAsyncTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public UserAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSaveIndicator.setVisibility(View.VISIBLE);
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setText(getResources().getString(R.string.settings_nickname_saving));
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String nickname = params[0];
            UserMsg msg = new UserMsg();
            msg.setUsername(nickname);
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(msg);
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
            URL userUrl = NetworkUtils.buildUrl(NetworkUtils.USER_BASE_URL);
            try {
                String method;
                if (NetworkUtils.getResponseFromHttpUrl(userUrl, "dupa2").isOk())
                    method = "PUT";
                else
                    method = "POST";

                return NetworkUtils.saveDataToServer(userUrl, "dupa2", method, jsonObject);

            } catch (IOException e) {
                Log.e(TAG, "Exception while invoking service" ,e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mSaveIndicator.setVisibility(View.INVISIBLE);
            if (success) {
                mMessage.setVisibility(View.INVISIBLE);
            } else {
                mMessage.setText(getResources().getString(R.string.settings_nickname_exists_error_msg));
                //Restore previous nickname
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getResources().getString(R.string.pref_nickname_key), mActualNickname);
                editor.commit();
            }
        }
    }
}
