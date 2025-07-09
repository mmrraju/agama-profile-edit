package org.gluu.agama.profile;

import io.jans.as.common.model.common.User;
import io.jans.as.common.service.common.EncryptionService;
import io.jans.as.common.service.common.UserService;
import io.jans.orm.exception.operation.EntryNotFoundException;
import io.jans.service.MailService;
import io.jans.model.SmtpConfiguration;
import io.jans.service.cdi.util.CdiUtil;
import io.jans.util.StringHelper;

import io.jans.agama.engine.script.LogUtils;
import java.io.IOException;
import io.jans.as.common.service.common.ConfigurationService;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.regex.Pattern;

import org.gluu.agama.profile.service.EmailTemplate;
import org.gluu.agama.profile.service.UserProfileEditService;

public class UserProfileEdit extends UserProfileEditService{

    private static final String COUNTRY = "country";
    private static final String SN = "sn";
    private static final String CONFIRM_PASSWORD = "confirmPassword";
    private static final String MAIL = "mail";
    private static final String UID = "uid";
    private static final String DISPLAY_NAME = "displayName";
    private static final String GIVEN_NAME = "givenName";
    private static final String PASSWORD = "userPassword";
    private static final String INUM_ATTR = "inum";
    private static final int OTP_LENGTH = 6;
    private static final String SUBJECT_TEMPLATE = "Here's your verification code: %s";
    private static final String MSG_TEMPLATE_TEXT = "%s is the code to complete your verification";   
    private static final SecureRandom RAND = new SecureRandom();

    private static UserProfileEdit INSTANCE = null;

    public UserProfileEdit() {}

    public static synchronized UserProfileEdit getInstance (){
        if(INSTANCE == null)
            INSTANCE = new UserProfileEdit();
        return INSTANCE;
    }

    @Override
    public Map<String, String> getUserEntity(String email) {
        User user = getUser(MAIL, email);
        boolean local = user != null;
        LogUtils.log("There is % local account for %", local ? "a" : "no", email);
    
        if (local) {            
            String uid = getSingleValuedAttr(user, UID);
            String inum = getSingleValuedAttr(user, INUM_ATTR);
            String name = getSingleValuedAttr(user, GIVEN_NAME);
    
            if (name == null) {
                name = getSingleValuedAttr(user, DISPLAY_NAME);
                if (name == null && email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf("@"));
                }
            }
    
            // Creating a truly modifiable map
            Map<String, String> userMap = new HashMap<>();
            userMap.put(UID, uid);
            userMap.put(INUM_ATTR, inum);
            userMap.put("name", name);
            userMap.put("email", email);
    
            return userMap;
        }
    
        return new HashMap<>();
    }

    @Override
    public String sendMail(String mail) {
        SmtpConfiguration smtpConfiguration = getSmtpConfiguration();
        IntStream digits = RAND.ints(OTP_LENGTH, 0, 10);
        String otp = digits.mapToObj(i -> "" + i).collect(Collectors.joining());

        String from = smtpConfiguration.getFromEmailAddress();
        String subject = String.format(SUBJECT_TEMPLATE, otp);
        String textBody = String.format(MSG_TEMPLATE_TEXT, otp);
        String htmlBody = EmailTemplate.get(otp);

        MailService mailService = CdiUtil.bean(MailService.class);
        if (mailService.sendMailSigned(from, from, mail, null, subject, textBody, htmlBody)) {
            LogUtils.log("E-mail has been delivered to % with code %", mail, otp);
            return otp;
        }

        LogUtils.log("E-mail delivery failed, check jans-auth logs");
        return null;
    }

    @Override
    public Map<String, Object> validateInputs(Map<String, String> profile) {
        LogUtils.log("Validate inputs ");
        Map<String, Object> result = new HashMap<>();

        if (StringHelper.isEmpty(profile.get(SN))) {
            result.put("valid", false);
            result.put("message", "Last name not provided");
            return result;
        }
        if (StringHelper.isEmpty(profile.get(GIVEN_NAME))) {
            result.put("valid", false);
            result.put("message", "Given name not provided");
            return result;
        }
        if (StringHelper.isEmpty(profile.get(DISPLAY_NAME))) {
            result.put("valid", false);
            result.put("message", "Display name not provided");
            return result;
        }        
        result.put("valid", true);
        result.put("message", "All inputs are valid.");
        return result;
    }

    @Override
    public String updateProfile(Map<String, String> profile, String email) {

        Set<String> attributes = new HashSet<>(Arrays.asList(SN, DISPLAY_NAME, GIVEN_NAME));
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
    
    private SmtpConfiguration getSmtpConfiguration() {
        ConfigurationService configurationService = CdiUtil.bean(ConfigurationService.class);
        SmtpConfiguration smtpConfiguration = configurationService.getConfiguration().getSmtpConfiguration();
        LogUtils.log("Your smtp configuration is %", smtpConfiguration);
        return smtpConfiguration;

    }     
}
