package io.agora.vlive.dialog

import android.view.Gravity
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseDialog
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe
import io.agora.vlive.Live
import io.agora.vlive.bean.BeanAudience
import io.agora.vlive.bean.BeanLinkUser
import io.agora.vlive.databinding.DialogDrawSelectUserBinding
import io.agora.vlive.databinding.ItemDrawUserBinding
import io.agora.vlive.databinding.ItemLinkMicListBinding
import io.agora.vlive.databinding.TrtcliveroomLinkMicListBinding
import kotlinx.android.synthetic.main.dialog_draw_select_user.*
import kotlinx.android.synthetic.main.dialog_draw_select_user.view.*
import kotlinx.android.synthetic.main.trtcliveroom_link_mic_list.view.*
import kotlinx.android.synthetic.main.trtcliveroom_link_mic_list.view.recycler
import okhttp3.internal.filterList
import java.util.ArrayList

/**
 * tag==在线用户
 * author:chenliang
 * date:2021/11/23
 */
@MyClass(mDialogGravity = Gravity.BOTTOM, mDialogAnimation = true)
class DrawingSelectorUserDialog : MyBaseDialog<DialogDrawSelectUserBinding>() {
    lateinit var clickFun: (user: BeanLinkUser, boo: Boolean) -> Unit
    override fun initCreate() {
        Live.audienceObjectList.forEach { it.itemSelected=false }
        Live.drawUserIds.clear()
        notifyDataSetChanged()
    }

    fun notifyDataSetChanged() {
        with(mRootView!!) {
            recycler.disable()
            recycler.bindData<BeanAudience> { bean ->
                with(bean.binding as ItemDrawUserBinding) {
                    this.user = bean
                    root.click {
                        bean.itemSelected = !bean.itemSelected
                        recycler.notifyDataSetChanged()
                    }
                }
            }


            /**
             * 过滤掉已经接受画板的用户
             */
            val filterList= ArrayList<BeanAudience>()
            Live.audienceObjectList.forEach { bean ->
                if (Live.drawingUsers.filter { it.id == bean.id }.isEmpty()) {
                    filterList.add(bean)
                }
            }
            this.userCount.text="(${Live.audienceObjectList.size})"
            recycler.addDatas(filterList)
        }
    }


    override fun initClick() {

        mBinding.finish.click {
            send(BusCode.LIVE_DRAW_SELECTED_USER_FINISH)
            dismiss()
        }
    }
}