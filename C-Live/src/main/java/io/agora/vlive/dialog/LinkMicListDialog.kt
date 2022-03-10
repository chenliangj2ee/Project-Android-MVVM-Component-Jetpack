package io.agora.vlive.dialog

import android.view.Gravity
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.utils.BusCode
import com.mtjk.utils.click
import com.mtjk.utils.show
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe
import io.agora.vlive.bean.BeanLinkUser
import io.agora.vlive.databinding.ItemLinkMicListBinding
import io.agora.vlive.databinding.TrtcliveroomLinkMicListBinding
import kotlinx.android.synthetic.main.trtcliveroom_link_mic_list.view.*

/**
 * tag==申请消息
 * author:chenliang
 * date:2021/11/23
 */
@MyClass(mDialogGravity = Gravity.BOTTOM,mDialogAnimation = true)
class LinkMicListDialog : MyBaseDialog<TrtcliveroomLinkMicListBinding>() {
    var users = ArrayList<BeanLinkUser>()
    lateinit var clickFun: (user: BeanLinkUser,boo:Boolean) -> Unit
    override fun initCreate() {
        RxBus.get().register(this)
        notifyDataSetChanged()
    }

    fun notifyDataSetChanged(){
        with(mRootView!!) {
            recycler.disable()
            recycler.bindData<BeanLinkUser> {
                var user = it
                with(it.binding as ItemLinkMicListBinding) {
                    this.user = it
                    this.accept.click {
                        if (clickFun != null && !user.linkAccept)
                            clickFun(user,true)
                    }
                    this.cancel.click {
                        if (clickFun != null && !user.linkAccept)
                            clickFun(user,false)
                    }
                }
            }
            close.click { dismiss() }
            placeholder.show(users.size == 0)
            recycler.addDatas(users)
        }
    }


    fun setItemClick(func: (user: BeanLinkUser,boo:Boolean) -> Unit) {
        this.clickFun = func
    }

    fun setData(users: ArrayList<BeanLinkUser>) {
        this.users = users
    }

    @Subscribe(code = BusCode.REFRESH_LINK_MIC_LIST)
    fun refresh(users: ArrayList<BeanLinkUser>) {
        if (mRootView == null)
            return
        with(mRootView) {
            this?.placeholder?.show(users.size == 0)
            this?.recycler!!.clearData()
            this?.recycler!!.addDatas(users)
        }
    }

    override fun dismiss() {
        super.dismiss()
        RxBus.get().unRegister(this)
    }
}