package io.github.thatsmusic99.headsplus.config.customheads;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import io.github.thatsmusic99.headsplus.config.customheads.icons.Challenge;
import io.github.thatsmusic99.headsplus.config.customheads.icons.Head;
import io.github.thatsmusic99.headsplus.config.customheads.icons.Nav;
import io.github.thatsmusic99.headsplus.config.customheads.icons.Stats;
import io.github.thatsmusic99.headsplus.config.customheads.inventories.*;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.nms.NewNMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class HeadInventory {

    private static HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    public abstract String getDefaultTitle();

    public abstract String getDefaultItems();

    public abstract String getDefaultId();

    public abstract String getName();

    public static HeadInventory getInventoryByName(String s) {
        for (HeadInventory h : getInventories()) {
            if (s.equalsIgnoreCase(h.getName())) {
                return h;
            }
        }
        return null;
    }

    public static List<HeadInventory> getInventories() {
        List<HeadInventory> inventories = new ArrayList<>();
        inventories.add(new ChallengesMenu());
        inventories.add(new ChallengeSection());
        inventories.add(new FavouritesMenu());
        inventories.add(new HeadMenu());
        inventories.add(new HeadSection());
        inventories.add(new SellheadMenu());
        return inventories;
    }

    public int getDefaultSize() {
        return 54;
    }

    public String getTitle() {
        return HeadsPlus.getInstance().getItems().getConfig().getString("inventories." + getName() + ".title");
    }

    public int getSize() {
        return HeadsPlus.getInstance().getItems().getConfig().getInt("inventories." + getName() + ".size");
    }
    
    public Inventory build(Player sender, List<ItemStack> list, String section, int page, int pages, boolean showTopMenu, boolean wideMenu) {
        Inventory inventory = Bukkit.createInventory(null, getSize(), getTitle()
                .replaceAll("\\{page}", String.valueOf(page + 1))
                .replaceAll("\\{pages}", list == null ? "" : String.valueOf(pages))
                .replace("{section}", section));
        HeadsPlus hp = HeadsPlus.getInstance();
        
        
        return inventory;
    }

    public Icon[] getIconArray(int page, boolean wide, boolean replaceTop) {
        FileConfiguration fc = HeadsPlus.getInstance().getItems().getConfig();
        Icon[] icons = new Icon[getSize()];
        String[] s = fc.getString("inventories." + getName() + ".icons").split(":");
        String l = page >= s.length ? s[0] : s[page];
        if (wide) {
            // edit the map
            // figure out what the main char should be
            char main = 0;
            for (char c : new char[]{'A', 'C', 'H', 'L'}) {
                if (l.indexOf(c) != -1) {
                    main = c;
                    break;
                }
            }
            if (main != 0) {
                StringBuilder l2 = new StringBuilder();
                int row = 1;
                if (!replaceTop) {
                    l2.append(l.substring(0, 9));
                    ++row;
                }
                for (; row < l.length() / 9; ++row) {
                    for (int i = 0; i < 9; ++i) {
                        l2.append(main);
                    }
                }
                l2.append(l.substring((row - 1) * 9));
                l = l2.toString();
            }
        }
        for (int i = 0; i < getSize(); ++i) {
            icons[i] = Icon.getIconFromSingleLetter(String.valueOf(l.charAt(i)), this instanceof ChallengesMenu);
        }
        return icons;
    }

    public Inventory build(Player sender, List<ItemStack> list, String section, int page, int pages, int items, boolean wideMenu) {
        Inventory inventory = Bukkit.createInventory(null, getSize(), getTitle()
                .replaceAll("\\{page}", String.valueOf(page + 1))
                .replaceAll("\\{pages}", list == null ? "" : String.valueOf(pages))
                .replace("{section}", section == null || section.isEmpty() ? "" : Character.toUpperCase(section.charAt(0)) + section.substring(1)));
        HeadsPlus hp = HeadsPlus.getInstance();
        NMSManager nms = hp.getNMS();
        NBTManager nbt = hp.getNBTManager();
        Icon[] icons = getIconArray(page, wideMenu, true);

        int itemIndex = 0;
        for (int o = 0; o < getSize(); ++o) {
            ItemStack is = null;
            if (icons[o] instanceof Head || icons[o] instanceof io.github.thatsmusic99.headsplus.config.customheads.icons.HeadSection) {
                is = getHeadItem(icons[o], list, itemIndex++, sender);
            } else if (icons[o] instanceof Challenge) {
                is = getChallengeItem(icons[o], sender, list, itemIndex++);
            } else if (icons[o] instanceof io.github.thatsmusic99.headsplus.config.customheads.icons.ChallengeSection) {
                is = getChallengeItem(icons[o], sender, list, itemIndex++);
            } else if (icons[o] instanceof Stats) {
                if (nms instanceof NewNMSManager) {
                    is = new ItemStack(icons[o].getMaterial(), 1);
                } else {
                    is = new ItemStack(icons[o].getMaterial(), 1,
                            (byte) hp.getItems().getConfig().getInt("icons.stats.data-value"));
                }
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(icons[o].getDisplayName(), sender)));
                List<String> ls = new ArrayList<>();
                for (String s : icons[o].getLore()) {
                    ls.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(s
                            .replaceAll("\\{heads}", String.valueOf(items))
                            .replaceAll("\\{pages}", String.valueOf(pages))
                            .replaceAll("\\{sections}", String.valueOf(hp.getHeadsXConfig().sections.size()))
                            .replaceAll("\\{balance}", (hp.econ() ? HeadsPlus.getInstance().getConfiguration().fixBalanceStr(hp.getEconomy().getBalance(sender)) : ""))
                            .replace("{section}", section), sender)));
                }
                im.setLore(ls);
                is.setItemMeta(im);
                is = nbt.setIcon(is, icons[o]);
            } else if (icons[o] instanceof Nav) {
                Icon icon;

                // is this navigation button applicable?
                Nav.Page dir = ((Nav) icons[o]).getNavigationPage();
                boolean ok = false;
                int targetPage = 0;
                switch (dir) {
                    case MENU:
                        targetPage = -1;
                        ok = !(this instanceof ChallengesMenu || this instanceof HeadMenu || this instanceof SellheadMenu);
                        break;
                    case START:
                        targetPage = 0;
                        ok = pages > 2 && page != 0;
                        break;
                    case LAST:
                        targetPage = pages - 1;
                        ok = pages > 2 && page != pages - 1;
                        break;
                    case NEXT:
                        targetPage = page + 1;
                        ok = page != pages - 1;
                        break;
                    case NEXT_2:
                        targetPage = page + 2;
                        ok = page < pages - 2;
                        break;
                    case NEXT_3:
                        targetPage = page + 3;
                        ok = page < pages - 3;
                        break;
                    case BACK:
                        targetPage = page - 1;
                        ok = page != 0;
                        break;
                    case BACK_2:
                        targetPage = page - 2;
                        ok = page > 1;
                        break;
                    case BACK_3:
                        targetPage = page - 3;
                        ok = page > 2;
                        break;
                }
                icon = ok ? icons[o] : icons[o].getReplacementIcon();

                if (HeadsPlus.getInstance().getNMS() instanceof NewNMSManager) {
                    is = new ItemStack(icon.getMaterial(), 1);
                } else {
                    is = new ItemStack(icon.getMaterial(), 1,
                            (byte) hp.getItems().getConfig().getInt("icons." + icon.getIconName() + ".data-value"));

                }
                ItemMeta im = is.getItemMeta();
                if (ok) {
                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', targetPage != -1 ? "Page " + (targetPage + 1) : hpc.formatMsg(icon.getDisplayName(), sender)));
                } else {
                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(hp.getItems().getConfig().getString("icons." + icon.getIconName() + ".display-name"), sender)));
                }
                List<String> ls = new ArrayList<>();
                for (String s : icon.getLore()) {
                    ls.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(s
                            .replaceAll("\\{heads}", String.valueOf(items))
                            .replaceAll("\\{pages}", String.valueOf(pages))
                            .replaceAll("\\{sections}", String.valueOf(hp.getHeadsXConfig().sections.size()))
                            .replaceAll("\\{balance}", String.valueOf(hp.getEconomy().getBalance(sender)))
                            .replace("{section}", section), sender)));
                }
                im.setLore(ls);
                is.setItemMeta(im);
                is = nbt.setIcon(is, icon);
            } else if(icons[o] != null) {
                is = new ItemStack(icons[o].getMaterial(), 1, (byte) hp.getItems().getConfig().getInt("icons." + icons[o].getIconName() + ".data-value"));
                ItemMeta im = is.getItemMeta();
                if (im != null) {
                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(icons[o].getDisplayName(), sender)));
                    List<String> ls = new ArrayList<>();
                    for (String s : icons[o].getLore()) {
                        ls.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(s, sender)));
                    }
                    im.setLore(ls);
                    is.setItemMeta(im);
                    is = nbt.setIcon(is, icons[o]);
                }
            }
            inventory.setItem(o, is);
        }

        return inventory;
    }

    ItemStack getHeadItem(Icon icon, List<ItemStack> list, int itemIndex, CommandSender sender) {
        final NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
        ItemStack is;
        if (itemIndex < list.size()) {
            is = list.get(itemIndex);
            ItemMeta im = is.getItemMeta();
            if(is.getItemMeta().hasDisplayName())
                im.setDisplayName(hpc.formatMsg(icon.getDisplayName().replace("{head-name}", is.getItemMeta().getDisplayName()), sender));
            if (this instanceof SellheadMenu) {
                im.setDisplayName(hpc.formatMsg(im.getDisplayName().replace("{default}",
                        HeadsPlus.getInstance().getHeadsConfig().getDisplayName(NBTManager.getType(is))), sender));
            }
            String s = "";
            if (this instanceof SellheadMenu) {
                s = nbt.getType(is);
            }
            is.setItemMeta(im);
            is = nbt.setIcon(is, icon);
            is = nbt.setType(is, s);
        } else {
            Icon ic = icon.getReplacementIcon();
            is = new ItemStack(ic.getMaterial(), 1, (byte) HeadsPlus.getInstance().getItems().getConfig().getInt("icons." + ic.getIconName() + ".data-value"));
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.setDisplayName(hpc.formatMsg(ic.getDisplayName(), sender));
                ic.getLore().forEach(s -> hpc.formatMsg(s, sender));
                im.setLore(ic.getLore());
                is.setItemMeta(im);
            }
            is = nbt.setIcon(is, ic);
        }
        return is;
    }

    ItemStack getChallengeItem(Icon icon, Player p, List<ItemStack> list, int itemIndex) {
        final NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
        ItemStack is;
        if (itemIndex < list.size()) {
            is = list.get(itemIndex);
            ItemMeta im = is.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (this instanceof ChallengesMenu) {
                io.github.thatsmusic99.headsplus.api.ChallengeSection section = HeadsPlus.getInstance().getSectionByName(nbt.getChallengeSection(is));
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(icon.getDisplayName(), p).replace("{section-name}", section.getDisplayName())));
                for (int z = 0; z < icon.getLore().size(); ++z) {
                    if (icon.getLore().get(z).contains("{section-lore}")) {
                        for (String s : section.getLore()) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(s, p)));
                        }
                    } else {
                        lore.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(icon.getLore().get(z), p)).replaceAll("\\{challenge-count}", String.valueOf(section.getChallenges().size())));
                    }
                }
            } else {
                io.github.thatsmusic99.headsplus.api.Challenge c = nbt.getChallenge(is);
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', icon.getDisplayName().replace("{challenge-name}", c.getChallengeHeader())));
                for (int z = 0; z < icon.getLore().size(); ++z) {
                    String loreStr = hpc.formatMsg(icon.getLore().get(z), p);
                    if (loreStr.contains("{challenge-lore}")) {
                        for (String s : c.getDescription()) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(s, p)));
                        }
                    }
                    if (loreStr.contains("{challenge-reward}") || loreStr.contains("{reward}")) {
                        StringBuilder sb = new StringBuilder();
                        HPChallengeRewardTypes re = c.getRewardType();
                        if (c.getReward().getRewardString() != null) {
                            sb.append(c.getReward().getRewardString());
                        } else if (re == HPChallengeRewardTypes.ECO) {
                            sb.append(hpc.getString("inventory.icon.reward.currency", p).replace("{amount}", c.getRewardValue().toString()));
                        } else if (re == HPChallengeRewardTypes.GIVE_ITEM) {
                            try {
                                Material.valueOf(c.getRewardValue().toString());
                                sb.append(hpc.getString("inventory.icon.reward.item-give", p)
                                        .replace("{amount}", String.valueOf(c.getRewardItemAmount()))
                                        .replace("{item}", WordUtils.capitalize(c.getRewardValue().toString().toLowerCase().replaceAll("_", " "))));
                            } catch (IllegalArgumentException ignored) {
                            }
                        } else if (re == HPChallengeRewardTypes.ADD_GROUP) {
                            sb.append(hpc.getString("inventory.icon.reward.group-add", p).replace("{group}", c.getRewardValue().toString()));
                        } else if (re == HPChallengeRewardTypes.REMOVE_GROUP) {
                            sb.append(hpc.getString("inventory.icon.reward.group-remove", p).replace("{group}", c.getRewardValue().toString()));
                        }
                        lore.add(ChatColor.translateAlternateColorCodes('&', loreStr.replace("{challenge-reward}", hpc.formatMsg(sb.toString(), p))
                                .replace("{reward}", hpc.formatMsg(sb.toString(), p))));
                    }
                    if (loreStr.contains("{completed}")) {
                        if (c.isComplete(p)) {
                            lore.add(HeadsPlus.getInstance().getMessagesConfig().getString("commands.challenges.challenge-completed", p));
                        }
                    }
                    if (loreStr.contains("{challenge-xp}") || loreStr.contains("{xp}")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(icon.getLore().get(z), p).replaceAll("\\{challenge-xp}", String.valueOf(c.getGainedXP()))
                                .replaceAll("\\{xp}", String.valueOf(c.getGainedXP()))));
                    }
                }
            }
            im.setLore(lore);

            is.setItemMeta(im);
            is = nbt.setIcon(is, icon);
        } else {
            Icon ic = icon.getReplacementIcon();
            is = new ItemStack(ic.getMaterial(), 1, (byte) HeadsPlus.getInstance().getItems().getConfig().getInt("icons." + ic.getIconName() + ".data-value"));
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(ic.getDisplayName(), p)));
                List<String> ls = new ArrayList<>();
                for (String s : ic.getLore()) {
                    ls.add(ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(s, p)));
                }
                im.setLore(ls);
                is.setItemMeta(im);
            }
            is = nbt.setIcon(is, ic);
        }
        return is;
    }

}
