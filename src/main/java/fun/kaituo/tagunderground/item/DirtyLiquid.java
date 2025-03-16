package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class DirtyLiquid extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }
    @Override
    public boolean use(Player p) {
        p.sendMessage("§a获得生命恢复和§c反胃效果！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,  150, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,  300, 0));
        return true;
    }
}
