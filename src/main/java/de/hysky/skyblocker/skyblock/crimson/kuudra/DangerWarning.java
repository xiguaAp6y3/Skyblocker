package de.hysky.skyblocker.skyblock.crimson.kuudra;

import java.util.function.Supplier;

import org.apache.commons.lang3.ObjectUtils;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.render.RenderHelper;
import de.hysky.skyblocker.utils.render.title.Title;
import de.hysky.skyblocker.utils.render.title.TitleContainer;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class DangerWarning {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Supplier<MutableText> DANGER_TEXT = () -> Text.translatable("skyblocker.crimson.kuudra.danger");
	private static final Title TITLE = new Title(DANGER_TEXT.get());

	static void init() {
		Scheduler.INSTANCE.scheduleCyclic(DangerWarning::updateIndicator, 5);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> reset());
	}

	private static void updateIndicator() {
		if (Utils.isInKuudra() && SkyblockerConfigManager.get().crimsonIsle.kuudra.dangerWarning && CLIENT.player != null && CLIENT.world != null) {
			BlockPos under = CLIENT.player.getBlockPos().down();
			BlockPos under2 = CLIENT.player.getBlockPos().down(2);
			BlockPos under3 = CLIENT.player.getBlockPos().down(3);
			BlockPos under4 = CLIENT.player.getBlockPos().down(4);
			BlockPos under5 = CLIENT.player.getBlockPos().down(5);

			Title title = ObjectUtils.firstNonNull(getDangerTitle(under), getDangerTitle(under2), getDangerTitle(under3), getDangerTitle(under4), getDangerTitle(under5));

			if (title != null) {
				RenderHelper.displayInTitleContainerAndPlaySound(title);
			} else {
				TitleContainer.removeTitle(TITLE);
			}
		}
	}

	private static Title getDangerTitle(BlockPos pos) {
		BlockState state = CLIENT.world.getBlockState(pos);
		Block block = state.getBlock();

		int argb = switch (block) {
			case Block b when b == Blocks.GREEN_TERRACOTTA -> DyeColor.GREEN.getEntityColor();
			case Block b when b == Blocks.LIME_TERRACOTTA -> DyeColor.LIME.getEntityColor();
			case Block b when b == Blocks.YELLOW_TERRACOTTA -> DyeColor.YELLOW.getEntityColor();
			case Block b when b == Blocks.ORANGE_TERRACOTTA -> DyeColor.ORANGE.getEntityColor();
			case Block b when b == Blocks.PINK_TERRACOTTA -> DyeColor.PINK.getEntityColor();
			case Block b when b == Blocks.RED_TERRACOTTA -> DyeColor.RED.getEntityColor();

			default -> 0;
		};

		return argb != 0 ? TITLE.setText(DANGER_TEXT.get().withColor(argb & 0x00FFFFFF)) : null;
	}

	private static void reset() {
		TitleContainer.removeTitle(TITLE);
	}
}
