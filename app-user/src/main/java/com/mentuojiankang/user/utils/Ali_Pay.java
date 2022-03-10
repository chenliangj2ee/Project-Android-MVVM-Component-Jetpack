package com.mentuojiankang.user.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class Ali_Pay {
    public static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCAQQhAKUIOsJo26t1BwTyi2yr6CVDFEdr51y+My7Txi7Ijao6rbLDkabiTGsYqK8QdOFWxIJv2EVPSWke9hNlh2GLBNhA7NtJ9g2ZU96XscUe8Ax2qQHjOpTVOixpqqQhk7hpqXU2BRgaEjtMYwehx++brXS1nZfwzj8ak07IxbFl0BColHR3vlSm9FF9+zIwrz9tmBtUEtCW6maFlKgLrx0Bc0V50XrOoyblsSIIOZ99pCPb6i9o17VfqWE3aa84YDREa62WZUK03HfKx3O0hty0Yrhl5EDyxT0/zTD1R7eaa009b17oIlNLdfcAVsLYJF3ini3o/DAo8NszokY1/AgMBAAECggEAC975Foc7GAT8oSyoAhgStrg2iCIombUHYayrOAr62oilmmWYgwFydhHbBbKquOiIJHQ8akKsbOA5s/SrQz1yqX3P1jZc1j5CyV9KL38kmnf5vctECagiBm6AOp7heRHgNwda5pFwRRDoNYjKvTXlf/di7lIXi8SCzXr3VfZKPgy5xu403xxpLxAdfR5zaxVQyoa6irl5X8VJnx3cqpMtlGhUuC5ABXIqCvhTog6/sywD8gYsY60FlKF2yWw8K67uJ3UZpEyPgBYaacNHWsSVxU5BSQA5TobXamCHSqPof5THq2JFeuqkWMCR7ZxgjFwiFStoCn++c9MWiHGCNKRjEQKBgQC3vytKPBQORF890cyLDx/NkL7i7PokZw5LElLJaO5vx8hxbQumgzQO+WMm8KfrQWzMaQs9gHm3N9tSL0wxqnjaMkvDl+VvRaAZDOs3RO5/HurN1M4kikeTvSETeDDkawwHCTnIJyaJ4McKw+oj+RnJ0wxNC8Rn+/YoS33qHgnZBQKBgQCyr7L8NvismYPjbS6njXBQ4tSpEijK5ck+1DBmsgwh+J1xJCWYDMJXJ1MMmb5k67dVl//RM3LlZD2ZkhFcQQlpSc5sbendJZ8V/DXkcb633qwFJvHWblA3dlXWDGbB1rUAZTSLH2q9umBdPFapq3VPlcfDoCz+BelNPRtb5+DDswKBgCMtm1kcBW7x0hYgy3s4CwutEY6adg1wGYFv4Z+j0jpeUla83MUlS1sujbgPzOYyYWUFG1zL8wGL1RZ37at4iqijhnPM6eUSvGLvYTz0moCF58g+XLUMMEXtqJ50nU+t2uCh+IkETdGJ0jGSbooyj6hfGnTJsvGBgJAYW+Ptmk8NAoGAcOd3zsnlQxoVLeTVL+W87D8HZHUm9V1QVbS5iIpEBEsVIGJYpsSZH3ynizSyCw6t3xWV/NfnZ255Rcn30jT9/1s1ZWnl9WhKJxgf7WK5wXCffPnooTc+1GyfPArF+wDxiXhpRObrCUr0jLUNhVPjYhAgE0noY/HUQouz99SCPuUCgYBxD+7z94SjXnSBYRWcBDHuKW1aBctNcledavJdX8ZEW79ppr/2SqAxiSU5RJg2aJbB0KK2VFbbmpQuMBUsOpCQpToPdTahI4Qbrd+yK5W3y5zXaruu+t0oM6+a6qrYKqDD2fClh/WJLEnX2RkSHedZ9FX/uJzvPjXy5i0ACNyyNA==";
    public static final String RSA_PRIVATE = "";
    public static final String APPID = "2021003104637583";
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private Activity mContext;

    public Ali_Pay(Activity context){
        this.mContext=context;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")

                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showAlert(mContext, "支付成功"+ payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(mContext, "支付失败"+ payResult);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(mContext, "支付成功" + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(mContext, "支付失败" + authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * 支付宝支付业务示例
     */
    public void payV2() {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            showAlert(mContext,"需要配置 PayDemoActivity 中的 APPID 和 RSA_PRIVATE");
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mContext);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("mdp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("确定", null)
                .setOnDismissListener(onDismiss)
                .show();
    }

}
