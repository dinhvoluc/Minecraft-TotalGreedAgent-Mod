package tga.ExSlots;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CheckInsertSlot extends Slot {
    private final SidedInventory Target;
    public CheckInsertSlot(SidedInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        Target = inventory;
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        return Target.canInsert(getIndex(), stack, null);
    }
}