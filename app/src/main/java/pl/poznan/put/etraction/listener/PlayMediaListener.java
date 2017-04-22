package pl.poznan.put.etraction.listener;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import pl.poznan.put.etraction.R;

/**
 * Created by Marcin on 22.04.2017.
 */

public class PlayMediaListener implements IPlayMediaListener {

    private FragmentActivity mFragmentActivity;
    public PlayMediaListener(FragmentActivity fragmentActivity){
        mFragmentActivity = fragmentActivity;
    }

    @Override
    public void playMedia(Uri file) {
        Intent intent = new Intent(Intent.ACTION_VIEW );
        intent.setDataAndType(file, "video/*");
        if (intent.resolveActivity(mFragmentActivity.getPackageManager()) != null)
            mFragmentActivity.startActivity(intent);
        else
            Toast.makeText(mFragmentActivity, R.string.cant_resolve_intent, Toast.LENGTH_SHORT).show();

    }
}
