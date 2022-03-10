package com.mtjk.player.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoSize
import com.mtjk.base.MyBaseActivity
import com.mtjk.player.R
import com.mtjk.utils.*
import com.mtjk.view.MyFrameLayout
import gorden.rxbus2.RxBus
import gorden.rxbus2.Subscribe
import kotlinx.android.synthetic.main.layout_controller.view.*
import kotlinx.android.synthetic.main.layout_player.view.*

/**
 * author:chenliang
 * date:2021/11/17
 */
class Player : RelativeLayout {
    private var exoPlayer: ExoPlayer = context?.let { ExoPlayer.Builder(it).build() }!!
    private lateinit var act: Activity
    lateinit var view: View
    lateinit var playerLayout: MyFrameLayout
    lateinit var fullScreenLayout: FrameLayout
    var verticalScreen = false
    var videoUrl = ""
    var videoID = ""
    var finish = false

    constructor(context: Context?) : super(context) {
        initPlayer(context)
        RxBus.get().register(this)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initPlayer(context)
    }

    fun initPlayerLayout(playerLayout: MyFrameLayout) {
        this.playerLayout = playerLayout
        this.playerLayout.addView(this)
    }

    fun initFullLayout(fullLayout: FrameLayout) {
        this.fullScreenLayout = fullLayout
    }

    private fun initPlayer(context: Context?) {
        act = context as Activity
        view = View.inflate(context, R.layout.layout_player, null)
        addView(view, ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        view.fullScreen.click {
            act?.screenLandscape()
            fullScreenPlay(true)
        }
        view.playerBack.click {
            act?.screenPortrait()
            fullScreenPlay(false)
        }
        view.playerBack.show(false)
        start.click {
            play(videoUrl)
            startBG.show(false)
            cover.show(false)
            start.show(false)
            progressBar.show(false)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig == null)
            return
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL;
            fullScreenPlay(true)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL;
            fullScreenPlay(false)
        }
    }

    @Subscribe(code = BusCode.FULL_SCREEN)
    fun fullScreenPlay(full: Boolean) {
        if (full) {
            toast("全屏")
            MyScreen.setFullscreen(
                context as Activity,
                isShowStatusBar = false,
                isShowNavigationBar = false
            )
            (context as MyBaseActivity<*, *>).mToolBar.show(false)
            playerLayout.removeAllViews()
            fullScreenLayout.removeAllViews()
            fullScreenLayout.addView(this)
            fullScreenLayout.show(true)
            view.fullScreen.show(false)
            view.playerBack.show(true)
        } else {
            toast("退出全屏")

            MyScreen.setFullscreen(
                context as Activity,
                isShowStatusBar = true,
                isShowNavigationBar = true
            )
            MyScreen.setStatusBarColor(context as Activity, Color.TRANSPARENT)
            (context as MyBaseActivity<*, *>).initStatusBar()
            (context as MyBaseActivity<*, *>).mToolBar.show(true)
            fullScreenLayout.removeAllViews()
            playerLayout.removeAllViews()
            playerLayout.addView(this)
            fullScreenLayout.show(false)
            view.fullScreen.show(true)
            view.playerBack.show(false)
        }
    }

    fun play(url: String?) {
        if (url == null)
            return
        playerView.player = exoPlayer;
        var dataSourceFactory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "myExoPlayer"))
        var mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
        exoPlayer.prepare(mediaSource);
        exoPlayer.addListener(object : Player.Listener {

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                toast("播放错误")
                log("${error.message}")
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                progressBar.show(isLoading)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
//                log("onPlaybackStateChanged")
                when (playbackState) {
                    Player.STATE_ENDED -> finish = true
                }
            }

            /**
             * 视频资源的宽高
             */
            override fun onVideoSizeChanged(videoSize: VideoSize) {
//                playerLayout.layoutParams.width=videoSize.width
                log("onVideoSizeChanged  ${playerLayout.width}")
                log("onVideoSizeChanged  ${videoSize.width}  ${videoSize.height}")
                if(videoSize.height>videoSize.width){
                    verticalScreen=true
                    fullScreen.show(false)
                    playerLayout.layoutParams.height = playerLayout.width * videoSize.height / videoSize.width
                }


            }


        })

        exoPlayer.play()
        if(videoID.isNotEmpty()){
            videoID.sendSelf(BusCode.COURSE_PLAY)
        }
        finish = false
        startBG.show(false)
        cover.show(false)
        start.show(false)
    }

    fun cover(url: String) {
        cover.load(url)
    }

    /**
     * 暂停播放
     */
    fun onPause() {
        exoPlayer.pause()
    }

    /**
     * 继续播放
     */
    fun onResume() {
        exoPlayer.play()
    }

    /**
     * 释放
     */
    fun onRelease() {
        exoPlayer.release()
        RxBus.get().unRegister(this)
    }

}