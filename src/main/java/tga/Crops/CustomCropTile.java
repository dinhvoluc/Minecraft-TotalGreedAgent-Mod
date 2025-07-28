package tga.Crops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CustomCropTile extends BlockEntity {
    public CustomCropTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}