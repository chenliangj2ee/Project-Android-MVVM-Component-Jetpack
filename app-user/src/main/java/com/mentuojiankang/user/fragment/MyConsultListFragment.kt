package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.ConsultInfoActivity
import com.mentuojiankang.user.bean.BeanMyConsult
import com.mentuojiankang.user.databinding.FragmentMyConsultListBinding
import com.mentuojiankang.user.databinding.ItemMyConsultBinding
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import kotlinx.android.synthetic.main.item_my_consult.view.*

/**
 * tag==咨询列表/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class MyConsultListFragment : MyBaseFragment<FragmentMyConsultListBinding, ConsultViewModel>() {

    @MyField
    var consultType = 0

    override fun initOnCreateView() {

        with(mBinding) {
            refresh.bindData<BeanMyConsult> {
                with(it.binding as ItemMyConsultBinding) {
                    order = it
                    root.goto(ConsultInfoActivity::class.java)
                    root.startConsult.click { _ ->
                        intoLiveRoom(it)
                    }
                }

            }

            refresh.loadData {
                activity?.let {
                    mViewModel.getMyConsultList(consultType, refresh.pageSize, refresh.pageIndex).obs(it) {
                        it.c { refresh.addCache(it.records) }
                        it.y { refresh.addCache(it.records) }

                    }
                }
            }

        }

    }

    /**
     * 进入直播室
     *
     */
    private fun intoLiveRoom(bean: BeanMyConsult) {
    }

    /**
     * 进入IM语音
     */
    private fun imAudio(bean: BeanMyConsult) {
    }


}