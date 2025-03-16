package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class DodoRunFaster extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public boolean canObtainDirectly() {
        return false;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§b获得加速！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
        return true;
    }
}
