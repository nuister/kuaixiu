package com.gsh.base.model;

import com.gsh.base.https.ApiResult;

/**
 *@author Tan Chunmao
 * interface between view and data handler
 */
public interface FetchDataListener {
    void onSuccess(ApiResult apiResult);
    void onFailure(String description);
}
