package com.gsh.base.viewmodel;

import com.gsh.base.Constant;
import com.gsh.base.https.ApiResult;
import com.gsh.base.https.HttpResultHandler;
import com.gsh.base.model.FetchDataListener;
import com.litesuits.ApplicationBase;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.common.cache.XmlCache;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.async.HttpAsyncExecutor;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Created by taosj on 15/5/18.
 */
public class ViewModelBase {

    private LiteHttpClient client;
    private HttpAsyncExecutor asyncExecutor;
    private final List<FutureTask<Response>> tasks = new ArrayList<FutureTask<Response>>();
    private XmlCache cache;


    public ViewModelBase(){

        client = LiteHttpClient.newApacheHttpClient(null);
        asyncExecutor = HttpAsyncExecutor.newInstance(client);
        cache = XmlCache.getInstance();

    }

    //    region http request
    public void execute(final Request req, final HttpResultHandler httpResultHandler) {
        String requestVersion = null;
        String url = null;
        try {
            url = req.getUrl();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
//        requestVersion = cache.getString(url+Constant.CacheConstants.KEY_SUFFIX_REQUEST_VERSION);
        req.addHeader(Constant.HttpConstants.KEY_VERSION,Constant.HttpConstants.VALUE_VERSION);
        req.addHeader(Constant.HttpConstants.KEY_REQUEST_VERSION,requestVersion);
        if(url.contains(Constant.HttpConstants.SUFFIX_NEED_AUTH)){
            String token = cache.getString(Constant.HttpConstants.KEY_TOKEN);
            if(token == null){
                httpResultHandler.handleResponse(req,null);
            }else{
                req.addHeader(Constant.HttpConstants.KEY_TOKEN,token);
            }
        }

        FutureTask<Response> responseFutureTask = asyncExecutor.execute(req, httpResultHandler);
        httpResultHandler.setOnFailureListener(onFailureListener);
        tasks.add(responseFutureTask);
    }

    private HttpResultHandler.OnFailureListener onFailureListener = new HttpResultHandler.OnFailureListener() {
        @Override
        public void onFailure(String errorCode) {

        }
    };

    private void removeAllHttpTasks() {
        for (FutureTask<Response> item : tasks) {
            if (!item.isCancelled())
                item.cancel(true);
        }
    }
//    endregion http request

    public void tearDown(){
        removeAllHttpTasks();
    }


    public void testHttps(final FetchDataListener fetchDataListener, final ApiResult apiResult) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                fetchDataListener.onSuccess(apiResult);
            }
        }.execute();
    }

    public void testHttps(final FetchDataListener fetchDataListener) {
        testHttps(fetchDataListener, null);
    }

}
