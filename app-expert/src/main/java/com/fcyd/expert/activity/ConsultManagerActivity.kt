package com.fcyd.expert.activity

import android.graphics.Color
import android.widget.TextView
import com.fcyd.expert.R
import com.fcyd.expert.databinding.ActivityMyOrderBinding
import com.fcyd.expert.fragment.ConsultManagerFragment
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.goto
import com.mtjk.utils.selected
import com.mtjk.utils.setBold

/**
 * tag==咨询管理
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "咨询管理")
class ConsultManagerActivity : MyBaseActivity<ActivityMyOrderBinding, DefaultViewModel>() {
    override fun initCreate() {
        with(mBinding) {
            var datas = ArrayList<String>()
            datas.add("已上架")
//            datas.add("审核中")
            datas.add("已下架")
            tab.selected {
                with(it?.customView?.findViewById<TextView>(R.id.title)) {
                    this?.setTextColor(Color.parseColor(if (it?.isSelected == true) "#008599" else "#A9ADB2"))
                    this?.textSize = if (it?.isSelected == true) 17F else 14f
                    this?.setBold(it?.isSelected == true)
                }

            }
            viewpager.setTabLayout(tab, datas, R.layout.item_my_consult_tab) {
                with(it.customView?.findViewById<TextView>(R.id.title)) { this?.text = it.text }
            }
            viewpager.addFragments(goto(ConsultManagerFragment::class.java, "status", 1))
//            viewpager.addFragments(goto(ConsultManagerFragment::class.java, "status", 2))
            viewpager.addFragments(goto(ConsultManagerFragment::class.java, "status", 3))

        }
    }


}