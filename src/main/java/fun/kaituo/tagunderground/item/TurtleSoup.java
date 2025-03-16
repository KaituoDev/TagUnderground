package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class TurtleSoup extends ActiveItem {

    @Override
    public boolean canObtainDirectly() {
        return false;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§a恢复一半最大生命值，并获得隐身！");
        int duration = TagUnderground.inst().getConfig().getInt("characters.FakeTurtle.invisibility-duration");
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
        if (p.getHealth() * 2 < p.getMaxHealth()) {
            p.setHealth(p.getHealth() + p.getMaxHealth() / 2);
        } else {
            p.setHealth(p.getMaxHealth());
        }
        return true;
    }
}
