package villagechecker;

import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;

/**
 * Created by yu on 2015/05/23.
 * 村をプレイヤーから近い順に並び替える
 */
public class VillageNearComparator implements Comparator {
    EntityPlayer player;

    public VillageNearComparator(EntityPlayer player){
        this.player = player;
    }

    @Override
    public int compare(Object o1, Object o2) {
        VillageData v1 = (VillageData)o1;
        VillageData v2 = (VillageData)o2;
        double d1 = player.getDistance(v1.posX,v1.posY, v1.posZ);
        double d2 = player.getDistance(v2.posX,v2.posY, v2.posZ);

        return (int)Math.signum(d1 - d2);
    }
}
