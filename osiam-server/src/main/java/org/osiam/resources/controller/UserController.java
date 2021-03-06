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

package org.osiam.resources.controller;

import org.osiam.resources.helper.JsonInputValidator;
import org.osiam.storage.dao.SCIMSearchResult;
import org.osiam.resources.provisioning.SCIMUserProvisioning;
import org.osiam.resources.helper.JsonResponseEnrichHelper;
import org.osiam.resources.helper.RequestParamHelper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;
import org.osiam.resources.scim.User;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * This Controller is used to manage User
 * <p/>
 * http://tools.ietf.org/html/draft-ietf-scim-core-schema-00#section-6
 * <p/>
 * it is based on the SCIM 2.0 API Specification:
 * <p/>
 * http://tools.ietf.org/html/draft-ietf-scim-api-00#section-3
 *
 *
 */
@Controller
@RequestMapping(value = "/User")
public class UserController {

    @Inject
    private SCIMUserProvisioning scimUserProvisioning;

    @Inject
    private JsonInputValidator jsonInputValidator;

    private RequestParamHelper requestParamHelper = new RequestParamHelper();
    private JsonResponseEnrichHelper jsonResponseEnrichHelper = new JsonResponseEnrichHelper();

    public void setScimUserProvisioning(SCIMUserProvisioning scimUserProvisioning) {
        this.scimUserProvisioning = scimUserProvisioning;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET) // NOSONAR - duplicate literals unnecessary
    @ResponseBody
    public User getUser(@PathVariable final String id) {
        User user = scimUserProvisioning.getById(id);
        //clone without password
        return User.Builder.generateForOuput(user);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public User create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = jsonInputValidator.validateJsonUser(request);
        User createdUser = scimUserProvisioning.create(user);
        String requestUrl = request.getRequestURL().toString();
        URI uri = new UriTemplate("{requestUrl}{internalId}").expand(requestUrl + "/", createdUser.getId());
        response.setHeader("Location", uri.toASCIIString());
        createdUser.getMeta().setLocation(uri.toASCIIString());
        return User.Builder.generateForOuput(createdUser);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT) // NOSONAR - duplicate literals unnecessary
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public User replace(@PathVariable final String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = jsonInputValidator.validateJsonUser(request);
        User createdUser = scimUserProvisioning.replace(id, user);
        return setLocationUriAndCreateUserForOutput(request, response, createdUser);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH) // NOSONAR - duplicate literals unnecessary
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public User update(@PathVariable final String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = jsonInputValidator.validateJsonUser(request);
        User createdUser = scimUserProvisioning.update(id, user);
        return setLocationUriAndCreateUserForOutput(request, response, createdUser);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE) // NOSONAR - duplicate literals unnecessary
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
        scimUserProvisioning.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String searchWithGet(HttpServletRequest request) {
        Map<String,Object> parameterMap = requestParamHelper.getRequestParameterValues(request);
        SCIMSearchResult scimSearchResult = scimUserProvisioning.search((String)parameterMap.get("filter"), (String)parameterMap.get("sortBy"), (String)parameterMap.get("sortOrder"),
                (int)parameterMap.get("count"), (int)parameterMap.get("startIndex"));

        return jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMap, scimSearchResult.getSchemas());
    }

    @RequestMapping(value = "/.search", method = RequestMethod.POST)
    @ResponseBody
    public String searchWithPost(HttpServletRequest request) {
        Map<String,Object> parameterMap = requestParamHelper.getRequestParameterValues(request);
        SCIMSearchResult scimSearchResult = scimUserProvisioning.search((String)parameterMap.get("filter"), (String)parameterMap.get("sortBy"), (String)parameterMap.get("sortOrder"),
                (int)parameterMap.get("count"), (int)parameterMap.get("startIndex"));

        return jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMap, scimSearchResult.getSchemas());
    }

    private User setLocationUriAndCreateUserForOutput(HttpServletRequest request, HttpServletResponse response,
                                                      User createdUser) {
        String requestUrl = request.getRequestURL().toString();
        response.setHeader("Location", requestUrl);
        createdUser.getMeta().setLocation(requestUrl);
        return User.Builder.generateForOuput(createdUser);
    }
}