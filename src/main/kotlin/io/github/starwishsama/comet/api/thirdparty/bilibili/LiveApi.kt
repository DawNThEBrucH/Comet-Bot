package io.github.starwishsama.comet.api.thirdparty.bilibili

import cn.hutool.http.HttpRequest
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.annotations.SerializedName
import io.github.starwishsama.comet.BotVariables.gson
import io.github.starwishsama.comet.api.thirdparty.ApiExecutor
import io.github.starwishsama.comet.api.thirdparty.bilibili.data.live.LiveRoomInfo
import io.github.starwishsama.comet.exceptions.RateLimitException
import io.github.starwishsama.comet.utils.FileUtil
import io.github.starwishsama.comet.utils.network.NetUtil
import retrofit2.Call
import retrofit2.http.GET

interface LiveApiTest {
    @GET("room/v1/Room/get_info")
    fun getLiveInfo(roomId: Long): Call<LiveRoomInfo>
}

object LiveApi : ApiExecutor {
    private const val liveUrl = "http://api.live.bilibili.com/room/v1/Room/get_info?id="
    private const val liveOldUrl = "https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid="
    private const val apiRateLimit = "BiliBili API调用已达上限"

    @Throws(RateLimitException::class)
    fun getLiveInfo(roomId: Long): LiveRoomInfo? {
        if (isReachLimit()) {
            throw RateLimitException(apiRateLimit)
        }

        val request = HttpRequest.get(liveUrl + roomId).timeout(500)
                .header("User-Agent", "Bili live status checker by StarWishsama")
        val response = request.executeAsync()

        return try {
            gson.fromJson(response.body())
        } catch (t: Throwable) {
            FileUtil.createErrorReportFile("在获取B站直播间信息时出现了意外", "bilibili", t, response.body(), request.url)
            null
        }
    }

    @Throws(RateLimitException::class)
    fun getLiveStatus(mid: Long): Boolean {
        val info = getLiveInfo(mid)
        return info?.data?.liveStatus == 1
    }

    fun getRoomIDByUID(uid: Long): Long {
        val result = NetUtil.executeHttpRequest(
                url = (liveOldUrl + uid),
                call = {
                    header("user-agent", "Bili live status checker by StarWishsama")
                }
        )

        result.use {
            if (result.isSuccessful) {
                val info = result.body()?.string()?.let { gson.fromJson<OldLiveInfo>(it) }
                if (info?.code != 0) {
                    return info?.data?.roomId ?: -1
                }
            }
        }

        return -1
    }

    override fun isReachLimit(): Boolean {
        val result = MainApi.usedTime > MainApi.getLimitTime()
        if (!result) MainApi.usedTime++
        return result
    }

    override var usedTime: Int = 0

    override fun getLimitTime(): Int = 1500

    private data class OldLiveInfo(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: LiveInfoData
    ) {
        data class LiveInfoData(
            val roomStatus: Int,
            val roundStatus: Int,
            val liveStatus: Int,
            val url: String,
            val title: String,
            val cover: String,
            val online: Int,
            @SerializedName("roomid")
            val roomId: Long,
            @SerializedName("broadcast_type")
            val broadcastType: Int,
            @SerializedName("online_hidden")
            val onlineHidden: Int
        )
    }
}