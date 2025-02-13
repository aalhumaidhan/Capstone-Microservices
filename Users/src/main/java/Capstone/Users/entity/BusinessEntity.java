package Capstone.Users.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BusinessEntity extends UserEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String businessLicenseId;

    @Column(nullable = false)
    private String bankAccountNumber;

    @OneToMany
    @JoinColumn(name = "business_id")
    private List<AssociateEntity> associates = new ArrayList<>();

    @Column(nullable = false)
    private ArrayList<Long> transactionHistory = new ArrayList<>();


    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessLicenseId() {
        return businessLicenseId;
    }

    public void setBusinessLicenseId(String businessLicenseId) {
        this.businessLicenseId = businessLicenseId;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public List<AssociateEntity> getAssociates() {
        return associates;
    }

    public void addAssociate(AssociateEntity associate) {
        associates.add(associate);
    }

    @Override
    public String getRole() {
        return "Business";
    }

    public ArrayList<Long> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(ArrayList<Long> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}

