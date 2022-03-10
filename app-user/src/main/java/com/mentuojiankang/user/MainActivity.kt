package com.mentuojiankang.user

import androidx.fragment.app.Fragment
import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.fragment.MineFragment
import com.mentuojiankang.user.fragment.RecommendFragment
import com.mentuojiankang.user.fragment.TodoFragment
import com.mentuojiankang.user.vm.AppViewModel
import com.mentuojiankang.user.vm.LiveViewModel
import com.mtjk.BaseInit
import com.mtjk.base.BaseTabActivity
import com.mtjk.base.databinding.ActivityBaseTabBinding
import com.mtjk.base.obs
import com.mtjk.bean.BeanLiveData
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe

@MyRoute(mPath = "/app/main")
class MainActivity : BaseTabActivity<ActivityBaseTabBinding, AppViewModel>() {
    var user = getBeanUser()
    override fun initData() {
        tabFragments =
            arrayOf<Fragment>(RecommendFragment(), TodoFragment(), goto("/im/main"), MineFragment())
        tabIconDefault = intArrayOf(
            R.mipmap.home_default,
            R.mipmap.find_default,
            R.mipmap.message_default,
            R.mipmap.mine_default
        )
        tabIconSelect = intArrayOf(
            R.mipmap.home_select,
            R.mipmap.find_select,
            R.mipmap.message_select,
            R.mipmap.mine_select
        )
        tabText = intArrayOf(R.string.home, R.string.find, R.string.message, R.string.mine)

    }


    override fun initCreate() {
        super.initCreate()
        tabSelected { fullscreenTransparentBar(it == 3) }

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
     * 根据channelName获取token
     */
    @Subscribe(code = BusCode.LIVE_GET_RTCTOKEN)
    public fun initRtcToken(channelName: String) {
        if (channelName == null) {
            toast("服务器数据异常")
            return
        }

        var beanData = BeanLiveData().get<BeanLiveData>() ?: BeanLiveData()
        beanData.channelName = channelName
        beanData.save()

        initVM(LiveViewModel::class.java).getRTCToken(channelName).obs(BaseInit.act!!) {
            it.y {
                var user = BeanUser().get<BeanUser>()
                user?.rtcToken = it
                user?.save()
                initRtmToken(channelName)
            }
        }
    }

    /**
     * 根据channelName获取token
     */
    private fun initRtmToken(channelName: String) {
        initVM(LiveViewModel::class.java).getRTMToken(channelName).obs(BaseInit.act!!) {
            it.y {
                var user = getBeanUser()
                user?.rtmToken = it
                user?.save()
            }
        }

    }

}