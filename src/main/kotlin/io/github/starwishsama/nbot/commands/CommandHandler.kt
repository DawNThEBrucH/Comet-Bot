package io.github.starwishsama.nbot.commands

import io.github.starwishsama.nbot.BotConstants
import io.github.starwishsama.nbot.BotInstance
import io.github.starwishsama.nbot.commands.interfaces.UniversalCommand
import io.github.starwishsama.nbot.objects.BotUser
import io.github.starwishsama.nbot.util.BotUtil
import io.github.starwishsama.nbot.util.BotUtil.toMirai
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain

/**
 * Mirai 命令处理器
 * 处理群聊/私聊聊天信息中存在的命令
 * @author Nameless
 */
object CommandHandler {
    var commands: List<UniversalCommand> = mutableListOf()

    /**
     * 注册命令
     *
     * @param command 要注册的命令
     */
    fun setupCommand(command: UniversalCommand) {
        if (!commands.contains(command)) {
            commands = commands + command
        }
    }

    /**
     * 注册命令
     *
     * @param commands 要注册的命令集合
     */
    fun setupCommand(commands: Array<UniversalCommand>) {
        commands.forEach {
            if (!this.commands.contains(it)) {
                this.commands = this.commands.plus(it)
            }
        }
    }

    /**
     * 执行消息中的命令
     *
     * @param message 消息
     */
    suspend fun execute(message: MessageEvent): MessageChain {
        if (message.message.contentToString().isNotEmpty() &&
            BotConstants.cfg.commandPrefix.contains(
                message.message.contentToString().substring(0, 1)
            )
        ) {
            val cmdPrefix = getCmdPrefix(message.message.contentToString())
            for (cmd in commands) {
                if (isPrefix(cmd, cmdPrefix)) {
                    BotInstance.logger.debug("[命令] " + message.sender.id + " 执行了命令: " + cmd.getProps().name)
                    var user = BotUser.getUser(message.sender.id)
                    if (user == null) {
                        user = BotUser.quickRegister(message.sender.id)
                    }

                    return if (user.compareLevel(cmd.getProps().level) || user.hasPermission(cmd.getProps().permission)) {
                        val splitMessage = message.message.contentToString().split(" ")
                        cmd.execute(message, splitMessage.subList(1, splitMessage.size), user)
                    } else {
                        BotUtil.sendMsgPrefix("你没有权限!").toMirai()
                    }
                }
            }
        }
        return EmptyMessageChain
    }

    private fun getCmdPrefix(command: String): String {
        var cmdPrefix = command
        for (string : String in BotConstants.cfg.commandPrefix){
            cmdPrefix = cmdPrefix.replace(string, "")
        }

        return cmdPrefix.split(" ")[0]
    }

    private fun isPrefix(cmd: UniversalCommand, prefix: String): Boolean {
        val props = cmd.getProps()
        when {
            props.name.contentEquals(prefix) -> {
                return true
            }
            props.aliases != null -> {
                props.aliases?.forEach {
                    if (it.contentEquals(prefix)) {
                        return true
                    }
                }
            }
            else -> {
                return false
            }
        }
        return false
    }
}