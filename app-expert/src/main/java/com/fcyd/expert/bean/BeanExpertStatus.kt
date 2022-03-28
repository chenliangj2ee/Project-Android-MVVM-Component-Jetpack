package com.fcyd.expert.bean

class BeanExpertStatus {
    var serverStatus = 0    //发布咨询状态 100: 未发布任何咨询服务, 200: 已发布咨询
    var shopStatus = 0      //店铺状态 100: 未认证，200：待审核，300：审核通过&未装修，400：审核通过&已装修
}