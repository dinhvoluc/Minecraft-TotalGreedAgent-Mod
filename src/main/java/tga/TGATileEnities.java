package tga;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import tga.BlockEntity.BoxStackTile;
import tga.BlockEntity.TankTile;
import tga.Machines.ManCrackerTile;

public class TGATileEnities {
    public static BlockEntityType<BoxStackTile> BOX_STACK_TILE;
    public static BlockEntityType<TankTile> TANK_TILE;
    public static BlockEntityType<ManCrackerTile> M_CRACKER_LV0;

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, TotalGreedyAgent.GetID(name), FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void Load(boolean isClientSide) {
        BOX_STACK_TILE = register("boxstacktile", BoxStackTile::new, TGABlocks.BOX_WOOD, TGABlocks.BOX_WOOD_FILLED, TGABlocks.BOX_COPPER, TGABlocks.BOX_COPPER_FILLED);
        TANK_TILE = register("tanktile", TankTile::new, TGABlocks.TANK_WOOD, TGABlocks.TANK_WOOD_FILLED, TGABlocks.TANK_COPPER, TGABlocks.TANK_COPPER_FILLED);
        M_CRACKER_LV0 = register("mt_cracker", ManCrackerTile::new, TGABlocks.MAN_CRACKER);

        FluidStorage.SIDED.registerForBlockEntity((a, b) -> a.InnerTank, TANK_TILE);
    }
}