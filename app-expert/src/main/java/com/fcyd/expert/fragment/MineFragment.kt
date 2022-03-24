package com.fcyd.expert.fragment

import com.fcyd.expert.activity.*
import com.fcyd.expert.databinding.FragmentMineBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import gorden.rxbus2.Subscribe
import gorden.rxbus2.ThreadMode

/**
 * tag==我的/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class MineFragment : MyBaseFragment<FragmentMineBinding, AccountViewModel>() {
    var user =  getBeanUser()
    override fun initOnCreateView() {
        mBinding.data = user
        refreshUserInfo()
    }

    override fun initClick() {
        super.initClick()
        with(mBinding) {
            this.myfans.goto(MyFansActivity::class.java)
            this.edit.goto(PersonalInfoEditActivity::class.java)
            this.myOrder.goto(MyOrderActivity::class.java)
            this.myHelp.goto(HelpAndActivity::class.java)
            this.mySetting.goto(SettingActivity::class.java)
        }
    }


    @Subscribe(code = BusCode.REFRESH_MINE_USER_INFO, threadMode = ThreadMode.MAIN)
    fun refreshUserInfo() {
        mViewModel.getUserInfo().obs(this) {
            it.y {
                mBinding.data = it
                user?.realName = it.realName
                user?.save()
                user?.sendSelf(BusCode.UPDATE_IM_USERINFO)
            }
        }

    }

    fun initExpertData() {
        refreshUserInfo()
        initVM(UserViewModel::class.java).getExpertData().obs(this) {
            it.c { mBinding.beanIncome = it }
            it.y { mBinding.beanIncome = it }
        }
    }

}