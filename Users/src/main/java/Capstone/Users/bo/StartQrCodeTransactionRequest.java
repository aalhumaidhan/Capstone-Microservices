package Capstone.Users.bo;

public class StartQrCodeTransactionRequest {

    private String senderId;

    private Double amount;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
