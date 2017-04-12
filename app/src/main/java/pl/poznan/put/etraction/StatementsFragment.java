package pl.poznan.put.etraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.poznan.put.etraction.model.StatementMsg;

/**
 * Created by Marcin on 11.04.2017.
 */

public class StatementsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StatementsAdapter mStatementsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_statements);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        /*
        Ustawienie na true moze polepszyc wydajnosc. Warto zastanowic sie nad ograniczeniem liczby znakow komunikatu i ustawić flagę na true.
         */
        mRecyclerView.setHasFixedSize(false);

        mStatementsAdapter = new StatementsAdapter();
        mRecyclerView.setAdapter(mStatementsAdapter);

        //TODO: Progrss bar os sth indicating loading time

        loadData();
    }

    private void loadData(){
        List<StatementMsg> statementMsgList = new ArrayList<>();
        StatementMsg msg1 = new StatementMsg();
        msg1.setId(1);
        msg1.setTitle("Awaria pociągu");
        msg1.setDateTime(new Date());
        msg1.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque dictum tristique commodo. Vivamus pharetra finibus velit, et aliquam nunc sollicitudin sed. Vestibulum condimentum auctor mi nec egestas.");

        StatementMsg msg2 = new StatementMsg();
        msg2.setId(2);
        msg2.setTitle("Opóźnienie");
        msg2.setDateTime(new Date());
        msg2.setContent("Cras porttitor felis sed purus lobortis lobortis. Nulla feugiat magna auctor nisi auctor, quis tincidunt leo cursus. Cras ut congue elit. Sed egestas fringilla ornare. Nunc eu efficitur ipsum. Etiam non faucibus elit. Ut tempus ornare posuere. Vivamus cursus erat a aliquam pharetra. Fusce feugiat sem at arcu congue, vitae consectetur quam vehicula");

        statementMsgList.add(msg1);
        statementMsgList.add(msg2);

        mStatementsAdapter.setStatementsData(statementMsgList);
    }
}
