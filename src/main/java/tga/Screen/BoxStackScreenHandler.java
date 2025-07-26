package tga.Screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import tga.BlockEntity.BoxStackTile;

public class BoxStackScreenHandler extends BasicScreenBase {
    private final BoxStackTile TargetTile;

    public BoxStackScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerEntity player, BoxStackTile target) {
        super(type, syncId, player);
        TargetTile = target;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return TargetTile != null && TargetTile.canPlayerUse(player);
    }
}