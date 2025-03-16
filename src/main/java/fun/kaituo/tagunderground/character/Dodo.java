package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.util.Human;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class Dodo extends Human {
    public static final String displayName = "渡渡";
    public static final String chooseMessage = "哼哼——！看来你的心已被吾辈俘获，是这么回事吧？";
    public static final ChatColor color = ChatColor.GRAY;

    private final int speedDuration;

    public Dodo(Player p) {
        super(p);
        speedDuration = getConfigInt("speed-duration");
    }

    @Override
    public void applyPotionEffects() {
        super.applyPotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (!e.getEntity().equals(player)) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        player.sendMessage("获得加速！");
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedDuration, 1));
    }
}
