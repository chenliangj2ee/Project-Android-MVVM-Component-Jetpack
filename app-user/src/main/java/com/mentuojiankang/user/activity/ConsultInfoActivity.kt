package com.mentuojiankang.user.activity

import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.mentuojiankang.user.R
import com.mentuojiankang.user.bean.BeanBanner
import com.mentuojiankang.user.bean.BeanConsult
import com.mentuojiankang.user.bean.BeanConsultRes
import com.mentuojiankang.user.bean.BeanConsultService
import com.mentuojiankang.user.databinding.ActivityConsultInfoBinding
import com.mentuojiankang.user.databinding.ItemConsultBinding
import com.mentuojiankang.user.dialog.ConsultSubDialog
import com.mentuojiankang.user.vm.ConsultViewModel
import com.mentuojiankang.user.vm.FavoriteViewModel
import com.mentuojiankang.user.vm.FollowViewModel
import com.mtjk.MyPath
import com.mtjk.act.gotoPhoto
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.WebViewActivity
import com.mtjk.base.obs
import com.mtjk.fragment.EvaluateFragment
import com.mtjk.player.view.PlayActivity
import com.mtjk.utils.*
import com.tencent.qcloud.tuikit.tuiconversation.util.IM
import kotlinx.android.synthetic.main.activity_consult_info.*

/**
 * \\tag==咨询详情
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "咨询详情", mRefresh = true)
class ConsultInfoActivity : MyBaseActivity<ActivityConsultInfoBinding, ConsultViewModel>() {
    @MyField
    lateinit var bean: BeanConsult
    override fun initCreate() {

        /**
         * 咨询服务列表
         */
        consults.disable()
        consults.bindData<BeanConsultService> { service ->
            with(service.binding as ItemConsultBinding) {
                this.data = service
                this.book.click { showSubDialog(service) }
            }
        }


        httpGetConsultInfo()

    }

    private fun initBanner(array: ArrayList<BeanBanner>) {
        with(mBinding) {
            banner.setBannerData(array)
            banner.loadImage { _, _, view, position ->
                (view as ImageView).load(array[position].xBannerUrl as String, 15)
                view.click {
                    var url = array[position].xBannerUrl as String
                    if (url.endsWith(".mp4")) {
                        goto(PlayActivity::class.java, "url", url)
                    } else {
                        var images = mBinding.bean?.introVideo?.filter { !it.endsWith(".mp4") }
                        var position =
                            if (images == mBinding.bean?.introVideo) position else position - 1
                        gotoPhoto(images!!, position)
                    }
                }
            }
            banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {


                    if (array[position].cover.endsWith(".mp4")) {
                        play.click {
                            goto(PlayActivity::class.java, "url", array[position].cover)
                        }
                        videobg.visibility = android.view.View.VISIBLE
                        play.visibility = android.view.View.VISIBLE
                    } else {
                        videobg.visibility = android.view.View.GONE
                        play.visibility = android.view.View.GONE
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })

        }
    }

    override fun refresh() {
        httpGetConsultInfo()
        (evaluate as EvaluateFragment).initShopId(bean.accountId)
    }

    private fun httpGetConsultInfo() {
        mViewModel.getConsultInfo(bean.id).obs(this) {
            it.c { getConsultInfoSuccess(it) }
            it.y { getConsultInfoSuccess(it) }
            stopRefresh()
        }
    }

    private fun getConsultInfoSuccess(bean: BeanConsultRes) {
        if (bean.expertDetailEntity == null) {
            toast("数据为空")
            finish()
            return
        }
        mToolBar.setTitle("${bean.expertDetailEntity!!.realName}的工作室")
        bean.expertDetailEntity!!.resetDefault()
        mBinding.bean = bean.expertDetailEntity
        mBinding.introduction.setHtmlText("<b><font color='#008599'>专业擅长:</font></b>${bean.expertDetailEntity?.introduction}")
        this.bean = mBinding.bean!!
//        initFavorite(bean.expertDetailEntity)
        var arrays = arrayListOf<BeanBanner>()
        mBinding.bean?.introVideo?.forEach {
            var bean = BeanBanner()
            bean.cover = it
            arrays.add(bean)
        }
        initBanner(arrays)
        (evaluate as EvaluateFragment).setUserName(mBinding.bean!!.realName)
        if (bean.expertServerBaseModelList.isNullOrEmpty())
            return
        consults.clearData()
        consults.addDatas(bean.expertServerBaseModelList)


        /**
         * 初始化评论
         */
        (evaluate as EvaluateFragment).initShopId(bean.expertDetailEntity!!.id)

    }

    override fun initClick() {
        super.initClick()
//        startSub.click { showSubDialog() }
        chat.click {
            log("聊天。。。。。" + bean.accountId)
            IM.gotoChat(bean.accountId, bean.realName)
        }
        header.click {
            goto(
                WebViewActivity::class.java,
                "url",
                MyPath.expertPath,
                "userId",
                bean.id
            )
        }
        lookInfo.click {
            goto(
                WebViewActivity::class.java,
                "url",
                MyPath.expertPath,
                "userId",
                bean.id
            )
        }

        follow.click {
            if (mBinding.bean == null)
                return@click
            var followViewModel = initVM(FollowViewModel::class.java)
            if (bean.follow) {
                followViewModel.cancelFollow(bean.expertId).obs(this) {
                    it.y {
                        bean.follow = false
                        mBinding.bean = bean
                    }
                }
            } else {
                followViewModel.follow(bean.expertId).obs(this) {
                    it.y {
                        bean.follow = true
                        mBinding.bean = bean
                    }
                }
            }

        }
    }

    private fun showSubDialog(service: BeanConsultService) {
        var dialog = ConsultSubDialog(service, bean)
        dialog.show(this)
    }


    /**
     * 初始化收藏按钮样式以及事件
     */
    private fun initFavorite(consult: BeanConsult?) {
        if (consult == null)
            return
        mToolBar.showRightIcon(R.drawable.icon_favorite_default) {
            if (consult.favorite) {
                initVM(FavoriteViewModel::class.java).cancelFavorite(consult.expertId).obs(this) {
                    it.y {
                        mToolBar.showRightIcon(R.drawable.icon_favorite_default)
                        consult.favorite = false
                    }
                }
            } else {
                initVM(FavoriteViewModel::class.java).favorite(0, consult.expertId).obs(this) {
                    it.y {
                        mToolBar.showRightIcon(R.drawable.icon_favorite_checked)
                        consult.favorite = true
                    }
                }
            }

        }

        if (consult.favorite) {
            mToolBar.showRightIcon(R.drawable.icon_favorite_checked)
        } else {
            mToolBar.showRightIcon(R.drawable.icon_favorite_default)
        }
    }

}