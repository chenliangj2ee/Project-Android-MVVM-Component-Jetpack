package com.fcyd.expert.fragment

import com.fcyd.expert.R
import com.fcyd.expert.activity.*
import com.fcyd.expert.bean.BeanWork
import com.fcyd.expert.databinding.FragmentWorksBinding
import com.fcyd.expert.databinding.ItemFbfwBinding
import com.fcyd.expert.databinding.ItemWorksBinding
import com.fcyd.expert.vm.UserViewModel
import com.mtjk.act.EvaluateActivity
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import kotlinx.android.synthetic.main.item_works.view.*


/**
 * tag==工作台/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class WorksFragment : MyBaseFragment<FragmentWorksBinding, AccountViewModel>() {

    var baseArrays = arrayListOf<BeanWork>(
        BeanWork(R.drawable.icon_index_rzrz, "入驻认证", AuthActivity::class.java, ""),
        //BeanWork(R.drawable.icon_gzszx, "来访者", null, ""),
        BeanWork(R.drawable.icon_index_zbgl, "直播管理", MyLiveListActivity::class.java, ""),
        BeanWork(R.drawable.icon_index_zxgl, "咨询管理", ConsultManagerActivity::class.java, ""),
        BeanWork(R.drawable.icon_index_plgl, "评价管理", null, ""),
        BeanWork(R.drawable.icon_index_ddgl, "订单管理", MyOrderActivity::class.java, ""),
        BeanWork(R.drawable.icon_index_wallet, "我的钱包", WalletActivity::class.java, ""),

    )
    var serviceArrays = arrayListOf<BeanWork>(
        BeanWork(R.drawable.icon_index_fbzx, "发布咨询", ReleaseConsultActivity::class.java, ""),
//        BeanWork(R.drawable.icon_zxgl, "发起直播", null, ""),
//        BeanWork(R.drawable.icon_zjxx, "发布课程", null, ""),
    )
    var user =  getBeanUser()
    override fun initOnCreateView() {

        mBinding.user = user

        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanWork> { bean ->
                with(bean.binding as ItemWorksBinding) {
                    data = bean
                    root.click {
                        when (bean.title) {
                            "来访者" -> toast("敬请期待")
                            "评价管理" -> {
                                goto(
                                    EvaluateActivity::class.java,
                                    "shopId",
                                    user!!.shopId,
                                    "title",
                                    "我的评价"
                                )
                            }
                        }
                        if (bean.cls != null) {
                            goto(bean.cls!!)
                        }
                    }
                }
            }
            refresh.addDatas(baseArrays)

        }

        with(mBinding) {
            serviceRecyclerView.disable()
            serviceRecyclerView.bindData<BeanWork> { bean ->
                with(bean.binding as ItemFbfwBinding) {
                    data = bean
                    root.click {
                        when (bean.title) {
                            "发起直播" -> toast("敬请期待")
                            "发布课程" -> toast("敬请期待")
                        }
                        if (bean.cls != null) {
                            goto(bean.cls!!)
                        }
                    }

                }
            }
            serviceRecyclerView.addDatas(serviceArrays)
        }

        initExpertData()

    }

    fun initExpertData() {
        initVM(UserViewModel::class.java).getExpertData().obs(this) {
            it.c { mBinding.beanIncome = it }
            it.y { mBinding.beanIncome = it }
        }
    }

}