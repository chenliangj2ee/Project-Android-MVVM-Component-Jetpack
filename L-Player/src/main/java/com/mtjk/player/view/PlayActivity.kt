package com.mtjk.player.view

import android.content.res.Configuration
import android.os.Bundle
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.DefaultViewModel
import com.mtjk.base.MyBaseActivity
import com.mtjk.player.databinding.ActivityPlayBinding
import com.mtjk.utils.log
import com.mtjk.utils.show
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.layout_player.view.*

/**
 * author:chenliang
 * date:2021/12/2
 */
@MyClass(mFullScreen = true)
class PlayActivity : MyBaseActivity<ActivityPlayBinding, DefaultViewModel>() {
    private lateinit var player: Player

    @MyField
    private var url: String = ""
    override fun initCreate() {
        log("播放地址：$url")
        player = Player(this)
        player.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        player?.initPlayerLayout(frame_layout)
        player?.initFullLayout(fullScreenLayout)
        player?.play(url)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullscreenTransparentBar(true)
            mToolBar.show(false)
        }
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