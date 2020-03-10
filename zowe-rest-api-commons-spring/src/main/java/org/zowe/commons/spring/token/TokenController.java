/*
 * This program and the accompanying materials are made available and may be used, at your option, under either:
 * * Eclipse Public License v2.0, available at https://www.eclipse.org/legal/epl-v20.html, OR
 * * Apache License, version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.commons.spring.token;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zowe.commons.rest.response.ApiMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.zowe.commons.apidoc.ApiDocConstants.DOC_SCHEME_BASIC_AUTH;

@Api(tags = "Login")
@RestController
@RequestMapping
@Slf4j
public class TokenController {

    @Autowired
    TokenService tokenService;

    QueryResponse queryResponse;

    @PostMapping(value = "/api/v1/auth/login", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "This API is used to return JWT token after successful login.", nickname = "login")
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Successful login"),
            @ApiResponse(code = 401, message = "The request has not been applied because it lacks valid authentication credentials for the target resource", response = ApiMessage.class) })
    @ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
    public void login(@Validated(LoginRequest.class) @RequestBody(required = false) LoginRequest loginRequest,
                      HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        if (Optional.ofNullable(tokenService.login(loginRequest, request, response)).isPresent()) {
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } else {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        }
    }

    @GetMapping("/api/v1/auth/query")
    @ApiOperation(value = "This API is used to return details of JWT token like Username, Issued Time and Expiration Time.", nickname = "query", authorizations = {
        @Authorization(value = DOC_SCHEME_BASIC_AUTH)})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful validation", response = QueryResponse.class),
        @ApiResponse(code = 401, message = "The request has not been applied because it lacks valid authentication credentials for the target resource", response = ApiMessage.class)})
    public QueryResponse queryResponseController(HttpServletRequest request) {
        try {
            queryResponse = tokenService.query(request);
        } catch (Exception e) {
            log.debug("Error with the http request {}.", e.getMessage());
        }
        return queryResponse;
    }
}