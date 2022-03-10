package com.mentuojiankang.user.fragment

import com.chenliang.processor.appuser.MySp
import com.mentuojiankang.user.activity.CourseInfoActivity
import com.mentuojiankang.user.bean.BeanCourse
import com.mentuojiankang.user.bean.BeanSection
import com.mentuojiankang.user.databinding.FragmentCourseInfoHeaderBinding
import com.mentuojiankang.user.databinding.ItemCourseCatalogueBinding
import com.mentuojiankang.user.dialog.CourseCatalogueDialog
import com.mentuojiankang.user.vm.CourseViewModel
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseFragment
import com.mtjk.base.obs
import com.mtjk.player.view.Player
import com.mtjk.utils.BusCode
import com.mtjk.utils.click
import com.mtjk.utils.toast
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_course_info.*
import kotlinx.android.synthetic.main.fragment_course_info_header.*
import kotlinx.android.synthetic.main.fragment_course_info_header.view.*


/**
 * tag==播放器
 * author:chenliang
 * date:2021/11/3
 */
class CourseInfoHeaderFragment :
    MyBaseFragment<FragmentCourseInfoHeaderBinding, CourseViewModel>() {
    var player: Player? = null
    var position = 0//记录上次看到的位置
    lateinit var bean: BeanCourse
    fun setData(course: BeanCourse) {
        this.bean = course
        this.player?.cover(bean.coverImage)
        if (bean.sectionList?.isNotEmpty() == true) {
            this.player?.videoUrl = bean.sectionList!![0].fileUrl
            this.player?.videoID = bean.sectionList!![0].id
        } else {
            this.player?.videoUrl = bean.coverVideo
        }

        this.mBinding.data = bean
        initSectionList()
    }

    /**
     * 课程选集
     */
    private fun initSectionList() {
        if (bean.sectionList.isNullOrEmpty())
            return
        this.position = MySp.getCourseSectionPosition(bean.id)
        with(mBinding) {
            catalogueRefresh.disable()
            catalogueRefresh.bindData<BeanSection> {
                with(it.binding as ItemCourseCatalogueBinding) {
                    course = bean
                    data = it
                    root.click { _ ->
                        if (it.itemPosition == 0 || bean.buy) {
                            catalogueRefresh.getData<BeanSection>().forEach { it.isPlay = false }
                            it.isPlay = true
                            player?.play(it.fileUrl)
                            playCount(it.id)
                            it.playCount++
                            MySp.setCourseSectionPosition(bean.id, it.itemPosition)//保存当前播放位置
                            catalogueRefresh.scrollToPosition(it.itemPosition)
                            catalogueRefresh.notifyDataSetChanged()
                        } else {
                            (activity as CourseInfoActivity).subscribe.performClick()
                        }

                    }
                }
            }

            //设置上次播放位置
            bean.sectionList?.get(position)?.isPlay = true
            catalogueRefresh.addDatas(bean.sectionList)
            catalogueRefresh.scrollToPosition(position)

        }
    }


    /**
     * 播放次数统计
     */

    @Subscribe(code=BusCode.COURSE_PLAY)
    public fun playCount(courseSectionId: String) {
        bean.playCount++
        mBinding.data = bean
        mViewModel.playCount(courseSectionId).obs(this) {}
    }


    override fun initClick() {
        super.initClick()
        mRootView.all.click { sectionDialog() }

    }


    /**
     * 课程选集Dialog
     */
    private fun sectionDialog() {
        if (bean.sectionList?.size ?: 0 > 0) {
            var dialog = CourseCatalogueDialog(bean)
            dialog.show(this)
            dialog.itemClick {

                if (it == 0 || bean.buy) {
                    bean.sectionList?.forEach { it.isPlay = false }
                    bean.sectionList?.get(it)?.isPlay = true
                    player?.play(bean.sectionList?.get(it)!!.fileUrl)
                    playCount(bean.sectionList?.get(it)!!.id)
                    bean.sectionList?.get(it)!!.playCount++
                    catalogueRefresh.notifyDataSetChanged()
                    catalogueRefresh.scrollToPosition(it)
                } else {
                    (activity as CourseInfoActivity).subscribe.performClick()
                }

                dialog.dismiss()

            }
        } else {
            toast("暂无课程")
        }
    }


    override fun initOnCreateView() {
        player = Player(context)
        player?.initPlayerLayout(mRootView.frame_layout)
    }


    override fun onPause() {
        super.onPause()
        player?.onPause()
    }

    override fun onResume() {
        super.onResume()
        player?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.onRelease()
    }
}