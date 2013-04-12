/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.ng.scim.mvc.user

import org.osiam.ng.scim.dao.SCIMGroupProvisioning
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import scim.schema.v2.Group
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

class GroupControllerTest extends Specification {
    def httpServletRequest = Mock(HttpServletRequest)
    def provisioning = Mock(SCIMGroupProvisioning)
    def underTest = new GroupController(scimGroupProvisioning: provisioning)
    def httpServletResponse = Mock(HttpServletResponse)
    Group group = new Group.Builder().setDisplayName("group1").setId(UUID.randomUUID().toString()).build()


    def "should contain a method to POST a group"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("createGroup", Group, HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.POST]
        body
        defaultStatus.value() == HttpStatus.CREATED
    }

    def "should set LocationURI to the new created Group on POST"() {
        given:
        httpServletRequest.getRequestURL() >> new StringBuffer("http://host:port/deployment/User/")
        def uri = new URI("http://host:port/deployment/User/" + group.id)

        when:
        def result = underTest.createGroup(group, httpServletRequest, httpServletResponse)

        then:
        1 * provisioning.createGroup(group) >> group
        1 * httpServletResponse.setHeader("Location", uri.toASCIIString())
        result == group
    }

    def "should contain a method to GET a group"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("getGroup", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        then:
        mapping.method() == [RequestMethod.GET]
        mapping.value() == ['/{id}']
        body
    }

    def "should call provisioning get on get call"() {
        when:
        def result = underTest.getGroup("id")
        then:
        1 * provisioning.getById("id") >> group
        result == group
    }

    def "should contain a method to DELETE a user"(){
        given:
        Method method = UserController.class.getDeclaredMethod("delete", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.DELETE]
        defaultStatus.value() == HttpStatus.OK
    }

    def "should call provisioning delete on DELETE"() {
        when:
        underTest.delete("id")
        then:
        1 * provisioning.deleteGroup("id")

    }


}
