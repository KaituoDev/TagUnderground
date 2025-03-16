package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class SerpentGodBlood extends ActiveItem {
    @Override
    public boolean use(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 300, 4));
        p.sendMessage("§c获得15秒无敌！");
        return true;
    }

    @Override
    public boolean canObtainDirectly() {
        return false;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }
}
