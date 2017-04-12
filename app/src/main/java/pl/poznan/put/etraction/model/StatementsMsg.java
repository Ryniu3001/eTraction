package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Klasa przechowująca dane komunikatu. Potrzebna do deserializacji JSON-a w celu odzwierciedlenia jego struktury.
 * Created by Marcin on 11.04.2017.
 */

public class StatementsMsg {

    @SerializedName("statements")
    List<StatementMsg> statements;

    public List<StatementMsg> getStatements() {
        return statements;
    }

    public void setStatements(List<StatementMsg> statements) {
        this.statements = statements;
    }


}
