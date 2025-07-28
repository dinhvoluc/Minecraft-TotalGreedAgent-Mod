package tga.Crops;

public class CustomCropBlock {
    public static final String MCID_GUAYULE = "guayule";
}

//public class CustomCropBlock extends BlockWithEntity {
//    public static final VoxelShape[] SHAPES;
//    static {
//        SHAPES = Block.createShapeArray(7, (age) -> Block.createColumnShape((double)16.0F, (double)0.0F, (double)(2 + age * 2)));
//    }
//    public CustomCropBlock() {
//
//    }
//
//    @Override
//    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
//        return null;
//    }
//
//    @Override
//    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//       if (world.isClient) return null;
//       BlockEntity be = world.getBlockEntity()
//       return new   new BlockEntityTicker<CustomCropBlock>() {
//           @Override
//           public void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
//
//           }
//       }
//        return world.isClient ? null : validateTicker(givenType, expectedType, (worldx, pos, state, blockEntity) -> AbstractFurnaceBlockEntity.tick(serverWorld, pos, state, blockEntity)) super.getTicker(world, state, type);
//    }
//
//    @Nullable
//    protected static <T extends BlockEntity> BlockEntityTicker<T> validateTicker(World world, BlockEntityType<T> givenType, BlockEntityType<? extends AbstractFurnaceBlockEntity> expectedType) {
//        BlockEntityTicker var10000;
//        if (world instanceof ServerWorld serverWorld) {
//            var10000 = validateTicker(givenType, expectedType, (worldx, pos, state, blockEntity) -> AbstractFurnaceBlockEntity.tick(serverWorld, pos, state, blockEntity));
//        } else {
//            var10000 = null;
//        }
//
//        return var10000;
//    }
//}