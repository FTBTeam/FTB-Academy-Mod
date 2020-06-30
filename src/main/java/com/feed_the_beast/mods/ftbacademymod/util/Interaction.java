package com.feed_the_beast.mods.ftbacademymod.util;

import com.feed_the_beast.ftbquests.FTBQuests;
import com.feed_the_beast.ftbquests.quest.QuestFile;
import dev.latvian.kubejs.block.BlockRightClickEventJS;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.text.Text;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class Interaction
{
	private static final List<Interaction> LIST = new ArrayList<>();

	public interface Action
	{
		boolean onEvent(BlockRightClickEventJS event);
	}

	@Nullable
	public static Interaction find(BlockRightClickEventJS event)
	{
		for (Interaction i : LIST)
		{
			if ((i.block == null || i.block.check(event.getBlock())) && (i.item == null || i.item.test(event.getItem())))
			{
				return i;
			}
		}

		return null;
	}

	public static void init()
	{
		// Custom interactions where action(event) is fired when you right-click on block with optional item (Have to be first)
		add().block("minecraft:chest").action(event -> {
			QuestFile file = FTBQuests.PROXY.getQuestFile(event.getPlayer().minecraftPlayer.world);
			file.getData(event.getPlayer().minecraftPlayer).getTaskData(file.getTask(0xf126973c)).addProgress(1L);

			event.removeGameStage("storage_7");
			event.addGameStage("storage_8");
			return true;
		});

		add().item("minecraft:water_bucket").block("botania:altar").action(event -> {
			event.removeGameStage("b_water");
			event.addGameStage("b1");
			return true;
		});

		add().block("minecraft:lever").action(event -> {
			if (event.hasGameStage("auto_lever"))
			{
				if (event.getBlock().getProperties().get("powered").equals("false"))
				{
					event.removeGameStage("auto_lever");
					event.getPlayer().give("thermalfoundation:wrench");
					event.addGameStage("auto_5");
					return true;
				}

				return false;
			}

			event.getPlayer().setStatusMessage(Text.of("Can't flip the lever, until you have completed the Filtering quest.").red());
			return false;
		});

		add().item("thermalfoundation:wrench").action(event -> !event.getPlayer().isSneaking() && new BlockIDPredicate("thermalexpansion:tank").check(event.getBlock()));

		add().block("draconicevolution:placed_item").action(event -> {
			if (event.getServer() != null)
			{
				event.getPlayer().tell(Text.of("You've found an easter egg!").green());
				event.getPlayer().addXPLevels(5);
				event.getBlock().set("minecraft:air");
				event.getServer().schedule(1000, event.getPlayer(), callback -> event.getPlayer().getInventory().clear(itemOnly("quark:parrot_egg")));
			}
			return false;
		});

		add().block("botania:spreader").itemIDOnly("botania:twigwand").action(event -> {
			QuestFile file = FTBQuests.PROXY.getQuestFile(event.getPlayer().minecraftPlayer.world);

			if (event.getPlayer().isSneaking() && !file.getData(event.getPlayer().minecraftPlayer).getTaskData(file.getTask(0x532efd73)).isComplete())
			{
				event.addGameStage("school_clicked_spreader");
				event.removeGameStage("b2");
				return true;
			}

			return false;
		});

		add().block("botania:pool").itemIDOnly("botania:twigwand").action(event -> {
			if (event.getPlayer().isSneaking() && event.hasGameStage("school_clicked_spreader"))
			{
				event.removeGameStage("school_clicked_spreader");
				return true;
			}

			return false;
		});


		//Allow placing down blocks or right-clicking on blocks with items
		add().item("actuallyadditions:block_coal_generator").block("minecraft:iron_block");
		add().item("thermaldynamics:duct_0").block("minecraft:wool");
		add().item("minecraft:crafting_table").block("minecraft:wool");
		add().item("astralsorcery:blockaltar").block("minecraft:quartz_block");
		add().itemIDOnly("botania:specialflower").block("minecraft:grass");
		add().item("minecraft:bucket").block("thermalexpansion:tank");
		add().item("minecraft:water_bucket").block("thermalexpansion:tank");
		add().itemIDOnly("thermalexpansion:machine").block("minecraft:lapis_block");

		//Allow right-clicking on blocks (Have to be last, because they ignore item, and some others require item)
		add().block("minecraft:chest");
		add().block("minecraft:trapped_chest");
		add().block("ironchest:iron_chest");
		add().block("actuallyadditions:block_giant_chest");
		add().block("storagedrawers:compdrawers");
		add().block("storagedrawers:basicdrawers");
		add().block("refinedstorage:grid");
		add().block("refinedstorage:disk_drive");
		add().block("thermaldynamics:duct_32");
		add().block("thermaldynamics:duct_16");
		add().block("actuallyadditions:block_coal_generator");
		add().block("thermalexpansion:machine");
		add().block("minecraft:crafting_table");
		add().block("astralsorcery:blockaltar");

		add().item("minecraft:water_bucket").action(event -> false);
	}

	public static Interaction add()
	{
		Interaction i = new Interaction();
		LIST.add(i);
		return i;
	}

	public BlockPredicate block;
	public IngredientJS item;
	public Action action;

	public Interaction block(BlockPredicate p)
	{
		block = p;
		return this;
	}

	public Interaction block(String id)
	{
		return block(new BlockIDPredicate(id));
	}

	public Interaction item(IngredientJS i)
	{
		item = i;
		return this;
	}

	public Interaction item(String id)
	{
		return item(ItemStackJS.of(id));
	}

	public static IngredientJS itemOnly(String id)
	{
		Item item = Item.getByNameOrId(id);
		return stack -> stack.getItem() == item;
	}

	public Interaction itemIDOnly(String id)
	{
		return item(itemOnly(id));
	}

	public Interaction action(Action a)
	{
		action = a;
		return this;
	}
}
