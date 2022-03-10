package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanMyLive
import com.fcyd.expert.databinding.ActivityMyLiveListBinding
import com.fcyd.expert.databinding.ItemMyLiveListBinding
import com.fcyd.expert.vm.LiveViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.*
import io.agora.vlive.bean.BeanParam
import kotlinx.android.synthetic.main.item_my_live_list.view.*

/**
 * tag==直播管理
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "直播管理")
class MyLiveListActivity : MyBaseActivity<ActivityMyLiveListBinding, LiveViewModel>() {
    var user =  getBeanUser()
    override fun initCreate() {

        with(mBinding) {
            refresh.bindData<BeanMyLive> { bean ->

                with(bean.binding as ItemMyLiveListBinding) {
                    data = bean
                    root.start.click {
                        var liveParam = BeanParam()
                        liveParam.liveTitle = bean.liveCourseSectionName
                        liveParam.save()
                        bean.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
                    }
                }

            }

            refresh.loadData {
                mViewModel.getMyLiveList().obs(this@MyLiveListActivity) {
                    it.c {
                        var data = ArrayList<BeanMyLive>()
                        it.forEach {
                            if (it.liveCourseSectionName.isNotBlank()) {
                                data.add(it)
                            }
                        }
                        refresh.addCache(data)
                    }
                    it.y {
                        var data = ArrayList<BeanMyLive>()
                        it.forEach {
                            if (it.liveCourseSectionName != null) {
                                data.add(it)
                            }
                        }
                        refresh.addDatas(data)
                    }

                }
            }
        }
    }

    override fun initClick() {
    }



}