package fun.kaituo.tagunderground.util;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.state.HuntState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Corpse{
    public static final int CORPSE_REMAIN_TICKS = 30 * 20;
    private final PlayerData data;
    @Getter
    private final ArmorStand headPart;
    @Getter
    private final ArmorStand mainPart;
    private final Set<Integer> taskIds = new HashSet<>();


    public Corpse(PlayerData data) {
        data.save();
        this.data = data;
        Player p = data.getPlayer();
        Location l = p.getLocation().clone();

        mainPart = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        mainPart.setBasePlate(false);
        mainPart.setInvisible(true);
        mainPart.setVelocity(new Vector(0, -50, 0));

        headPart = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        skullMeta.setOwningPlayer(p);
        headItem.setItemMeta(skullMeta);
        headPart.setBasePlate(false);
        headPart.setSmall(true);
        headPart.getEquipment().setHelmet(headItem);
        headPart.setGravity(false);
        headPart.setCustomName(p.getName());
        headPart.setCustomNameVisible(true);
        headPart.setInvisible(true);
        EulerAngle angle = new EulerAngle(Math.PI, 0, 0);
        headPart.setLeftLegPose(angle);
        headPart.setRightLegPose(angle);

        taskIds.add(Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> {
            mainPart.setGravity(false);
            Location iceLocation = mainPart.getLocation().clone();
            iceLocation.setY(iceLocation.getY() - 1.4);
            mainPart.teleport(iceLocation);
            iceLocation.setY(iceLocation.getY() + 0.75);
            headPart.teleport(iceLocation);
        }, 5).getTaskId());
        taskIds.add(Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> {
            mainPart.getEquipment().setHelmet(new ItemStack(Material.VAULT));
            headPart.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 0));
        }, 6).getTaskId());
        taskIds.add(Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> {
            for (Player player : TagUnderground.inst().getPlayers()) {
                player.sendMessage("§f" + p.getName() + " §c 被逐出了箱庭！");
            }
            destroy();
        }, CORPSE_REMAIN_TICKS).getTaskId());
    }

    public void destroy() {
        if (headPart.isValid()) {
            headPart.remove();
            mainPart.remove();
        }
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    public boolean revive() {
        if (HuntState.INST.isEnded()) {
            return false;
        }
        UUID targetId = data.getPlayerId();
        Player target = Bukkit.getPlayer(targetId);
        if (target == null) {
            return false;
        }
        data.onRejoin();
        TagUnderground.inst().idDataMap.put(targetId, data);
        target.setHealth(data.maxHealth);
        Location reviveLocation = mainPart.getLocation().clone();
        reviveLocation = reviveLocation.add(0, 1.4, 0);
        target.teleport(reviveLocation);
        target.setGameMode(GameMode.ADVENTURE);
        destroy();
        return true;
    }
}
