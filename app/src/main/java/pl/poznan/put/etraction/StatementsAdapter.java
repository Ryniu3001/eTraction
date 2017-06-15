package pl.poznan.put.etraction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.etraction.model.StatementMsg;

/**
 * Created by Marcin on 11.04.2017.
 */

public class StatementsAdapter extends RecyclerView.Adapter<StatementsAdapter.StatementsAdapterViewHolder> {

    private List<StatementMsg> mStatementsList;

    public void setStatementsData(List<StatementMsg> statementsList){
        mStatementsList = statementsList;
        notifyDataSetChanged();
    }

    public void addStatementMessage(StatementMsg message){
        if (mStatementsList == null)
            mStatementsList = new ArrayList<>();

        mStatementsList.add(0, message);
        notifyItemInserted(0);
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item you
     *                  can use this viewType integer to provide a different layout
     * @return
     */
    @Override
    public StatementsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.statements_card_view, parent, false);
        return new StatementsAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the statement
     * for this particular position, using the "position" argument that is conveniently
     * passed into us.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(StatementsAdapterViewHolder holder, int position) {
        StatementMsg statementsMsg = mStatementsList.get(position);
        holder.mTitle.setText(statementsMsg.getTitle());
        String dateTime = DateFormat.getDateTimeInstance().format(statementsMsg.getDateTime());
        holder.mDateTime.setText(dateTime);
        holder.mContent.setText(statementsMsg.getContent());

    }

    @Override
    public int getItemCount() {
        if (null == mStatementsList) return 0;
        return mStatementsList.size();
    }

    /**
     * Allows caching each item.
     */
    public class StatementsAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mTitle;
        final TextView mDateTime;
        final TextView mContent;

        public StatementsAdapterViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_statement_title);
            mDateTime = (TextView) itemView.findViewById(R.id.tv_statement_time);
            mContent = (TextView) itemView.findViewById(R.id.tv_statement_content);
        }
    }
}
