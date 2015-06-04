package com.gsh.kuaixiu;

/**
 * @author Tan Chunmao
 */
public final class Constant {
    private Constant() {
    }

    public static final class SMS {
        public static final String APPKEY = "7b450e51b0c5";
        public static final String APPSECRET = "cbf0ebc3f27b48da2eabf93105a757e9";
    }

    public static final class Alipay {

        public static final String PARTNER = "2088911524084206";

        public static final String SELLER = "kxiu@gangsh.com";

        public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM66DxvbTREud22bDzkcGCu5lEorS9aQcjEJQWw6e7wUIEXNHGtrjVgmKu8uDxuoTr9Tez0p9EJiVwGpQ2wp3UqBgwt/BbYUvG3Be4uNxHF4pmOi5Tx+3FQRiGHETMeLDg0kyo+NQtUhRbn/14Aw+4J94swwzPbJcXceRY7I1iiPAgMBAAECgYBkY/KGYp8wYtJYhd6Eq2IJidu7kP8JOahaq9X7iwMfuN5bR1ovyFEhp5deA44OrlNfkljcwQz/V+ZMxjgZxNLTRlMdxHKJWoHwcPnu512nwGyIoNAZ1UBTWyT36lL8DUxMDbdVTJDxglm+lWKcMe0sXrkUEz50jx1MZvQ90gjrcQJBAOiIgkBAohB1ND+WfQ8qi1SsSnAN7KVGitlUh0raJdnX6qZjFajdztBO8GtI8By+CYWRIlXzjpzQn1HXoOe/Gc0CQQDjltdelqKfsF1gBxEGoeKuW+8aWZ2CZ+IxCoAFLSWTOLrj6821bu8XdREMQRKkBNu7WhY/7C6+2TelTKuQgH/LAkEApiXbw2MrKU1FbuXtJ6gDdBXC/jvEyfcUgOMX5FDo2LWhUQ/dSxJpkofE2zN7dV4H2arw+K8VSFD8dXL34n62RQJBAIA9Pp3Nw1S3y+WQFcfm8BPPOatfU6FnZ8y6XeiMx99s1+IVvJO/LnnJPRHqmNZS5n1rBZ5/NtfTDcfWq9WAkoECQAU7/+sTM5hxpYHU+oEU3KLhwwZ+CF7gI2szFk+SinO824NUVEqs40wsBUT/5qyl+c72yFf9g3lIYjLpfuYAnX4=";

        public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

        public static final String REDIRECT_URL = "m.alipay.com";
        public static final String NOTIFY_URL = "m.alipay.com";
    }

    public static final class Request {
        public static final int RESET_PASSWORD = 3038;
        public static final int LOCATION=3039;
        public static final int ORDER_ACTION=8964;
    }

    public static final class Action {
        public static final String POLLING="com.gsh.kuaixiu.ACTION.POLLING";
        public static final String ORDER="com.gsh.kuaixiu.ACTION.ORDER";
    }

    /**
     * @author Tan Chunmao
     */
    public static final class Urls {
        private Urls() {
        }

        public enum Error_Code {
            token_invalid("10011"),
            captcha_invalid("10012"),
            password_wrong("10013"),;
            public String number;

            private Error_Code(String number) {
                this.number = number;
            }
        }

        public static final String TCP_HOST="192.168.0.101";
        public static final int TCP_PORT=7777;

        public static final int CODE_SUCCESS = 200;//成功
        public static final int CODE_PASSWORD_WRONG = 10013;//密码错误
        public static final int CODE_USER_EXIST = 10000;//用户已存在
        public static final int CODE_TOKEN_INVALID = 10001;//token失效
        public static final int CODE_CAPCHA_WRONG = 10003;//账号或密码错误

        //    public static final String BASE_URL = "http://192.168.0.189:8888/rest";

//        public static final String BASE_URL = "http://192.168.0.189:8888/api/";//official
                public static final String BASE_URL = "http://192.168.0.101:81/api/";//xu
//                public static final String api = "http://192.168.0.189:8888/api/index";//
        //    public static final String BASE_URL = "http://ugou.imolin.cn/rest";//official
        public static final String IMAGE_PREFIX = "http://ugou.images.imolin.cn/";
        public static final String IMAGE_LOGO = "http://ugou.images.imolin.cn/logo.png";
        public static final String SYNC = BASE_URL + "/api/sale/master/registration";
        public static final String TYPE_LIST = BASE_URL + "/api/sale/get/types";

        //    public static final String COMBO_LIST = BASE_URL + "/combo/list";
        public static final String MEMBER_LOGIN = BASE_URL + "member/token/login";
        public static final String MEMBER_REGISTER = BASE_URL + "member/token/register";
        public static final String MEMBER_OAUTH_RESET = BASE_URL + "member/oauth/reset";


        public static final String KUAIXIU_TYPE = BASE_URL + "order/repair/price";
        public static final String KUAIXIU_POST = BASE_URL + "order/oauth/create";
        public static final String KUAIXIU_ORDER_LIST = BASE_URL + "order/oauth/list";
        public static final String KUAIXIU_ORDER_DETAIL = BASE_URL + "order/oauth/details";

        public static final String KUAIXIU_ORDER_START = BASE_URL + "order/oauth/start";
        public static final String KUAIXIU_ORDER_COMMENT = BASE_URL + "order/oauth/comment";


        public static final String KUAIXIU_ORDER_CANCEL = BASE_URL + "order/oauth/close";
        public static final String MEMEBER_MASTER = BASE_URL + "member/oauth/master";
    }
}
