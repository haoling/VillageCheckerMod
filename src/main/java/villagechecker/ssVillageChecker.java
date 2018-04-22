package villagechecker;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid= "ssvillagevhecker", name="show village mod")
public class ssVillageChecker {
    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("ssVC");

	@SidedProxy(clientSide = "villagechecker.ClientProxy", serverSide = "villagechecker.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance("ssVillageChecker")
	public static ssVillageChecker instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
        networkWrapper.registerMessage(OnPacket.class, OnPacket.class, 0, Side.SERVER);
        networkWrapper.registerMessage(VillageDataPacket.class, VillageDataPacket.class, 1, Side.CLIENT);

		FMLCommonHandler.instance().bus().register(new CommonTicEventHandler());

		proxy.register();
	}
}
