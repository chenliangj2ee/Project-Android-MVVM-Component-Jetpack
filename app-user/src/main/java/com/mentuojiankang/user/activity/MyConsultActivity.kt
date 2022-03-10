package com.mentuojiankang.user.activity

import android.graphics.Color
import android.text.Html
import android.widget.TextView
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanTrouble
import com.mentuojiankang.user.databinding.ActivityMyCollectionBinding
import com.mentuojiankang.user.databinding.ActivityMyConsultBinding
import com.mentuojiankang.user.databinding.ActivityMyFollowBinding
import com.mentuojiankang.user.fragment.*
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.utils.goto
import com.mtjk.utils.selected
import kotlinx.android.synthetic.main.activity_my_collection.*
import kotlinx.android.synthetic.main.activity_my_follow.*

/**
 * tag==我的咨询
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "我的咨询")
class MyConsultActivity : MyBaseActivity<ActivityMyConsultBinding, DefaultViewModel>() {
    override fun initCreate() {
        with(mBinding) {
            var datas = ArrayList<String>()
            datas.add("一对多")
            datas.add("一对一")
            tab.selected {
                var textColor =
                    if (it?.isSelected == true) Color.parseColor("#008599") else Color.parseColor("#A9ADB2")
                it?.customView?.findViewById<TextView>(R.id.title)?.setTextColor(textColor)

                if (it?.isSelected == true) {
                    var str = "${it?.text}";
                    it?.customView?.findViewById<TextView>(R.id.title)?.text = Html.fromHtml(str)
                } else {
                    var str = "${it?.text}";
                    it?.customView?.findViewById<TextView>(R.id.title)?.text = Html.fromHtml(str)
                }


            }
            viewpager.setTabLayout(tab, datas, R.layout.item_my_consult_tab) {
                it.customView?.findViewById<TextView>(R.id.title)?.text = it.text
            }

//            viewpager.addFragments(goto(MyConsultListFragment::class.java, "consultType", 300))
            viewpager.addFragments(goto(MyConsultListFragment::class.java, "consultType", 200))

        }
    }


}