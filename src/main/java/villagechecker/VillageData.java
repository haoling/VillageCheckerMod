package villagechecker;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.Village;

public class VillageData {
	//中心座標
	public int posX;
	public int posY;
	public int posZ;

	public int numDoors;//ドアの数

	public List<VillageDataDoor> doors;

	public int numVillagers;//村人の数
	public int numIronGolems;//ゴーレムの数

	public int villageRadius;//村の半径
	public int noBreedTicks;//不明
	public int ticks;//不明

	public VillageData(Village v)
	{
		NBTTagCompound nbt = new NBTTagCompound();

		v.writeVillageDataToNBT(nbt);

		posX = nbt.getInteger("CX");
		posY = nbt.getInteger("CY");
		posZ = nbt.getInteger("CZ");

		NBTTagList nbttaglist = nbt.getTagList("Doors", 10);

		numDoors = nbttaglist.tagCount();
		doors = new ArrayList<VillageDataDoor>(numDoors);

		for (int i = 0; i < numDoors; ++i)
		{
			NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
			doors.add(new VillageDataDoor(nbt1));
		}

		villageRadius = nbt.getInteger("Radius");
		numVillagers = nbt.getInteger("PopSize");
		numIronGolems = nbt.getInteger("Golems");
		noBreedTicks = nbt.getInteger("MTick");
		ticks = nbt.getInteger("Tick");
	}

	public VillageData(ByteBuf dis)
	{
		posX = dis.readInt();
		posY = dis.readInt();
		posZ = dis.readInt();
		numDoors = dis.readInt();

		doors = new ArrayList<VillageDataDoor>(numDoors);
		for (int i = 0; i < numDoors; ++i)
		{
			doors.add(new VillageDataDoor(dis));
		}

		villageRadius = dis.readInt();
		numVillagers = dis.readInt();
		numIronGolems = dis.readInt();
		noBreedTicks = dis.readInt();
		ticks = dis.readInt();
	}

	public void writeData(ByteBuf dos)
	{
	    dos.writeInt(posX);
	    dos.writeInt(posY);
		dos.writeInt(posZ);
		dos.writeInt(numDoors);

		for (int i = 0; i < numDoors; ++i)
		{
			doors.get(i).writeData(dos);
		}

		dos.writeInt(villageRadius);
		dos.writeInt(numVillagers);
		dos.writeInt(numIronGolems);
		dos.writeInt(noBreedTicks);
		dos.writeInt(ticks);
	}
}
