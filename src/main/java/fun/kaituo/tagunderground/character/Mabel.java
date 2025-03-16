package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.state.HuntState;
import fun.kaituo.tagunderground.util.Human;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class Mabel extends Human {
    public static final String displayName = "梅贝尔";
    public static final String chooseMessage = "真的可以么？不要后悔哟~";
    public static final ChatColor color = ChatColor.GRAY;

    private final int duration;

    public Mabel(Player p) {
        super(p);
        duration = getConfigInt("duration");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!e.getEntity().equals(player)) {
            return;
        }
        if (!(e.getDamager() instanceof Player damager)) {
            return;
        }
        if (!HuntState.INST.getHunters().contains(damager)) {
            return;
        }
        damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
        player.sendMessage("§a成功给予攻击者失明效果！");
        damager.sendMessage("§c被梅贝尔给予失明效果！");
    }

    @Override
    public boolean castSkill() {
        for (Player target : HuntState.INST.getHunters()) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
            target.sendMessage("§c被梅贝尔给予失明和发光效果！");
        }
        player.sendMessage("§a成功给予所有鬼失明和发光效果！");
        return true;
    }
}
