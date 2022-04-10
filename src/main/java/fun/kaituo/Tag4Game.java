package fun.kaituo;


import fun.kaituo.event.PlayerChangeGameEvent;
import fun.kaituo.event.PlayerEndGameEvent;
import fun.kaituo.utils.ItemStackBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

import static fun.kaituo.GameUtils.*;

public class Tag4Game extends Game implements Listener {
    private static final Tag4Game instance = new Tag4Game((Tag4) Bukkit.getPluginManager().getPlugin("Tag4"));
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Scoreboard tag4 = Bukkit.getScoreboardManager().getNewScoreboard();
    Tag4 plugin;
    List<Player> humans = new ArrayList<>();
    List<Player> devils = new ArrayList<>();
    long startTime;
    long gameTime;
    Team team;
    Location[] locations = new Location[]{
            new Location(world, -1005, 69, 1000),
            new Location(world, -991, 65, 1005),
            new Location(world, -1019, 66, 1014),
            new Location(world, -1010, 67, 1005),
            new Location(world, -1018, 66, 992),
            new Location(world, -983, 64, 997),
            new Location(world, -998, 54, 1009),
            new Location(world, -986, 56, 1012),
            new Location(world, -1015, 54, 1015),
            new Location(world, -1017, 51, 996),
            new Location(world, -981, 57, 986),
            new Location(world, -986, 48, 981),
            new Location(world, -1000, 68, 1013),
            new Location(world, -1001, 53, 998),
            new Location(world, -991, 57, 998)};
    boolean running = false;
    int countDownSeconds = 10;


    HashMap<Player, Long> cd1 = new HashMap<>();
    HashMap<Player, Long> cd2 = new HashMap<>();
    HashMap<ArmorStand, ArmorStand> armourStandMap = new HashMap<>();
    HashMap<ArmorStand, Player> playerMap = new HashMap<>();

    ItemStack chicken = generateItemStack(Material.CHICKEN, "§l§b渡渡快跑", new String[]{"可以调整移动速度", "§7模仿那只渡渡的跑法的话，应该就能以势不可挡的步伐冲刺了吧"});
    ItemStack cooked_chicken = generateItemStack(Material.COOKED_CHICKEN, "§l§b渡渡快跑", new String[]{"可以调整移动速度", "§7模仿那只渡渡的跑法的话，应该就能以势不可挡的步伐冲刺了吧"});
    ItemStack glass_bottle = generateItemStack(Material.GLASS_BOTTLE, "§l§7透明身体", new String[]{"将身体变得透明", "§7欺骗敌人"});
    ItemStack nether_star = generateItemStack(Material.NETHER_STAR, "§l§c还魂", new String[]{"使友方复活", "§7已经被消费掉的东西是无法返还的...", "§7是呢，除非回溯时间"});
    ItemStack clock = generateItemStack(Material.CLOCK, "§b§l兔子的怀表", new String[]{"防止一次收到的伤害", "§7被规矩束缚的白兔绝不会在任何一场审判中迟到"});
    ItemStack coal = generateItemStack(Material.COAL, "§l§8污秽的黑之魂", new String[]{"回复所有生命值，但会降低最大生命上限", "§7这个国家洋溢着大量的黑之魂！太棒了！"});
    ItemStack potion = generateItemStack(Material.POTION, "§l§1星水", new String[]{"使用后一段时间内不会受到伤害，但效果结束后会被强制击倒", "§7没有门扉的箱庭会有漂流者顺流而来，其中似乎也混杂着外来者"});
    ItemStack dragon_breath = generateItemStack(Material.DRAGON_BREATH, "§l§d心跳悦动酒", new String[]{"瞬间恢复一定生命值，但会获得两倍回复量的发光时间", "§7梅贝尔最爱喝的酒！"});
    ItemStack honey_bottle = generateItemStack(Material.HONEY_BOTTLE, "§l§6黄金的蜂蜜酒", new String[]{"减少一半的当前生命值，获得减少数量两倍的加速时间", "§7带翼的贵妇人相当喜欢这个蜂蜜酒"});
    ItemStack enchanted_book = generateItemStack(Material.ENCHANTED_BOOK, "§l§8§k爱丽丝·里德尔的誓约", new String[]{"持有者速度永久增加，但无法被复活", "§7§m我将永远爱着你"});
    ItemStack mushroom_stew = generateItemStack(Material.MUSHROOM_STEW, "§2比尔的便当", new String[]{"为一定范围内的己方角色回复全部生命值"});

    List<ItemStack> gadgets = Arrays.asList(chicken, glass_bottle, nether_star, clock, potion, honey_bottle, coal, dragon_breath);
    List<Integer> gadgetWeights = Arrays.asList(10, 5, 1, 10, 1, 10, 5, 10);

    int totalWeight;

    private Tag4Game(Tag4 plugin) {
        this.plugin = plugin;
        initializeGame(plugin, "Tag4", "§f待定", new Location(world, -1003.0, 81, 2021.0),
                new BoundingBox(-1200, 0, 1800, -800, 200, 2200));
        initializeButtons(new Location(world, -1003, 82, 2027), BlockFace.NORTH,
                new Location(world, -1004, 82, 2027), BlockFace.NORTH);
        players = Tag4.players;
        tag4.registerNewObjective("tag4", "dummy", "鬼抓人");
        tag4.getObjective("tag4").setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i : gadgetWeights) {
            totalWeight += i;
        }
    }

    public static Tag4Game getInstance() {
        return instance;
    }

    @EventHandler
    public void clearCoolDown(PlayerDeathEvent pde) {
        clearCoolDown(pde.getEntity());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ede) { //受击特效
        if (!(ede.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) ede.getEntity();
        if (p.getInventory().contains(Material.STRING)) {
            if (ede.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                ((Player) ede.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2, false, false));
                ede.setCancelled(true);
            } else if (p.getInventory().contains(Material.CLOCK, 1)) {
                removeItem(p, Material.CLOCK);
                ede.setCancelled(true);
            }
        } else if (p.getInventory().contains(Material.CLOCK, 1)) {
            removeItem(p, Material.CLOCK);
            ede.setCancelled(true);
        }


        //以下是自带天赋
        if (p.getInventory().contains(Material.HEART_OF_THE_SEA)) {
            if (checkCoolDown(p, 60, cd1)) {
                p.sendMessage("§b获得暂时隐身！");
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
            }
        } else if (p.getInventory().contains(Material.FEATHER, 1)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
        } else if (p.getInventory().contains(Material.RED_DYE)) {
            p.sendMessage("获得生命恢复与发光！");
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, false, false));
            for (Player devil : devils) {
                if (getTeamPlayerIsIn(devil).equals("tag3L")) {
                    devil.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false));
                }
            }
        }
    }

    private void initializePlayer(Player p) {
        switch (getTeamPlayerIsIn(p)) {
            case "tag4Y" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10000000, 4, true, false));
            }
            case "tag4B" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 10000000, 0, false, false));
            }
            case "tag4X" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 0, false, false));
            }
            case "tag4R" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000, 1, false, false));
            }
            case "tag4U" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0, false, false));
            }
            case "tag4I" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000000, 1, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000, 1, false, false));
            }
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 0, false, false));
    }

    private void revivePlayer(PlayerInteractAtEntityEvent piaee) {
        ArmorStand s = (ArmorStand) piaee.getRightClicked();
        if ((armourStandMap.get(s) == null)) {
            return;
        }
        Player p = playerMap.get(s);
        if (!(players.contains(p) && !humans.contains(p))) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location l = s.getLocation();
            l.setY(l.getY() + 1.4);
            humans.add(p);
            p.teleport(l);
            p.setGameMode(GameMode.ADVENTURE);
            initializePlayer(p);
        }, 1);
        armourStandMap.get(s).remove();
        s.remove();
        armourStandMap.remove(s);
        playerMap.remove(s);
    }

    @EventHandler
    public void armorStandOperation(PlayerInteractAtEntityEvent piaee) {
        if (!piaee.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        if (!(players.contains(piaee.getPlayer()))) {
            return;
        }
        if (!(piaee.getRightClicked() instanceof ArmorStand)) {
            return;
        }
        if (piaee.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            piaee.setCancelled(true);
        }

        ItemStack item = piaee.getPlayer().getInventory().getItemInMainHand();
        if (item.getType().equals(Material.BOOK)) {
            revivePlayer(piaee);
            item.setAmount(item.getAmount() - 1);
            piaee.getPlayer().getInventory().addItem(enchanted_book);
            piaee.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 0));

        } else if (item.getType().equals(Material.NETHER_STAR)) {
            revivePlayer(piaee);
            item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler
    public void destroyChest(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }// 不是右键方块
        Player executor = pie.getPlayer();
        if (!(devils.contains(executor))) {
            return;
        } //不是鬼
        Block block = pie.getClickedBlock();
        if (block.getType().equals(Material.TRAPPED_CHEST)) {
            if (pie.getItem() == null) {
                return;
            }//没有物品
            if (!pie.getItem().getType().equals(Material.POPPED_CHORUS_FRUIT)) {
                return;
            }
            pie.setCancelled(true);
            if (((Chest) (block.getState())).getBlockInventory().isEmpty()) {
                executor.sendMessage("§c这个箱子是空的！");
            } else if (checkCoolDown(executor, 600, cd1)) {
                ((Chest) (block.getState())).getBlockInventory().clear();
                executor.sendMessage("§a成功破坏箱子内的所有道具！");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent pie) { //右键特效
        if (!pie.getAction().equals(Action.RIGHT_CLICK_AIR) && !pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }// 不是右键
        Player executor = pie.getPlayer();
        if (!players.contains(executor)) {
            return;
        }//不在tag4里
        if (devils.contains(executor)) { //是鬼
            if (pie.getItem() == null) {
                return;
            }//没有物品
            //这里开始添加内容

            switch (pie.getItem().getType()) {
                case EMERALD -> {
                    if (checkCoolDown(executor, 1200, cd2)) {
                        for (Player p: humans) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, false));
                        }
                        for (Player p : devils) {
                            if (getTeamPlayerIsIn(p).equals("tag3L")) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false));
                            }
                        }
                    }
                }
            }
        } else if (humans.contains(executor)) { //是人
            if (pie.getClickedBlock() != null) {
                if (pie.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
                    if (!pie.getPlayer().isSneaking()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0));
                        for (Player p : devils) {
                            if (getTeamPlayerIsIn(p).equals("tag3L")) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, true, false));
                            }
                        }
                        return;
                    }
                }
            }
            if (pie.getItem() == null) {
                return;
            }//没有物品
            //这里开始添加内容

            switch (pie.getItem().getType()) {
                case COAL -> {
                    if (scoreboard.getTeam("tag4Y").hasPlayer(executor) || scoreboard.getTeam("tag4O").hasPlayer(executor)) {
                        executor.sendMessage("§c生命全部恢复！");
                        pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    } else {
                        if (executor.getMaxHealth() > 3) {
                            executor.sendMessage("§c最大生命值减少，生命全部恢复！");
                            executor.setMaxHealth(executor.getMaxHealth() - 3);
                            if (!getTeamPlayerIsIn(executor).equals("tag4Q") && !getTeamPlayerIsIn(executor).equals("tag4U")) {
                                executor.setHealth(executor.getMaxHealth());
                            }
                            pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                        } else {
                            executor.sendMessage("§c生命上限过低，无法使用！");
                        }
                    }
                }
                case POTION -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c30秒内无敌，30秒后将被强制击倒！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 254, false, false));
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        executor.setHealth(0);
                    }, 600);
                }
                case GLASS_BOTTLE -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c隐身10秒！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false));
                }
                case DRAGON_BREATH -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c回复一半最大生命值，获得与回复量相同的发光时长！");
                    if (executor.getHealth() * 2 < executor.getMaxHealth()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) (executor.getMaxHealth() * 10), 0, false, false));
                        if (!getTeamPlayerIsIn(executor).equals("tag4Q") && !getTeamPlayerIsIn(executor).equals("tag4U")) {
                            executor.setHealth(executor.getHealth() + executor.getMaxHealth() / 2);
                        }
                        for (Player p : devils) {
                            if (getTeamPlayerIsIn(p).equals("tag3L")) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (executor.getMaxHealth() * 10), 1, true, false));
                            }
                        }
                    } else {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) ((executor.getMaxHealth() - executor.getHealth()) * 20), 0, false, false));
                        if (!getTeamPlayerIsIn(executor).equals("tag4Q") && !getTeamPlayerIsIn(executor).equals("tag4U")) {
                            executor.setHealth(executor.getMaxHealth());
                        }
                        for (Player p : devils) {
                            if (getTeamPlayerIsIn(p).equals("tag3L")) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) ((executor.getMaxHealth() - executor.getHealth()) * 20), 1, true, false));
                            }
                        }
                    }
                }
                case HONEY_BOTTLE -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§e减少一半的当前生命值，获得与减少量两倍相同的加速时间！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (executor.getHealth() * 20), 0, false, false));
                    executor.setHealth(executor.getHealth() / 2);
                }
                case CHICKEN -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.getInventory().addItem(cooked_chicken);
                    executor.sendMessage("§b获得加速！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false));
                    if (getTeamPlayerIsIn(executor).equals("tag4U")) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0));
                    }
                }
                case COOKED_CHICKEN -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§b获得加速！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false));
                    if (getTeamPlayerIsIn(executor).equals("tag4U")) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0));
                    }
                }
                case MUSHROOM_STEW -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§a为身边己方角色恢复全部生命值！");
                    for (Entity e : executor.getNearbyEntities(5,5,5)) {
                        if (humans.contains(e)) {
                            if (!getTeamPlayerIsIn((Player)e).equals("tag4Q")) {
                                ((Player)e).setHealth(((Player)e).getMaxHealth());;
                            }
                        }
                    }
                }
                case BEETROOT_SOUP -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§a恢复一半最大生命，获得隐身！");
                    if (executor.getHealth() * 2 < executor.getMaxHealth()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                        executor.setHealth(executor.getHealth() + executor.getMaxHealth() / 2);

                    } else {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                        executor.setHealth(executor.getMaxHealth());
                    }
                }
            }
        }
    }

    @EventHandler
    public void cancelItemMovement(InventoryClickEvent ice) {
        if (!(ice.getWhoClicked() instanceof Player)) {
            return;
        }
        if (ice.getCurrentItem() == null) {
            return;
        }
        Player p = (Player) ice.getWhoClicked();
        if (devils.contains(p)) {
            ice.setCancelled(true);
        } else if (humans.contains(p)) {
            if (ice.getCurrentItem().getType().equals(Material.BOOK)
                    || ice.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)
                    || ice.getCurrentItem().getType().equals(Material.HEART_OF_THE_SEA)
                    || ice.getCurrentItem().getType().equals(Material.RABBIT_FOOT)
                    || ice.getCurrentItem().getType().equals(Material.ENDER_EYE)
                    || ice.getCurrentItem().getType().equals(Material.STRING)
                    || ice.getCurrentItem().getType().equals(Material.RED_DYE)
                    || ice.getCurrentItem().getType().equals(Material.POPPED_CHORUS_FRUIT)) {
                ice.setCancelled(true);
            }
        }
    }

    public void removeItem(Player p, Material material) {
        p.getInventory().all(material).get(0);
        for (Map.Entry entry : p.getInventory().all(material).entrySet()) {
            ItemStack i = (ItemStack) entry.getValue();
            i.setAmount(i.getAmount() - 1);
            break;
        }
    }

    public void clearCoolDown(Player p) {
        if (cd1.get(p) != null) {
            cd1.remove(p);
        }
        if (cd2.get(p) != null) {
            cd2.remove(p);
        }
    }

    public boolean checkCoolDown(Player p, long coolDown, HashMap<Player, Long> map) {
        if (map.get(p) == null) {
            map.put(p, getTime(world));
            return true;
        } else {
            long timeLapsed = getTime(world) - map.get(p);
            if (timeLapsed < coolDown) {
                p.sendMessage("§c§l技能冷却中！ 还剩 §6§l" + (int) ((coolDown - timeLapsed) / 20) + " §c§l秒");
                return false;
            } else {
                map.put(p, getTime(world));
                return true;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (!players.contains(pde.getEntity())) {
            return;
        }
        devils.remove(pde.getEntity());
        humans.remove(pde.getEntity());
        pde.getEntity().setGameMode(GameMode.SPECTATOR);
        for (Player p : players) {
            p.sendMessage("§f" + pde.getEntity().getName() + " §c的灵魂被收割了！");
        }
    }

    @EventHandler
    public void freezeGui(EntityDamageByEntityEvent edbee) {
        if (!(edbee.getDamager() instanceof Player)) {
            return;
        }
        if (!(edbee.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) edbee.getEntity();
        Player damager = (Player) edbee.getDamager();
        if (humans.contains(damager) && devils.contains(victim)) {
            if (getTeamPlayerIsIn(damager).equals("tag4T")) {
                if (damager.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
                    if (checkCoolDown(damager, 1200, cd1)) {
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false));
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0, false, false));
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                        damager.sendMessage("§c获得加速、恢复和隐身！");
                    }
                }
            }
            edbee.setCancelled(true);
        } else if (humans.contains(damager) && humans.contains(victim)) {
            edbee.setCancelled(true);
        } else if (devils.contains(damager) && humans.contains(victim)) {
            if (getTeamPlayerIsIn((Player) damager).equals("tag4C")) {
                if (scoreboard.getTeam("tag4Y").hasPlayer(victim)) {
                    victim.sendMessage("§a成功免疫最大生命减少效果！");
                } else {
                    if (victim.getMaxHealth() > 3) {
                        victim.sendMessage("§c被巴风特攻击，最大生命值减少！");
                        victim.setMaxHealth(victim.getMaxHealth() - 3);
                    } else {
                        victim.sendMessage("§a生命上限过低，无法继续减少！");
                    }
                }
            } else
            if (getTeamPlayerIsIn(victim).equals("tag4G")) {
                damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            }
            int freezeTime = 60;
            if (getTeamPlayerIsIn((Player) damager).equals("tag4E")) {
                freezeTime = 100;
            }
            Location l = damager.getLocation().clone();
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                damager.teleport(l);
            }, 1, 1);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getScheduler().cancelTask(id);
            }, freezeTime);
            damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, freezeTime, 254, false, false));
            damager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, freezeTime, 190, false, false));
            damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, freezeTime, 254, false, false));
        }
    }

    //seems not useful
    /*
    @EventHandler
    public void cancelPickup(PlayerPickupItemEvent ppie) {
        Player p = ppie.getPlayer();
        if (humans.contains(p)) {
            if (ppie.getItem().getItemStack().getType().equals(Material.CLOCK)
                    || ppie.getItem().getItemStack().getType().equals(Material.EMERALD)) {
                ppie.setCancelled(true);
            }
        } else if (devils.contains(p)) {
            if (ppie.getItem().getItemStack().getType().equals(Material.DIAMOND)
                    || ppie.getItem().getItemStack().getType().equals(Material.RED_DYE)
                    || ppie.getItem().getItemStack().getType().equals(Material.HEART_OF_THE_SEA)) {
                ppie.setCancelled(true);
            }
        }
    }

     */

    public ItemStack generateItemStack(Material material, String name, String[] lore) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore.clone()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void summonCorpse(PlayerDeathEvent pde) {
        if (!(players.contains(pde.getEntity()))) {
            return;
        }
        if ((devils.contains(pde.getEntity()))) {
            return;
        }
        if (pde.getEntity().getInventory().contains(Material.ENCHANTED_BOOK, 1)) {
            for (Player p : players) {
                p.sendMessage("§f" + pde.getEntity().getName() + " §c 永远葬身于寒冷");
            }
            return;
        } else if (pde.getEntity().getKiller() != null) {
            if (scoreboard.getTeam("tag4C").hasPlayer(pde.getEntity().getKiller())) {
                for (Player p : players) {
                    p.sendMessage("§f" + pde.getEntity().getName() + " §c 永远葬身于寒冷");
                }
                return;
            }
        }
        Location l = pde.getEntity().getLocation().clone();

        ArmorStand ice = (ArmorStand) world.spawnEntity(l, EntityType.ARMOR_STAND);
        ice.setBasePlate(false);
        ice.setInvisible(true);
        ice.setVelocity(new Vector(0, -50, 0));

        l.setY(0);

        ArmorStand head = (ArmorStand) world.spawnEntity(l, EntityType.ARMOR_STAND);
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        skullMeta.setOwningPlayer(pde.getEntity());
        headItem.setItemMeta(skullMeta);
        head.setBasePlate(false);
        head.setSmall(true);
        head.getEquipment().setHelmet(headItem);
        head.setGravity(false);
        head.setCustomName(pde.getEntity().getName());
        head.setCustomNameVisible(true);
        head.setInvisible(true);
        EulerAngle angle = new EulerAngle(Math.PI, 0, 0);
        head.setLeftLegPose(angle);
        head.setRightLegPose(angle);

        armourStandMap.put(ice, head);
        playerMap.put(ice, pde.getEntity());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ice.setGravity(false);
            Location iceLocation = ice.getLocation().clone();
            iceLocation.setY(iceLocation.getY() - 1.4);
            ice.teleport(iceLocation);
            iceLocation.setY(iceLocation.getY() + 0.75);
            head.teleport(iceLocation);
        }, 5);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ice.getEquipment().setHelmet(new ItemStack(Material.ICE));
            head.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 594, 0));
        }, 6);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (head.isValid()) {
                head.remove();
                ice.remove();
                for (Player p : players) {
                    p.sendMessage("§f" + pde.getEntity().getName() + " §c 永远葬身于寒冷");
                }
            }
        }, 600);
    }

    @EventHandler
    public void preventDroppingItem(PlayerDropItemEvent pdie) {
        if (!(players.contains(pdie.getPlayer()))) {
            return;
        }
        if (!pdie.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        switch (pdie.getItemDrop().getItemStack().getType()) {
            case RABBIT_FOOT, BOOK, STRING, HEART_OF_THE_SEA, ENCHANTED_BOOK, ENDER_EYE, RED_DYE, POPPED_CHORUS_FRUIT,
                    TOTEM_OF_UNDYING, END_ROD, FEATHER, TURTLE_EGG,MEDIUM_AMETHYST_BUD, BLAZE_POWDER, MUSHROOM_STEW, BEETROOT_SOUP-> pdie.setCancelled(true);
            default -> {
            }
        }
    }

    @EventHandler
    public void preventRegen(EntityRegainHealthEvent erhe) {
        if (!(erhe.getEntity() instanceof Player)) {
            return;
        }
        if (!(players.contains(erhe.getEntity()))) {
            return;
        }
        if (!((Player) erhe.getEntity()).getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        if (getTeamPlayerIsIn((Player)erhe.getEntity()).equals("tag4Q")) {
            erhe.setCancelled(true);
        }
        if (erhe.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            erhe.setCancelled(true);
        } else if (erhe.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.EATING)) {
            erhe.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChangeGame(PlayerChangeGameEvent pcge) {
        players.remove(pcge.getPlayer());
        humans.remove(pcge.getPlayer());
        devils.remove(pcge.getPlayer());
    }

    public void savePlayerQuitData(Player p) {
        PlayerQuitData quitData = new PlayerQuitData(p, this, gameUUID);
        quitData.getData().put("team", whichGroup(p));
        setPlayerQuitData(p.getUniqueId(), quitData);
        players.remove(p);
        humans.remove(p);
        devils.remove(p);
    }

    @Override
    protected void rejoin(Player p) {
        if (!running) {
            p.sendMessage("§c游戏已经结束！");
            return;
        }
        if (!getPlayerQuitData(p.getUniqueId()).getGameUUID().equals(gameUUID)) {
            p.sendMessage("§c游戏已经结束！");
            return;
        }
        PlayerQuitData pqd = getPlayerQuitData(p.getUniqueId());
        pqd.restoreBasicData(p);
        players.add(p);
        team.addPlayer(p);
        p.setScoreboard(tag4);
        if (pqd.getData().get("team") != null) {
            ((List<Player>) pqd.getData().get("team")).add(p);
        }
        setPlayerQuitData(p.getUniqueId(), null);
    }

    private List<Player> whichGroup(Player p) {
        if (humans.contains(p)) {
            return humans;
        } else if (devils.contains(p)) {
            return devils;
        } else {
            return null;
        }
    }

    private void endGame(String msg, List<Player> winningPlayers) {
        List<Player> winningPlayersCopy = new ArrayList<>(winningPlayers);
        List<Player> playersCopy = new ArrayList<>(players);
        for (Player p : winningPlayersCopy) {
            spawnFireworks(p);
        }
        for (Player p : playersCopy) {
            p.sendTitle(msg, null, 5, 50, 5);
            p.resetPlayerWeather();
            p.resetPlayerTime();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                p.teleport(new Location(world, -1003.0, 81, 2021.0));
                Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p, this));
            }, 100);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                scoreboard.getTeam("tag4Y").addPlayer(p);
            }, 101);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : world.getNearbyEntities(new Location(world, -1000, 128, 2000), 200, 200, 200)) {
                if (e instanceof Item) {
                    e.remove();
                }
            }
            clearChests();
            for (Map.Entry<ArmorStand, ArmorStand> entry : armourStandMap.entrySet()) {
                entry.getKey().remove();
                entry.getValue().remove();
            }
            armourStandMap.clear();
            playerMap.clear();
            removeSpectateButton();
            placeStartButton();
            HandlerList.unregisterAll(this);
            tag4.getObjective("tag4").getScore("剩余人数").setScore(0);
            tag4.getObjective("tag4").getScore("剩余时间").setScore(0);
        }, 100);
        players.clear();
        humans.clear();
        devils.clear();
        team.unregister();
        running = false;
        gameUUID = UUID.randomUUID();
        cancelGameTasks();
    }

    @Override
    protected void initializeGameRunnable() {
        gameRunnable = () -> {
            gameTime = Tag4.gameTime;
            team = tag4.registerNewTeam("tag4");
            team.setNameTagVisibility(NameTagVisibility.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
            team.setAllowFriendlyFire(true);
            for (Player p : getPlayersNearHub(50, 50, 50)) {
                switch (getTeamPlayerIsIn(p)) {
                    case "tag4B", "tag4W", "tag4G", "tag4Y", "tag4X", "tag4H" , "tag4Q", "tag4T", "tag4U", "tag4I", "tag4O", "tag4P"-> {
                        humans.add(p);
                    }
                    case "tag4R", "tag4E", "tag4L","tag4C" -> {
                        devils.add(p);
                    }
                }
                players.add(p);
                team.addPlayer(p);
            }
            if (players.size() < 2) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要2人才能开始游戏！");
                }
                players.clear();
                humans.clear();
                team.unregister();

            } else if (humans.size() == 0) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要1个人类才能开始游戏！");
                }
                players.clear();
                humans.clear();
                team.unregister();
            } else if (devils.size() == 0) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要1个鬼才能开始游戏！");
                }
                players.clear();
                humans.clear();
                team.unregister();
            } else {
                startTime = getTime(world) + countDownSeconds * 20L + 400;
                running = true;
                removeStartButton();
                startCountdown(countDownSeconds);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Player p : players) {
                        p.setPlayerWeather(WeatherType.DOWNFALL);
                        p.setPlayerTime(18000, false);
                        p.getInventory().clear();
                    }
                });
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getPluginManager().registerEvents(this, plugin);
                    for (Player p : humans) {
                        p.teleport(new Location(world, -1003.0, 85, 2002.0));
                    }
                    for (Player p : players) {
                        initializePlayer(p);
                        p.setScoreboard(tag4);
                    }

                }, countDownSeconds * 20L);
                for (int i = 0; i < 5; i++) {
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (Player p : players) {
                            p.sendTitle("§a" + (5 - finalI), null, 2, 16, 2);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                        }
                    }, countDownSeconds * 20L + 300 + i * 20);
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    placeSpectateButton();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function tag4:go");
                    for (Player p : devils) {
                        p.teleport(new Location(world, -1003.0, 85, 2002.0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 4, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 1, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false));
                        ItemStack skull = new ItemStackBuilder(Material.WITHER_SKELETON_SKULL).addEnchantment(Enchantment.BINDING_CURSE, 1).build();
                        ItemStack chestPlate = new ItemStackBuilder(Material.NETHERITE_CHESTPLATE).addEnchantment(Enchantment.BINDING_CURSE, 1).setUnbreakable(true).build();
                        ItemStack leggings = new ItemStackBuilder(Material.NETHERITE_LEGGINGS).addEnchantment(Enchantment.BINDING_CURSE, 1).setUnbreakable(true).build();
                        ItemStack boots = new ItemStackBuilder(Material.NETHERITE_BOOTS).addEnchantment(Enchantment.BINDING_CURSE, 1).setUnbreakable(true).build();
                        p.getInventory().setItem(EquipmentSlot.HEAD, skull);
                        p.getInventory().setItem(EquipmentSlot.CHEST, chestPlate);
                        p.getInventory().setItem(EquipmentSlot.LEGS, leggings);
                        p.getInventory().setItem(EquipmentSlot.FEET, boots);
                    }
                    for (Player p : players) {
                        if (getTeamPlayerIsIn(p).equals("tag4")) {

                        }
                        p.sendTitle("§e游戏开始！", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 2f);
                    }

                }, countDownSeconds * 20L + 400);

                taskIds.add(
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                            for (Player p : humans) {
                                if (p.getInventory().contains(Material.ENDER_EYE)) {
                                    for (Player victim : players) {
                                        victim.sendMessage("§7梅贝尔§f在场，所有鬼发光5秒！");
                                        if (devils.contains(victim)) {
                                            victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, false, false));
                                            for (Player devil : devils) {
                                                if (getTeamPlayerIsIn(devil).equals("tag4L")) {
                                                    devil.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true, false));
                                                }
                                            }
                                        }
                                    }
                                    return;
                                }

                            }
                        }, countDownSeconds * 20L + 400 + 600, 600));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : players) {
                        if (devils.contains(p)) {
                            for (Player victim : players) {
                                if (humans.contains(victim)) {
                                    if (p.getLocation().distance(victim.getLocation()) < 10) {
                                        victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        }, 3);
                                    }
                                    if (p.getLocation().distance(victim.getLocation()) < 5) {
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        }, 10);
                                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                            victim.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.BLOCKS, 2f, 0f);
                                        }, 13);
                                    }

                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400, 20));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : players) {
                        p.sendMessage("§a道具已刷新！");
                    }
                    for (Location loc : locations) {
                        double spawnChance = random.nextDouble();
                        if (spawnChance < 0.5) {//overall chance
                            int spawnNo = random.nextInt(totalWeight);
                            int counter = 0;
                            for (int i = 0; i < gadgets.size(); i++) {
                                counter += gadgetWeights.get(i);
                                if (spawnNo < counter) {
                                    //((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(gadgets.get(i));
                                    break;
                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400 + 600, 1200));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : devils) {
                        if (getTeamPlayerIsIn(p).equals("tag4E")) {
                            p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                            p.sendMessage("§a获得末影珍珠！");
                        }
                    }
                }, countDownSeconds * 20L + 400, 600));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : humans) {
                        if (getTeamPlayerIsIn(p).equals("tag4P")) {
                            int spawnNo = random.nextInt(totalWeight);
                            int counter = 0;
                            for (int i = 0; i < gadgets.size(); i++) {
                                counter += gadgetWeights.get(i);
                                if (spawnNo < counter) {
                                    p.getInventory().addItem(gadgets.get(i));
                                    p.sendMessage("§a获得随机道具！");
                                    break;
                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400, 20));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : humans) {
                        if (getTeamPlayerIsIn(p).equals("tag4O")) {
                            for (Entity e : p.getNearbyEntities(5,5,5)) {
                                if (humans.contains(e)) {
                                    return;
                                }
                            }
                            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 0, false, false));
                        }
                    }
                }, countDownSeconds * 20L + 400, 50));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : humans) {
                        if (getTeamPlayerIsIn(p).equals("tag4B")) {
                            if (p.isSwimming()) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 0, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 50, 0, true, false));
                                for (Player devil : devils) {
                                    if (getTeamPlayerIsIn(devil).equals("tag3L")) {
                                        devil.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50, 1, true, false));
                                    }
                                }
                            }
                        }
                    }
                }, countDownSeconds * 20L + 400, 50));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    long time = getTime(world);
                    if (time - startTime > gameTime) {
                        endGame("§e时间到，人类获胜！", humans);
                        return;
                    }
                    if (humans.size() <= 0) {
                        endGame("§e无人幸存，鬼获胜！", devils);
                        return;
                    }
                    if (devils.size() <= 0) {
                        endGame("§e鬼不复存在，人类获胜！", humans);
                        return;
                    }
                    tag4.getObjective("tag4").getScore("剩余人数").setScore(humans.size());
                    tag4.getObjective("tag4").getScore("剩余时间").setScore((int) ((gameTime - (time - startTime)) / 20));
                }, countDownSeconds * 20L + 400, 1));
            }
        };
    }

    private String getTeamPlayerIsIn(Player p) {
        for (Team t : scoreboard.getTeams()) {
            if (t.hasPlayer(p)) {
                return t.getName();
            }
        }
        return null;
    }

    public void clearChests() {
        for (Location l : locations) {
            //((Chest) (world.getBlockAt(l).getState())).getBlockInventory().clear();
        }
    }
}

