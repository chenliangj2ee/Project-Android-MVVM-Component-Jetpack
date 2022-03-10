package com.fcyd.expert.activity

import com.daimajia.swipe.SwipeLayout
import com.fcyd.expert.bean.BeanAuth
import com.mtjk.bean.BeanCertificate
import com.fcyd.expert.databinding.ActivityQualificationBinding
import com.fcyd.expert.databinding.ItemQualificationListBinding
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import gorden.rxbus2.Subscribe

/**
 * tag==资格证书
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "资格证书")
class QualificationListActivity : MyBaseActivity<ActivityQualificationBinding, AccountViewModel>() {
    var arrayList = arrayListOf<BeanCertificate>()
    @MyField
    var beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()
    override fun initCreate() {
        if (beanAuth == null)
            beanAuth = BeanAuth().get<BeanAuth>() ?: BeanAuth()
        if (beanAuth != null && beanAuth.zgzs != null)
            arrayList.addAll(beanAuth.zgzs)
        mToolBar.showRight("添加证书") { goto(QualificationEditActivity::class.java) }
        var swip: SwipeLayout? = null
        mBinding.refresh.disable()
        mBinding.refresh.bindData<BeanCertificate> {
            with(it.binding as ItemQualificationListBinding) {
                this.data = it
                edit.goto(QualificationEditActivity::class.java, "bean", it)
                delete.click { _ ->
                    arrayList.remove(it)
                    mBinding.refresh.removeData(it)
                    mBinding.refresh.notifyDataSetChanged()
                    swipe.close()
                    mBinding.empty.show(arrayList.isEmpty())
                }
                swipe.addSwipeListener(object : SwipeLayout.SwipeListener {
                    override fun onStartOpen(layout: SwipeLayout?) {
                        swip?.close()
                        swip = swipe
                    }

                    override fun onOpen(layout: SwipeLayout?) {}

                    override fun onStartClose(layout: SwipeLayout?) {}

                    override fun onClose(layout: SwipeLayout?) {}

                    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {}

                    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}
                })
                swipe.close()
            }
        }
        mBinding.refresh.addDatas(arrayList)
        mBinding.empty.show(arrayList.isEmpty())
    }

    override fun initClick() {
        with(mBinding) {
            add.click { goto(QualificationEditActivity::class.java) }
        }
    }

    @Subscribe(code = BusCode.RESULT_DELETE_ZGZS)
    fun delete(bean: BeanCertificate) {
        for (item in arrayList) {
            if (item.myId == bean.myId) {
                arrayList.removeAt(bean.itemPosition)
                mBinding.refresh.clearData()
                mBinding.refresh.addDatas(arrayList)
                mBinding.refresh.notifyDataSetChanged()
                break
            }
        }
    }

    @Subscribe(code = BusCode.RESULT_ADD_ZGZS)
    fun add(bean: BeanCertificate) {

        //添加
        if (arrayList.none { it.myId == bean.myId }) {
            arrayList.add(bean)
        } else {//修改
            for ((index, item) in arrayList.withIndex()) {
                if (item.myId == bean.myId) {
                    arrayList[index] = bean
                    break
                }
            }
        }

        mBinding.refresh.clearData()
        mBinding.refresh.addDatas(arrayList)
        mBinding.empty.show(arrayList.isEmpty())
    }

    override fun onPause() {
        super.onPause()
        beanAuth?.zgzs = arrayList
        beanAuth?.save()
        send(BusCode.REFRESH_EXPERT_EDIT)

    }


}