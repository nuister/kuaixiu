/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.litesuits.http.impl.apache;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The default {@link HttpRequestRetryHandler} used by request executors.
 *
 * @since 4.0
 */
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {

	public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();

	/** the number of times a method will be retried */
	private final int retryCount;

	/** Whether or not methods that have successfully sent their request will be retried */
	private final boolean requestSentRetryEnabled;

	private final Set<Class<? extends IOException>> nonRetriableClasses;

	/**
	 * Create the request retry handler using the specified IOException classes
	 *
	 * @param retryCount how many times to retry; 0 means no retries
	 * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
	 * @param clazzes the IOException types that should not be retried
	 * @since 4.3
	 */
	protected DefaultHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled,
			final Collection<Class<? extends IOException>> clazzes) {
		super();
		this.retryCount = retryCount;
		this.requestSentRetryEnabled = requestSentRetryEnabled;
		this.nonRetriableClasses = new HashSet<Class<? extends IOException>>();
		for (final Class<? extends IOException> clazz : clazzes) {
			this.nonRetriableClasses.add(clazz);
		}
	}

	/**
	 * Create the request retry handler using the following list of
	 * non-retriable IOException classes: <br>
	 * <ul>
	 * <li>InterruptedIOException</li>
	 * <li>UnknownHostException</li>
	 * <li>ConnectException</li>
	 * <li>SSLException</li>
	 * </ul>
	 * @param retryCount how many times to retry; 0 means no retries
	 * @param requestSentRetryEnabled true if it's OK to retry requests that have been sent
	 */
	@SuppressWarnings("unchecked")
	public DefaultHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
		this(retryCount, requestSentRetryEnabled, Arrays.asList(InterruptedIOException.class,
                UnknownHostException.class, ConnectException.class, SSLException.class));
	}

	/**
	 * Create the request retry handler with a retry count of 3, requestSentRetryEnabled false
	 * and using the following list of non-retriable IOException classes: <br>
	 * <ul>
	 * <li>InterruptedIOException</li>
	 * <li>UnknownHostException</li>
	 * <li>ConnectException</li>
	 * <li>SSLException</li>
	 * </ul>
	 */
	public DefaultHttpRequestRetryHandler() {
		this(3, false);
	}

	/**
	 * Used <code>retryCount</code> and <code>requestSentRetryEnabled</code> to determine
	 * if the given method should be retried.
	 */
	public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
		if (executionCount > this.retryCount) {
			// Do not retry if over max retry count
			return false;
		}
		if (this.nonRetriableClasses.contains(exception.getClass())) {
			return false;
		} else {
			for (final Class<? extends IOException> rejectException : this.nonRetriableClasses) {
				if (rejectException.isInstance(exception)) { return false; }
			}
		}
		return retryRequest(context);
	}

	protected boolean retryRequest(final HttpContext context) {
		final HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		final Object obj = context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		final boolean isSent = obj == null ? false : (Boolean) obj;

		if (requestIsAborted(request)) { return false; }

		if (handleAsIdempotent(request)) {
			// Retry if the request is considered idempotent
			return true;
		}
		if (!isSent || this.requestSentRetryEnabled) {
			// Retry if the request has not been sent fully or
			// if it's OK to retry methods that have been sent
			return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if this handler will retry methods that have
	 * successfully sent their request, <code>false</code> otherwise
	 */
	public boolean isRequestSentRetryEnabled() {
		return requestSentRetryEnabled;
	}

	public int getRetryCount() {
		return retryCount;
	}

	protected boolean handleAsIdempotent(final HttpRequest request) {
		return !(request instanceof HttpEntityEnclosingRequest);
	}

	@Deprecated
	protected boolean requestIsAborted(final HttpRequest request) {
		if (request == null) return false;
		HttpRequest req = request;
		if (request instanceof RequestWrapper) { // does not forward request to
													// original
			req = ((RequestWrapper) request).getOriginal();
		}
		return (req instanceof HttpUriRequest && ((HttpUriRequest) req).isAborted());
	}

}
