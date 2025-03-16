package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.character.Bill;
import fun.kaituo.tagunderground.character.Dodo;
import fun.kaituo.tagunderground.util.ActiveItem;
import fun.kaituo.tagunderground.util.PlayerData;
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
        PlayerData data = TagUnderground.inst().idDataMap.get(p.getUniqueId());
        assert data != null;
        if (data.getClass().equals(Bill.class) || data.getClass().equals(Dodo.class)) {
            p.sendMessage("§c你不能使用这个道具！");
            return false;
        }
        p.sendMessage("§a获得生命恢复和§c反胃效果！");
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,  150, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,  300, 0));
        return true;
    }
}
