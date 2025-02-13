package Capstone.Users.bo;

import Capstone.Users.entity.DependentEntity;
import Capstone.Users.entity.PersonalEntity;

public class SetLimitResponse {

    private DependentEntity familyMember;
    private String message;

    public DependentEntity getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(DependentEntity familyMember) {
        this.familyMember = familyMember;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

