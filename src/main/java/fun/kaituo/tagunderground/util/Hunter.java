package fun.kaituo.tagunderground.util;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import fun.kaituo.tagunderground.TagUnderground;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Hunter extends PlayerData{
    public Hunter(Player p) {
        super(p);
    }

    public int getAttackCooldownTicks() {
        return getConfigInt("attack-cooldown-ticks");
    }

    @Override
    public void applyPotionEffects() {
        super.applyPotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 4, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, -1, 0, false, false));
    }

    @EventHandler
    public void preventJumpDuringAttackCooldown(PlayerJumpEvent e) {
        if (!e.getPlayer().getUniqueId().equals(playerId)) {
            return;
        }
        if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getUniqueId().equals(playerId)) {
            Bukkit.broadcastMessage("1");
            return;
        }
        PlayerData victimData = TagUnderground.inst().idDataMap.get(e.getEntity().getUniqueId());
        if (victimData == null) {
            Bukkit.broadcastMessage("2");
            return;
        }
        if (victimData instanceof Hunter) {
            Bukkit.broadcastMessage("3");
            e.setCancelled(true);
        } else if (victimData instanceof Human) {

            Bukkit.broadcastMessage("4");
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, getAttackCooldownTicks(), 99));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, getAttackCooldownTicks(), 4));
        }
    }
}
