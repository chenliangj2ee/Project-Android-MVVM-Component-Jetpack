package com.mtjk.base

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.chenliang.processor.LBase.MySp
import com.google.android.material.tabs.TabLayout
import com.mtjk.BaseInit
import com.mtjk.base.databinding.ActivityBaseTabBinding
import com.mtjk.base.databinding.TabItemBinding
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.vm.AccountViewModel
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_base_tab.*
import java.util.*

abstract class BaseTabActivity<BINDING : ViewDataBinding, VM : ViewModel> :
    MyBaseActivity<BINDING, VM>() {
    protected var tabFragments: Array<Fragment>? = null
    protected lateinit var tabIconSelect: IntArray
    protected lateinit var tabIconDefault: IntArray
    protected lateinit var tabText: IntArray
    private var selectedTextColor = R.color.tab_color
    private var defaultTextColor = R.color.tab_default_color
    private var tabBinding: ActivityBaseTabBinding? = null
    private var tabPagerAdapter: TabPagerAdapter? = null
    private var fragmentManager: FragmentManager? = null

    override fun initCreate() {
        tabBinding = this.mBinding as ActivityBaseTabBinding
        initData()
        initView()
        //调用IM的userSig

        getIMToken()
        send(BusCode.MAIN_IN)
        if (!isNotificationEnabled() && !MySp.isNotifyDialog()) {
            dialog("为了更好聊天的体验，请开启通知权限")
                .n("拒绝") { MySp.setNotifyDialog(false) }
                .y("同意") { gotoSetting() }
                .show(this)
        }
        checkUpgrade(false)

//        if (!Settings.canDrawOverlays(this) && BaseInit.isUserApp && !MySp.isFloatDialog()) {
//            dialog("为了更好聊天的体验，请开启悬浮权限")
//                .n("拒绝") { MySp.setFloatDialog(false) }
//                .y("同意") { applyOverlays(this) }
//                .show(this)
//        }
    }


    override fun onResume() {
        super.onResume()
        intent?.sendSelf(BusCode.MAIN_RESUME)
    }

    /**
     * 初始化数据
     */
    protected abstract fun initData()


    /**
     * 初始化传入参数
     */
    private fun initView() {
        if (tabFragments == null) {
            throw EmptyStackException()
        }
        fragmentManager = supportFragmentManager
        tabPagerAdapter =
            TabPagerAdapter(fragmentManager!!, tabFragments)
        tabBinding!!.viewPager.adapter = tabPagerAdapter
        tabBinding!!.viewPager.offscreenPageLimit = 3
        tabBinding!!.viewPager.setEnableScroll(false)
        tabBinding!!.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                setTabIconColor(position)
                if (tabSelectedFun != null) {
                    tabSelectedFun?.let { it(position) }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        tabBinding!!.viewPager.currentItem = 0
        initTabLayout()
        setTabIconColor(0)
    }

    var tabSelectedFun: ((Int) -> Unit?)? = null
    fun tabSelected(func: (index: Int) -> Unit) {
        this.tabSelectedFun = func
    }

    private fun initTabLayout() {
        tabBinding!!.tabLayout.tabMode = TabLayout.MODE_FIXED
        tabBinding!!.tabLayout.setupWithViewPager(tabBinding!!.viewPager)
        for (i in tabFragments!!.indices) {
            val tabView = getTabView(tabIconDefault[i], tabText[i])
            tabBinding!!.tabLayout.getTabAt(i)!!.customView = tabView
        }
    }

    open fun toTab(index: Int) {
        tabBinding!!.tabLayout.getTabAt(index)?.view?.performClick()
    }

    private fun getTabView(tabIcon: Int, tabName: Int): View {
        val itemBinding: TabItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.tab_item, tabBinding!!.tabLayout, false
        )
        itemBinding.imageView.setImageResource(tabIcon)
        itemBinding.textName.setText(tabName)
        itemBinding.textName.setTextColor(ColorUtil.getColor(this, defaultTextColor))
        return itemBinding.root
    }

    private fun setTabIconColor(position: Int) {
        for (i in tabFragments!!.indices) {
            val imageView = tabBinding!!.tabLayout.getTabAt(i)!!
                .customView!!.findViewById<ImageView>(R.id.image_view)
            val textView =
                tabBinding!!.tabLayout.getTabAt(i)!!.customView!!.findViewById<TextView>(R.id.text_name)
            if (i == position) {
                textView.setTextColor(ColorUtil.getColor(this, selectedTextColor))
                imageView.setImageResource(tabIconSelect[i])
            } else {
                textView.setTextColor(ColorUtil.getColor(this, defaultTextColor))
                imageView.setImageResource(tabIconDefault[i])
            }
        }
    }


//    //before 2.0
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true)
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    @Subscribe(code = BusCode.LOGIN_SUCCESS)
    fun getIMToken() {
        var user = getBeanUser()
        var vm = initVM(AccountViewModel::class.java)
        var userSig = user?.userSig

        if (userSig.isNullOrEmpty()) {
            if (user != null || user?.isLogin == true) {
                vm.getUserSig().obs(this) {
                    it.y {
                        user.userSig = it.toString()
                        user.save()
                        send(BusCode.GET_USERSIG_SUCCESS)
                    }
                }
            }
        } else {
            send(BusCode.GET_USERSIG_SUCCESS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unRegister(this)
    }


    fun setNumber(index: Int, num: Int) {
        var tab = tab_layout.getTabAt(index)
        var textView = tab?.customView?.findViewById<TextView>(R.id.dot)
        textView?.text = num.toString()
        textView?.show(num > 0)
    }
}