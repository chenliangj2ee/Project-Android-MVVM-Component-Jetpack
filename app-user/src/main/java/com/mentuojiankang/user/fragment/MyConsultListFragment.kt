package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanMyConsult
import com.mentuojiankang.user.databinding.FragmentMyConsultListBinding
import com.mentuojiankang.user.databinding.ItemMyConsultBinding
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.BusCode
import com.mtjk.utils.click
import com.mtjk.utils.sendSelf
import io.agora.vlive.bean.BeanParam
import kotlinx.android.synthetic.main.item_my_consult.view.*

/**
 * tag==咨询列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class MyConsultListFragment : MyBaseFragment<FragmentMyConsultListBinding, ConsultViewModel>() {

    @MyField
    var consultType = 0

    override fun initOnCreateView() {

        with(mBinding) {
            refresh.bindData<BeanMyConsult> {
                with(it.binding as ItemMyConsultBinding) {
                    order = it
                    root.startConsult.click { _ ->
                        intoLiveRoom(it)
                    }
                }

            }

            refresh.loadData {
                activity?.let {
                    mViewModel.getMyConsultList(consultType, refresh.pageIndex, refresh.pageSize)
                        .obs(it) {
                            it.c { refresh.addCache(it.records) }
                            it.y { refresh.addCache(it.records) }

                        }
                }
            }

        }

    }

    /**
     * 进入直播室
     *
     */
    private fun intoLiveRoom(bean: BeanMyConsult) {
        if(bean.channelName.isNullOrEmpty())
            return
        var liveParam = BeanParam()
        when (bean.consultType) {
            1 -> {//视频
                liveParam.liveType = BeanParam.LiveType.VIDEO_ONE
            }
            2 -> {//语音
                liveParam.liveType = BeanParam.LiveType.AUDIO_ONE
            }
        }

        liveParam.liveTitle = bean.title
        liveParam.userId = bean.expertId
        liveParam.userHeader = bean.avatar
        liveParam.userName = bean.expertName
        liveParam.save()
        bean.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
    }

    /**
     * 进入IM语音
     */
    private fun imAudio(bean: BeanMyConsult) {
    }


}