package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class HeartbeatWine extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§c回复一半最大生命值，获得与回复量相同的发光时长！");
        if (p.getHealth() * 2 < p.getMaxHealth()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) (p.getMaxHealth() * 10), 0));
            p.setHealth(p.getHealth() + p.getMaxHealth() / 2);
        } else {
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) ((p.getMaxHealth() - p.getHealth()) * 20), 0));
            p.setHealth(p.getMaxHealth());
        }
        return true;
    }
}
