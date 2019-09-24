/**
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 */
package com.oracle.bmc.http.internal;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A client request filter to remove content-length.
 * It is required for calls to only allow Apache and Resteasy to set the content-length header.
 */
@Slf4j
public class ContentLengthFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) {
        final MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        final String method = requestContext.getMethod();
        final String uri = requestContext.getUri().toString();

        if (headers == null) {
            LOG.debug(
                    "Headers from request context is null for Method [{}], URI [{}]", method, uri);
            return;
        }

        String contentLengthHeader = null;
        for (String key : headers.keySet()) {
            if (StringUtils.equalsIgnoreCase("content-length", key)) {
                contentLengthHeader = key;
            }
        }

        if (contentLengthHeader == null) {
            LOG.debug("content-length not found for Method [{}], URI [{}]", method, uri);
            return;
        }

        final Object existingContentLengthValue = headers.remove(contentLengthHeader);
        if (existingContentLengthValue != null) {
            LOG.debug(
                    "Removed existing content-length header for Method [{}], URI [{}], Existing Value [{}]",
                    method,
                    uri,
                    existingContentLengthValue);
        } else {
            LOG.debug("content-length not found for Method [{}], URI [{}]", method, uri);
        }
    }
}