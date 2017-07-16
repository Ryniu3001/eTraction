package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Marcin on 16.07.2017.
 */

public class UserVideosTabsFragment extends BaseRecyclerViewFragment {
    private static final String TAG = UserVideosTabsFragment.class.getSimpleName();
    private FragmentTabHost mTabHost;

    public static final String ALL_MOVIES = "all_movies";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_videos_rv_layout, container, false);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        Bundle ownVideosBundle = new Bundle();
        ownVideosBundle.putBoolean(ALL_MOVIES, false);

        Bundle allVideosBundle = new Bundle();
        allVideosBundle.putBoolean(ALL_MOVIES, true);

        mTabHost.addTab(mTabHost.newTabSpec("fragmenta").setIndicator(getString(R.string.users_videos_tab)), UsersVideosFragment.class, allVideosBundle);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator(getString(R.string.user_own_videos_tab)), UsersVideosFragment.class, ownVideosBundle);

        return rootView;
    }
}
