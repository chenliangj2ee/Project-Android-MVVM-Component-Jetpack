package com.mentuojiankang.user.fragment

import android.widget.TextView
import com.chenliang.annotation.MyRoute
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanType
import com.mentuojiankang.user.databinding.FragmentConsultBinding
import com.mentuojiankang.user.vm.AppViewModel
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.goto
import com.mtjk.utils.selected
import com.mtjk.utils.setBold
import com.mtjk.utils.setTextSizeDip

/**
 * tag==咨询tab/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyRoute(mPath = "/user/consult")
class ConsultFragment : MyBaseFragment<FragmentConsultBinding, AppViewModel>() {
    override fun initOnCreateView() {

        mViewModel.getType2().obs(this) {
            it.c { initViewPager(it) }
            it.y { initViewPager(it) }
        }


    }

    private fun initViewPager(arraylist: List<BeanType>) {
        with(mBinding) {
            consultTab.selected {
                with(it?.customView?.findViewById<TextView>(R.id.title)) {
                    this?.isEnabled = it?.isSelected == false
                    this?.setTextSizeDip(if (it?.isSelected == true) 15 else 15)
                    this?.setBold(it?.isSelected == true)
                }
            }
            consultViewpager.clearFragments()
            var tabTitles = ArrayList<String>()
            arraylist.forEach {
                tabTitles.add(it.name)
                consultViewpager.addFragments(goto(ConsultListFragment()::class.java, "type", it.id))
            }

            consultViewpager.setTabLayout(consultTab, tabTitles, R.layout.item_home_tab) {
                it.customView?.findViewById<TextView>(R.id.title)?.text = it.text
            }


        }
    }
}