package com.mentuojiankang.user.fragment

import android.view.View
import com.mentuojiankang.user.activity.*
import com.mentuojiankang.user.databinding.FragmentMineBinding
import com.mentuojiankang.user.vm.OrderViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.BusCode
import com.mtjk.utils.getBeanUser
import com.mtjk.utils.goto
import com.mtjk.utils.show
import com.mtjk.vm.AccountViewModel
import gorden.rxbus2.Subscribe
import gorden.rxbus2.ThreadMode
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * tag==我的/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class MineFragment : MyBaseFragment<FragmentMineBinding, AccountViewModel>() {
    var ordermodel: OrderViewModel = OrderViewModel()
    var user =  getBeanUser()
    override fun initOnCreateView() {
        mBinding.data = user
        ordermodel.getblance().obs(this) {
            it.y {
                if (it.listWorker.size > 0) {
                    conmpanyname.show(true)
//                    conmpanyname.text = it.listWorker[0].companyName
                    conmpanyname.visibility = if(it.listWorker[0].companyName.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun initClick() {
        super.initClick()
        with(mBinding) {
            this.edit.goto(PersonalInfoEditActivity::class.java)
            this.follow.goto(MyFollowActivity::class.java)
            this.collection.goto(MyCollectionActivity::class.java)
            this.history.goto(MyHistoryActivity::class.java)
            this.myTest.goto(MyTestingActivity::class.java)
            this.myConsult.goto(MyConsultActivity::class.java)
            this.myCourse.goto(MyCourseActivity::class.java)
            this.myOrder.goto(MyOrderActivity::class.java)
            this.myWallet.goto(MyWalletActivity::class.java)
            this.myHelp.goto(HelpActivity::class.java)
            this.myVipcard.goto(VPICardActivity::class.java, "enterway", false)
            this.mySetting.goto(SettingActivity::class.java)
        }
    }


    @Subscribe(code = BusCode.REFRESH_MINE_USER_INFO, threadMode = ThreadMode.MAIN)
    fun refreshUserInfo() {
        mViewModel.getUserInfo().obs(this) {
            it.y {
                mBinding.data = it
                it?.save()
            }
        }
    }

}