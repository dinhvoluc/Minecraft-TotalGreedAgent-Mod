package tga;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import tga.BlockEntity.*;
import tga.Mechanic.PipeManager;
import tga.Str.PipeProperty;

public class TGATileEnities {
    public static BlockEntityType<BoxStackTile> BOX_STACK_TILE;
    public static BlockEntityType<TankTile> TANK_TILE;
    public static BlockEntityType<ManCrackerTile> M_CRACKER_LV0;
    public static BlockEntityType<MetalWorkbenchTile> M_METAL_WORKBENCH;
    public static BlockEntityType<FluidInside> FLUID_INSIDE;
    public static BlockEntityType<PipeBaseEnity> PIPE_ENITY;

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, TotalGreedyAgent.GetID(name), FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void Load(boolean isClientSide) {
        //Fluid
        TGAShared.VARIANT_WATER = FluidVariant.of(Fluids.WATER);
        FLUID_INSIDE = register("havefluid", FluidInside::new,
                TGABlocks.JRK_PUMP);
        //Pipe
        PipeBaseEnity.PIPE_SHARED_INFO.put(TGAID.ID_PIPE_BRONZE, new PipeProperty(1000, 800, 20));
        PipeBaseEnity.PIPE_SHARED_INFO.put(TGAID.ID_PIPE_STEEL, new PipeProperty(2000, 1600, 40));
        PIPE_ENITY = register("pipe", PipeBaseEnity::new,
                TGABlocks.PIPE_BRONZE, TGABlocks.PIPE_STEEL);
        BOX_STACK_TILE = register("boxstacktile", BoxStackTile::new,
                TGABlocks.BOX_WOOD, TGABlocks.BOX_WOOD_FILLED,
                TGABlocks.BOX_COPPER, TGABlocks.BOX_COPPER_FILLED,
                TGABlocks.BOX_BRONZE, TGABlocks.BOX_BRONZE_FILLED);
        TANK_TILE = register("tanktile", TankTile::new,
                TGABlocks.TANK_WOOD, TGABlocks.TANK_WOOD_FILLED,
                TGABlocks.TANK_COPPER, TGABlocks.TANK_COPPER_FILLED,
                TGABlocks.TANK_BRONZE, TGABlocks.TANK_BRONZE_FILLED);
        M_CRACKER_LV0 = register("mt_cracker", ManCrackerTile::new, TGABlocks.MAN_CRACKER);
        M_METAL_WORKBENCH = register("mt_metalwb", MetalWorkbenchTile::new, TGABlocks.METAL_WORKBENCH);

        FluidStorage.SIDED.registerForBlockEntity((a, b) -> a.InnerTank, TANK_TILE);
        FluidStorage.SIDED.registerForBlockEntity((a, b) -> a.InnerTank, M_METAL_WORKBENCH);
        FluidStorage.SIDED.registerForBlockEntity((a,b)-> a.Buffer, PIPE_ENITY);
        //Manager
        if (isClientSide) return;
        PipeManager.INTANCE = new PipeManager();
        ServerTickEvents.END_SERVER_TICK.register(server -> PipeManager.INTANCE.ServerTick());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> PipeManager.INTANCE.ClearQueue());
    }
}