package Capstone.Users.bo;

import jakarta.persistence.Embeddable;

@Embeddable
public class TempTransactionDTO {
    private Long id;
    private Double amount;
    private String dateTime;
    private String status;

    public TempTransactionDTO() {
    }

    public TempTransactionDTO(Long id, Double amount, String dateTime, String status) {
        this.id = id;
        this.amount = amount;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
