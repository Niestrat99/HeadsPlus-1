package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@CommandInfo(
        commandname = "hplb",
        permission = "headsplus.leaderboards",
        subcommand = "Hplb",
        maincommand = false,
        usage = "/hplb [Total|Entity|Page No.] [Page No.] [Hunting|Selling|Crafting]"
)
public class LeaderboardsCommand implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final HashMap<String, Boolean> tests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        try {
            tests.put("No permission", !cs.hasPermission("headsplus.leaderboards"));
            tests.put("Arguments", args.length > 0);
            if (cs.hasPermission("headsplus.leaderboards")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            if (args.length > 0) {
                                try {
                                    boolean b = HeadsPlus.getInstance().getDeathEvents().ableEntities.contains(EntityType.valueOf(args[0].toUpperCase()));
                                    tests.put("Valid Entity", b);
                                    String sec = b ? args[0].toUpperCase() : args[0];
                                    if (b || sec.equalsIgnoreCase("player")) {
                                        if (args.length > 1) {
                                            if (CachedValues.MATCH_PAGE.matcher(args[1]).matches()) {
                                                if (args.length > 2) {
                                                    if (args[2].equalsIgnoreCase("crafting")
                                                            || args[2].equalsIgnoreCase("selling")
                                                            || args[2].equalsIgnoreCase("hunting")) {
                                                        cs.sendMessage(getLeaderboard(cs, sec, Integer.parseInt(args[1]), args[2]));
                                                    } else {
                                                        cs.sendMessage(getLeaderboard(cs, sec, Integer.parseInt(args[1]), "hunting"));
                                                    }
                                                } else {
                                                    cs.sendMessage(getLeaderboard(cs, sec, Integer.parseInt(args[1]), "hunting"));
                                                }
                                            } else if (args[1].equalsIgnoreCase("crafting")
                                                    || args[1].equalsIgnoreCase("selling")
                                                    || args[1].equalsIgnoreCase("hunting")) {
                                                if (args.length > 2) {
                                                    if (CachedValues.MATCH_PAGE.matcher(args[2]).matches()) {
                                                        cs.sendMessage(getLeaderboard(cs, sec, Integer.parseInt(args[2]), args[1]));
                                                    } else {
                                                        cs.sendMessage(getLeaderboard(cs, sec, 1, args[1]));
                                                    }
                                                } else {
                                                    cs.sendMessage(getLeaderboard(cs, sec, 1, args[1]));
                                                }
                                            } else {
                                                cs.sendMessage(getLeaderboard(cs, sec, 1, "hunting"));
                                            }
                                        } else {
                                            cs.sendMessage(getLeaderboard(cs, sec, 1, "hunting"));
                                        }
                                    }
                                } catch (IllegalArgumentException ex) {
                                    tests.put("Valid Entity", false);
                                    if (args[0].equalsIgnoreCase("total")) {
                                        if (args.length > 1) {
                                            if (CachedValues.MATCH_PAGE.matcher(args[1]).matches()) {
                                                if (args.length > 2) {
                                                    if (args[2].equalsIgnoreCase("crafting")
                                                            || args[2].equalsIgnoreCase("selling")
                                                            || args[2].equalsIgnoreCase("hunting")) {
                                                        cs.sendMessage(getLeaderboard(cs, args[0], Integer.parseInt(args[1]), args[2]));
                                                    } else {
                                                        cs.sendMessage(getLeaderboard(cs, args[0], Integer.parseInt(args[1]), "hunting"));
                                                    }
                                                } else {
                                                    cs.sendMessage(getLeaderboard(cs, args[0], Integer.parseInt(args[1]), "hunting"));
                                                }
                                            } else if (args[1].equalsIgnoreCase("crafting")
                                                    || args[1].equalsIgnoreCase("selling")
                                                    || args[1].equalsIgnoreCase("hunting")) {
                                                cs.sendMessage(getLeaderboard(cs, args[0], 1, args[1]));
                                            } else {
                                                cs.sendMessage(getLeaderboard(cs, args[0], 1, "hunting"));
                                            }
                                        } else {
                                            cs.sendMessage(getLeaderboard(cs, args[0], 1, "hunting"));
                                        }
                                    } else if (CachedValues.MATCH_PAGE.matcher(args[0]).matches()) {
                                        cs.sendMessage(getLeaderboard(cs, "total", Integer.parseInt(args[0]), "hunting"));
                                    } else if (args[0].equalsIgnoreCase("player")) {
                                        if (args.length > 1) {
                                            if (CachedValues.MATCH_PAGE.matcher(args[1]).matches()) {
                                                if (args.length > 2) {
                                                    if (args[2].equalsIgnoreCase("crafting")
                                                            || args[2].equalsIgnoreCase("selling")
                                                            || args[2].equalsIgnoreCase("hunting")) {
                                                        cs.sendMessage(getLeaderboard(cs, args[0], Integer.parseInt(args[1]), args[2]));
                                                    } else {
                                                        cs.sendMessage(getLeaderboard(cs, args[0], Integer.parseInt(args[1]), "hunting"));
                                                    }
                                                } else {
                                                    cs.sendMessage(getLeaderboard(cs, args[0], Integer.parseInt(args[1]), "hunting"));
                                                }
                                            } else {
                                                if (args.length > 2) {
                                                    if (args[2].equalsIgnoreCase("crafting")
                                                            || args[2].equalsIgnoreCase("selling")
                                                            || args[2].equalsIgnoreCase("hunting")) {
                                                        cs.sendMessage(getLeaderboard(cs, args[0], 1, args[2]));
                                                    } else {
                                                        cs.sendMessage(getLeaderboard(cs, args[0], 1, "hunting"));
                                                    }
                                                } else {
                                                    cs.sendMessage(getLeaderboard(cs, args[0], 1, "hunting"));
                                                }
                                            }

                                        } else {
                                            cs.sendMessage(getLeaderboard(cs, args[0], 1, "hunting"));
                                        }
                                    }  else if (args[0].equalsIgnoreCase("crafting")
                                            || args[0].equalsIgnoreCase("selling")
                                            || args[0].equalsIgnoreCase("hunting")) {
                                        if (args.length > 1) {
                                            if (CachedValues.MATCH_PAGE.matcher(args[1]).matches()) {
                                                cs.sendMessage(getLeaderboard(cs, "total", Integer.parseInt(args[1]), args[0]));
                                            } else {
                                                cs.sendMessage(getLeaderboard(cs, "total", 1, args[0]));
                                            }
                                        }


                                    } else {
                                        cs.sendMessage(getLeaderboard(cs, "total", 1, "hunting"));
                                    }
                                }

                            } else {
                                cs.sendMessage(getLeaderboard(cs, "total", 1, "hunting"));
                            }
                        } catch (Exception e) {
                            DebugPrint.createReport(e, "Command (leaderboard)", true, cs);
                        }

                    }
                }.runTaskAsynchronously(HeadsPlus.getInstance());

            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (leaderboard)", true, cs);
        }
        return false;
    }

    private String getLeaderboard(CommandSender sender, String sec, int page, String part) {
        return HeadsPlusConfigTextMenu.LeaderBoardTranslator.translate(sender, sec, part, page);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hplb", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getEntities(), results);
        } else if (args.length == 4) {
            StringUtil.copyPartialMatches(args[3], Arrays.asList("hunting", "selling", "crafting"), results);
        }
        return results;
    }
}
