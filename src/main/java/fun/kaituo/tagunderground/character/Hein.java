package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.state.HuntState;
import fun.kaituo.tagunderground.util.Hunter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class Hein extends Hunter {
    public static final String displayName = "海因";
    public static final String chooseMessage = "这里满溢着大量的黑之魂！太棒了！";
    public static final ChatColor color = ChatColor.DARK_GRAY;

    private final int witherDuration;

    public Hein(Player p) {
        super(p);
        witherDuration = getConfigInt("wither-duration");
    }

    @Override
    public void applyPotionEffects() {
        super.applyPotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, -1, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e) {
        if (!(e.getEntity() instanceof Player victim)) {
            return;
        }
        if (!HuntState.INST.getHumans().contains(victim)) {
            return;
        }
        PotionEffect newEffect = e.getNewEffect();
        if (e.getNewEffect() == null) {
            return;
        }
        if (!e.getNewEffect().getType().equals(PotionEffectType.GLOWING)) {
            return;
        }
        victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDuration, 0));
        victim.sendMessage("§c海因使你获得凋零效果！");
    }
}
