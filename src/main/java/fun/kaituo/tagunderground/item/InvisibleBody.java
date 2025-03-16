package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class InvisibleBody extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§c隐身10秒！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false));
        return true;
    }
}
