package io.github.starwishsama.namelessbot.commands;

import cc.moecraft.icq.command.CommandProperties;
import cc.moecraft.icq.command.interfaces.EverywhereCommand;
import cc.moecraft.icq.event.events.message.EventMessage;
import cc.moecraft.icq.user.User;
import io.github.starwishsama.namelessbot.BotConstants;
import io.github.starwishsama.namelessbot.BotMain;
import io.github.starwishsama.namelessbot.utils.BotUtils;
import io.github.starwishsama.namelessbot.utils.LiveUtils;

import java.util.ArrayList;

public class RSSCommand implements EverywhereCommand {
    @Override
    public String run(EventMessage event, User sender, String command, ArrayList<String> args) {
        if (BotUtils.isNoCoolDown(sender)){
            if (args.size() > 0 && BotUtils.isBotAdmin(sender) && BotUtils.isBotOwner(sender)){
                switch (args.get(0).toLowerCase()){
                    case "bilibili":
                    case "哔哩哔哩":
                        if (args.size() == 2){
                            try {
                                if (LiveUtils.getBiliLiver(args.get(1)) != null){
                                    BotConstants.livers.add(args.get(1));
                                    return BotUtils.getLocalMessage("msg.bot-prefix") + "订阅 " + args.get(1) + "成功!";
                                }
                            } catch (Exception e){
                                BotMain.getLogger().warning("在获取主播信息时发生了一个错误, 错误信息: " + e);
                            }
                        } else {
                            return BotUtils.getLocalMessage("msg.bot-prefix") + "/订阅 [哔哩哔哩/YouTube] [频道名]\n由于 API 的关系, 现在只能订阅B站的虚拟主播";
                        }
                    default:
                        return BotUtils.getLocalMessage("msg.bot-prefix") + "/订阅 [哔哩哔哩/YouTube] [频道名]\n由于 API 的关系, 现在只能订阅B站的虚拟主播";
                }
            } else
                return BotUtils.getLocalMessage("msg.bot-prefix") + BotUtils.getLocalMessage("msg.no-permission");
        }
        return null;
    }

    @Override
    public CommandProperties properties() {
        return new CommandProperties("rss", "订阅");
    }
}
