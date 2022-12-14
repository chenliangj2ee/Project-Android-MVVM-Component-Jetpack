package com.tencent.qcloud.tuikit.tuichat.util;

import com.mtjk.utils.MyFunctionKt;
import com.tencent.imsdk.common.IMLog;

public class TUIChatLog extends IMLog {

    private static final String PRE = "TUIChat-";

    private static String mixTag(String tag) {
        return PRE + tag;
    }

    /**
     * 打印INFO级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void v(String strTag, String strInfo) {
        MyFunctionKt.log(strTag,strTag+":"+strInfo);
    }

    /**
     * 打印DEBUG级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void d(String strTag, String strInfo) {
        MyFunctionKt.log(strTag,strTag+":"+strInfo);
    }

    /**
     * 打印INFO级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void i(String strTag, String strInfo) {
        MyFunctionKt.log(strTag, strTag + ":" + strInfo);
    }

    /**
     * 打印WARN级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void w(String strTag, String strInfo) {
        MyFunctionKt.log(strTag, strTag + ":" + strInfo);
    }

    /**
     * 打印WARN级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void w(String strTag, String strInfo, Throwable e) {
        MyFunctionKt.log(strTag, strTag + ":" + strInfo);
    }

    /**
     * 打印ERROR级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void e(String strTag, String strInfo) {
        MyFunctionKt.log(strTag, strTag + ":" + strInfo);
    }

}
