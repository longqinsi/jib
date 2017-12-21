/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.crepecake.http;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nullable;

/**
 * Sends an HTTP {@link Request} and stores the {@link Response}.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * try (Connection connection = new Connection(url)) {
 *   Response response = connection.get(request);
 *   // ... process the response
 * }
 * }</pre>
 */
public class Connection implements Closeable {

  private static final HttpRequestFactory HTTP_REQUEST_FACTORY =
      new NetHttpTransport().createRequestFactory();

  private HttpRequestFactory requestFactory = HTTP_REQUEST_FACTORY;

  @Nullable private HttpResponse httpResponse;

  /** The URL to send the request to. */
  private final GenericUrl url;

  /**
   * Make sure to wrap with a try-with-resource to ensure that the connection is closed after usage.
   */
  public Connection(URL url) {
    this.url = new GenericUrl(url);
  }

  @Override
  public void close() throws IOException {
    if (httpResponse == null) {
      return;
    }

    httpResponse.disconnect();
  }

  /** Sends the request with method GET. */
  public Response get(Request request) throws IOException {
    return send(HttpMethods.GET, request);
  }

  /** Sends the request with method POST. */
  public Response post(Request request) throws IOException {
    return send(HttpMethods.POST, request);
  }

  /** Sends the request with method PUT. */
  public Response put(Request request) throws IOException {
    return send(HttpMethods.PUT, request);
  }

  /** Sends the request. */
  private Response send(String httpMethod, Request request) throws IOException {
    httpResponse =
        requestFactory
            .buildRequest(httpMethod, url, request.getHttpContent())
            .setHeaders(request.getHeaders())
            .execute();
    return new Response(httpResponse);
  }
}