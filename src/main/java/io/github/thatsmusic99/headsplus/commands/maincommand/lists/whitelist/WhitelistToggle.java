package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListToggle;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        commandname = "whitelist",
        permission = "headsplus.maincommand.whitelist.toggle",
        subcommand = "Whitelist",
        maincommand = true,
        usage = "/hp whitelist [On|Off]"
)
public class WhitelistToggle extends AbstractListToggle {

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.whitelist", sender);
    }

    @Override
    public HeadsPlusMainConfig.SelectorList getSelList() {
        return config.getHeadsWhitelist();
    }

    @Override
    public List<String> getList() {
        return getSelList().list;
    }

    @Override
    public String getPath() {
        return "whitelist.default.enabled";
    }

    @Override
    public String getListType() {
        return "wl";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getFullName() {
        return "whitelist";
    }

}
