package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanIndexRecommend
import com.mentuojiankang.user.databinding.FragmentRecommendBinding
import com.mentuojiankang.user.vm.IndexViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.utils.log
import com.mtjk.utils.toJson
import kotlinx.android.synthetic.main.fragment_recommend.*

/**
 * tag==首页推荐/Fragment
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mRefresh = true)
class RecommendFragment : MyBaseFragment<FragmentRecommendBinding, IndexViewModel>() {
    private lateinit var bannerFragment: BannerFragment
    private lateinit var consultantFragment: RecommendConsultantFragment
    private lateinit var videoFragment: RecommendVideoFragment
    private lateinit var articleFragment: RecommendArticleFragment

    override fun initOnCreateView() {
        bannerFragment= childFragmentManager.findFragmentById(R.id.bannerFragment) as BannerFragment
        consultantFragment = childFragmentManager.findFragmentById(R.id.consultantFragment) as RecommendConsultantFragment
        videoFragment = childFragmentManager?.findFragmentById(R.id.videoFragment) as RecommendVideoFragment
        articleFragment = childFragmentManager?.findFragmentById(R.id.articleFragment) as RecommendArticleFragment
        refresh()
    }

    /**
     * 加载网络数据
     */
    override fun refresh() {
        super.refresh()
        mViewModel.getIndexRecommend().obs(this) {
            it.c { initData(it) }
            it.y { initData(it) }
            stopRefresh()
        }

    }

    /**
     * 数据同步到fragment
     */
    private fun initData(res: BeanIndexRecommend) {
        res.consultShopList?.let { consultantFragment.addData(it) }
        res.courseList?.let { videoFragment.addData(it) }
        res.articleList?.let { articleFragment.addData(it) }
        res.bannerList?.let { bannerFragment.addData(it) }
    }

}