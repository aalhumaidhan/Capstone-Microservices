package Capstone.Users.bo;

public class GetTransactionByIdResponse {

    private TransactionDTO transaction;

    private String message;

    public TransactionDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDTO transaction) {
        this.transaction = transaction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


