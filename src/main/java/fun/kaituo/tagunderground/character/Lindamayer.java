package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.state.HuntState;
import fun.kaituo.tagunderground.util.Hunter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class Lindamayer extends Hunter {
    public static final String displayName = "琳达梅尔";
    public static final String chooseMessage = "我要重建新的黒之裁判，将盘踞于大地之上的罪人处刑！";
    public static final ChatColor color = ChatColor.DARK_GRAY;

    private final int glowingDuration;

    public Lindamayer(Player p) {
        super(p);
        glowingDuration = getConfigInt("glowing-duration");
    }

    @Override
    public boolean castSkill() {
        for (Player victim: HuntState.INST.getHumans()) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, glowingDuration, 0));
        }
        player.sendMessage("§a使全部人类发光！");
        return true;
    }
}
