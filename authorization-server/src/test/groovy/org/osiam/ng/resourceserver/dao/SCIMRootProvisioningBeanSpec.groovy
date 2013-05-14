package org.osiam.ng.resourceserver.dao

import org.hibernate.search.SearchException
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 13.05.13
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
class SCIMRootProvisioningBeanSpec extends Specification {

    def userDAO = Mock(UserDAO)
    def groupDAO = Mock(GroupDAO)
    def scimRootProvisioningBean = new SCIMRootProvisioningBean(userDAO: userDAO, groupDAO: groupDAO)

    def "should call dao search on search"() {
        when:
        scimRootProvisioningBean.search("anyFilter")

        then:
        1 * userDAO.search("anyFilter") >> []
        1 * groupDAO.search("anyFilter") >> []
    }

    def "should ignore SearchException on UserDAO"() {
        when:
        scimRootProvisioningBean.search("anyFilter")

        then:
        1 * userDAO.search("anyFilter") >> { throw new SearchException("moep") }
        1 * groupDAO.search("anyFilter") >> []
    }

    def "should ignore SearchException on GroupDAO"() {
        when:
        scimRootProvisioningBean.search("anyFilter")

        then:
        1 * userDAO.search("anyFilter") >> []
        1 * groupDAO.search("anyFilter") >> { throw new SearchException("moep") }
    }
}
