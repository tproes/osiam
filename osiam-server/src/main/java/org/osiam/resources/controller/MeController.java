package org.osiam.resources.controller;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.NameEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Controller
@RequestMapping(value = "/me")
/**
 * This Controller is used for getting information about the user who initialised the access_token.
 *
 */
public class MeController {

    @Inject
    private InMemoryTokenStore inMemoryTokenStore;

    /**
     * This method is used to get information about the user who initialised the authorization process.
     * <p/>
     * The result should be in json format and look like:
     * <p/>
     * {
     * "id": "73821979327912",
     * "name": "Arthur Dent",
     * "first_name": "Arthur
     * "last_name": "Dent",
     * "link": "https://www.facebook.com/arthur.dent.167",
     * "username": "arthur.dent.167",
     * "gender": "male",
     * "email": "...@....de",
     * "timezone": 2,
     * "locale": "en_US",
     * "verified": true,
     * "updated_time": "2012-08-20T08:03:30+0000"
     * }
     * <p/>
     * if some information are not available then ... will happen.
     *
     * @return an object to represent the json format.
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public FacebookInformationConstruct getInformation(HttpServletRequest request) {
        String accessToken = getAccessToken(request);
        Authentication userAuthentication = inMemoryTokenStore.readAuthentication(accessToken).getUserAuthentication();
        Object o = userAuthentication.getPrincipal();
        if (o instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) o;
            return new FacebookInformationConstruct(userEntity);
        } else {
            throw new IllegalArgumentException("User was not authenticated with OSIAM.");
        }


    }

    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        return accessToken != null ? accessToken : getBearerToken(request);
    }

    private String getBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) { throw new IllegalArgumentException("No access_token submitted!"); }
        return authorization.substring("Bearer ".length(), authorization.length());
    }

    public static class FacebookInformationConstruct {

        @JsonIgnore
        private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        private String id;
        private String name;
        private String first_name; // NOSONAR - needed pattern due to json serializing
        private String last_name; // NOSONAR - needed pattern due to json serializing
        private String link;
        private String userName;
        //fallback is female...
        private String gender = "female";
        private String email;
        // our timezone is string, theirs is int ... dunno how to format.
        private int timezone = 2;
        private String locale;
        private boolean verified = true;
        private String updated_time; // NOSONAR - needed pattern due to json serializing

        public FacebookInformationConstruct(UserEntity userEntity) {
            this.id = userEntity.getId().toString();
            setName(userEntity);
            this.link = "not supported.";
            this.email = lookForEmail(userEntity.getEmails());
            this.locale = userEntity.getLocale();
            this.updated_time = dateTimeFormatter.print(userEntity.getMeta().getLastModified().getTime());
            this.userName = userEntity.getUsername();
        }

        private String lookForEmail(Set<EmailEntity> emails) {
            IllegalArgumentException noPrimaryEmail = new IllegalArgumentException(
                    "Unable to generate facebook credentials, no primary email submitted.");
            if (emails == null) {
                throw noPrimaryEmail;
            }
            for (EmailEntity e : emails) { if (e.isPrimary()) { return e.getValue(); } }
            throw noPrimaryEmail;

        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        private void setName(UserEntity userEntity) {
            NameEntity nameEntity = userEntity.getName();
            if (nameEntity == null) {
                throw new IllegalArgumentException("Unable to generate facebook credentials, no name submitted.");
            }
            this.name = nameEntity.getFormatted();
            this.first_name = nameEntity.getGivenName();
            this.last_name = nameEntity.getFamilyName();
        }

        public String getFirst_name() { // NOSONAR - needed pattern due to json serializing
            return first_name;
        }

        public String getLast_name() { // NOSONAR - needed pattern due to json serializing
            return last_name;
        }

        public String getLink() {
            return link;
        }

        public String getUserName() { // NOSONAR - (confusing names) UserEntity.getUsername() is overwritten from super class
            return userName;
        }

        public String getGender() {
            return gender;
        }

        public String getEmail() {
            return email;
        }

        public int getTimezone() {
            return timezone;
        }

        public String getLocale() {
            return locale;
        }

        public boolean isVerified() {
            return verified;
        }

        public String getUpdated_time() { // NOSONAR - needed pattern due to json serializing
            return updated_time;
        }
    }


}
