package org.osiam.oauth2.client

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.PutMethod
import org.json.JSONException
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 26.03.13
 * Time: 10:11
 * To change this template use File | Settings | File Templates.
 */
class UpdateResourceControllerSpec extends Specification {

    def servletRequest = Mock(HttpServletRequest)
    def httpClient = Mock(HttpClient)
    def jsonString = "{\"userName\":\"userName\",\"password\":\"password\",\"externalId\":\"externalId\"}"

    UpdateResourceController updateResourceController = new UpdateResourceController(httpClient: httpClient)


    def "should be able to update a user resource"() {
        given:
        servletRequest.getScheme() >> "http"
        servletRequest.getServerName() >> "localhost"

        when:
        updateResourceController.updateResource(servletRequest, "externalId", "userName", "password", "access_token", "idForUpdate")

        then:
        1 * httpClient.executeMethod({PutMethod put ->
            put.responseStream = new ByteArrayInputStream(jsonString.getBytes())
        })
        1 * servletRequest.setAttribute("userResponse", _)
        1 * servletRequest.setAttribute("LocationHeader", _)
    }

    def "should wrap json exception to IllegalStateException"() {
        given:
        servletRequest.getScheme() >> "http"
        servletRequest.getServerName() >> "localhost"
        String jsonString = "{\"userName\":\"userName\",\"password\":\"password\",\"externalId\":\"externalId\""

        when:
        updateResourceController.updateResource(servletRequest, "externalId", "userName", "password", "access_token", "idForUpdate")

        then:
        1 * httpClient.executeMethod({PutMethod put ->
            put.responseStream = new ByteArrayInputStream(jsonString.getBytes())
        })
        IllegalStateException e = thrown()
        e.message == "Expected a ',' or '}' at character 70"
        e.cause.class == JSONException.class
    }
}