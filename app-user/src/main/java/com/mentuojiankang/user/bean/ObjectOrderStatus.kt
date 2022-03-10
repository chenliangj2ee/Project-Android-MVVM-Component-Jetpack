package com.mentuojiankang.user.bean

/**
 * author:chenliang
 * date:2021/11/18
 */
object ObjectOrderStatus {
    var noPayment = 10//10=>待付款;
    var yesPayment = 60//60=>已支付;//待确认/当前时间判断开始时间
    var noEvaluate = 65//65 待评价
    var orderSuccess = 70//70=>订单成功;
    var finish = 80// 80=>交易完成;
    var cancel = 90//90=>订单取消;
    var close = 100//100=>交易关闭;
    var refundUnderView = 110//110=>退单待审核;
    var returnProducting = 120// 120=>退单退货处理;
    var returnMoneying = 130// 130=>退单退款处理;
    var allFinish = 140//  140=>已完成;


}