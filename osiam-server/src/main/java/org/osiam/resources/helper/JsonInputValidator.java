package org.osiam.resources.helper;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.springframework.stereotype.Service;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.06.13
 * Time: 09:43
 * To change this template use File | Settings | File Templates.
 */
@Service
public class JsonInputValidator {

    public User validateJsonUser(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);

        if(jsonInput.contains("userName")) {
            return validateResource(jsonInput, User.class);
        }
        throw new IllegalArgumentException("The user name is mandatory and MUST NOT be null");
    }

    public Group validateJsonGroup(HttpServletRequest request) throws IOException {
        String jsonInput = getRequestBody(request);

        if(jsonInput.contains("displayName")) {
            return validateResource(jsonInput, Group.class);
        }
        throw new IllegalArgumentException("The display name is mandatory and MUST NOT be null.");
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuffer jb = new StringBuffer();
        String line;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            jb.append(line);
        }

        return jb.toString();
    }

    private <T> T validateResource(String jsonInput, Class<T> clazz) throws IOException {
        T resource;
        ObjectMapper mapper = new ObjectMapper();
        try {
            resource = mapper.readValue(jsonInput, clazz);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("The JSON structure is incorrect");
        } catch (UnrecognizedPropertyException e) {
            Pattern pattern = Pattern.compile("^([^\"]*\"[^\"]*\")");
            Matcher matcher = pattern.matcher(e.getMessage());
            matcher.find();
            throw new IllegalArgumentException(matcher.group());
        } catch (JsonMappingException e) {
            Pattern pattern = Pattern.compile("^([^']*'[^']*')");
            Matcher matcher = pattern.matcher(e.getMessage());
            matcher.find();
            throw new IllegalArgumentException(matcher.group());
        }
        return resource;
    }
}