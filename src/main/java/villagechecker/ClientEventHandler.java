package villagechecker;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ClientEventHandler
{
	static Minecraft mc = FMLClientHandler.instance().getClient();

	private KeyBinding key_on  = new KeyBinding("key.VillageCheckerActive", Keyboard.KEY_V, "villagechecker");
	private KeyBinding key_option  = new KeyBinding("key.VillageCheckerSort", Keyboard.KEY_O, "villagechecker");

    public ClientEventHandler()
    {
		ClientRegistry.registerKeyBinding(key_on);
		ClientRegistry.registerKeyBinding(key_option);
    }

	@SubscribeEvent
	public void resetVillageData(EntityJoinWorldEvent event){//次元移動やリログ等でリセット
		if(event.getEntity() == mc.thePlayer){
			ClientProxy.VillageData.clear();
			ClientProxy.show = false;
		}
	}

	@SubscribeEvent
	public void keyTick(InputEvent.KeyInputEvent event){
		keyUpdate();
	}

	@SubscribeEvent
	public void ClickTick(InputEvent.KeyInputEvent event){
		keyUpdate();
	}

	private void keyUpdate(){
		if(key_on.isPressed())
		{
			ClientProxy.show = !ClientProxy.show;

			ssVillageChecker.networkWrapper.sendToServer(new OnPacket());
		}

		if(key_option.isPressed())
		{
			ClientProxy.nextMode();
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event){
		if(event.phase == TickEvent.Phase.END){
			ClientProxy.VillageData.clear();
			ClientProxy.VillageData.addAll(ClientProxy.nextVillageData);
		}
	}

	@SubscribeEvent
	public void RenderVillageList(RenderGameOverlayEvent event){
		ClientProxy.mode mode = ClientProxy.getMode();

		if(!ClientProxy.show) return;

		if(mc.currentScreen != null) return;//何かを開いていたら表示しない

		if(mc.gameSettings.showDebugInfo) return;//F3画面を表示中は表示しない

		VillageData[] villageDatas = ClientProxy.VillageData.toArray(new VillageData[ClientProxy.VillageData.size()]);
		Arrays.sort(villageDatas, new VillageNearComparator(mc.thePlayer));

		int nearVillageCount = 0;
		for (VillageData villageData : villageDatas) {
			if(mc.thePlayer.getDistance(villageData.posX,villageData.posY,villageData.posZ) < 8)
				nearVillageCount++;
			else break;
		}

		mc.fontRendererObj.drawStringWithShadow(I18n.format("dc.VillageChecker1")+nearVillageCount+I18n.format("dc.VillageChecker2"), 10, 10, -1);

		if(mode == ClientProxy.mode.none){
			mc.fontRendererObj.drawStringWithShadow(I18n.format("dc.VillageChecker3"), 10, 20, -1);
		}
		else if(mode == ClientProxy.mode.near){
			mc.fontRendererObj.drawStringWithShadow(I18n.format("dc.VillageChecker4"), 10, 20, -1);
			for (int i = 0;i<villageDatas.length;i++) {
				VillageData villageData = villageDatas[i];
				mc.fontRendererObj.drawStringWithShadow("[" + i + "]" + "[X:" + villageData.posX + "][Y:" + villageData.posY + "][Z:" + villageData.posZ + "][D:" + villageData.numDoors + "][G:" + villageData.numIronGolems + "][R:" + villageData.villageRadius + "]", 10, 30 + i * 10, -1);
				if(i > 12) break;//表示は12個まで
			}
		} else if(mode == ClientProxy.mode.far){
			Arrays.sort(villageDatas,new VillageFarComparator(mc.thePlayer));
			mc.fontRendererObj.drawStringWithShadow(I18n.format("dc.VillageChecker5"), 10, 20, -1);
			for (int i = 0;i<villageDatas.length;i++) {
				VillageData villageData = villageDatas[i];
				mc.fontRendererObj.drawStringWithShadow("["+i+"]"+"[X:"+villageData.posX+"][Y:"+villageData.posY+"][Z:"+villageData.posZ+"][D:"+villageData.numDoors+"][G:"+villageData.numIronGolems+"][R:"+villageData.villageRadius+"]", 10, 30+i*10, -1);
				if(i > 12) break;//表示は12個まで
			}
		} else if(mode == ClientProxy.mode.door){
			Arrays.sort(villageDatas,new VillageDoorComparator());
			mc.fontRendererObj.drawStringWithShadow(I18n.format("dc.VillageChecker6"), 10, 20, -1);
			for (int i = 0;i<villageDatas.length;i++) {
				VillageData villageData = villageDatas[i];
				mc.fontRendererObj.drawStringWithShadow("["+i+"]"+"[X:"+villageData.posX+"][Y:"+villageData.posY+"][Z:"+villageData.posZ+"][D:"+villageData.numDoors+"][G:"+villageData.numIronGolems+"][R:"+villageData.villageRadius+"]", 10, 30+i*10, -1);
				if(i > 12) break;//表示は12個まで
			}
		}
	}

    @SubscribeEvent
    public void RenderVillageChecker(RenderWorldLastEvent event){
		if(ClientProxy.show){
			GL11.glPushMatrix();

			Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer renderer = tessellator.getBuffer();

			{//村の中心を描画
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_CULL_FACE);

				Iterator<VillageData> it = ClientProxy.VillageData.iterator();

				int i = 0;
				while(it.hasNext()) {
					i++;
					GL11.glPushMatrix();

					VillageData village = it.next();

					GL11.glTranslated(
							village.posX - TileEntityRendererDispatcher.staticPlayerX
							, village.posY - TileEntityRendererDispatcher.staticPlayerY
							, village.posZ - TileEntityRendererDispatcher.staticPlayerZ);

					double a = 0.01;
					double b = 0.99;

					Random rnd = new Random(i);
					GL11.glColor4d(rnd.nextInt(100) / 100.0D, rnd.nextInt(100) / 100.0D, rnd.nextInt(100) / 100.0D, 1);

					{//村の中心
                        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
						//上
                        renderer.pos(b, b, b).endVertex();
						renderer.pos(a, b, b).endVertex();
						renderer.pos(a, b, a).endVertex();
						renderer.pos(b, b, a).endVertex();

						//下
						renderer.pos(b, a, b).endVertex();
						renderer.pos(a, a, b).endVertex();
						renderer.pos(a, a, a).endVertex();
						renderer.pos(b, a, a).endVertex();

						//左
						renderer.pos(a, b, b).endVertex();
						renderer.pos(a, b, a).endVertex();
						renderer.pos(a, a, a).endVertex();
						renderer.pos(a, a, b).endVertex();

						//右
						renderer.pos(b, b, b).endVertex();
						renderer.pos(b, b, a).endVertex();
						renderer.pos(b, a, a).endVertex();
						renderer.pos(b, a, b).endVertex();

						//左
						renderer.pos(b, b, a).endVertex();
						renderer.pos(a, b, a).endVertex();
						renderer.pos(a, a, a).endVertex();
						renderer.pos(b, a, a).endVertex();

						//右
						renderer.pos(b, b, b).endVertex();
						renderer.pos(a, b, b).endVertex();
						renderer.pos(a, a, b).endVertex();
						renderer.pos(b, a, b).endVertex();

						tessellator.draw();
					}

					{//ゴーレム
						GL11.glLineWidth(10);

                        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
						renderer.pos(7.8, 3, 7.8).endVertex();
						renderer.pos(-7.8, 3, 7.8).endVertex();
						renderer.pos(-7.8, 3, -7.8).endVertex();
						renderer.pos(7.8, 3, -7.8).endVertex();
						renderer.pos(7.8, 3, 7.8).endVertex();
						tessellator.draw();

                        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
						renderer.pos(7.8, -3, 7.8).endVertex();
						renderer.pos(-7.8, -3, 7.8).endVertex();
						renderer.pos(-7.8, -3, -7.8).endVertex();
						renderer.pos(7.8, -3, -7.8).endVertex();
						renderer.pos(7.8, -3, 7.8).endVertex();
						tessellator.draw();
					}

					{//村の半径
						double aaa = village.villageRadius;
						GL11.glLineWidth(5);

                        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
						for (int y = 0; y < 73; y++) {
							double d0 = Math.cos((double) y * 5 * Math.PI / 180.0D) * aaa;
							double d1 = Math.sin((double) y * 5 * Math.PI / 180.0D) * aaa;

							renderer.pos(d0, 0, d1).endVertex();
						}

						tessellator.draw();
					}

					{//ドアへの線
						for (VillageDataDoor door : village.doors) {
                            renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
							renderer.pos(0.5, 0.5, 0.5).endVertex();
							renderer.pos(door.X - village.posX + 0.5, door.Y - village.posY + 1, door.Z - village.posZ + 0.5).endVertex();
							tessellator.draw();
						}
					}

					GL11.glTranslated(0.5,0.5,0.5);
					GL11.glScaled(0.02, 0.02, 0.02);
					GL11.glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glScaled(-1, -1, 1);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glTranslated(-25,-85,0);

					mc.fontRendererObj.drawStringWithShadow("村人の数" + village.numVillagers, 0, 10, -1);
					mc.fontRendererObj.drawStringWithShadow("湧き時間"+village.noBreedTicks, 0, 20, -1);
					mc.fontRendererObj.drawStringWithShadow("ドアの数"+village.numDoors, 0, 30, -1);
					mc.fontRendererObj.drawStringWithShadow("ゴーレム"+village.numIronGolems, 0, 40, -1);
					mc.fontRendererObj.drawStringWithShadow("村の半径"+village.villageRadius, 0, 50, -1);

					renderer.setTranslation(0, 0, 0);

					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_CULL_FACE);

					GL11.glPopMatrix();
				}
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);

			GL11.glPopMatrix();
			GL11.glLineWidth(1);
		}
	}
}
