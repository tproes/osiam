package org.osiam.resources.controller;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.osiam.resources.scim.Constants;

import java.util.Set;

@Controller
@RequestMapping(value = "/ServiceProviderConfig")
public class ServiceProviderConfigController {
    @RequestMapping
    @ResponseBody
    public ServiceProviderConfig getConfig() {
        return ServiceProviderConfig.INSTANCE;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    public static class ServiceProviderConfig {
        public static final ServiceProviderConfig INSTANCE = new ServiceProviderConfig(); // NOSONAR - Needed public due to json serializing
        public  Set<String> schemas = Constants.CORE_SCHEMAS; // NOSONAR - Needed public due to json serializing
        public final Supported patch = new Supported(true); // NOSONAR - Needed public due to json serializing
        public final Supported bulk = new BulkSupported(false); // NOSONAR - Needed public due to json serializing
        public final Supported filter = new FilterSupported(true, Constants.MAX_RESULT); // NOSONAR - Needed public due to json serializing
        public final Supported changePassword = new Supported(false); // NOSONAR - Needed public due to json serializing
        public final Supported sort = new Supported(true); // NOSONAR - Needed public due to json serializing
        public final Supported etag = new Supported(false); // NOSONAR - Needed public due to json serializing
        public final Supported xmlDataFormat = new Supported(false); // NOSONAR - Needed public due to json serializing
        public final AuthenticationSchemes authenticationSchemes = new AuthenticationSchemes( // NOSONAR - Needed public due to json serializing
                new AuthenticationSchemes.AuthenticationScheme("Oauth2 Bearer", // NOSONAR - Field is readable after serializing
                        "OAuth2 Bearer access token is used for authorization.", "http://tools.ietf.org/html/rfc6749",
                        "http://oauth.net/2/"));

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class Supported {
            public final boolean supported; // NOSONAR - Needed public due to json serializing

            public Supported(boolean b) {
                supported = b; // NOSONAR - Field is readable after serializing
            }
        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class FilterSupported extends Supported{
            public final Integer maxResults; // NOSONAR - Needed public due to json serializing

            public FilterSupported(boolean b, Integer maxresults) {
                super(b);
                this.maxResults = maxresults; // NOSONAR - Field is readable after serializing
            }
        }


        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class BulkSupported extends Supported {
            public final Integer maxOperations; // NOSONAR - Needed public due to json serializing
            public final Integer maxPayloadSize; // NOSONAR - Needed public due to json serializing

            public BulkSupported(boolean b) {
                super(b);
                this.maxOperations = null; // NOSONAR - Field is readable after serializing
                this.maxPayloadSize = null; // NOSONAR - Field is readable after serializing
            }

        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        static class AuthenticationSchemes {
            public AuthenticationScheme[] authenticationSchemes; // NOSONAR - Needed public due to json serializing

            public AuthenticationSchemes(AuthenticationScheme... authenticationScheme) {
                this.authenticationSchemes = authenticationScheme; // NOSONAR - Field is readable after serializing

            }

            @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
            public static class AuthenticationScheme {
                public final String name; // NOSONAR - Needed public due to json serializing
                public final String description; // NOSONAR - Needed public due to json serializing
                public final String specUrl; // NOSONAR - Needed public due to json serializing
                public final String documentationUrl; // NOSONAR - Needed public due to json serializing

                AuthenticationScheme(String name, String description, String specUrl, String documentationUrl) {
                    this.name = name; // NOSONAR - Field is readable after serializing
                    this.description = description; // NOSONAR - Field is readable after serializing
                    this.specUrl = specUrl; // NOSONAR - Field is readable after serializing
                    this.documentationUrl = documentationUrl; // NOSONAR - Field is readable after serializing
                }
            }
        }
    }
}
