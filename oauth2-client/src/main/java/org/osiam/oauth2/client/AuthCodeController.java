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

package org.osiam.oauth2.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
public class AuthCodeController {

    @RequestMapping("/authcode")
    public String redirectTogetAuthCode(HttpServletRequest req) throws ServletException, IOException {
        String environment = req.getScheme() +
                "://" +
                req.getServerName() +
                ":8080";
        String clientId = "testClient";
        String redirectUri = req.getScheme() +
                "://" +
                req.getServerName() +
                ":8080/oauth2-client/accessToken";
        String url = environment +
                "/authorization-server/oauth/authorize?response_type=code&scope=GET%20POST%20PUT%20PATCH%20DELETE&state=haha&" +
                "client_id=" +
                clientId +
                "&redirect_uri=" +
                URLEncoder.encode(redirectUri, "UTF-8");
        return "redirect:" + url;
    }
}
