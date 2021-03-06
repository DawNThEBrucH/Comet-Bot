package io.github.starwishsama.comet.api.thirdparty.bilibili.data.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import io.github.starwishsama.comet.BotVariables
import io.github.starwishsama.comet.api.thirdparty.bilibili.data.dynamic.dynamicdata.*
import io.github.starwishsama.comet.api.thirdparty.bilibili.data.user.UserProfile
import io.github.starwishsama.comet.objects.wrapper.MessageWrapper
import java.time.LocalDateTime

interface DynamicData {
    suspend fun getContact(): MessageWrapper

    suspend fun compare(other: Any?): Boolean {
        if (other == null) return false
        if (other !is DynamicData) return false

        return getContact().text == other.getContact().text
    }

    fun getSentTime(): LocalDateTime
}

object DynamicTypeSelector {
    fun getType(type: Int): Class<out DynamicData> {
        return when (type) {
            1 -> Repost::class.java
            2 -> TextWithPicture::class.java
            4 -> PlainText::class.java
            8 -> Video::class.java
            16 -> MiniVideo::class.java
            64 -> Article::class.java
            256 -> Music::class.java
            2048 -> ShareContext::class.java
            4200 -> LiveRoom::class.java
            4308 -> LiveBroadcast::class.java
            else -> UnknownType::class.java
        }
    }
}

data class Dynamic(
        val code: Int,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("data")
        val data: Data
) {
    data class Data(
            /** /space_history Only */
            @SerializedName("has_more")
            val hasMoreInfo: Int?,
            @SerializedName("card")
            val card: Card?,
            /** /space_history Only */
            @SerializedName("cards")
            val cards: List<Card>?,
            @SerializedName("result")
            val result: Int,
            @SerializedName("attentions")
            val attentions: Attentions,
            /** /space_history Only */
            @SerializedName("next_offset")
            val nextOffset: Long?,
            @SerializedName("_gt_")
            val gtNumber: Int,
            @SerializedName("extension")
            val extension: JsonObject,
            @SerializedName("extend_json")
            val extendJson: String
    ) {
        data class Attentions(
                @SerializedName("uids")
                val uidList: List<Long>
        )
    }
}

data class Card(
        @SerializedName("desc")
        val description: DynamicDesc,
        @SerializedName("card")
        val card: String,
        @SerializedName("extend_json")
        val extendJson: String,
        @SerializedName("extra")
        val extraJson: JsonObject?,
        @SerializedName("display")
        val displayJson: DynamicDisplay
) {
    data class DynamicDesc(
            val uid: Int,
            val type: Int,
            val rid: Long,
            val acl: Int,
            @SerializedName("view")
            val viewCount: Int,
            @SerializedName("repost")
            val repostCount: Int,
            @SerializedName("like")
            val likeCount: Int,
            @SerializedName("is_liked")
            val liveStatus: Int,
            @SerializedName("dynamic_id")
            val dynamicId: Long,
            @SerializedName("timestamp")
            val timeStamp: Long,

            /** 转发的动态ID, 无转发为0 */
            @SerializedName("pre_dy_id")
            val repostDynamicId: Long,

            /** 转发的起始动态ID, 无转发为0 */
            @SerializedName("orig_dy_id")
            val originalDynamicId: Long,

            @SerializedName("orig_type")
            val originalType: Int,

            @SerializedName("user_profile")
            val userProfile: UserProfile,

            @SerializedName("uid_type")
            val uidType: Int,

            @SerializedName("status")
            val dynamicStatus: Int,

            @SerializedName("dynamic_id_str")
            val dynamicIdAsString: String,

            @SerializedName("pre_dy_id_str")
            val previousDynamicIdAsString: String,

            @SerializedName("orig_dy_id_str")
            val originalDynamicIdAsString: String,

            @SerializedName("rid_str")
            val ridAsString: String,

            val origin: JsonObject
    )

    data class DynamicDisplay(
            val origin: JsonObject?,
            val relation: JsonObject?,
            @SerializedName("comment_info")
            val hotComment: HotComments?
    ) {
        data class HotComments(
                val comments: HotComment
        ) {
            data class HotComment(
                    val uid: Int,
                    @SerializedName("name")
                    val name: String,
                    val content: String
            )
        }
    }
}

fun Dynamic.convertToDynamicData(): DynamicData? {
    if (data.cards != null) {
        val card = data.cards[0]
        val singleDynamicObject = JsonParser.parseString(card.card)
        if (singleDynamicObject.isJsonObject) {
            val dynamicType = DynamicTypeSelector.getType(card.description.type)
            if (dynamicType != UnknownType::class) {
                return BotVariables.gson.fromJson(card.card, dynamicType)
            }
        }
    }
    return null
}

suspend fun Dynamic.convertDynamic(): MessageWrapper {
    return try {
        val data = convertToDynamicData()
        data?.getContact() ?: MessageWrapper("错误: 不支持的动态类型", false)
    } catch (e: Exception) {
        if (e is ArrayIndexOutOfBoundsException) {
            MessageWrapper("没有发过动态", false)
        } else {
            MessageWrapper("解析动态失败", false)
        }
    }
}