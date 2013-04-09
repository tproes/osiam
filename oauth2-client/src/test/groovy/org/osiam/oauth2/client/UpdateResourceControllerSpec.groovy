package org.osiam.oauth2.client

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class UpdateResourceControllerSpec extends Specification {
    def getResponseAndCast = Mock(GetResponseAndCast)
    HttpServletRequest servletRequest = Mock(HttpServletRequest)
    UpdateResourceController updateResourceController = new UpdateResourceController(getResponeAndCast: getResponseAndCast)


    def "should be able to update a user resource"() {
        when:
        updateResourceController.updateResource(servletRequest,
                "schema",
                "user_name",
                "firstname",
                "lastname",
                "displayname",
                "nickname",
                "profileurl",
                "title",
                "usertype",
                "preferredlanguage",
                "locale",
                "timezone",
                //timezone
                "password",
                "access_token",
                "idForUpdate"
        )

        then:
        1 * getResponseAndCast.getResponseAndSetAccessToken(servletRequest, "access_token", _, _)
        1 * servletRequest.getScheme()
        1 * servletRequest.getServerName()

    }

}