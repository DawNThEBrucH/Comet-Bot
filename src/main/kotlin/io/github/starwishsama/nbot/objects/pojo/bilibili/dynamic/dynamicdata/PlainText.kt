package io.github.starwishsama.nbot.objects.pojo.bilibili.dynamic.dynamicdata

import io.github.starwishsama.nbot.objects.pojo.bilibili.dynamic.DynamicData
import io.github.starwishsama.nbot.objects.pojo.bilibili.user.UserProfile

data class PlainText(var item: ItemBean,
                     var user: UserProfile.Info) : DynamicData {
    data class ItemBean (var context: String)

    override suspend fun getContact(): List<String> {
        return arrayListOf("发布了动态: \n${item.context}\n")
    }
}