package com.gsh.base.viewmodel;

import com.gsh.base.https.ApiResult;
import com.gsh.base.https.HttpResultHandler;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.User;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.request.Request;

import java.io.Serializable;

/**
 * @author Tan Chunmao
 */
public class RegisterViewModel extends ViewModelBase {
    public String mobile;
    public String password;
    public String deviceId;

    public RegisterViewModel() {

    }

    public void regulateInput(FetchDataListener l) {
        if (!StringUtils.checkPhoneNo(mobile)) {
            l.onFailure("手机号不合法");
        } else if (!StringUtils.checkPwd(password)) {
            l.onFailure("请输入6到15位由数字或字母组成的密码");
        } else {
            submitData(l);
        }
    }

    static class Data implements Serializable {
        public String token;
        public long dateExpired;
    }

    private void submitData(final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.MEMBER_REGISTER).addUrlParam("mobile", mobile).addUrlParam("password", password).addUrlParam("device", deviceId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        Data data = apiResult.getModel(Data.class);
                        User user = User.load(User.class);
                        user.setToken(data.token);
                        user.setDateExpired(data.dateExpired);
                        user.save();
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        if (code == Constant.Urls.CODE_USER_EXIST) {
                            toast = "用户已存在";
                        }
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }
}
