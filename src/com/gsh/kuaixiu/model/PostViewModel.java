package com.gsh.kuaixiu.model;

import android.net.Uri;
import android.text.TextUtils;
import com.gsh.base.https.ApiResult;
import com.gsh.base.https.HttpResultHandler;
import com.gsh.base.model.FetchDataListener;
import com.gsh.base.viewmodel.ViewModelBase;
import com.gsh.kuaixiu.Constant;
import com.litesuits.common.io.FileUtils;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.MultipartBody;
import com.litesuits.http.request.content.multi.FilePart;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class PostViewModel extends ViewModelBase {



    public PostViewModel() {

    }

    public static class Data implements Serializable {
        public long id;
        public String name;
        public double price;

        public Data() {
        }
    }


    public void fetchData(String cityName, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.KUAIXIU_TYPE).addUrlParam("cityName", String.valueOf(cityName)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }

    public static class Entry {
        public Data type;
        public String longitude;
        public String latitude;
        public String address;

        public List<Uri> imageUris;
        public String description;
    }



    public void submitData(final Entry entry, final FetchDataListener fetchDataListener) {
        if(TextUtils.isEmpty(entry.latitude) || TextUtils.isEmpty(entry.longitude)) {
           fetchDataListener.onFailure("定位不成功");
            return;
        }
        Request request = new Request(Constant.Urls.KUAIXIU_POST);
        request.addUrlParam("repairTypeId", String.valueOf(entry.type.id));
//        if (TextUtils.isEmpty(entry.address)) {
//            fetchDataListener.onFailure("请输入您的地址");
//        } else {
//            request.addUrlParam("address", entry.address);
//        }
        request.addUrlParam("lat",entry.latitude);
        request.addUrlParam("lng",entry.longitude);
        request.addUrlParam("address",entry.address);
        if (!TextUtils.isEmpty(entry.description)) {
            request.addUrlParam("remark", entry.description);
        }

        if (entry.imageUris != null && entry.imageUris.size() > 0) {
            MultipartBody body = new MultipartBody();
            for (Uri uri : entry.imageUris) {
                String path = FileUtils.getRealPathFromURI(uri);
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    body.addPart(new FilePart("pictures", file, "image/jpeg"));
                }
            }
            request.setHttpBody(body);
        }


        execute(request,
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }
}
