package com.mentuojiankang.user.fragment

import com.mentuojiankang.user.activity.ArticleListActivity
import com.mentuojiankang.user.bean.BeanArticle
import com.mentuojiankang.user.databinding.FragmentRecommendArticleBinding
import com.mentuojiankang.user.databinding.ItemRecommendArticleBinding
import com.mentuojiankang.user.vm.ArticleViewModel
import com.mtjk.MyPath
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.utils.*
import kotlinx.android.synthetic.main.layout_recommend_module_title.view.*

/**
 * tag==首页推荐文章/Fragment
 * author:chenliang
 * date:2021/11/3
 */
class RecommendArticleFragment :
    MyBaseFragment<FragmentRecommendArticleBinding, ArticleViewModel>() {
    override fun initOnCreateView() {
        mRootView.show(false)
        with(mBinding) {
            root.more.click { goto(ArticleListActivity::class.java) }
            mBinding.root.title.text = "精选好文"
            articleRefresh.disable()
        }

    }

    fun addData(list: ArrayList<BeanArticle>) {

        if(list.isNullOrEmpty())
            return
        mRootView.show(true)
        with(mBinding) {
            articleRefresh.bindData<BeanArticle> { bean ->
                with(bean.binding as ItemRecommendArticleBinding) {
                    data = bean
                    root.click { getArticleInfo(bean) }
                }

            }
            articleRefresh.addDatas(list)
        }
    }


    private fun getArticleInfo(bean: BeanArticle) {
        goto(WebViewActivity::class.java, "url", MyPath.articlePath, "title", bean.title, "articleId", bean.id)
        addReadCount(bean)
    }

    private fun addReadCount(bean: BeanArticle) {
        mViewModel.addReadCount(bean.id).obs(this) {}
    }

}