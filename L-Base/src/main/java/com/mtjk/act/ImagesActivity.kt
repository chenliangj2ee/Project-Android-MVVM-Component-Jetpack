package com.mtjk.act

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.databinding.ActivityImagesBinding
import com.mtjk.fragment.ImageFragment
import com.mtjk.utils.goto

/**
 * author:chenliang
 * date:2021/12/22
 */
@MyClass(mFullScreen = true)
class ImagesActivity : MyBaseActivity<ActivityImagesBinding, DefaultViewModel>() {
    @MyField
    lateinit var images: ArrayList<String>

    @MyField
    var position: Int = 0

    override fun initCreate() {
        fullscreenTransparentBar(true)
        var fs = ArrayList<Fragment>()
        images.forEach { fs.add(ImageFragment(it)) }
        mBinding.viewpager.addFragments(fs)
        mBinding.viewpager.currentItem = position

    }
}

fun Context.gotoPhoto(images: List<String>, position: Int) {
    goto(ImagesActivity::class.java, "images", images, "position", position)
}