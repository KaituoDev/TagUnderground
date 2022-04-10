package fun.kaituo;


import fun.kaituo.event.PlayerChangeGameEvent;
import org.bukkit.*;
import org.bukkit.block.Sign;
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

import java.util.ArrayList;
import java.util.List;

import static fun.kaituo.GameUtils.unregisterGame;
import static fun.kaituo.GameUtils.world;

public class Tag4 extends JavaPlugin implements Listener {
    static List<Player> players;
    static long gameTime;
    Scoreboard scoreboard;
    List<String> teamNames = new ArrayList<>(List.of(new String[]{"tag4Y", "tag4W","tag4X","tag4H",
            "tag4R","tag4G","tag4B","tag4E","tag4L","tag4C"}));
    List<Team> teams = new ArrayList<>();

    public static Tag4Game getGameInstance() {
        return Tag4Game.getInstance();
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
            if (scoreboard.getTeam("tag4Y").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "诺登", "§f");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4Y " + player.getName());
            player.sendMessage("§f诺登§f： 欢迎回来，" + player.getName() + "大人");
        } else if (x == -1006 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4W").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "柴郡猫", "§d");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4W " + player.getName());
            player.sendMessage("§d柴郡猫§f： 能和你再说上话真是太好喵");
        } else if (x == -1007 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4X").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "小红帽", "§c");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4X " + player.getName());
            player.sendMessage("§c小红帽§f： .......好了,我们出发吧");
        } else if (x == -1008 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4H").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "爱丽丝", "§b");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4H " + player.getName());
            player.sendMessage("§b爱丽丝§f： 去寻觅爱的浪漫吧~☆");
        } else if (x == -1009 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4G").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "梅贝尔", "§7");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4G " + player.getName());
            player.sendMessage("§7梅贝尔§f： 真的可以么？不要后悔哟~");
        } else if (x == -1010 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4B").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "克缇", "§9");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4B " + player.getName());
            player.sendMessage("§9克缇§f： 嗯，嗯，克缇，记住了哦。请多指教，"+ player.getName() + "酱");
        } else if (x == -1000 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4R").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "海因", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4R " + player.getName());
            player.sendMessage("§8海因§f： 待补充");
        } else if (x == -999 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4C").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "巴风特", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4C " + player.getName());
            player.sendMessage("§8巴风特§f： 我也没办法啦，希望你们能理解一下~");
        } else if (x == -1002 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4L").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "琳达梅尔", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4L " + player.getName());
            player.sendMessage("§8琳达梅尔§f： 我要重建新的黒之裁判，将盘踞于大地之上的罪人处刑！");
        } else if (x == -1001 && y == 81 && z == 2027) {
            if (scoreboard.getTeam("tag4E").hasPlayer(player)) {
                return;
            }
            sendMessageToTag4Players(player, "米兰达", "§8");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join tag4E " + player.getName());
            player.sendMessage("§8米兰达§f： 总有一天，这个虚假的世界会迎来崩坏的时刻......");
        }
    }
    public void onEnable() {
        players = new ArrayList<>();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (String name : teamNames) {
            teams.add(scoreboard.getTeam(name));
        }
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

    private void sendMessageToTag4Players(Player player, String role, String color) {
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
}

