package com.fcyd.expert.fragment

import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.fcyd.expert.bean.BeanBanner
import com.fcyd.expert.databinding.FragmentBannerBinding
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.WebViewActivity
import com.mtjk.base.databinding.ItemBannerPointBinding
import com.mtjk.bean.BeanPoint
import com.mtjk.utils.dip2px
import com.mtjk.utils.goto
import com.mtjk.utils.load
import com.mtjk.utils.log
import kotlinx.android.synthetic.main.fragment_banner.*

/**
 * tag==轮播图/首页
 */
class BannerFragment : MyBaseFragment<FragmentBannerBinding, DefaultViewModel>() {
    var points = ArrayList<BeanPoint>()
    override fun initOnCreateView() {
        mBinding.points.disable()
        mBinding.points.bindData<BeanPoint> {
            with(it.binding as ItemBannerPointBinding){
                data=it
            }
        }.loadData {  }
    }


    override fun onResume() {
        super.onResume()
        with(mBinding) {
            var array = ArrayList<BeanBanner>()
            var beanBanner=BeanBanner()
            beanBanner.url="https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/%E5%90%B1%E5%90%B1%E5%BF%83%E7%90%86%E5%92%A8%E8%AF%A2%E5%B8%88%E7%AB%AF%E9%A6%96%E9%A1%B5banner/expert_index_banner_01.png"
            beanBanner.path= "https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/%E5%90%B1%E5%90%B1%E5%BF%83%E7%90%86%E5%92%A8%E8%AF%A2%E5%B8%88%E7%AB%AF%E9%A6%96%E9%A1%B5banner/expert_index_banner_01_1.png"
            array.add(beanBanner)

            beanBanner=BeanBanner()
            beanBanner.url="https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/%E5%90%B1%E5%90%B1%E5%BF%83%E7%90%86%E5%92%A8%E8%AF%A2%E5%B8%88%E7%AB%AF%E9%A6%96%E9%A1%B5banner/expert_index_banner_02.png"
            beanBanner.path= "https://fcyd-website.obs.cn-north-4.myhuaweicloud.com/%E5%90%B1%E5%90%B1%E5%BF%83%E7%90%86%E5%92%A8%E8%AF%A2%E5%B8%88%E7%AB%AF%E9%A6%96%E9%A1%B5banner/expert_index_banner_02_2.png"
            array.add(beanBanner)

            banner.setBannerData(array)
            banner.loadImage { _, _, view, position ->
                (view as ImageView).load(array[position].xBannerUrl as String, 15)
                view.goto(WebViewActivity::class.java,"imgurl",array[position].path,"title","介绍")
            }

            banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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

            initPoints(array)
        }

    }

    private fun initPoints(array:ArrayList<BeanBanner>){
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
    }
}