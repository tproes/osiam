package org.osiam.ng.scim

import org.osiam.ng.scim.mvc.user.ServiceProviderConfigController
import scim.schema.v2.Constants
import spock.lang.Specification

class ServiceProviderConfigTest extends Specification {
    def underTest = new ServiceProviderConfigController()
    def "should return a ServiceProviderConfig"(){
        when:
        def config = underTest.getConfig()
        then:
        config.schemas == Constants.CORE_SCHEMAS
        config.patch.supported
        !config.bulk.supported
        !config.bulk.maxOperations
        !config.bulk.maxPayloadSize
        config.filter.supported
        config.filter.maxResults == 100
        !config.changePassword.supported
        config.sort.supported
        !config.etag.supported
        !config.xmlDataFormat.supported
        config.authenticationSchemes.authenticationSchemes.length == 1
        config.authenticationSchemes.authenticationSchemes[0].name == "Oauth2 Bearer"
        config.authenticationSchemes.authenticationSchemes[0].description
        config.authenticationSchemes.authenticationSchemes[0].specUrl == "http://tools.ietf.org/html/rfc6749"
        config.authenticationSchemes.authenticationSchemes[0].documentationUrl == "http://oauth.net/2/"



    }
}
