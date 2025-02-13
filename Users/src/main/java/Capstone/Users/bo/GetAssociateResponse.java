package Capstone.Users.bo;

import Capstone.Users.entity.AssociateEntity;

public class GetAssociateResponse {

    private AssociateEntity associate;

    private String message;

    public AssociateEntity getAssociate() {
        return associate;
    }

    public void setAssociate(AssociateEntity associate) {
        this.associate = associate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
