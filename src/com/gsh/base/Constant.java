package com.gsh.base;

/**
 * Created by Administrator on 2015/5/18.
 */
public final class Constant {
    private Constant() {
    }

    public static final class HttpConstants {
        public static final String SUFFIX_NEED_AUTH = "/oauth/";
        public static final String SUFFIX_RETURN_TOKEN = "/token/";
        public static final String SUFFIX_SUPPORT_CACHE = "/support-cache";

        public static final String KEY_SET_TOKEN = "set-token";
        public static final String KEY_TOKEN = "token";

        public static final String KEY_SET_EXPIRE="dateExpired";
        public static final String KEY_EXPIRE="expire";

        public static final String KEY_VERSION = "version";
        public static final String VALUE_VERSION = "1.0";

        public static final String KEY_RESPONSE_VERSION = "etag";
        public static final String KEY_REQUEST_VERSION = "If-None-Match";
    }

    public static final class CacheConstants {
        public static final String KEY_SUFFIX_REQUEST_VERSION = "_version";
        public static final String KEY_SUFFIX_REQUEST_DATA = "_data";
    }

    public static final class HttpCodeConstants {
        public static final int NEED_TOKEN = 0x9527;
        public static final int LOCAL_CACHE_NOT_FOUND = 0x9526;
        public static final int INNER_EXCEPTION = 0x9525;
    }

    public static final class VendorConstants {

    }


    public static final class Request {
        public static final int PICTURE_CAMERA = 2038;
        public static final int PICTURE_GALLERY = 2039;
        public static final int PICTURE_CROP = 2040;
    }
}
