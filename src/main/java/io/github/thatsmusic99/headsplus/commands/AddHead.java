package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;

import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.util.prompts.DataListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "addhead",
        permission = "headsplus.addhead",
        subcommand = "addhead",
        maincommand = false,
        usage = "/addhead [player]"
)
public class AddHead implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length > 0) {
            if (args[0].matches("^[A-Za-z0-9_]+$")) {
                if (args[0].length() > 2) {
                    if (args[0].length() < 17) {
                        HeadsPlus hp = HeadsPlus.getInstance();
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                        String uuid = p.getUniqueId().toString();
                        if (!hp.getServer().getOnlineMode()) {
                            hp.getLogger().warning("Server is in offline mode, player may have an invalid account! Attempting to grab UUID...");
                            uuid = hp.getHeadsXConfig().grabUUID(p.getName(), 3, null);
                        }
                        if(hp.getHeadsXConfig().grabProfile(uuid, sender, true)) {
                            sender.sendMessage(hp.getMessagesConfig().getString("commands.addhead.head-adding", sender)
                                    .replace("{player}", p.getName())
                                    .replace("{header}", hp.getMenus().getConfig().getString("profile.header")));
                        }
                        return true;
                    } else {
                        sender.sendMessage(hpc.getString("commands.head.head-too-long", sender));
                    }
                } else {
                    sender.sendMessage(hpc.getString("commands.head.head-too-short", sender));
                }
            } else {
                sender.sendMessage(hpc.getString("commands.head.alpha-names", sender));
            }
        } else {
            if (sender.hasPermission("headsplus.addhead.texture")) {
                if (sender instanceof Conversable) {
                    ConversationFactory factory = new ConversationFactory(HeadsPlus.getInstance());
                    Conversation conversation = factory.withLocalEcho(false)
                            .withFirstPrompt(new DataListener(0, hpc.getString("commands.addhead.id", sender)))
                            .buildConversation((Conversable) sender);
                    conversation.addConversationAbandonedListener(event -> {
                        if (event.gracefulExit()) {
                            ConversationContext context = event.getContext();
                            if (context.getSessionData("cancel") != null) {
                                return;
                            }
                            String id = String.valueOf(context.getSessionData("id"));
                            HeadsPlusConfigCustomHeads customHeads = HeadsPlus.getInstance().getHeadsXConfig();
                            for (Object key : context.getAllSessionData().keySet()) {
                                if (key.equals("id")) continue;
                                customHeads.getConfig().addDefault("heads." + id + "." + key, context.getSessionData(key));
                            }
                            customHeads.getConfig().options().copyDefaults(true);
                            customHeads.save();
                            customHeads.addHeadToCache(id, String.valueOf(context.getSessionData("section")));
                            sender.sendMessage(hpc.getString("commands.addhead.custom-head-added").replaceAll("\\{id}", id));
                        }
                    });
                    conversation.begin();
                }
            } else {
                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
            }
        }
        return true;
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.addhead", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], IHeadsPlusCommand.getPlayers(), results);
        }
        return results;
    }

}
