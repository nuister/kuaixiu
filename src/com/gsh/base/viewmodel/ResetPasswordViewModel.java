package com.gsh.base.viewmodel;

import com.gsh.base.https.ApiResult;
import com.gsh.base.https.HttpResultHandler;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.Constant;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.request.Request;

/**
 * @author Tan Chunmao
 */
public class ResetPasswordViewModel extends ViewModelBase {
    public static class Entry {
        public String mobile;
        public String password;
    }

    public ResetPasswordViewModel() {

    }

    public void regulateInput(final Entry entry, final FetchDataListener l) {
        if (!StringUtils.checkPhoneNo(entry.mobile)) {
            l.onFailure("手机号不合法");
        } else if (!StringUtils.checkPwd(entry.password)) {
            l.onFailure("请输入6到15位由数字或字母组成的密码");
        } else {
            submitData(entry, l);
//            testHttps(l);
        }
    }

    private void submitData(final Entry entry, final FetchDataListener l) {
        execute(new Request(Constant.Urls.MEMBER_OAUTH_RESET).addUrlParam("mobile", entry.mobile).addUrlParam("password", entry.password),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        l.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        l.onFailure("请求失败");
                    }
                }
        );
    }
}
