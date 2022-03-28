package com.fcyd.expert.fragment

import com.fcyd.expert.activity.ReleaseConsultActivity
import com.fcyd.expert.bean.BeanConsult
import com.fcyd.expert.databinding.*
import com.fcyd.expert.vm.ConsultViewModel
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.fragment_consult_manager_list.*

/**
 * tag==咨询管理/列表
 * author:chenliang
 * date:2021/11/3
 */
class ConsultManagerFragment :
    MyBaseFragment<FragmentConsultManagerListBinding, ConsultViewModel>() {
    @MyField
    var status = 0
    override fun initOnCreateView() {

        with(mBinding) {

            refresh.bindData<BeanConsult> {
                initItem(it.binding as ItemConsultManagerBinding, it)
            }

            refresh.loadData { httpGetConsultList() }
        }

    }

    private fun initItem(binding: ItemConsultManagerBinding, data: BeanConsult) {
        binding.state = status
        binding.bean = data
        binding.up.click { up(data) }
        binding.down.click { down(data) }
        binding.update.click { update(data) }
        binding.delete.click { deleteDialog(data) }
        binding.reason.click {
            dialog(data.reason)?.n("关闭") {}.y("修改") { }.show(this)
        }
    }

    /**
     * 获取列表
     */
    private fun httpGetConsultList() {
        with(mBinding) {
            mViewModel.consultList(status, refresh.pageIndex, refresh.pageSize)
                .obs(this@ConsultManagerFragment) {
                    it.c { refresh.addCache(it.records) }
                    it.y { refresh.addDatas(it.records) }
                }
        }
    }

    /**
     * 上架
     */
    private fun up(bean: BeanConsult) {
        mViewModel.upConsult(bean.id).obs(this) { }
    }

    /**
     * 下架
     */
    private fun down(bean: BeanConsult) {
        mViewModel.downConsult(bean.id).obs(this) { }
    }

    /**
     * 删除
     */
    private fun delete(bean: BeanConsult) {
        mViewModel.deleteConsult(bean.id).obs(this) { }
    }


    /**
     * 上架，下架，删除-刷新列表
     */
    @Subscribe(code = BusCode.REFRESH_CONSULT_LIST)
    fun refreshList() {
        mBinding.refresh.refresh()
    }

    /**
     * 修改
     */
    private fun update(bean: BeanConsult) {
        goto(ReleaseConsultActivity::class.java, "id", bean.id)
    }

    /**
     * 删除确认
     */
    private fun deleteDialog(bean: BeanConsult) {
        dialog("确定删除?")
            .n("取消") {}
            .y("确定") { delete(bean) }
            .show(this)

    }


}