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

package org.osiam.resources.exceptions

import org.osiam.resources.exceptions.HandleException
import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.exceptions.SchemaUnknownException
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.PhotoEntity
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.WebRequest
import spock.lang.Specification

class HandleExceptionTest extends Specification {
    def underTest = new HandleException()
    WebRequest request = Mock(WebRequest)

    def "exception result should contain a code and a description"() {
        when:
        def errorResult = new HandleException.JsonErrorResult("hacja", "unso")
        then:
        errorResult.error_code == "hacja"
        errorResult.description == "unso"
    }

    def "should generate a response entity"() {
        when:
        def result = underTest.handleConflict(new NullPointerException("Dunno"), request)
        then:
        result.getStatusCode() == HttpStatus.CONFLICT
        (result.getBody() as HandleException.JsonErrorResult).error_code == HttpStatus.CONFLICT.name()
        (result.getBody() as HandleException.JsonErrorResult).description == "Dunno"
    }

    def "should set status to ResourceNotFound when org.osiam.resources.exceptions.ResourceNotFoundException occurs"() {
        when:
        def result = underTest.handleConflict(new ResourceNotFoundException("Dunno"), request)
        then:
        result.getStatusCode() == HttpStatus.NOT_FOUND
        (result.getBody() as HandleException.JsonErrorResult).error_code == HttpStatus.NOT_FOUND.name()
        (result.getBody() as HandleException.JsonErrorResult).description == "Dunno"
    }

    def "should set status to I_AM_A_TEAPOT when org.osiam.resources.exceptions.SchemaUnknownException occurs"() {
        when:
        def result = underTest.handleConflict(new SchemaUnknownException(), request)
        then:
        result.getStatusCode() == HttpStatus.I_AM_A_TEAPOT
        (result.getBody() as HandleException.JsonErrorResult).error_code == HttpStatus.I_AM_A_TEAPOT.name()
        (result.getBody() as HandleException.JsonErrorResult).description == "Delivered schema is unknown."
    }

    def "should transform PhotoEntity No enum constant error message to a more readable error response"(){
        given:

        def e = get_exeception {new PhotoEntity().setType("huch")}
        when:
           def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as HandleException.JsonErrorResult).description == "huch is not a valid Photo type"
    }

    def "should transform EmailEntity No enum constant error message to a more readable error response"(){
        given:

        def e = get_exeception {new EmailEntity().setType("huch")}
        when:
        def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as HandleException.JsonErrorResult).description == "huch is not a valid Photo type"
    }

    def "should transform ImEntity No enum constant error message to a more readable error response"(){
        given:

        def e = get_exeception {new ImEntity().setType("huch")}
        when:
        def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as HandleException.JsonErrorResult).description == "huch is not a valid Photo type"
    }

    def "should transform PhoneNumberEntity No enum constant error message to a more readable error response"(){
        given:

        def e = get_exeception {new PhoneNumberEntity().setType("huch")}
        when:
        def result = underTest.handleConflict(e, request)
        then:
        (result.getBody() as HandleException.JsonErrorResult).description == "huch is not a valid Photo type"
    }

    def get_exeception(Closure c){
        try{
            c.call()
        }catch (IllegalArgumentException a){
            return a
        }
    }



}
