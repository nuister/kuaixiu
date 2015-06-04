package com.gsh.kuaixiu;

import android.text.TextUtils;

import com.gsh.base.*;
import com.litesuits.common.cache.XmlCache;
import com.litesuits.common.cache.XmlCacheModel;
import com.litesuits.common.utils.TimeUtil;

/**
 * @author Tan Chunmao
 */
public class User extends XmlCacheModel {
    private long id;
    private String nickname;
    private String avatarPath;
    private String token;
    private long dateExpired;

    public User() {
        reset();
    }

    public void reset() {
        id = -1;
        nickname = "";
        avatarPath = "";
        XmlCache xmlCache = XmlCache.getInstance();
        token = xmlCache.getString(com.gsh.base.Constant.HttpConstants.KEY_TOKEN);
        dateExpired = xmlCache.getLong(com.gsh.base.Constant.HttpConstants.KEY_EXPIRE);
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getToken() {
        return token;
    }


    public long getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(long dateExpired) {
        this.dateExpired = dateExpired;
    }

    public boolean loggedIn() {
        return !TextUtils.isEmpty(token) && !TimeUtil.isExpired(dateExpired);

        //test
//        return !TimeUtil.isExpired(dateExpired);
    }
}
