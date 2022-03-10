package com.mentuojiankang.user.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mentuojiankang.user.R
import com.mentuojiankang.user.databinding.FragmentHomeBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.utils.selected
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * tag==首页/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class HomeFragment : MyBaseFragment<FragmentHomeBinding, DefaultViewModel>() {

    var datas = arrayListOf<String>(
        "推荐",
        "直播",
//        "测评",
        "课程",
        "咨询"
    )
    var fs = arrayListOf<Fragment>(
        RecommendFragment(),
        LiveListFragment(),
//        TestTabFragment(),
        CourseTabFragment(),
        ConsultFragment()
    )

    override fun initOnCreateView() {
        with(mBinding) {
            homeTab.selected {
                with(it?.customView?.findViewById<TextView>(R.id.title)) {
                    var textColor =
                        Color.parseColor(if (it?.isSelected == true) "#133C53" else "#A9ADB2")
                    var textSize = if (it?.isSelected == true) 20 else 14
                    this?.setTextColor(textColor)
                    this?.textSize = textSize.toFloat()
                }
            }
            homeViewPager.setTabLayout(homeTab, datas, R.layout.item_my_consult_tab) {
                with(it.customView?.findViewById<TextView>(R.id.title)) {
                    this?.text = it.text
                    this?.setTextColor(Color.parseColor(if (it.position == 0) "#133C53" else "#A9ADB2"))
                    this?.typeface = Typeface.defaultFromStyle(Typeface.BOLD);
                }
            }
            homeViewPager.addFragments(fs)
        }


    }
}