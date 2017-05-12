package villagechecker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class CommonTicEventHandler {
    public static ArrayList<EntityPlayer> lastPlayers = new ArrayList<EntityPlayer>();

    private int time = 0;

    @SubscribeEvent
    public void offVillageChecker(EntityJoinWorldEvent event) {//次元移動やリログ等でリセット
        if (event.entity instanceof EntityPlayer) {
            CommonProxy.enablePlayers.remove(event.entity);
        }
    }

    @SubscribeEvent
    public void removePlayer(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if(time > 0)
                time--;

            if (time == 0) {
                time = 20;

                Iterator it = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
                while (it.hasNext()) {
                    EntityPlayer player = (EntityPlayer) it.next();
                    if (CommonProxy.enablePlayers.contains(player)) {
                        ssVillageChecker.networkWrapper.sendTo(new VillageDataPacket(player.worldObj.villageCollectionObj.getVillageList()), (EntityPlayerMP) player);
                        lastPlayers.add(player);
                    }
                }

                CommonProxy.enablePlayers.clear();
                CommonProxy.enablePlayers.addAll(lastPlayers);
                lastPlayers.clear();
            }
        }
    }
}
