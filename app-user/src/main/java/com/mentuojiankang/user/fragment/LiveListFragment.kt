package com.mentuojiankang.user.fragment

import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.activity.LiveDetailActivity
import com.mentuojiankang.user.bean.BeanLive
import com.mentuojiankang.user.databinding.FragmentLiveListBinding
import com.mentuojiankang.user.databinding.ItemLiveListBinding
import com.mentuojiankang.user.vm.LiveViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.*
import io.agora.vlive.bean.BeanParam
import kotlinx.android.synthetic.main.item_live_list.view.*

/**
 * tag==活动室/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/user/live")
class LiveListFragment : MyBaseFragment<FragmentLiveListBinding, LiveViewModel>() {

    override fun initOnCreateView() {
        with(mBinding) {
            refresh.setDisableLoadMore()
            refresh.bindData<BeanLive> { live ->
                with(live.binding as ItemLiveListBinding) {
                    bean = live
                    root.click {
                        if (live.status == 1) {
                            toLiveRoom(live)
                        } else {
                            toDetail(live)
                        }
                    }
                    root.book.click { bookClick(live) }
                }
            }.loadData {
                mViewModel.getLiveList().obs(this@LiveListFragment) {
                    it.c { refresh.addCache(it) }
                    it.y { refresh.addDatas(it) }
                }
            }
        }
    }

    private fun bookClick(live: BeanLive) {
        if(live.free == 1) {
            mViewModel.bookLive(live).obs(this@LiveListFragment) {
                it.y { mBinding.refresh.refresh() }
            }
        } else {
            toDetail(live)
        }
    }

    private fun toLiveRoom(live: BeanLive) {
        if(live.free == 2 && live.bought != 2) {
            toast("未购买直播课程，无法进入房间")
            return
        }
        var liveParam = BeanParam()
        liveParam.liveTitle = live.liveCourseName
        liveParam.userId = live.accountId
        liveParam.userHeader = live.avatar
        liveParam.userName = live.nickname
        liveParam.liveType=BeanParam.LiveType.VIDEO_MORE
        liveParam.save()
        live.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
    }

    private fun toDetail(live: BeanLive) {
        goto(
            LiveDetailActivity::class.java,
            "live", live
        )
    }

}