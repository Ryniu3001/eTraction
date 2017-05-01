package pl.poznan.put.etraction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
        View view = inflater.inflate(R.layout.restaurant_menu_card_view, parent, false);
        return new RestaurantMenuAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RestaurantMenuAdapterViewHolder holder, int position) {

        RestaurantMenuItemMsg itemMsg = mMenuItemsList.get(position);

        holder.mName.setText(itemMsg.getName());
        holder.mPrice.setText(DecimalFormat.getCurrencyInstance().format(itemMsg.getPrice()));
        Picasso.with(holder.mImage.getContext())
                .load(itemMsg.getImageUrl())
                .placeholder(R.drawable.poster_loading)
                .error(R.drawable.poster_error)
                .into(holder.mImage);
        holder.mWeight.setText(itemMsg.getWeight() + " dag");

    }

    @Override
    public int getItemCount() {
        if (mMenuItemsList == null) return 0;
        return mMenuItemsList.size();
    }

    public class RestaurantMenuAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mName;
        final TextView mPrice;
        final ImageView mImage;
        final TextView mWeight;

        public RestaurantMenuAdapterViewHolder(final View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_meal_name);
            mPrice = (TextView) itemView.findViewById(R.id.tv_meal_price);
            mImage = (ImageView) itemView.findViewById(R.id.iv_meal_image);
            mWeight = (TextView) itemView.findViewById(R.id.tv_meal_weight);
        }
    }
}
