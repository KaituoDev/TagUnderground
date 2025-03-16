package fun.kaituo.tagunderground.util;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.state.HuntState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Human extends PlayerData{
    public Human(Player p) {
        super(p);
        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagUnderground.inst(), this::heartbeat, 20, 20));
    }

    @Override
    public void onRejoin() {
        super.onRejoin();
    }

    private void heartbeat() {
        double minDistance = 9999;
        for (Player hunter : HuntState.INST.getHunters()) {
            double distance = hunter.getLocation().distance(player.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        if (minDistance < 10) {
            player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1f, 0f);
            Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> {
                if (player == null) {
                    return;
                }
                player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1f, 0f);
            }, 3);
        }
        if (minDistance < 5) {
            Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> {
                if (player == null) {
                    return;
                }
                player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1f, 0f);
            }, 10);
            Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> {
                if (player == null) {
                    return;
                }
                player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1f, 0f);
            }, 13);
        }
    }


    @EventHandler
    public void noFriendlyFire(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getUniqueId().equals(playerId)) {
            return;
        }
        PlayerData victimData = TagUnderground.inst().idDataMap.get(e.getEntity().getUniqueId());
        if (victimData == null) {
            return;
        }
        if (victimData instanceof Human) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void preventHealthRegain(EntityRegainHealthEvent e) {
        if (!e.getEntity().getUniqueId().equals(player.getUniqueId())) {
            return;
        }
        if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) ||
            e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.EATING)) {
            e.setCancelled(true);
        }
    }
}
