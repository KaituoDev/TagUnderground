package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.state.HuntState;
import fun.kaituo.tagunderground.util.Human;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class Eunice extends Human {
    public static final String displayName = "尤妮丝";
    public static final String chooseMessage = "很好，让我们一起守护平等而纯洁的世界吧！";
    public static final ChatColor color = ChatColor.WHITE;

    private final int duration;
    private final int speedAmplifier;
    private final int regenerationAmplifier;

    public Eunice(Player p) {
        super(p);
        duration = getConfigInt("duration");
        speedAmplifier = getConfigInt("speed-amplifier");
        regenerationAmplifier = getConfigInt("regeneration-amplifier");
    }

    @Override
    public boolean castSkill() {
        player.sendMessage("§a获得隐身、速度和生命恢复效果！");
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, speedAmplifier, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, regenerationAmplifier, false, false));
        return true;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!e.getDamager().equals(player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player victim)) {
            return;
        }
        if (!HuntState.INST.getHunters().contains(victim)) {
            return;
        }
        tryCastSkill();
    }
}
