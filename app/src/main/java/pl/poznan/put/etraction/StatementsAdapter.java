package pl.poznan.put.etraction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.poznan.put.etraction.model.StatementMsg;

/**
 * Created by Marcin on 11.04.2017.
 */

public class StatementsAdapter extends RecyclerView.Adapter<StatementsAdapter.StatementsAdapterViewHolder> {

    private List<StatementMsg> mStatementsList;

    public void setStatementsData(List<StatementMsg> statementsList){
        mStatementsList = statementsList;
    }

    @Override
    public StatementsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForItem = R.layout.statements_card_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForItem, parent, false);
        return new StatementsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatementsAdapterViewHolder holder, int position) {

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

        TextView mTitle;
        TextView mDateTime;
        TextView mContent;

        public StatementsAdapterViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_statement_title);
            mDateTime = (TextView) itemView.findViewById(R.id.tv_statement_time);
            mContent = (TextView) itemView.findViewById(R.id.tv_statement_content);
        }
    }
}
