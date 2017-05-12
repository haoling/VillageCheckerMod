package villagechecker;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by yu on 2015/05/22.
 * クライアント -> サーバー
 * 有効無効の切り替え
 */
public class OnPacket implements IMessage, IMessageHandler<OnPacket, IMessage> {
    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(OnPacket message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;

        if(CommonProxy.enablePlayers.contains(player))
        {
            CommonProxy.enablePlayers.remove(player);
        }
        else
        {
            CommonProxy.enablePlayers.add(player);
        }

        return null;
    }
}
