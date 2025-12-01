package org.gluu.agama.profile.service;

import java.util.Map;

import org.gluu.agama.profile.UserProfileEditServiceImpl;

public abstract class UserProfileEditService {

    public abstract Map<String, String> getUserEntity(String userId);

    public abstract Map<String, Object> validateInputs(Map<String, String> profile);

    public abstract String updateProfile (Map<String, String> profile, String userId);

    public static UserProfileEditService getInstance(){
        return UserProfileEditServiceImpl.getInstance();
    }

}
