package org.gluu.agama.profile.service;

import java.util.Map;

import org.gluu.agama.profile.UserProfileEdit;

public abstract class UserProfileEditService {

    public abstract Map<String, String> getUserEntity(String mail);

    public abstract String sendMail(String mail);

    public abstract Map<String, Object> validateInputs(Map<String, String> profile);

    public abstract String updateProfile (Map<String, String> profile, String mail);

    public static UserProfileEditService getInstance(){
        return UserProfileEdit.getInstance();
    }

}

