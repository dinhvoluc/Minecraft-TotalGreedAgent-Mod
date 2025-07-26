package tga.Screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public abstract class BasicScreenBase extends ScreenHandler {
    protected final PlayerEntity TargetPlayer;
    public BasicScreenBase(ScreenHandlerType<?> type, int syncId, PlayerEntity player)
    {
        super(type, syncId);
        TargetPlayer = player; int startX = 8;
        int startY = 84;
        Inventory playerInventory = player.getInventory();
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
        // ホットバー
        for (int i = 0; i < 9; ++i) addSlot(new Slot(playerInventory, i, startX + i * 18, startY + 58));
    }

    @Override
    public abstract ItemStack quickMove(PlayerEntity player, int slot);

    @Override
    public abstract boolean canUse(PlayerEntity player);
}
