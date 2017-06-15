package pl.poznan.put.etraction;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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

    /**
     * Check whether the last item in RecyclerView is being displayed or not
     *
     * @param recyclerView which you would like to check
     * @param bottomEnd true is the last items are on the bottom of the recyclerView, false if otherwise
     * @return true if last position was Visible and false Otherwise
     */
    protected boolean isLastItemDisplaying(RecyclerView recyclerView, boolean bottomEnd) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            if (bottomEnd) {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                    return true;
            } else {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == 0)
                    return true;
            }
        }
        return false;
    }
}
