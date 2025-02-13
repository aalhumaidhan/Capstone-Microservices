package Capstone.Transactions.bo;

import java.util.List;

public class GetTransactionsBySenderResponse {

    private List<TransactionDTO> transactions;
    private String message;

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
