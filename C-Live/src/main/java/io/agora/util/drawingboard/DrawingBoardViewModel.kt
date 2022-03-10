package io.agora.util.drawingboard

import com.chenliang.processor.CLive.API
import com.mtjk.base.MyBaseViewModel
import com.mtjk.utils.body

class DrawingBoardViewModel : MyBaseViewModel() {

    fun getRoomToken(channelName: String) = go {
        API.getDrawingBoardRoomToken(body("channelName", channelName))
    }
}