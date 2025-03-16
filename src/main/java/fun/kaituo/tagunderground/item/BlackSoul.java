package fun.kaituo.tagunderground.item;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.character.Norden;
import fun.kaituo.tagunderground.character.Victoria;
import fun.kaituo.tagunderground.util.ActiveItem;
import fun.kaituo.tagunderground.util.PlayerData;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class BlackSoul extends ActiveItem {
    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public boolean use(Player p) {
        PlayerData data = TagUnderground.inst().idDataMap.get(p.getUniqueId());
        assert data != null;
        if (data.getClass().equals(Norden.class) || data.getClass().equals(Victoria.class)) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage("§c生命全部恢复！");
            return true;
        }
        if (p.getMaxHealth() > 3) {
            p.sendMessage("§c最大生命值减少，生命全部恢复！");
            p.setMaxHealth(p.getMaxHealth() - 3);
            p.setHealth(p.getMaxHealth());
            return true;
        }
        p.sendMessage("§c生命上限过低，无法使用！");
        return false;
    }

}
