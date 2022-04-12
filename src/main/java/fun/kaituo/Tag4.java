package fun.kaituo;


import fun.kaituo.event.PlayerChangeGameEvent;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static fun.kaituo.GameUtils.unregisterGame;
import static fun.kaituo.GameUtils.world;

public class Tag4 extends JavaPlugin implements Listener {
    static List<Player> players;
    static long gameTime;
    Scoreboard scoreboard;
    Team tag4norden;
    Team tag4chechirecat;
    Team tag4redhat;
    Team tag4alice;
    Team tag4mabel;
    Team tag4kelti;
    Team tag4bill;
    Team tag4eunice;
    Team tag4dodo;
    Team tag4faketurtle;
    Team tag4victoria;
    Team tag4leaf;

    Team tag4miranda;
    Team tag4hein;
    Team tag4lindamayer;
    Team tag4baphomet;
    List<Team> teams;
    BoundingBox box = new BoundingBox(-1200, 0, 1800, -800, 200, 2200);

    public static Tag4Game getGameInstance() {
        return Tag4Game.getInstance();
    }

    @EventHandler
    public void preventClickingTrapDoor(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!(pie.getClickedBlock().getBlockData() instanceof TrapDoor)) {
            return;
        }
        if (!pie.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (box.contains(pie.getClickedBlock().getLocation().toVector())) {
                pie.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onButtonClicked(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!pie.getClickedBlock().getType().equals(Material.OAK_BUTTON)) {
            return;
        }
        if (pie.getClickedBlock().getLocation().equals(new Location(world,-1003,82,2027))) {
            Tag4Game.getInstance().startGame();
        }
    }
    @EventHandler
    public void setGameTime(PlayerInteractEvent pie) {
        Player player = pie.getPlayer();
        if (pie.getClickedBlock() == null) {
            return;
        }
        Location location = pie.getClickedBlock().getLocation();
        long x = location.getBlockX(); long y = location.getBlockY(); long z = location.getBlockZ();
        if (x == -1003 && y == 83 && z == 2027) {
            switch ((int)gameTime) {
                case 8400:
                case 10800:
                case 13200:
                case 15600:
                    gameTime += 2400;
                    break;
                case 18000:
                    gameTime = 8400;
                    break;
                default:
                    break;
            }
            Sign sign = (Sign) pie.getClickedBlock().getState();
            sign.setLine(2,"当前时间为 " + gameTime/1200 + " 分钟");
            sign.update();
        }
        if (Tag4Game.getInstance().running) {
            return;
        }
        if (x == -1005 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4norden)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "诺登", "§f");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4norden " + player.getName());
            player.sendMessage("§f诺登§f： 欢迎回来，" + player.getName() + "大人");
        } else if (x == -1006 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4chechirecat)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "柴郡猫", "§d");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4chechirecat " + player.getName());
            player.sendMessage("§d柴郡猫§f： 能和你再说上话真是太好喵");
        } else if (x == -1007 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4redhat)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "小红帽", "§c");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4redhat " + player.getName());
            player.sendMessage("§c小红帽§f： .......好了,我们出发吧");
        } else if (x == -1008 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4alice)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "爱丽丝", "§b");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4alice " + player.getName());
            player.sendMessage("§b爱丽丝§f： 去寻觅爱的浪漫吧~☆");
        } else if (x == -1009 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4mabel)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "梅贝尔", "§7");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4mabel " + player.getName());
            player.sendMessage("§7梅贝尔§f： 真的可以么？不要后悔哟~");
        } else if (x == -1010 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4kelti)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "克缇", "§9");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4kelti " + player.getName());
            player.sendMessage("§9克缇§f： 嗯，嗯，克缇，记住了哦。请多指教，"+ player.getName() + "酱");
        } else if (x == -1000 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4hein)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "海因", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4hein " + player.getName());
            player.sendMessage("§8海因§f： 待补充");
        } else if (x == -999 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4baphomet)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "巴风特", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4baphomet " + player.getName());
            player.sendMessage("§8巴风特§f： 我也没办法啦，希望你们能理解一下~");
        } else if (x == -1002 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4lindamayer)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "琳达梅尔", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4lindamayer " + player.getName());
            player.sendMessage("§8琳达梅尔§f： 我要重建新的黒之裁判，将盘踞于大地之上的罪人处刑！");
        } else if (x == -1001 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4miranda)) {
                return;
            }
            broadcastDevilChoiceMessage(player, "米兰达", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4miranda " + player.getName());
            player.sendMessage("§8米兰达§f： 总有一天，这个虚假的世界会迎来崩坏的时刻......");
        }else if (x == -1011 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4bill)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "比尔", "§2");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4bill " + player.getName());
            player.sendMessage("§2比尔§f： 那个...我为"+ player.getName()+"做了便当...不介意的话请您尝尝看吧~");
        }else if (x == -1012 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4eunice)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "尤妮丝", "§f");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4eunice " + player.getName());
            player.sendMessage("§f尤妮丝§f： 很好，让我们一起守护平等而纯洁的世界吧");
        }else if (x == -1013 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4dodo)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "渡渡", "§7");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4dodo " + player.getName());
            player.sendMessage("§7渡渡§f： 哼哼——！看来你的心已被吾辈俘获，是这么回事吧？");
        }else if (x == -1014 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4faketurtle)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "假海龟", "§3");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4faketurtle " + player.getName());
            player.sendMessage("§3假海龟§f： 是呢...只要有你在就没什么可怕的了......");
        }else if (x == -1015 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4victoria)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "维多利雅", "§d");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4victoria " + player.getName());
            player.sendMessage("§d维多利雅§f： 欢迎回来,主人大人");
        }else if (x == -1016 && y == 81 && z == 2027) {
            if (scoreboard.getPlayerTeam(player).equals(tag4leaf)) {
                return;
            }
            broadcastHumanChoiceMessage(player, "莉耶芙", "§a");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4leaf " + player.getName());
            player.sendMessage("§a莉耶芙§f： 对吧,这果然就是所谓的命运啊!");
        }
    }
    public void onEnable() {
        saveDefaultConfig();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        players = new ArrayList<>();
        tag4norden = scoreboard.getTeam("tag4norden");
        tag4baphomet = scoreboard.getTeam("tag4baphomet");
        tag4chechirecat = scoreboard.getTeam("tag4chechirecat");
        tag4redhat = scoreboard.getTeam("tag4redhat");
        tag4alice = scoreboard.getTeam("tag4alice");
        tag4mabel = scoreboard.getTeam("tag4mabel");
        tag4kelti = scoreboard.getTeam("tag4kelti");
        tag4bill = scoreboard.getTeam("tag4bill");
        tag4eunice = scoreboard.getTeam("tag4eunice");
        tag4dodo = scoreboard.getTeam("tag4dodo");
        tag4faketurtle = scoreboard.getTeam("tag4faketurtle");
        tag4victoria = scoreboard.getTeam("tag4victoria");
        tag4leaf = scoreboard.getTeam("tag4leaf");

        tag4miranda = scoreboard.getTeam("tag4miranda");
        tag4hein = scoreboard.getTeam("tag4hein");
        tag4lindamayer = scoreboard.getTeam("tag4lindamayer");
        tag4baphomet = scoreboard.getTeam("tag4baphomet");
        teams = List.of(tag4norden,tag4chechirecat, tag4redhat,tag4alice,tag4mabel,tag4kelti,tag4bill,tag4eunice,tag4dodo,tag4faketurtle,tag4victoria,tag4leaf,tag4miranda,tag4hein,tag4lindamayer,tag4baphomet);
        Bukkit.getPluginManager().registerEvents(this, this);
        gameTime = 8400;
        Sign sign = (Sign) world.getBlockAt(-1003, 83, 2027).getState();
        sign.setLine(2,"当前时间为 " + gameTime/1200 + " 分钟");
        sign.update();
        GameUtils.registerGame(getGameInstance());
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll((Plugin)this);
        if (players.size() > 0) {
            for (Player p : players) {
                p.teleport(new Location(world, 0.5,89.0,0.5));
                Bukkit.getPluginManager().callEvent(new PlayerChangeGameEvent(p, getGameInstance(), null));
            }
        }
        unregisterGame(getGameInstance());
    }

    private void broadcastHumanChoiceMessage(Player player, String role, String color) {
        for (Team team: teams) {
            for (String entryName : team.getEntries()) {
                Player p = Bukkit.getPlayer(entryName);
                if (p != null) {
                    if (p.isOnline()) {
                        p.sendMessage(color + player.getName()+" §r誓约了 " + color + role);
                    }
                }
            }
        }
    }
    private void broadcastDevilChoiceMessage(Player player, String role, String color) {
        for (Team team: teams) {
            for (String entryName : team.getEntries()) {
                Player p = Bukkit.getPlayer(entryName);
                if (p != null) {
                    if (p.isOnline()) {
                        p.sendMessage(color + player.getName()+" §r选择成为 " + color + role);
                    }
                }
            }
        }
    }
}

