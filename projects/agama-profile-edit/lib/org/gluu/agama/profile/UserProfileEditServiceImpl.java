package org.gluu.agama.profile;

import io.jans.as.common.model.common.User;
import io.jans.as.common.service.common.EncryptionService;
import io.jans.as.common.service.common.UserService;
import io.jans.orm.exception.operation.EntryNotFoundException;
import io.jans.service.cdi.util.CdiUtil;
import io.jans.util.StringHelper;

import io.jans.agama.engine.script.LogUtils;
import java.io.IOException;
import io.jans.as.common.service.common.ConfigurationService;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.regex.Pattern;

import org.gluu.agama.profile.service.UserProfileEditService;

public class UserProfileEditServiceImpl extends UserProfileEditService{

    private static final String JANS_STATUS = "jansStatus";
    private static final String GENDER = "gender";
    private static final String COUNTRY = "country";
    private static final String CONFIRM_PASSWORD = "confirmPassword";
    private static final String MAIL = "mail";
    private static final String UID = "uid";
    private static final String DISPLAY_NAME = "displayName";
    private static final String FIRST_NAME = "givenName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String LAST_NAME = "sn";
    private static final String PASSWORD = "userPassword";
    private static final String INUM_ATTR = "inum";


    private static UserProfileEditServiceImpl INSTANCE = null;

    public UserProfileEditServiceImpl() {}

    public static synchronized UserProfileEditServiceImpl getInstance (){
        if(INSTANCE == null)
            INSTANCE = new UserProfileEditServiceImpl();
        return INSTANCE;
    }

    @Override
    public Map<String, String> getUserEntity(String userId) {
        Map<String, String> userMap = new HashMap<>();
        User user = getUser(UID, userId);
        boolean local = user != null;
        LogUtils.log("There is % local account for %", local ? "a" : "no", userId);
    
        if (local) {            
            String email = getSingleValuedAttr(user, MAIL);
            String inum = getSingleValuedAttr(user, INUM_ATTR);
            String firstName = getSingleValuedAttr(user, FIRST_NAME);
            String displayName = getSingleValuedAttr(user, DISPLAY_NAME);
            String lastName = getSingleValuedAttr(user, LAST_NAME);
            String middleName = getSingleValuedAttr(user, MIDDLE_NAME);
            // Creating a truly modifiable map
            userMap.put(UID, userId);
            userMap.put(INUM_ATTR, inum);
            userMap.put(MAIL, email);
            userMap.put(FIRST_NAME, firstName);           
            userMap.put(MIDDLE_NAME, middleName);
            userMap.put(LAST_NAME, lastName);
            userMap.put(DISPLAY_NAME, displayName);
    
            return userMap;
        }else{
            LogUtils.log("User not found for : %", userId);
        }
    
        return userMap;
    }

    @Override
    public Map<String, Object> validateInputs(Map<String, String> profile) {
        LogUtils.log("Validate inputs %", profile);
        Map<String, Object> result = new HashMap<>();
        if (StringHelper.isEmpty(profile.get(FIRST_NAME))) {
            result.put("valid", false);
            result.put("message", "Given name not provided");
            return result;
        }
        if (StringHelper.isEmpty(profile.get(DISPLAY_NAME))) {
            result.put("valid", false);
            result.put("message", "Display name not provided");
            return result;
        }  
        if (StringHelper.isEmpty(profile.get(LAST_NAME))) {
            result.put("valid", false);
            result.put("message", "Last name not provided");
            return result;
        }
      
        result.put("valid", true);
        result.put("message", "All inputs are valid.");
        return result;
    }

    @Override
    public String updateProfile(Map<String, String> profile, String email) {

        Set<String> attributes = new HashSet<>(Arrays.asList(FIRST_NAME, MIDDLE_NAME, DISPLAY_NAME, LAST_NAME, MAIL, JANS_STATUS, GENDER ));
        User user = getUser(MAIL, email);
        attributes.forEach(attr -> {
            String val = profile.get(attr);
            if (StringHelper.isNotEmpty(val)) {
                user.setAttribute(attr, val);
            }
        });
        UserService userService = CdiUtil.bean(UserService.class);
        user = userService.updateUser(user);
    
        if (user == null) {
            throw new EntryNotFoundException("Updated user not found");
        }
    
        return getSingleValuedAttr(user, INUM_ATTR);
    }

    private String getSingleValuedAttr(User user, String attribute) {
        Object value = null;
        if (attribute.equals(UID)) {
            //user.getAttribute("uid", true, false) always returns null :(
            value = user.getUserId();
        } else {
            value = user.getAttribute(attribute, true, false);
        }
        return value == null ? null : value.toString();

    }

    private static User getUser(String attributeName, String value) {
        UserService userService = CdiUtil.bean(UserService.class);
        return userService.getUserByAttribute(attributeName, value, true);
    }   
}
