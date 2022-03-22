package com.fcyd.expert.fragment

import com.fcyd.expert.R
import com.fcyd.expert.activity.ReleaseConsultActivity
import com.fcyd.expert.activity.StudioMediaEditActivity
import com.fcyd.expert.bean.BeanExpertStatus
import com.fcyd.expert.bean.BeanTODO
import com.fcyd.expert.bean.BeanTodoGuide
import com.fcyd.expert.databinding.FragmentTodoBinding
import com.fcyd.expert.databinding.ItemTodoBinding
import com.fcyd.expert.databinding.ItemTodoGuideBinding
import com.fcyd.expert.vm.TodoViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.obj.ObjectLocalTodo
import com.mtjk.obj.ObjectProduct
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import io.agora.vlive.bean.BeanParam
import java.lang.Exception

/**
 * tag==待办/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "待办")
class TodoFragment : MyBaseFragment<FragmentTodoBinding, TodoViewModel>() {

    private val ITEM_TYPE_LOCAL = 1
    private val ITEM_TYPE_COMMON = 2

    private var localTodo = ArrayList<BeanTODO>()

    override fun initOnCreateView() {
        mBinding.todoList
            .bindTypeToItemView(ITEM_TYPE_LOCAL, R.layout.item_todo_guide)
            .bindTypeToItemView(ITEM_TYPE_COMMON, R.layout.item_todo)
            .setDisableLoadMore()
            .bindData<BeanTODO>(::bindItem)
            .loadData(::initStudio)
    }

    /**
     * 绑定列表item
     */
    private fun bindItem(bean: BeanTODO) {
        if(bean.itemType == ITEM_TYPE_LOCAL) {
            with(bean.binding as ItemTodoGuideBinding) {
                this.data = bean.guide
                when (bean.guide.todoType) {
                    ObjectLocalTodo.WORKROOM -> {
                        editButton.goto(StudioMediaEditActivity::class.java)
                    }
                    ObjectLocalTodo.CONSULT -> {
                        editButton.goto(ReleaseConsultActivity::class.java)
                    }
                    else -> {}
                }
            }
            return
        }
        with(bean.binding as ItemTodoBinding) {
            this.data = bean
            stop.click { stopService(bean) }
            start.click { startService(bean) }
        }
    }

    /**
     * 加载待办列表数据
     */
    @Subscribe(code = BusCode.STUDIO_EDIT_SUCCESS)
    public fun initStudio() {
        mViewModel.getExpertStatus().obs(this@TodoFragment) {
            it.y { initLocalTodo(it) }
            loadTodoList()
        }
    }

    private fun initLocalTodo(status: BeanExpertStatus) {
        localTodo?.clear()
        if(status.shopStatus != 400) {
            localTodo?.add(createLocalTodoItem("工作室信息填写", "请完成工作室信息填写", ObjectLocalTodo.WORKROOM))
        }
        if(status.serverStatus != 200) {
            localTodo?.add(createLocalTodoItem("发布咨询", "请发布您的咨询服务", ObjectLocalTodo.CONSULT))
        }
    }

    private fun createLocalTodoItem(title: String, desc: String, type: Int) :BeanTODO{
        var todoItem = BeanTODO()
        var guide = BeanTodoGuide()
        guide.title = title
        guide.desc = desc
        guide.todoType = type
        todoItem.guide = guide
        todoItem.itemType = ITEM_TYPE_LOCAL
        return todoItem
    }

    private fun loadTodoList() {
        mViewModel.getTodoList().obs(this@TodoFragment) {
            it.c {
                /**
                 * 去掉重复，后台bug
                 */
                var data = ArrayList<BeanTODO>()
                if(!localTodo?.isEmpty()) {
                    data.addAll(localTodo)
                }
                it.forEach {
                    it.itemType = ITEM_TYPE_COMMON
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
                if(!localTodo?.isEmpty()) {
                    data.addAll(localTodo)
                }
                it.forEach {
                    it.itemType = ITEM_TYPE_COMMON
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
            it.n {
                if(!localTodo?.isEmpty()) {
                    var data = ArrayList<BeanTODO>()
                    data.addAll(localTodo)
                    mBinding.todoList.addDatas(data)
                }
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