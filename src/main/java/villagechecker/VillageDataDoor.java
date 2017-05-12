package villagechecker;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class VillageDataDoor {
	public int X;
	public int Y;
	public int Z;
	public int IDX;
	public int IDZ;

	public VillageDataDoor(NBTTagCompound nbt)
	{
		X = nbt.getInteger("X");
		Y = nbt.getInteger("Y");
		Z = nbt.getInteger("Z");
		IDX = nbt.getInteger("IDX");
		IDZ = nbt.getInteger("IDZ");
	}

	public VillageDataDoor(ByteBuf dis)
	{
		X = dis.readInt();
		Y = dis.readInt();
		Z = dis.readInt();
		IDX = dis.readInt();
		IDZ = dis.readInt();
	}

	public void writeData(ByteBuf dos)
	{
		dos.writeInt(X);
		dos.writeInt(Y);
		dos.writeInt(Z);
		dos.writeInt(IDX);
		dos.writeInt(IDZ);
	}
}
