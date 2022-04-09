package fun.kaituo;


import fun.kaituo.event.PlayerChangeGameEvent;
import fun.kaituo.event.PlayerEndGameEvent;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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
import static fun.kaituo.GameUtils.getPlayerQuitData;

public class Tag4Game extends Game implements Listener {
    private static Tag4Game instance = new Tag4Game((Tag4) Bukkit.getPluginManager().getPlugin("Tag4"));
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Scoreboard tag4 = Bukkit.getScoreboardManager().getNewScoreboard();
    Tag4 plugin;
    List<Player> humans = new ArrayList<>();
    List<Player> devils = new ArrayList<>();
    long startTime;
    long gameTime;
    Team team;
    Location[] locations;
    boolean running = false;
    int countDownSeconds =10;

    public static Tag4Game getInstance() {
        return instance;
    }


    HashMap<Player, Long> cd;
    HashMap<ArmorStand, ArmorStand> armourStandMap;
    HashMap<ArmorStand, Player> playerMap;
    ItemStack feather; ItemStack glass_bottle; ItemStack nether_star; ItemStack clock; ItemStack cooked_chicken;
    ItemStack dragon_breath; ItemStack coal; ItemStack honey_bottle; ItemStack potion;

    ItemStack enchanted_book;

    ItemStack popped_chorus_fruit;

    private Tag4Game(Tag4 plugin) {
        this.plugin = plugin;
        initializeGame(plugin, "Tag4", "§f待定", new Location(world,-1003.0, 81, 2021.0),
                new BoundingBox(-1200, 60, 1800,-800, 220, 2200));
        initializeButtons(new Location(world,-1003, 82, 2027),BlockFace.NORTH,
                new Location(world, -1004, 82, 2027),BlockFace.NORTH);
        players = Tag4.players;
        tag4.registerNewObjective("tag4", "dummy", "鬼抓人");
        tag4.getObjective("tag4").setDisplaySlot(DisplaySlot.SIDEBAR);
        /*
        locations = new Location[] {
                new Location(world,-1005,69,1000),
                new Location(world,-991,65,1005),
                new Location(world,-1019,66,1014),
                new Location(world,-1010,67,1005),
                new Location(world,-1018,66,992),
                new Location(world,-983,64,997),
                new Location(world,-998,54,1009),
                new Location(world,-986,56,1012),
                new Location(world,-1015,54,1015),
                new Location(world,-1017,51,996),
                new Location(world,-981,57,986),
                new Location(world,-986,48,981),
                new Location(world,-1000,68,1013),
                new Location(world,-1001,53,998),
                new Location(world,-991,57,998)};

         */

        feather = generateItemStack(Material.FEATHER,"§l§b渡渡快跑", new String[]{"可以调整移动速度", "§7模仿那只渡渡的跑法的话，应该就能以势不可挡的步伐冲刺了吧"});
        cooked_chicken = generateItemStack(Material.COOKED_CHICKEN,"§l§b渡渡快跑", new String[]{"可以调整移动速度", "§7模仿那只渡渡的跑法的话，应该就能以势不可挡的步伐冲刺了吧"});
        glass_bottle = generateItemStack(Material.GLASS_BOTTLE, "§l§7透明身体",new String[]{"将身体变得透明","§7欺骗敌人"});
        nether_star = generateItemStack(Material.NETHER_STAR, "§l§c还魂", new String[]{"使友方复活","§7已经被消费掉的东西是无法返还的...","§7是呢，除非回溯时间"});
        clock = generateItemStack(Material.CLOCK,"§b§l兔子的怀表",new String[]{"防止一次收到的伤害","§7被规矩束缚的白兔绝不会在任何一场审判中迟到"});
        coal = generateItemStack(Material.COAL,"§l§8污秽的黑之魂", new String[]{"回复所有生命值，但会降低最大生命上限","§7这个国家洋溢着大量的黑之魂！太棒了！"});
        potion = generateItemStack(Material.POTION,"§l§1星水", new String[]{"使用后一段时间内不会受到伤害，但效果结束后会被强制击倒", "§7没有门扉的箱庭会有漂流者顺流而来，其中似乎也混杂着外来者"});
        dragon_breath = generateItemStack(Material.DRAGON_BREATH, "§l§d心跳悦动酒",new String[]{"瞬间恢复一定生命值，但会获得两倍回复量的发光时间","§7梅贝尔最爱喝的酒！"});
        honey_bottle = generateItemStack(Material.HONEY_BOTTLE,"§l§6黄金的蜂蜜酒",new String[]{"减少一半的当前生命值，获得减少数量两倍的加速时间", "§7带翼的贵妇人相当喜欢这个蜂蜜酒"});
        enchanted_book= generateItemStack(Material.ENCHANTED_BOOK,"§l§8§k爱丽丝·里德尔的誓约",new String[]{"持有者速度永久增加，但无法被复活","§7§m我将永远爱着你"});
        popped_chorus_fruit = generateItemStack(Material.POPPED_CHORUS_FRUIT,"§l§5道具破坏",new String[]{"破坏一个箱子里的所有道具"});

        cd = new HashMap<Player, Long>();
        armourStandMap = new HashMap<ArmorStand, ArmorStand>();
        playerMap = new HashMap<ArmorStand, Player>();
    }

    @EventHandler public void clearCoolDown(PlayerDeathEvent pde) {clearCoolDown(pde.getEntity());}

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ede) {
        if (!(ede.getEntity() instanceof Player)) {
            return;
        }
        if (ede.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            if ((((EntityDamageByEntityEvent)ede).getDamager() instanceof Player)) {
                if (humans.contains(((EntityDamageByEntityEvent)ede).getDamager())) {
                    return;
                }
            }
        }
        Player p = (Player)ede.getEntity();
        if (p.getInventory().contains(Material.STRING)) {
            if (ede.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                ((Player) ede.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED,60,1,false,false));
                ede.setCancelled(true);
            } else if (p.getInventory().contains(Material.CLOCK,1)) {
                removeItem(p,Material.CLOCK);
                ede.setCancelled(true);
            }
        } else if (p.getInventory().contains(Material.CLOCK,1)) {
            removeItem(p,Material.CLOCK);
            ede.setCancelled(true);
        }
        if (p.getInventory().contains(Material.HEART_OF_THE_SEA)) {
            if (checkCoolDown(p,60)) {
                p.sendMessage("§b获得暂时隐身！");
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,60,0,false,false));
            }
        }
        if (p.getInventory().contains(Material.RED_DYE)) {
            p.sendMessage("获得生命恢复与发光！");
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,200,0,false,false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,100,0,false,false));
        }
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
            if ((armourStandMap.get(piaee.getRightClicked()) == null)) {
                return;
            }
            Player p = playerMap.get(piaee.getRightClicked());
            if (!(players.contains(p) && !humans.contains(p))) {
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location l = piaee.getRightClicked().getLocation();
                l.setY(l.getY() + 1.4);
                humans.add(p);
                p.teleport(l);
                p.setGameMode(GameMode.ADVENTURE);
                if (scoreboard.getTeam("tag4Y").hasPlayer(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,10000000,4,true,false));
                } else if (scoreboard.getTeam("tag4B").hasPlayer(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,10000000,0,false,false));
                } else if (scoreboard.getTeam("tag4G").hasPlayer(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,10000000,0,false,false));
                }
            },1);
            armourStandMap.get(piaee.getRightClicked()).remove();
            piaee.getRightClicked().remove();
            armourStandMap.remove(piaee.getRightClicked());
            playerMap.remove(piaee.getRightClicked());


            item.setAmount(item.getAmount() - 1);
            piaee.getPlayer().getInventory().addItem(enchanted_book);
            piaee.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,10000000,0));

        } else if (item.getType().equals(Material.NETHER_STAR)) {
            if ((armourStandMap.get(piaee.getRightClicked()) == null)) {
                return;
            }

            Player p = playerMap.get(piaee.getRightClicked());
            if (!(players.contains(p) && !humans.contains(p))) {
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location l = piaee.getRightClicked().getLocation();
                l.setY(l.getY() + 1.4);
                humans.add(p);
                p.teleport(l);
                p.setGameMode(GameMode.ADVENTURE);
                if (scoreboard.getTeam("tag4Y").hasPlayer(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,10000000,4,true,false));
                } else if (scoreboard.getTeam("tag4B").hasPlayer(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,10000000,0,false,false));
                } else if (scoreboard.getTeam("tag4G").hasPlayer(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,10000000,0,false,false));
                }
            },1);
            armourStandMap.get(piaee.getRightClicked()).remove();
            piaee.getRightClicked().remove();
            armourStandMap.remove(piaee.getRightClicked());
            playerMap.remove(piaee.getRightClicked());

            item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler
    public void destroyChest(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {return;}// 不是右键方块
        Player executor = pie.getPlayer();
        if (!(devils.contains(executor))) { return; } //不是鬼
        Block block = pie.getClickedBlock();
        if (block.getType().equals(Material.TRAPPED_CHEST)) {
            if (pie.getItem() == null) {return;}//没有物品
            if (!pie.getItem().getType().equals(Material.POPPED_CHORUS_FRUIT)) {return;}
            pie.setCancelled(true);
            if (((Chest)(block.getState())).getBlockInventory().isEmpty()) {
                executor.sendMessage("§c这个箱子是空的！");
            } else if (checkCoolDown(executor,600)) {
                ((Chest)(block.getState())).getBlockInventory().clear();
                executor.sendMessage("§a成功破坏箱子内的所有道具！");
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.RIGHT_CLICK_AIR) && !pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {return;}// 不是右键
        Player executor = pie.getPlayer();
        if (!players.contains(executor)) {return;}//不在tag4里
        if (devils.contains(executor)) { //是鬼
            if (pie.getItem() == null) {return;}//没有物品
            //这里开始添加内容

            switch (pie.getItem().getType()) {
                case EMERALD -> {
                    for (Player p: humans) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                    }
                }
            }
        } else {
            if (pie.getClickedBlock() != null) {
                if (pie.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
                    if (!pie.getPlayer().isSneaking()) {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,60,0));
                        return;
                    }
                }
            }
            if (pie.getItem() == null) {return;}//没有物品
            //这里开始添加内容

            switch (pie.getItem().getType()) {
                case COAL -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§c最大生命值减少，生命全部恢复！");
                    executor.setMaxHealth(executor.getMaxHealth() - 3);
                    executor.setHealth(executor.getMaxHealth());
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
                        executor.setHealth(executor.getHealth() + executor.getMaxHealth() / 2);
                    } else {
                        executor.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) ((executor.getMaxHealth() - executor.getHealth()) * 20), 0, false, false));
                        executor.setHealth(executor.getMaxHealth());
                    }
                }
                case HONEY_BOTTLE -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§e减少一半的当前生命值，获得与减少量两倍相同的加速时间！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (executor.getHealth() * 20), 0, false, false));
                    executor.setHealth(executor.getHealth() / 2);
                }
                case FEATHER -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.getInventory().addItem(cooked_chicken);
                    executor.sendMessage("§b获得加速！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false));
                }
                case COOKED_CHICKEN -> {
                    pie.getItem().setAmount(pie.getItem().getAmount() - 1);
                    executor.sendMessage("§b获得加速！");
                    executor.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false));
                }
            }
        }
    }
    @EventHandler
    public void cancelItemMovement (InventoryClickEvent ice) {
        if (!(ice.getWhoClicked() instanceof Player)) {
            return;
        }
        if (ice.getCurrentItem() == null) {
            return;
        }
        Player p = (Player)ice.getWhoClicked();
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
        if (cd.get(p) != null) {
            cd.remove(p);
        }
    }

    public boolean checkCoolDown(Player p, long coolDown) {
        if (cd.get(p) == null) {
            cd.put(p,getTime(world));
            return true;
        } else {
            long timeLapsed = getTime(world) - cd.get(p);
            if (timeLapsed < coolDown) {
                p.sendMessage("§c§l技能冷却中！ 还剩 §6§l" + (int)((coolDown - timeLapsed) / 20) + " §c§l秒");
                return false;
            } else {
                cd.put(p,getTime(world));
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
        if (humans.contains(edbee.getDamager())) {
            edbee.setCancelled(true);
        }
        if (devils.contains(edbee.getDamager()) && humans.contains(edbee.getEntity())) {
            Location l = edbee.getDamager().getLocation().clone();
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                edbee.getDamager().teleport(l);
            },1,1);
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                Bukkit.getScheduler().cancelTask(id);
            }, 60);
            ((Player) edbee.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 254, false, false));
            ((Player) edbee.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 190, false, false));
            ((Player) edbee.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,60,254, false, false));
        }
    }
    public ItemStack generateItemStack(Material material, String name, String[] lore) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore.clone()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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

    @EventHandler
    public void summonCorpse(PlayerDeathEvent pde) {
        if (!(players.contains(pde.getEntity()))) {
            return;
        }
        if ((devils.contains(pde.getEntity()))) {
            return;
        }
        if (pde.getEntity().getInventory().contains(Material.ENCHANTED_BOOK,1)) {
            for (Player p: players) {
                p.sendMessage("§f" + pde.getEntity().getName()+" §c 永远葬身于寒冷");
            }
            return;
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
        head.setBasePlate(false);head.setSmall(true);
        head.getEquipment().setHelmet(headItem);
        head.setGravity(false);
        head.setCustomName(pde.getEntity().getName());
        head.setCustomNameVisible(true);
        head.setInvisible(true);
        EulerAngle angle = new EulerAngle(Math.PI,0,0);
        head.setLeftLegPose(angle);
        head.setRightLegPose(angle);

        armourStandMap.put(ice, head);
        playerMap.put(ice,pde.getEntity());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ice.setGravity(false);
            Location iceLocation = ice.getLocation().clone();
            iceLocation.setY(iceLocation.getY() - 1.4);
            ice.teleport(iceLocation);
            iceLocation.setY(iceLocation.getY() + 0.75);
            head.teleport(iceLocation);
        },5);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ice.getEquipment().setHelmet(new ItemStack(Material.ICE));
            head.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,550,0));
        },6);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (head.isValid()) {
                head.remove();
                ice.remove();
                for (Player p: players) {
                    p.sendMessage("§f" + pde.getEntity().getName()+" §c 永远葬身于寒冷");
                }
            }
        },600);
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
            case RABBIT_FOOT:
                pdie.setCancelled(true);
                break;
            case STRING:
                pdie.setCancelled(true);
                break;
            case BOOK:
                pdie.setCancelled(true);
                break;
            case ENCHANTED_BOOK:
                pdie.setCancelled(true);
                break;
            case HEART_OF_THE_SEA:
                pdie.setCancelled(true);
                break;
            case ENDER_EYE:
                pdie.setCancelled(true);
                break;
            case RED_DYE:
                pdie.setCancelled(true);
                break;
            case POPPED_CHORUS_FRUIT:
                pdie.setCancelled(true);
                break;
            default:
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
            ((List<Player>)pqd.getData().get("team")).add(p);
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

    private void endGame() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : world.getNearbyEntities(new Location(world, -1000, 128, 2000), 200, 200, 200)) {
                if (e instanceof Item) {
                    e.remove();
                }
            }
            //clearChests();
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
            for (Player p : getPlayersNearHub(50,50,50)) {
                if (scoreboard.getTeam("tag4B").hasPlayer(p)) {
                    humans.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4W").hasPlayer(p)) {
                    humans.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4G").hasPlayer(p)) {
                    humans.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4Y").hasPlayer(p)) {
                    humans.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4X").hasPlayer(p)) {
                    humans.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4H").hasPlayer(p)) {
                    humans.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4R").hasPlayer(p)) {
                    devils.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4E").hasPlayer(p)) {
                    devils.add(p);
                    players.add(p);
                    team.addPlayer(p);
                } else if (scoreboard.getTeam("tag4L").hasPlayer(p)) {
                    devils.add(p);
                    players.add(p);
                    team.addPlayer(p);
                }

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
                running = true;
                startTime = getTime(world);
                removeStartButton();
                startCountdown(countDownSeconds);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Player p : players) {
                        p.setPlayerWeather(WeatherType.DOWNFALL);
                        p.setPlayerTime(18000,false);
                        p.getInventory().clear();
                    }
                });
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getPluginManager().registerEvents(this, plugin);
                    for (Player p : humans) {
                        p.teleport(new Location(world, -1004, 85, 2002));
                    }
                    for (Player p : players) {
                        p.setScoreboard(tag4);
                        if (scoreboard.getTeam("tag4Y").hasPlayer(p)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,10000000,4,true,false));
                        } else if (scoreboard.getTeam("tag4B").hasPlayer(p)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,10000000,0,false,false));
                        } else if (scoreboard.getTeam("tag4G").hasPlayer(p)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,10000000,0,false,false));
                        } else if (scoreboard.getTeam("tag4R").hasPlayer(p)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,10000000,1,false,false));
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 0, false, false));
                    }

                }, countDownSeconds * 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player p : players) {
                        p.sendTitle("§a5", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    }
                }, countDownSeconds * 20 + 300);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player p : players) {
                        p.sendTitle("§a4", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    }
                }, countDownSeconds * 20 + 320);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player p : players) {
                        p.sendTitle("§a3", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    }
                }, countDownSeconds * 20 + 340);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player p : players) {
                        p.sendTitle("§a2", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    }
                }, countDownSeconds * 20 + 360);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player p : players) {
                        p.sendTitle("§a1", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    }
                }, countDownSeconds * 20 + 380);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    placeSpectateButton();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "function tag4:go");
                    for (Player p : devils) {
                        p.teleport(new Location(world, -1004, 85, 2002));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 4, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 1, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false));
                        ItemStack skull = new ItemStack(Material.WITHER_SKELETON_SKULL);
                        skull.addEnchantment(Enchantment.BINDING_CURSE,1);
                        p.getInventory().setItem(EquipmentSlot.HEAD, skull);
                        ItemStack chestPlate = new ItemStack(Material.NETHERITE_CHESTPLATE);
                        chestPlate.addEnchantment(Enchantment.BINDING_CURSE,1);
                        ItemMeta chestPlateMeta = chestPlate.getItemMeta().clone();
                        chestPlateMeta.setUnbreakable(true);
                        chestPlate.setItemMeta(chestPlateMeta);
                        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
                        leggings.addEnchantment(Enchantment.BINDING_CURSE,1);
                        ItemMeta leggingsMeta = leggings.getItemMeta().clone();
                        leggingsMeta.setUnbreakable(true);
                        leggings.setItemMeta(leggingsMeta);
                        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
                        boots.addEnchantment(Enchantment.BINDING_CURSE,1);
                        ItemMeta bootsMeta = boots.getItemMeta().clone();
                        bootsMeta.setUnbreakable(true);
                        boots.setItemMeta(bootsMeta);
                        p.getInventory().setItem(EquipmentSlot.CHEST,chestPlate);
                        p.getInventory().setItem(EquipmentSlot.LEGS,leggings);
                        p.getInventory().setItem(EquipmentSlot.FEET,boots);
                    }
                    for (Player p : players) {
                        p.sendTitle("§e游戏开始！", null, 2, 16, 2);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 2f);
                    }

                }, countDownSeconds * 20 + 400);
                taskIds.add(
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                            for (Player p :players) {
                                if (scoreboard.getTeam("tag4E").hasPlayer(p)) {
                                    p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                                } else if (scoreboard.getTeam("tag4L").hasPlayer(p)) {
                                    p.getInventory().addItem(new ItemStack(Material.EMERALD));
                                }
                            }
                }, countDownSeconds * 20 + 400, 40));
                taskIds.add(
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,() -> {
                            for (Player p: humans) {
                                if (p.getInventory().contains(Material.ENDER_EYE)) {
                                    for (Player victim: players) {
                                        victim.sendMessage("§7梅贝尔§f在场，所有鬼发光5秒！");
                                        if (devils.contains(victim)) {
                                            victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,100,0,false,false));
                                        }
                                    }
                                    return;
                                }

                            }
                        },countDownSeconds * 20 + 400 + 600,600));

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
                }, countDownSeconds * 20 + 400, 20));

                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (Player p : players) {
                        p.sendMessage("§a道具已刷新！");
                    }
                    for (Location loc : locations) {
                        double spawnChance = random.nextDouble();
                        if (spawnChance < 0.5) {
                            double spawnNo = random.nextDouble();
                            if (spawnNo < (1f / 52 * 10)) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(feather);
                            } else if (spawnNo < 1f / 52 * 15) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(glass_bottle);
                            } else if (spawnNo < 1f / 52 * 16) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(nether_star);
                            } else if (spawnNo < 1f / 52 * 26) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(clock);
                            } else if (spawnNo < 1f / 52 * 27) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(potion);
                            } else if (spawnNo < 1f / 52 * 37) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(honey_bottle);
                            } else if (spawnNo < 1f / 52 * 42) {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(coal);
                            } else {
                                ((Chest) (world.getBlockAt(loc).getState())).getBlockInventory().addItem(dragon_breath);
                            }
                        }
                    }
                }, countDownSeconds * 20 + 400 + 600, 1200));


                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    long time = getTime(world);
                    if (time - startTime > gameTime) {
                        List<Player> humansCopy = new ArrayList<>(humans);
                        List<Player> playersCopy = new ArrayList<>(players);
                        for (Player p : humansCopy) {
                            spawnFireworks(p);
                        }
                        for (Player p : playersCopy) {
                            p.sendTitle("§e时间到，人类获胜！", null, 5, 50, 5);
                            p.resetPlayerWeather();
                            p.resetPlayerTime();
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                p.teleport(new Location(world, -1003.0, 81, 2021.0));
                                Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p,this));
                            }, 100);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                scoreboard.getTeam("tag4Y").addPlayer(p);
                            }, 101);
                        }
                        endGame();
                        return;
                    }
                    if (humans.size() <= 0) {
                        List<Player> devilsCopy = new ArrayList<>(devils);
                        List<Player> playersCopy = new ArrayList<>(players);
                        for (Player p : devilsCopy) {
                            spawnFireworks(p);
                        }
                        for (Player p : playersCopy) {
                            p.sendTitle("§e无人幸存，鬼获胜！", null, 5, 50, 5);
                            p.resetPlayerWeather();
                            p.resetPlayerTime();
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                p.teleport(new Location(world, -1003.0, 81, 2021.0));
                                Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p,this));
                            }, 100);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                scoreboard.getTeam("tag4Y").addPlayer(p);
                            }, 101);
                        }
                        endGame();
                        return;
                    }
                    if (devils.size() <= 0) {
                        List<Player> humansCopy = new ArrayList<>(humans);
                        List<Player> playersCopy = new ArrayList<>(players);
                        for (Player p : humansCopy) {
                            spawnFireworks(p);
                        }
                        for (Player p : playersCopy) {
                            p.sendTitle("§e鬼不复存在，人类获胜！", null, 5, 50, 5);
                            p.resetPlayerWeather();
                            p.resetPlayerTime();
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                p.teleport(new Location(world, -1003.0, 81, 2021.0));
                                Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p,this));
                            }, 100);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                scoreboard.getTeam("tag4Y").addPlayer(p);
                            }, 101);
                        }
                        endGame();
                        return;
                    }
                    tag4.getObjective("tag4").getScore("剩余人数").setScore(humans.size());
                    tag4.getObjective("tag4").getScore("剩余时间").setScore((int) ((gameTime - (time - startTime)) / 20));
                }, countDownSeconds * 20 + 400, 1));
            }
        };
    }

    public long getTime(World world) {
        return (world.getGameTime());
    }
    public void clearChests() {
        for (Location l : locations) {
            ((Chest)(world.getBlockAt(l).getState())).getBlockInventory().clear();
        }
    }
}

