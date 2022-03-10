package com.fcyd.expert.activity

import com.fcyd.expert.bean.BeanImageSelected
import com.fcyd.expert.bean.BeanStudio
import com.fcyd.expert.bean.BeanTags
import com.fcyd.expert.databinding.ActivityStudioMediaEditBinding
import com.fcyd.expert.databinding.ItemStudioMediaBinding
import com.fcyd.expert.databinding.ItemTagsBinding
import com.fcyd.expert.vm.StudioViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.player.view.PlayActivity
import com.mtjk.utils.*
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.activity_studio_media_edit.*

/**
 * tag==工作室装修/上传视频和图片
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "工作室装修")
class StudioMediaEditActivity : MyBaseActivity<ActivityStudioMediaEditBinding, StudioViewModel>() {

    //保存图库资源
    var arrays = ArrayList<BeanImageSelected>()

    //保存网络资源
    var networkArrays = ArrayList<BeanImageSelected>()
    var networkCategoryList = arrayListOf<String>()

    //+图片/视频
    var addBean = BeanImageSelected("", true)

    override fun initCreate() {
//        mToolBar.showRight("帮助") { }

        with(mBinding) {
            refresh.disable()
            refresh.bindData<BeanImageSelected>(::bindItem)
            arrays.add(addBean)//默认展示+图片/视频
            refresh.addDatas(arrays)

            tags.disable()
            tags.bindData<BeanTags> { bean ->
                with(bean.binding as ItemTagsBinding) {
                    data = bean
                    root.click { _ ->
                        bean.itemSelected = !bean.itemSelected
                        tags.notifyDataSetChanged()
                        enableSubmit()
                    }
                }
            }

            userInfo.goto(PersonalInfoEditActivity::class.java)
        }
        initStudio()
    }


    private fun enableSubmit() {
        submit.isEnabled = tags.getData<BeanTags>()
            .any { it.itemSelected } && (networkArrays.filter { !it.add }.size + selectImages.size >= 1)
    }

    private fun bindItem(image: BeanImageSelected) {
        with(image.binding as ItemStudioMediaBinding) {
            this.image = image
            //添加
            root.click {
                if (image.add) {
                    if (networkArrays.none { it.url.endsWith(".mp4") }) {
                        selectImages(true, 10 - networkArrays.size )
                    } else {
                        selectImages(false,10 - networkArrays.size )
                    }
                }
            }

            //删除
            delete.click {
                log("delete:" + image.itemPosition)
                for (item in networkArrays) {
                    if (image.url == item.url) {
                        networkArrays.remove(item)
                        break
                    }
                }
                for (item in arrays) {
                    if (image.url == item.url) {
                        arrays.remove(item)
                        break
                    }
                }
                refresh.removeData(image)
                selectImages.removeIf { image.url == it }
                enableSubmit()
            }
            //查看
            play.goto(PlayActivity::class.java, "url", image.url)
        }
    }


    @Subscribe(code = BusCode.RESULT_EXPERT_STUDIO_USER_EDIT)
    fun resultUserInfo(user: BeanUser) {
        userName.text = user.nickName
    }


    var selectedTags = ArrayList<String>()

    override fun initClick() {
        submit.click {
            tags.getData<BeanTags>().filter { it.itemSelected }.forEach { selectedTags.add(it.id) }
//            if (selectedTags.isEmpty()) {
//                toast("请选择咨询分类")
//                return@click
//            }
            loadImage()
        }

    }


    /**
     * 提交上传
     */
    private fun loadImage() {
        if (selectImages.isNullOrEmpty()) {
            var images = arrayListOf<String>()
            networkArrays.filter { !it.add }.forEach {
                images.add(it.url)
            }
            submit(images)
        } else {
            var loading = loading("上传中")
            loading.setCancelable(false)
            var count = 0

            var loadImages = arrayListOf<String>()
            arrays.forEach {
                if (it.url.startsWith("http")) {
                    loadImages.add(it.url)
                } else if (it.url.isNotEmpty() && !it.url.startsWith("http")) {
                    log("上传:${it.url}")
                    MyImage.uploadBusiness(it.url) {
                        if (it.finish) {
                            count++
                            loadImages.add(it.path)
                        }
                        if (count == selectImages.size) {
                            loading.dismiss()
                            submit(loadImages)
                        }
                    }
                }


            }
        }

    }

    /**
     * 提交信息
     */
    private fun submit(images: ArrayList<String>) {
        mViewModel.updateStudioInfo(images, selectedTags).obs(this) {
            it.y { gotoFinish(ReleaseConsultRemindActivity::class.java) }
        }
    }

    /**
     * 获取信息
     */
    private fun initStudio() {
        mViewModel.getStudioInfo().obs(this) {
            it.c { initStudioInfo(it) }
            it.y { initStudioInfo(it) }
        }
        mViewModel.getStudioTags().obs(this) {
            it.c { tags.addCache(it.filter { it.id != "0" }) }
            it.y { tags.addDatas(it.filter { it.id != "0" }) }
            resetTags()
        }
    }

    private fun resetTags() {
        if (networkCategoryList.isNullOrEmpty() || tags.getData<BeanTags>().isNullOrEmpty())
            return
        tags.getData<BeanTags>().forEach { bean ->
            bean.itemSelected = networkCategoryList.any { it == bean.id }
        }
        tags.notifyDataSetChanged()
    }

    private fun initStudioInfo(it: BeanStudio) {
        networkArrays.clear()
        it.images.forEach {
            networkArrays.add(BeanImageSelected(it, false))
        }
        networkArrays.add(addBean)
        refresh.addDatas(networkArrays)
        mBinding.refresh.recyclerView?.scrollBy(100000, 0)
        mBinding.studio = it
        networkCategoryList = it.categoryList
        resetTags()
        tags.notifyDataSetChanged()
    }

    /**
     * 图片选择回调
     */
    override fun resultSelectImage(array: ArrayList<String>) {
        super.resultSelectImage(array)

        if (array.filter { it.endsWith(".mp4") }.size > 1) {
            toast("只能选择一个视频")
            return
        }


        arrays.clear()
        arrays.addAll(networkArrays)
        arrays.remove(addBean)
        array.forEach {
            arrays.add(BeanImageSelected(it, false))
        }
        arrays.sortBy { !it.url.endsWith("mp4") }

        arrays.add(addBean)
        mBinding.refresh.clearData()
        mBinding.refresh.addDatas(arrays)
        mBinding.refresh.recyclerView?.scrollBy(100000, 0)
        enableSubmit()
    }

    @Subscribe(code = BusCode.UPDATE_EXPERT_INFO)
    fun updateUserInfo() {
        finish()
    }
}