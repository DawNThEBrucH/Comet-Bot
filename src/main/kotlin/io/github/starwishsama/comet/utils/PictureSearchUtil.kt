package io.github.starwishsama.comet.utils

import cn.hutool.http.HttpRequest
import com.google.gson.JsonParser
import io.github.starwishsama.comet.BotVariables
import io.github.starwishsama.comet.objects.pojo.PicSearchResult

object PictureSearchUtil {
    private val apiUrl =
        "https://saucenao.com/search.php?db=999&output_type=2&api_key=${BotVariables.cfg.saucenaoApiKey}&numres=16&url="

    fun sauceNaoSearch(url: String): PicSearchResult {
        val result = HttpRequest.get(apiUrl + url).timeout(5000).executeAsync()
        if (result.isOk){
            val resultBody = JsonParser.parseString(result.body())
            if (resultBody.isJsonObject){
                val resultJson = resultBody.asJsonObject["results"].asJsonArray[0].asJsonObject
                val similarity = resultJson["header"].asJsonObject["similarity"].asDouble
                val pictureUrl = resultJson["header"].asJsonObject["thumbnail"].asString
                val originalUrl = resultJson["data"].asJsonObject["ext_urls"].asJsonArray[0].asString
                return PicSearchResult(pictureUrl, originalUrl, similarity)
            }
        }
        return PicSearchResult.emptyResult()
    }
}