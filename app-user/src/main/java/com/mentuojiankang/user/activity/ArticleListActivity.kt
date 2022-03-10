package com.mentuojiankang.user.activity

import com.mentuojiankang.user.bean.BeanArticle
import com.mentuojiankang.user.databinding.ActivityArticleListBinding
import com.mentuojiankang.user.databinding.ItemRecommendArticleBinding
import com.mentuojiankang.user.vm.ArticleViewModel
import com.mtjk.MyPath
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.utils.click
import com.mtjk.utils.goto
import kotlinx.android.synthetic.main.activity_article_list.*


/**
 * tag==文章
 */
@MyClass(mToolbarTitle = "文章")
class ArticleListActivity : MyBaseActivity<ActivityArticleListBinding, ArticleViewModel>() {
    override fun initCreate() {
        articleRefresh.bindData<BeanArticle> { bean ->
            with(bean.binding as ItemRecommendArticleBinding) {
                data = bean
                root.click {
                    getArticleInfo(bean)
                    addReadCount(bean)
                }

            }
        }

        articleRefresh.loadData {
            mViewModel.getArticleList(articleRefresh.pageIndex, articleRefresh.pageSize).obs(this) {
                it.c { articleRefresh.addCache(it.records) }
                it.y { articleRefresh.addDatas(it.records) }
            }

        }
    }


    private fun addReadCount(bean: BeanArticle) {
        mViewModel.addReadCount(bean.id).obs(this) {}
    }


    private fun getArticleInfo(bean: BeanArticle) {
        goto(WebViewActivity::class.java, "url", MyPath.articlePath, "title", bean.title, "articleId", bean.id)
//        mViewModel.getArticleInfo(bean.id).obs(this) {
//            it.y { goto(WebViewActivity::class.java, "content", bean.content, "title", bean.title) }
//        }
    }
}