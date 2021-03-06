package io.github.starwishsama.comet

/**
 * Comet 的 Console 插件部分
 *
 * 注意: 使用 Console 启动是实验性功能!
 * 由于 Comet 的设计缺陷, 在 Console 有多个机器人的情况下可能无法正常工作.
 */
/**@AutoService(JvmPlugin::class)
object CometConsoleLoader : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.starwishsama.comet",
        version = "0.6-M2",
    )
) {

    @ExperimentalTime
    override fun onEnable() {
        Bot.botInstances.forEach { bot ->
            /** 命令处理 */
            CommandExecutor.startHandler(bot)

            /** 监听器 */
            val listeners = arrayOf(ConvertLightAppListener, RepeatListener)

            listeners.forEach { it.register(bot) }
        }
    }

    override fun onDisable() {
        logger.info("[Bot] 正在关闭 Bot...")
        DataSetup.saveFiles()
        BotVariables.service.shutdown()
        BotVariables.rCon?.disconnect()
    }
}*/