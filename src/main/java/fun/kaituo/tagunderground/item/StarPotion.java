package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.util.ActiveItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class StarPotion extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.LEGENDARY;
    }

    @Override
    public boolean use(Player p) {
        p.sendMessage("§c30秒内无敌，30秒后将被强制击倒！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 4));
        taskIds.add(Bukkit.getScheduler().runTaskLater(TagUnderground.inst(), () -> p.setHealth(0), 600).getTaskId());
        return true;
    }
}
