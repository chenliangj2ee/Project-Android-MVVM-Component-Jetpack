package com.mentuojiankang.user.activity

import android.view.View
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanCard
import com.mentuojiankang.user.databinding.ActivityVipCardBinding
import com.mentuojiankang.user.databinding.ItemVipCardBinding
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.*
import kotlinx.android.synthetic.main.activity_vip_card.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * tag==会员卡
 * author:chenliang
 * date:2021/11/9
 */
@MyClass(mToolbarTitle = "会员卡")
class VPICardActivity : MyBaseActivity<ActivityVipCardBinding, OrderViewModel>() {
    var vipCardId = "0"
    var vipCardAllNum = ArrayList<BeanCard>()
    var vipcards = ArrayList<BeanCard>()
    var beanCard = ArrayList<BeanCard>()
    var startTime = ""
    var endTime = ""
    var vipenterway = false

    @MyField
    var enterway = false
    override fun initCreate() {
        vipenterway = enterway
        initDate()
    }

    private fun initDate() {
        refresh.bindData<BeanCard> { vipbean ->
            with(vipbean.binding as ItemVipCardBinding) {
                data = vipbean
                //订单确认和会员卡两个入口，ui显示不同
                vipCardUse.show(vipenterway)
                //会员卡默认选择第一张且只能选择一张
                root.click {
                    if (vipbean.itemSelected) {
                        vipCardId = "-1"
                        vipbean.itemSelected = false
                        refresh.notifyDataSetChanged()
                    } else {
                        refresh.selected(vipbean.itemPosition)
                        vipCardId = vipbean.itemPosition.toString()
                    }
                }

                if (vipbean != null) {
                    if (vipbean.startTime != null && vipbean.validTime != null) {
                        startTime = timeToData(vipbean.startTime)
                        endTime = timeToData(vipbean.validTime)
                        startToEnd.setText("有效期:" + startTime + "-" + endTime)
                    } else {
                        startToEnd.setText("有效期:")
                    }

                }
            }
        }

        refresh.loadData {
            mViewModel.getvipcardslist().obs(this) {
                it.y {
                    if (it != null && it.size > 0) {
                        refresh.addDatas(it)
                        refresh.selected(0)
                        useVipCard.show(vipenterway)
                    }
                }
                it.n { toast(it) }
                it.c {
                    if (it != null && it.size > 0) {
                        refresh.addDatas(it)
                        refresh.selected(0)
                        useVipCard.show(vipenterway)
                    }
                }
            }
        }

    }

    override fun initClick() {
        useVipCard.click {
            postDelayed(500) {
                vipCardId.sendSelf(BusCode.VIP_CARD_USEORCANLE)
                finish()
            }
        }
    }
    //2022-01-07T16:00:00.000+00:00
    fun timeToData(time: String): String {
        return time.dateT("yyyy-MM-dd")
    }
}