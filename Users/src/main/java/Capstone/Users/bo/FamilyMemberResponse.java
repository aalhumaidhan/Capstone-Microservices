package Capstone.Users.bo;

import Capstone.Users.entity.DependentEntity;

import java.util.List;

public class FamilyMemberResponse {

    private List<DependentEntity> familyMembers;

    private String message;

    public List<DependentEntity> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<DependentEntity> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

