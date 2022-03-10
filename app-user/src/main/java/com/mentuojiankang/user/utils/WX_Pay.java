package com.mentuojiankang.user.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.mtjk.BaseInit;
import com.mtjk.utils.MyFunctionKt;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WX_Pay {
    public IWXAPI api;
    private PayReq req;
    //添加appId
    private String wxAPPID = "wx4a3828868bbdc84f";
    private Context mcontext;

    public WX_Pay(Context context) {
        mcontext = context;
        api = WXAPIFactory.createWXAPI(context, wxAPPID, false);
    }

    /**
     * 向微信服务器发起的支付请求
     */
    public void pay(String partnerid, String prepayid) {
        req = new PayReq();
        req.appId = wxAPPID;//APPID
        req.partnerId = partnerid;//    商户号
        req.prepayId = prepayid;//  预付款ID
        req.nonceStr = getRoundString();//随机数
        req.timeStamp = getTimeStamp();//时间戳
        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
        String sign = getSign();
        req.sign = sign;//签名

        api.registerApp(wxAPPID);
        api.sendReq(req);
    }
    //目前新版微信sdk已经弃用直接跳转到微信服务号的方法
        /*public void jumpToWixinprofile(){
            if (api.isWXAppInstalled()) {
                JumpToBizProfile.Req req = new JumpToBizProfile.Req();
                req.toUserName = "";
                req.extMsg = "";
                req.profileType = JumpToBizProfile.JUMP_TO_NORMAL_BIZ_PROFILE;
                api.sendReq(req);
            } else {
                ToastUtil.toastLongMessage("微信未安装");
            }
        }*/

    /**
     * 如果需要拉起微信再去关注公众号
     * 跳转到微信
     */
    public void getWechatApi() {

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            BaseInit.INSTANCE.getAct().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            MyFunctionKt.toast(mcontext, "检查到您手机没有安装微信，请安装后使用该功能");
        }
    }


    public void jumpWX(String token, String orderId) {
        String appId = wxAPPID; // 填应用AppId
        IWXAPI api = WXAPIFactory.createWXAPI(mcontext, appId);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_8c8ff05fc55a"; // 填小程序原始id
        req.path = "pages/index/index?orderId=" + orderId + "&token=" + token;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        //req.path ="pages/index/index";
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// WXMiniProgramTypeRelease 正式版    WXMiniProgramTypeTest 开发版    WXMiniProgramTypePreview 体验版
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;// WXMiniProgramTypeRelease 正式版
        api.sendReq(req);
    }

    @NonNull
    private String getSign() {
        Map<String, String> map = new HashMap<>();
        map.put("appid", req.appId);
        map.put("partnerid", req.partnerId);
        map.put("prepayid", req.prepayId);
        map.put("package", req.packageValue);
        map.put("noncestr", req.nonceStr);
        map.put("timestamp", req.timeStamp);

        ArrayList<String> sortList = new ArrayList<>();
        sortList.add("appid");
        sortList.add("partnerid");
        sortList.add("prepayid");
        sortList.add("package");
        sortList.add("noncestr");
        sortList.add("timestamp");
        sort(sortList);

        String md5 = "";
        int size = sortList.size();
        for (int k = 0; k < size; k++) {
            if (k == 0) {
                md5 += sortList.get(k) + "=" + map.get(sortList.get(k));
            } else {
                md5 += "&" + sortList.get(k) + "=" + map.get(sortList.get(k));
            }
        }
        String stringSignTemp = md5 + "&key=商户密钥";

        String sign = MD5.Md5(stringSignTemp).toUpperCase();

        return sign;
    }

    private String getRoundString() {

        Random random = new Random();

        return random.nextInt(10000) + "";
    }

    private String getTimeStamp() {
        return new Date().getTime() / 10 + "";
    }


    private static void sort(ArrayList<String> strings) {
        Collections.sort(strings);
    }
}
