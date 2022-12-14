package com.fcyd.expert

import androidx.fragment.app.Fragment
import com.chenliang.annotation.MyRoute
import com.fcyd.expert.activity.ExpertAuthSubmitSuccessActivity
import com.fcyd.expert.activity.ExpertSettledActivity
import com.fcyd.expert.fragment.MineFragment
import com.fcyd.expert.fragment.TodoFragment
import com.fcyd.expert.fragment.WorksFragment
import com.fcyd.expert.vm.LiveViewModel
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.BaseInit
import com.mtjk.base.BaseTabActivity
import com.mtjk.base.databinding.ActivityBaseTabBinding
import com.mtjk.base.obs
import com.mtjk.base.obsf
import com.mtjk.bean.BeanLiveData
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe

@MyRoute(mPath = "/app/main")
class MainActivity : BaseTabActivity<ActivityBaseTabBinding, UserViewModel>() {
    var user = getBeanUser()
    override fun initData() {
        tabFragments =
            arrayOf<Fragment>(WorksFragment(), TodoFragment(), goto("/im/main"), MineFragment())
        tabIconDefault = intArrayOf(
            R.mipmap.works_default,
            R.mipmap.find_default,
            R.mipmap.message_default,
            R.mipmap.mine_default
        )
        tabIconSelect = intArrayOf(
            R.mipmap.works_selected,
            R.mipmap.find_select,
            R.mipmap.message_select,
            R.mipmap.mine_select
        )
        tabText = intArrayOf(R.string.works, R.string.find, R.string.message, R.string.mine)
        send(BusCode.MAIN_IN)
    }


    override fun initCreate() {
        super.initCreate()
        tabSelected {

            if (it == 0) {
                (tabFragments?.get(0) as WorksFragment).initExpertData()
            }
            if (it == 1) {
                (tabFragments?.get(1) as TodoFragment).initStudio()
            }
            if (it == 3) {
                (tabFragments?.get(3) as MineFragment).initExpertData()
            }
            fullscreenTransparentBar(it == 3)
        }

        networkChange { initAccountInfo() }

    }

    /**
     * ???????????????????????????
     */
    @Subscribe(code = BusCode.UPDATE_EXPERT_INFO)
    fun initAccountInfo() {
        mViewModel.init().obs(this) {
            it.y {
                it.save()
                authResult(it.verify)
            }
        }
    }

    /**
     * verify:0-???????????????1-????????????2-???????????????3-????????????',4-???????????????
     */
    private fun authResult(verify: Int) {
        when (verify) {
            0 -> gotoFinish(ExpertSettledActivity::class.java)//?????????
            1 -> gotoFinish(ExpertAuthSubmitSuccessActivity::class.java)//???????????????
            2 -> {
            }//????????????
            3 -> authError()//????????????
            4 -> {
            }//???????????????
        }
    }


    /**
     * ????????????
     */
    private fun authError() {
        var dialog = dialog("??????????????????????????????????????????")
            .n("????????????") { finish() }
            .y("????????????") { gotoFinish(ExpertSettledActivity::class.java) }
        dialog.cancelable(false)
        dialog.show(this)
    }

    @Subscribe(code = BusCode.EXIT)
    fun exit() {
        finish()
    }


    @Subscribe(code = BusCode.IM_UNREADNUM_CHANGE)
    fun getImUnreadNum(num: String) {
        setNumber(2, num.toInt())
        send(BusCode.CONVERSATION_ITEM_NUM)
    }


    /**
     * ??????channelName??????token
     */
    @Subscribe(code = BusCode.LIVE_GET_RTCTOKEN)
    public fun initRtcToken(channelName: String) {
        var beanData = BeanLiveData().get<BeanLiveData>() ?: BeanLiveData()
        beanData.channelName = channelName
        beanData.save()
        initVM(LiveViewModel::class.java).getRTCToken(channelName).obsf() {
            it.y {
                var user = getBeanUser()
                user?.rtcToken = it
                user?.save()
                initRtmToken(channelName)
            }
        }
    }

    /**
     * ??????channelName??????token
     */
    private fun initRtmToken(channelName: String) {
        initVM(LiveViewModel::class.java).getRTMToken(channelName).obsf() {
            it.y {
                var user = getBeanUser()
                user?.rtmToken = it
                user?.save()
            }
        }

    }


}


