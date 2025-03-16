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
public class Kelti extends Human {

    public static final String displayName = "克缇";
    public static final String chooseMessage = "嗯，嗯，克缇，记住了哦。请多指教。";
    public static final ChatColor color = ChatColor.BLUE;

    private final int invisibilityDuration;
    private final int skillDuration;

    public Kelti(Player p) {
        super(p);
        skillDuration = getConfigInt("skill-duration");
        invisibilityDuration = getConfigInt("invisibility-duration");
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, invisibilityDuration, 0));
        player.sendMessage("§a获得隐身效果！");
    }

    @Override
    public boolean castSkill() {
        for (Player target : HuntState.INST.getHumans()) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, skillDuration, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, skillDuration, 0));
            target.sendMessage("§a被克缇给予生命恢复和速度效果！");
        }
        player.sendMessage("§a成功给予所有人类生命恢复和速度效果！");
        return true;
    }
}
