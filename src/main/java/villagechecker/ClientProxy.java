package villagechecker;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy{
	public static boolean show = false;
	private static mode currentMode = mode.none;
	public static List<VillageData> VillageData = new ArrayList<VillageData>();

	public static List<VillageData> nextVillageData = new ArrayList<VillageData>();

	public static void nextMode(){
		if(currentMode == mode.none)currentMode = mode.near;
		else if(currentMode == mode.near)currentMode = mode.far;
		else if(currentMode == mode.far)currentMode = mode.door;
		else if(currentMode == mode.door)currentMode = mode.none;
	}

	public static mode getMode(){
		return currentMode;
	}

	@Override
	public void register(){
		ClientEventHandler event = new ClientEventHandler();
		FMLCommonHandler.instance().bus().register(event);//キー用のTickイベントでの追加
		MinecraftForge.EVENT_BUS.register(event);//描画系での登録
	}

	enum mode {none,near,far,door}
}
