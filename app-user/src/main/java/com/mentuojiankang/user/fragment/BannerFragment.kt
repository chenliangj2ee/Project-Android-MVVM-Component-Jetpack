package com.mentuojiankang.user.fragment

import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.mentuojiankang.user.bean.BeanBanner
import com.mentuojiankang.user.databinding.FragmentBannerBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.WebViewActivity
import com.mtjk.base.databinding.ItemBannerPointBinding
import com.mtjk.bean.BeanPoint
import com.mtjk.utils.*
import kotlinx.android.synthetic.main.fragment_banner.*

/**
 * tag==轮播图/首页
 */
class BannerFragment : MyBaseFragment<FragmentBannerBinding, DefaultViewModel>() {

    var array = ArrayList<BeanBanner>()
    var points = ArrayList<BeanPoint>()

    override fun initOnCreateView() {
        var beanBanner = BeanBanner()
        beanBanner.cover =
            "https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/%E5%90%B1%E5%90%B1%E5%BF%83%E7%90%86%E7%94%A8%E6%88%B7%E7%AB%AF%E9%A6%96%E9%A1%B5banner/user_index_banner_01.png"
        beanBanner.url =
            "https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/%E5%90%B1%E5%90%B1%E5%BF%83%E7%90%86%E7%94%A8%E6%88%B7%E7%AB%AF%E9%A6%96%E9%A1%B5banner/user_index_banner_02_1.png"
        array.add(beanBanner)

        mBinding.points.disable()
        mBinding.points.bindData<BeanPoint> {
            with(it.binding as ItemBannerPointBinding){
                data=it
            }
        }.loadData {  }
    }


    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    fun addData(list: ArrayList<BeanBanner>) {
        if (list.isEmpty())
            return
        array.clear()
        array.addAll(list)
        points.clear()
        for(item in array){
            points.add(BeanPoint())
        }
        if(points.size>0)
            points[0].itemSelected=true

        mBinding.points.addDatas(points)
       var params= mBinding.points.layoutParams
        params.width=(20*points.size).dip2px()
        mBinding.points.layoutParams=params

        var m = GridLayoutManager(context, points.size)
        mBinding.points.recyclerView?.layoutManager = m
        refreshUI()
    }

    private fun refreshUI() {
        banner.setBannerData(array)
        banner.setOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                mBinding.points.selected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        banner.loadImage { _, _, view, position ->
            (view as ImageView).load(array[position].xBannerUrl as String, 15)
            view.goto(WebViewActivity::class.java, "imgurl", array[position].url, "title", "介绍")
        }
    }
}