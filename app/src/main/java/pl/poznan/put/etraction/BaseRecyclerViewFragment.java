package pl.poznan.put.etraction;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Marcin on 22.04.2017.
 */

public abstract class BaseRecyclerViewFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected TextView mErrorView;


    protected void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    protected void showDataView() {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
