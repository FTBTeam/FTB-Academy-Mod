package com.feed_the_beast.mods.ftbacademymod.blocks;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftbquests.net.edit.MessageChangeProgressResponse;
import com.feed_the_beast.ftbquests.quest.EnumChangeProgress;
import com.feed_the_beast.ftbquests.quest.ITeamData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class BlockDuctDetector extends BlockDetectorBase
{
	public static final ResourceLocation DUCT_ID = new ResourceLocation("thermaldynamics:duct_item_transparent");

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new Entity();
	}

	public static class Entity extends TileEntity implements ITickable
	{
		public static class FilterItem
		{
			public final String id;
			public final int damage;

			public FilterItem(String i, int d)
			{
				id = i;
				damage = d;
			}

			public FilterItem(String i)
			{
				this(i, 0);
			}

			public FilterItem(IForgeRegistryEntry entry)
			{
				this(entry.getRegistryName().toString());
			}

			@Override
			public int hashCode()
			{
				return id.hashCode() + damage;
			}

			@Override
			public boolean equals(Object obj)
			{
				if (obj == this)
				{
					return true;
				}
				else if (obj instanceof FilterItem)
				{
					FilterItem i = (FilterItem) obj;
					return damage == i.damage && id.equals(i.id);
				}

				return false;
			}
		}

		public static class FilterVariant
		{
			public static final Map<String, FilterVariant> VARIANT_MAP = new HashMap<>();

			static
			{
				VARIANT_MAP.put("first", new FilterVariant("first", 25, new FilterItem(Blocks.DIRT), new FilterItem(Blocks.IRON_ORE), new FilterItem(Blocks.GOLD_ORE), new FilterItem(Blocks.DIAMOND_ORE)));
				VARIANT_MAP.put("second", new FilterVariant("second", 24, new FilterItem(Blocks.DIRT)));
				VARIANT_MAP.put("third", new FilterVariant("third", 24, new FilterItem(Blocks.IRON_ORE), new FilterItem(Blocks.GOLD_ORE), new FilterItem(Blocks.DIAMOND_ORE)));
			}

			public final String name;
			public final int flags;
			public final HashSet<FilterItem> items;

			public FilterVariant(String n, int f, FilterItem... i)
			{
				name = n;
				flags = f;
				items = new HashSet<>(Arrays.asList(i));
			}
		}

		public int distance = 0;
		public int id = 0;
		public UUID player = new UUID(0, 0);
		public FilterVariant variant = null;

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound)
		{
			compound.setInteger("dist", distance);
			compound.setInteger("object", id);
			compound.setUniqueId("player", player);

			if (variant != null)
			{
				compound.setString("variant", variant.name);
			}

			return super.writeToNBT(compound);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound)
		{
			distance = compound.getInteger("dist");
			id = compound.getInteger("object");
			player = compound.getUniqueId("player");
			variant = FilterVariant.VARIANT_MAP.get(compound.getString("variant"));
			super.readFromNBT(compound);
		}

		@Override
		public void update()
		{
			if (variant != null && !world.isRemote && world.getTotalWorldTime() % 20L == 0L)
			{
				QuestObject object = ServerQuestFile.INSTANCE.get(id);
				ITeamData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

				if (object != null && data != null)
				{
					TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP, distance));

					if (tileEntity != null && DUCT_ID.equals(TileEntity.getKey(tileEntity.getClass())))
					{
						NBTTagList attachments = tileEntity.writeToNBT(new NBTTagCompound()).getTagList("Attachments", Constants.NBT.TAG_COMPOUND);

						if (attachments.tagCount() == 1)
						{
							NBTTagCompound nbt = attachments.getCompoundTagAt(0);

							if (nbt.getByte("Flags") == variant.flags)
							{
								HashSet<FilterItem> inv = new HashSet<>();

								NBTTagList invList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

								for (int i = 0; i < invList.tagCount(); i++)
								{
									NBTTagCompound nbt1 = invList.getCompoundTagAt(i);
									inv.add(new FilterItem(nbt1.getString("id"), nbt1.getShort("Damage")));
								}

								if (variant.items.equals(inv))
								{
									EnumChangeProgress.sendUpdates = false;
									object.changeProgress(data, EnumChangeProgress.COMPLETE);
									EnumChangeProgress.sendUpdates = true;
									new MessageChangeProgressResponse(data.getTeamUID(), object.id, EnumChangeProgress.COMPLETE).sendToAll();
									world.setBlockToAir(pos);
								}
							}
						}
					}
				}
			}
		}
	}
}