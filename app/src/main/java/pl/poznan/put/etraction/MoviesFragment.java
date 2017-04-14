package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.etraction.model.MovieMsg;

/**
 * Created by Marcin on 14.04.2017.
 */

public class MoviesFragment extends Fragment {

    private static final String TAG = MoviesFragment.class.getSimpleName();
    //id of loader
    private static final int MOVIES_GET_LOADER = 21;

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorView;
    private ProgressBar mLoadingIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movies, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_movies);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter();
        mRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_movies_loading_indicator);
        mErrorView = (TextView) view.findViewById(R.id.tv_movies_error);

        loadFakeData();
    }

    private void loadFakeData(){
        MovieMsg msg = new MovieMsg();
        msg.setTitle("Power Rangers");
        msg.setGenre("Akcja, Sci-Fi");
        msg.setLength(124);
        msg.setPosterUrl("http://1.fwcdn.pl/po/43/87/714387/7776413.3.jpg");
        List<MovieMsg> movies = new ArrayList<>();
        movies.add(msg);
        mMoviesAdapter.setMoviesData(movies);
    }


    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void showStatementsDataView() {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

}
