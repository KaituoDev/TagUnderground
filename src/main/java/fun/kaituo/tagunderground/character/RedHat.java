package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.util.Human;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class RedHat extends Human {
    public static final String displayName = "小红帽";
    public static final String chooseMessage = ".......好了,我们出发吧";
    public static final ChatColor color = ChatColor.RED;

    private final int duration;
    private final int regenerationAmplifier;
    private final int resistanceAmplifier;

    public RedHat(Player p) {
        super(p);
        duration = getConfigInt("duration");
        regenerationAmplifier = getConfigInt("regenerationAmplifier");
        resistanceAmplifier = getConfigInt("resistanceAmplifier");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (!e.getEntity().equals(player)) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        player.sendMessage("获得生命恢复与发光！");
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, regenerationAmplifier));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, false, false));
    }

    @EventHandler
    public void onWither(EntityPotionEffectEvent e) {
        if (!e.getEntity().equals(player)) {
            return;
        }
        PotionEffect newEffect = e.getNewEffect();
        if (newEffect == null) {
            return;
        }
        if (newEffect.getType().equals(PotionEffectType.WITHER)) {
            e.setCancelled(true);
            player.sendMessage("成功免疫凋零效果！");
        }
    }

    @Override
    public void applyPotionEffects() {
        super.applyPotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, resistanceAmplifier, false, false));
    }
}
