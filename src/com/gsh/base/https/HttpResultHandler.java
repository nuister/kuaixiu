package com.gsh.base.https;

import com.gsh.base.Constant;
import com.litesuits.common.cache.XmlCache;
import com.litesuits.common.utils.MD5Util;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpResponseHandler;

/**
 * Created by taosj on 15/2/27.
 */
public abstract class HttpResultHandler extends HttpResponseHandler {
    private OnFailureListener onFailureListener;

    private XmlCache cache;

    @Override
    protected void onSuccess(Request req, Response res, HttpStatus status, NameValuePair[] headers) {
        res.printInfo();
    }

    @Override
    protected void onFailure(Request req, Response res, HttpException e) {
        res.printInfo();
    }

    protected abstract void onSuccess(ApiResult apiResult);

    protected abstract void onFailure(int code);


    public HttpResultHandler() {
        cache = XmlCache.getInstance();
    }


    public interface OnFailureListener {
        void onFailure(String errorCode);
    }


    public void setOnFailureListener(OnFailureListener onFailureListener) {
        this.onFailureListener = onFailureListener;
    }


    @Override
    public HttpResponseHandler handleResponse(Request req, Response res) {
        if (res != null && res.getHttpStatus() != null && res.getHttpStatus().getCode() == 304) {
            String url = null;
            try {
                url = req.getUrl();
            } catch (HttpClientException e1) {
                e1.printStackTrace();
            }
            String key = new String(MD5Util.md5(url + Constant.CacheConstants.KEY_SUFFIX_REQUEST_DATA));
            String data = cache.getString(key);

            if (data == null) {
                ApiResult apiResult = new ApiResult();
                apiResult.code = 1;
                apiResult.data = null;
                onSuccess(apiResult);
            } else {
                onFailure(Constant.HttpCodeConstants.LOCAL_CACHE_NOT_FOUND);
            }
        }

        HttpException e = res.getException();
        if (e != null) {
            onFailure(req, res, e);
            onFailure(Constant.HttpCodeConstants.INNER_EXCEPTION);
            return this;
        }

        if (req != null && res == null) {
            onFailure(Constant.HttpCodeConstants.NEED_TOKEN);
            return this;
        }

        onSuccess(req, res, res.getHttpStatus(), res.getHeaders());
        ApiResult apiResult = res.getObject(ApiResult.class);
        if (apiResult == null) {
            onFailure(Constant.HttpCodeConstants.NEED_TOKEN);
        } else {
            String url = null;
            try {
                url = req.getUrl();
            } catch (HttpClientException e1) {
                e1.printStackTrace();
            }

            if (apiResult.isOk()) {
                if (url.contains(Constant.HttpConstants.SUFFIX_RETURN_TOKEN)) {
                    NameValuePair tokenPair = null;
                    NameValuePair[] headers = res.getHeaders();
                    if (headers != null) {
                        for (int i = 0; i < headers.length; i++) {
                            NameValuePair pair = headers[i];
                            if (pair.getName().equals(Constant.HttpConstants.KEY_SET_TOKEN)) {
                                cache.putString(Constant.HttpConstants.KEY_TOKEN, pair.getValue().replaceAll("\n", ""));
                            } else if (pair.getName().equals(Constant.HttpConstants.KEY_SET_EXPIRE)) {
                                long date = Long.parseLong(pair.getValue().replaceAll("\n", ""));
                                cache.putLong(Constant.HttpConstants.KEY_EXPIRE, date);
                            }
                        }
                    }
                }

                if (!url.contains(Constant.HttpConstants.SUFFIX_SUPPORT_CACHE)) {
                    onSuccess(apiResult);
                    return this;
                }

                NameValuePair requestVersionPair = null;
                if (url != null) {
                    NameValuePair[] headers = res.getHeaders();
                    if (headers != null) {
                        for (int i = 0; i < headers.length; i++) {
                            NameValuePair pair = headers[i];
                            if (pair.getName().equals(Constant.HttpConstants.KEY_RESPONSE_VERSION)) {
                                requestVersionPair = pair;
                                break;
                            }
                        }
                    }
                }

                String etagKey = new String(MD5Util.md5(url + Constant.CacheConstants.KEY_SUFFIX_REQUEST_VERSION));
                cache.put(etagKey, requestVersionPair.getValue());
                String key = new String(MD5Util.md5(url + Constant.CacheConstants.KEY_SUFFIX_REQUEST_DATA));
                cache.put(key, apiResult.data.toString());
                onSuccess(apiResult);
            } else {
                onFailure(apiResult.code);
            }
        }
        return this;
    }
}
