package pl.poznan.put.etraction;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pl.poznan.put.etraction.model.MovieMsg;

/**
 * Created by Marcin on 14.04.2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{

    private List<MovieMsg> mMoviesList;

    public void setMoviesData(List<MovieMsg> moviesList){
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movies_card_view, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        MovieMsg movieMsg = mMoviesList.get(position);
        holder.mTitle.setText(movieMsg.getTitle());
        holder.mGenre.setText(movieMsg.getGenre());
        holder.mDuration.setText(createDurationString(movieMsg.getLength(), holder.mDuration.getResources()));
        Picasso.with(holder.mPoster.getContext())
                .load(movieMsg.getPosterUrl())
                .into(holder.mPoster);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesList) return 0;
        return mMoviesList.size();
    }

    private String createDurationString(int duration, Resources resources){
        int hours = duration / 60;
        int minutes = duration % 60;
        StringBuilder sb = new StringBuilder();
        if (hours != 0)
            sb.append(hours).append(" ").append(resources.getString(R.string.movie_hour)).append(" ");
         sb.append(minutes).append(" ").append(resources.getString(R.string.movie_min));
        return sb.toString();
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mTitle;
        final TextView mDuration;
        final TextView mGenre;
        final ImageView mPoster;

        public MoviesAdapterViewHolder(final View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            mDuration = (TextView) itemView.findViewById(R.id.tv_movie_duration);
            mGenre = (TextView) itemView.findViewById(R.id.tv_movie_genre);
            mPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }
    }
}
