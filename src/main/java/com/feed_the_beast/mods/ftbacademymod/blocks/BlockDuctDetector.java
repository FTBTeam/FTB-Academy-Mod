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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
			public final NBTTagCompound nbt;

			public FilterItem(String i, int d, @Nullable NBTTagCompound n)
			{
				id = i;
				damage = d;
				nbt = n;
			}

			public FilterItem(String i)
			{
				this(i, 0, null);
			}

			public FilterItem(IForgeRegistryEntry entry)
			{
				this(entry.getRegistryName().toString());
			}

			@Override
			public int hashCode()
			{
				return Objects.hash(id, damage, nbt);
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
					return damage == i.damage && id.equals(i.id) && Objects.equals(nbt, i.nbt);
				}

				return false;
			}
		}

		public static class FilterVariant
		{
			public static final Map<String, FilterVariant> VARIANT_MAP = new HashMap<>();

			static
			{
				VARIANT_MAP.put("first", new FilterVariant(25, new FilterItem(Blocks.DIRT), new FilterItem(Blocks.IRON_ORE), new FilterItem(Blocks.GOLD_ORE), new FilterItem(Blocks.DIAMOND_ORE)));
				VARIANT_MAP.put("second", new FilterVariant(24, new FilterItem(Blocks.DIRT)));
				VARIANT_MAP.put("third", new FilterVariant(24, new FilterItem(Blocks.IRON_ORE), new FilterItem(Blocks.GOLD_ORE), new FilterItem(Blocks.DIAMOND_ORE)));
			}

			public final int flags;
			public final HashSet<FilterItem> items;

			public FilterVariant(int f, FilterItem... i)
			{
				flags = f;
				items = new HashSet<>(Arrays.asList(i));
			}
		}

		public int distance = 0;
		public int id = 0;
		public UUID player = new UUID(0, 0);
		public FilterVariant variant = null;

		@Override
		public void update()
		{
			if (variant != null && !world.isRemote && world.getTotalWorldTime() % 20L == 0L)
			{
				TileEntity tileEntity = world.getTileEntity(pos.offset(EnumFacing.UP, distance));

				if (tileEntity != null && DUCT_ID.equals(TileEntity.getKey(tileEntity.getClass())))
				{
					QuestObject object = ServerQuestFile.INSTANCE.get(id);
					ITeamData data = ServerQuestFile.INSTANCE.getData(FTBLibAPI.getTeam(player));

					if (object != null && data != null)
					{
						NBTTagList attachments = tileEntity.writeToNBT(new NBTTagCompound()).getTagList("Attachments", Constants.NBT.TAG_COMPOUND);

						if (attachments.tagCount() == 1)
						{
							NBTTagCompound nbt = attachments.getCompoundTagAt(0);

							if (nbt.getInteger("Flags") == variant.flags)
							{
								HashSet<FilterItem> inv = new HashSet<>();

								NBTTagList invList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

								for (int i = 0; i < invList.tagCount(); i++)
								{
									NBTTagCompound nbt1 = invList.getCompoundTagAt(i);
									inv.add(new FilterItem(nbt1.getString("id"), nbt1.getShort("Damage"), (NBTTagCompound) nbt1.getTag("tag")));
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