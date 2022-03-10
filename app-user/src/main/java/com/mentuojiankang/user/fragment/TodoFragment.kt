package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.bean.BeanTODO
import com.mentuojiankang.user.databinding.FragmentTodoBinding
import com.mentuojiankang.user.databinding.ItemTodoBinding
import com.mentuojiankang.user.vm.UserViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import gorden.rxbus2.Subscribe
import io.agora.vlive.bean.BeanParam
import kotlinx.android.synthetic.main.fragment_todo.*


/**
 * tag==待办/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "待办")
class TodoFragment : MyBaseFragment<FragmentTodoBinding, UserViewModel>() {
    var user = getBeanUser()
    override fun initOnCreateView() {
        mBinding.todoList.setDisableLoadMore()
        mBinding.todoList.bindData<BeanTODO>(::bindItem).loadData(::loadTodos)
    }

    /**
     * 绑定列表item
     */
    private fun bindItem(bean: BeanTODO) {
        with(bean.binding as ItemTodoBinding) {
            data = bean
            start.click { startService(bean) }
        }
    }

    /**
     * 加载列表数据
     */
    private fun loadTodos() {
        mViewModel.getTodoList()
            .obs(this@TodoFragment) {
                it.c { mBinding.todoList.addCache(it) }
                it.y { mBinding.todoList.addDatas(it) }
            }
    }

    @Subscribe(code = BusCode.TODO_REFRESH)
    override fun refresh() {
        mBinding.todoList.refresh()
    }

    /**
     * 开始服务
     */
    private fun startService(bean: BeanTODO) {

        var liveParam = BeanParam()
        liveParam.liveTitle = bean.extend.title
        liveParam.userId = bean.extend.expertAccountId
        liveParam.userHeader = bean.extend.avatar
        liveParam.userName = bean.extend.expertName

        when (bean.productType) {
            //咨询
            ObjectProduct.TYPE_CONSULT -> {

                when (bean.extend.consultType) {
                    "1" -> {//视频
                        liveParam.liveType = BeanParam.LiveType.VIDEO_ONE
                        liveParam.save()
                        bean.extend.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)

                    }
                    "2" -> {//语音
                        liveParam.liveType = BeanParam.LiveType.AUDIO_ONE
                        liveParam.save()
                        bean.extend.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
                    }
                }

            }
            //直播
            ObjectProduct.TYPE_LIVE -> {
                liveParam.liveType = BeanParam.LiveType.VIDEO_MORE
                liveParam.save()
                bean.extend.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
            }
        }
    }


}