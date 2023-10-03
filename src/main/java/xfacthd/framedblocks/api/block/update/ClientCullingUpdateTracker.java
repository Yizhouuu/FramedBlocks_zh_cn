package xfacthd.framedblocks.api.block.update;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.client.ClientUtils;

import java.util.Collection;
import java.util.Objects;

@ApiStatus.Internal
final class ClientCullingUpdateTracker
{
    public static void handleCullingUpdates(Collection<CullingUpdateChunk> chunks)
    {
        ClientUtils.enqueueClientTask(1, () ->
        {
            Level level = Objects.requireNonNull(Minecraft.getInstance().level);
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
            chunks.forEach(chunk ->
            {
                ChunkPos cp = chunk.chunk();
                if (level.hasChunk(cp.x, cp.z))
                {
                    chunk.positions().forEach(pos ->
                    {
                        blockPos.set(pos);
                        if (level.getBlockEntity(blockPos) instanceof FramedBlockEntity be)
                        {
                            be.updateCulling(true, true);
                        }
                    });
                }
            });
        });
    }
}
