package Capstone.Users.bo;

import Capstone.Users.Enums.Methods;

public class MakeQRCodeTransactionRequest {

    private String senderId;
    private String receiverId;
    private Double amount;
    private Methods method;
    private String associateId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String id) {
        senderId = id;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Methods getMethod() {
        return method;
    }

    public void setMethod(Methods method) {
        this.method = method;
    }

    public String getAssociateId() {
        return associateId;
    }

    public void setAssociateId(String associateId) {
        this.associateId = associateId;
    }
}

