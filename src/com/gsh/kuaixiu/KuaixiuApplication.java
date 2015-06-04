package com.gsh.kuaixiu;

import com.gsh.base.FrameApplication;

/**
 *@author Tan Chunmao
 */
public class KuaixiuApplication extends FrameApplication{
    public static User user;
    public static final boolean test = true;

    @Override
    public void onCreate() {
        super.onCreate();
        initUser();
    }

    private void initUser() {
        user = User.load(User.class);
        if(user==null) {
            user=new User();
        }
    }

    public void saveUser() {
        user.save();
    }
}
