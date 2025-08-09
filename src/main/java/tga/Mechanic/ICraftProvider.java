package tga.Mechanic;

import net.minecraft.item.ItemStack;

public interface ICraftProvider {
    int GetCraftInputSize();

    ItemStack GetCraftInputStack(int i);

    void SetCraftLeft(ItemStack[] setSlot);
}