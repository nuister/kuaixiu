package com.litesuits.http.response.handler;

import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * Handle Response on UI Thread
 * 
 * @author MaTianyu
 * 2014-2-26下午9:02:09
 */
public abstract class HttpResponseHandler {
	protected abstract void onSuccess(Request req,Response res, HttpStatus status, NameValuePair[] headers);

	protected abstract void onFailure(Request req,Response res, HttpException e);

	public HttpResponseHandler handleResponse(Request req, Response res) {
		if (res != null) {
			HttpException e = res.getException();
			if (e == null) {
				onSuccess(req,res, res.getHttpStatus(), res.getHeaders());
			} else {
				onFailure(req,res, e);
			}
		}
		return this;
	}
}
