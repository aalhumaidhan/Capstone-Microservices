package Capstone.Users.bo;

import Capstone.Users.entity.AssociateEntity;

import java.util.List;

public class BusinessAssociateListResponse {

    private List<AssociateEntity> associateList;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AssociateEntity> getAssociateList() {
        return associateList;
    }

    public void setAssociateList(List<AssociateEntity> associateList) {
        this.associateList = associateList;
    }
}
