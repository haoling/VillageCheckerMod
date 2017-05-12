package villagechecker;

import io.netty.buffer.ByteBuf;
import net.minecraft.village.Village;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class VillageDataPacket implements IMessage, IMessageHandler<VillageDataPacket, IMessage>
{
    List<Village> lt;

    //デフォルトコンストラクタは必須
    public VillageDataPacket(){}

    public VillageDataPacket(List<Village> lv) {
        this.lt = lv;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ClientProxy.nextVillageData.clear();
        int m = buf.readInt();

        for (int i = 0; i < m; ++i)
        {
            ClientProxy.nextVillageData.add(new VillageData(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(lt.size());

        for (Village aLt : lt) {
            new VillageData(aLt).writeData(buf);
        }
    }

    @Override
    public IMessage onMessage(VillageDataPacket message, MessageContext ctx) {
        return null;
    }
}
