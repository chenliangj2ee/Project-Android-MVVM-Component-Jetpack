package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.R
import com.mentuojiankang.user.activity.ArticleListActivity
import com.mentuojiankang.user.activity.FragmentActivity
import com.mentuojiankang.user.activity.SettingActivity
import com.mentuojiankang.user.bean.BeanMenu
import com.mentuojiankang.user.databinding.FragmentIndexMenuBinding
import com.mentuojiankang.user.databinding.ItemMenuBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.utils.click
import com.mtjk.utils.goto
import com.mtjk.utils.toast


/**
 * tag==首页菜单
 * author:chenliang
 * date:2021/11/3
 */
class IndexMenuFragment : MyBaseFragment<FragmentIndexMenuBinding, DefaultViewModel>() {

    private var arrays = arrayListOf<BeanMenu>(
        BeanMenu(R.drawable.icon_menu_live, "活动室", FragmentActivity::class.java, "", "/user/live"),
        BeanMenu(R.drawable.icon_menu_consult, "咨询", FragmentActivity::class.java, "", "/user/consult"),
        BeanMenu(R.drawable.icon_menu_course, "课程", FragmentActivity::class.java, "", "/user/course"),
        BeanMenu(R.drawable.icon_menu_article, "测评", FragmentActivity::class.java, "", "/user/test"),
//        BeanMenu(R.drawable.icon_menu_article, "文章", ArticleListActivity::class.java, "", ""),
    )

    override fun initOnCreateView() {

        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanMenu> { bean ->
                with(bean.binding as ItemMenuBinding) {
                    data = bean

                    root.click {
                        if (bean.title == "文章") {
                            goto(bean.cls!!)
                        } else {
                            goto(bean.cls!!, "fragment", bean.fragmentRoute, "title", bean.title)
                        }

                    }
                }
            }
            refresh.addDatas(arrays)
        }

    }


}