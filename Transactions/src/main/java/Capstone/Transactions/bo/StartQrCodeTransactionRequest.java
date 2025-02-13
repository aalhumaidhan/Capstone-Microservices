package Capstone.Transactions.bo;

public class StartQrCodeTransactionRequest {

    private Long senderId;

    private Double amount;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
