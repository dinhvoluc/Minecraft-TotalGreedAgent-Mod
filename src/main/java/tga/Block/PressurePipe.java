package tga.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.BlockEntity.PressurePipeTile;
import tga.BlockEntity.TankTile;
import tga.Mechanic.IPipeType;
import tga.TGAHelper;
import tga.TotalGreedyAgent;

public class PressurePipe extends Block implements BlockEntityProvider  {
    public PressurePipe(Settings settings) {
        super(settings);
    }

    public static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 4, 2, 14, 8, 14),
            Block.createCuboidShape(0, 8, 0, 16, 16, 16),
            Block.createCuboidShape(4, 0, 4, 12, 4, 12)
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressurePipeTile(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof PressurePipeTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
        }
        return ActionResult.SUCCESS;
    }



    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;
        if (!(world.getBlockEntity(pos) instanceof IPipeType pipe)) return;
        pipe.QueueNext();
    }
}