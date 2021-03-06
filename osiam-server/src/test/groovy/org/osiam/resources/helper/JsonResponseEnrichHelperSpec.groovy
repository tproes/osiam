package org.osiam.resources.helper

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.storage.dao.SCIMSearchResult
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.User
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.05.13
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
class JsonResponseEnrichHelperSpec extends Specification {

    def jsonResponseEnrichHelper = new JsonResponseEnrichHelper()

    def "should not throw exception if result list is empty"() {
        given:
        def userList = [] as List<User>
        def parameterMapMock = Mock(Map)
        def attributes = ["userName"] as String[]

        parameterMapMock.get("count") >> 10
        parameterMapMock.get("startIndex") >> 0
        parameterMapMock.get("attributes") >> attributes

        def scimSearchResult = Mock(SCIMSearchResult)
        def set = [] as Set

        scimSearchResult.getResult() >> userList
        scimSearchResult.getTotalResult() >> 1337

        when:
        def jsonResult = jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMapMock, set)

        then:
        jsonResult.contains("\"totalResults\":1337,\"itemsPerPage\":10,\"startIndex\":0,\"schemas\":\"\",\"Resources\":[]")
    }

    def "should return Json string with additional values for searches on users, groups and filtering for userName"() {
        given:
        def user = new User.Builder("username").setSchemas(["schemas:urn:scim:schemas:core:1.0"] as Set).build()
        def userList = [user] as List<User>
        def parameterMapMock = Mock(Map)
        def attributes = ["userName"] as String[]

        parameterMapMock.get("count") >> 10
        parameterMapMock.get("startIndex") >> 0
        parameterMapMock.get("attributes") >> attributes

        def scimSearchResult = Mock(SCIMSearchResult)
        def set = ["schemas:urn:scim:schemas:core:1.0"] as Set

        scimSearchResult.getResult() >> userList
        scimSearchResult.getTotalResult() >> 1337

        when:
        def jsonResult = jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMapMock, set)

        then:
        jsonResult.contains("\"totalResults\":1337,\"itemsPerPage\":10,\"startIndex\":0,\"schemas\":\"schemas:urn:scim:schemas:core:1.0\",\"Resources\":[{\"userName\":\"username\"}]")
    }

    def "should not filter the search result if attributes are empty"() {
        given:
        def user = new User.Builder("username").setSchemas(["schemas:urn:scim:schemas:core:1.0"] as Set).build()
        def userList = [user] as List<User>
        def parameterMapMock = Mock(Map)
        def attributes = [] as String[]

        parameterMapMock.get("count") >> 10
        parameterMapMock.get("startIndex") >> 0
        parameterMapMock.get("attributes") >> attributes

        def scimSearchResult = Mock(SCIMSearchResult)
        def set = ["schemas:urn:scim:schemas:core:1.0"] as Set

        scimSearchResult.getResult() >> userList
        scimSearchResult.getTotalResult() >> 1337

        when:
        def jsonResult = jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMapMock, set)

        then:
        jsonResult.contains("\"totalResults\":1337,\"itemsPerPage\":10,\"startIndex\":0,\"schemas\":\"schemas:urn:scim:schemas:core:1.0\",\"Resources\":[{\"schemas\":[\"schemas:urn:scim:schemas:core:1.0\"],\"userName\":\"username\"}]")

    }

    def "should return only data of a complex type"() {
        given:
        def set = ["schemas:urn:scim:schemas:core:1.0"] as Set
        String[] pA = ["meta", "created"]
        def param = ["attributes": pA, "count": 23, "startIndex": 23]

        def actualDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime();
        def date = new DateTime(actualDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(actualDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user], 23)
        when:
        def result = jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, param, set)
        then:
        result == '{"totalResults":23,"itemsPerPage":23,"startIndex":23,"schemas":"schemas:urn:scim:schemas:core:1.0","Resources":[{"meta":{"created":"' + created + '"}}]}'

    }

}