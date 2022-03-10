package com.fcyd.expert.fragment

import com.chenliang.processor.appexpert.MySp
import com.fcyd.expert.activity.ReleaseConsultActivity
import com.fcyd.expert.activity.ReleaseConsultRemindActivity
import com.fcyd.expert.activity.StudioMediaEditActivity
import com.fcyd.expert.bean.BeanTODO
import com.fcyd.expert.databinding.FragmentTodoBinding
import com.fcyd.expert.databinding.ItemTodoBinding
import com.fcyd.expert.vm.StudioViewModel
import com.fcyd.expert.vm.TodoViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import gorden.rxbus2.Subscribe
import io.agora.vlive.bean.BeanParam
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.android.synthetic.main.fragment_todo.view.*
import kotlinx.android.synthetic.main.layout_todo_setting.*
import kotlinx.android.synthetic.main.layout_todo_setting.view.*
import java.lang.Exception

/**
 * tag==待办/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "待办")
class TodoFragment : MyBaseFragment<FragmentTodoBinding, TodoViewModel>() {
    var user = getBeanUser()
    override fun initOnCreateView() {
        mRootView.layout_todo_setting.show(!MySp.isCloseNotify())
        initStudio()
        mBinding.todoList.setDisableLoadMore()
        mBinding.todoList.bindData<BeanTODO>(::bindItem).loadData(::loadTodos)
    }

    override fun initClick() {
        mRootView.fbzx.goto(ReleaseConsultActivity::class.java)
        mRootView.closeNotify.click {
            MySp.setCloseNotify(true)
            mRootView.layout_todo_setting.show(false)
        }
    }

    /**
     * 工作室装修
     */
    @Subscribe(code = BusCode.STUDIO_EDIT_SUCCESS)
    public fun initStudio() {
        initVM(StudioViewModel::class.java).getStudioInfo().obs(this) {
            it.y {
                when (it.verify) {
                    0 -> {
                        verify.show(false)
                        mRootView.gzszx.goto(StudioMediaEditActivity::class.java)
                    }
                    1 -> {
                        verify.text = "待审核"
                        mRootView.gzszx.goto(ReleaseConsultRemindActivity::class.java)
                    }
                    2 -> {
                        verify.text = "审核通过"
                        layout_todo_setting.show(false)
                        mBinding.todoList.show(true)
                    }
                    3 -> {
                        verify.text = "审核不通过"
                        mRootView.gzszx.goto(StudioMediaEditActivity::class.java)
                    }
                }
                user?.shopId = it.id;
                user?.save()
            }
        }
    }

    /**
     * 绑定列表item
     */
    private fun bindItem(bean: BeanTODO) {
        with(bean.binding as ItemTodoBinding) {
            this.data = bean
            stop.click { stopService(bean) }
            start.click { startService(bean) }
        }
    }

    /**
     * 加载待办列表数据
     */
    private fun loadTodos() {
        mViewModel.getTodoList().obs(this@TodoFragment) {
            it.c {

                /**
                 * 去掉重复，后台bug
                 */
                var data = ArrayList<BeanTODO>()
                it.forEach {
                    if (it.productType == ObjectProduct.TYPE_LIVE) {
                        data.add(it)
                    }
                    if (it.productType == ObjectProduct.TYPE_CONSULT && it.extend.userList.isNotEmpty()) {
                        data.add(it)
                    }
                }
                /**
                 * 去掉重复，后台bug
                 */
                mBinding.todoList.addCache(data)
            }
            it.y {
                /**
                 * 去掉重复，后台bug
                 */
                var data = ArrayList<BeanTODO>()
                it.forEach {
                    if (it.productType == ObjectProduct.TYPE_LIVE) {
                        data.add(it)
                    }
                    if (it.productType == ObjectProduct.TYPE_CONSULT && it.extend.userList.isNotEmpty()) {
                        data.add(it)
                    }
                }
                /**
                 * 去掉重复，后台bug
                 */
                mBinding.todoList.addDatas(data)
            }
        }
    }

    /**
     * 结束服务
     */
    private fun stopService(bean: BeanTODO) {

        dialog("确定结束服务?").y {
            mViewModel.stopTodoService(bean.id).obs(this) {
                it.y { mBinding.todoList.refresh() }
            }
        }.show(this)


    }

    /**
     * 开始服务
     */
    private fun startService(bean: BeanTODO) {

        try {

            log("开始服务：${bean.toJson()}")
            var liveParam = BeanParam()
            liveParam.liveTitle = bean.extend.title + ""
            liveParam.liveChannelName = bean.extend.channelName
            if (!bean.extend.userList.isNullOrEmpty())
                liveParam.userId = bean.extend.userList[0].userId
            if (bean.productType == ObjectProduct.TYPE_CONSULT) {

                log("咨询》。。。。。。。。。。。。")
                if (bean.extend.userList.isNotEmpty()) {
                    var resultUser = bean.extend.userList?.get(0)
                    when (bean.extend.consultType) {
                        "1" -> {//视频
                            log("视频直播》。。。。。。。。。。。。${bean.extend.channelName}")
                            liveParam.liveType = BeanParam.LiveType.VIDEO_ONE
                            liveParam.save()
                            bean.extend.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
                            return
                        }
                        "2" -> {//语音
                            log("语音直播》。。。。。。。。。。。。${bean.extend.channelName}")
                            liveParam.liveType = BeanParam.LiveType.AUDIO_ONE
                            liveParam.save()
                            bean.extend.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
                        }
                    }

                } else {
                    toast("服务器数据异常")
                }
                return
            }

            if (bean.productType == ObjectProduct.TYPE_LIVE) {
                log("多人直播》。。。。。。。。。。。。${bean.extend.channelName}")
                liveParam.liveType = BeanParam.LiveType.VIDEO_MORE
                liveParam.save()
                bean.extend.channelName?.sendSelf(BusCode.LIVE_GET_RTCTOKEN)
                return
            }
        } catch (e: Exception) {

        }

    }


    @Subscribe(code = BusCode.ORDER_REFRESH)
    override fun refresh() {
        mBinding.todoList.refresh()
    }
}