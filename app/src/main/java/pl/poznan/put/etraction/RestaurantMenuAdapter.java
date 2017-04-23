package pl.poznan.put.etraction;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import pl.poznan.put.etraction.model.RestaurantMenuItemMsg;

/**
 * Created by Marcin on 23.04.2017.
 */

public class RestaurantMenuAdapter extends RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuAdapterViewHolder> {

    private List<RestaurantMenuItemMsg> mMenuItemsList;

    public void setMenuData(List<RestaurantMenuItemMsg> menuItemsList){
        mMenuItemsList = menuItemsList;
        notifyDataSetChanged();
    }

    @Override
    public RestaurantMenuAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movies_card_view, parent, false);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_movie_poster);
        iv.getLayoutParams().width = (int) convertDpToPixel(200.0f, parent.getContext());
        return new RestaurantMenuAdapterViewHolder(view);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @Override
    public void onBindViewHolder(RestaurantMenuAdapterViewHolder holder, int position) {

        RestaurantMenuItemMsg itemMsg = mMenuItemsList.get(position);

        holder.mTitle.setText(itemMsg.getName());
        holder.mPrice.setText(DecimalFormat.getCurrencyInstance().format(itemMsg.getPrice()));
        Picasso.with(holder.mImage.getContext())
                .load("http://www.halopizza-luban.pl/wp-content/uploads/2016/02/pizza-1.jpg")
                .placeholder(R.drawable.poster_loading)
                .error(R.drawable.poster_error)
                .into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        if (mMenuItemsList == null) return 0;
        return mMenuItemsList.size();
    }

    public class RestaurantMenuAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mTitle;
        final TextView mPrice;
        final ImageView mImage;

        public RestaurantMenuAdapterViewHolder(final View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            mPrice = (TextView) itemView.findViewById(R.id.tv_movie_duration);
            mImage = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }
    }
}
