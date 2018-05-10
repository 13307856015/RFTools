package mcjty.rftools.blocks.storagemonitor;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.clientinfo.InfoPacketClient;
import mcjty.lib.network.clientinfo.InfoPacketServer;
import mcjty.lib.varia.WorldTools;
import mcjty.rftools.varia.RFToolsTools;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;
import java.util.Optional;

public class GetContentsInfoPacketServer implements InfoPacketServer {

    private int id;
    private BlockPos pos;
    private BlockPos cpos;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        pos = NetworkTools.readPos(byteBuf);
        cpos = NetworkTools.readPos(byteBuf);
        id = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        NetworkTools.writePos(byteBuf, pos);
        NetworkTools.writePos(byteBuf, cpos);
        byteBuf.writeInt(id);
    }

    public GetContentsInfoPacketServer() {
    }

    public GetContentsInfoPacketServer(int worldId, BlockPos pos, BlockPos cpos) {
        this.id = worldId;
        this.pos = pos;
        this.cpos = cpos;
    }

    @Override
    public Optional<InfoPacketClient> onMessageServer(EntityPlayerMP entityPlayerMP) {
        World world = DimensionManager.getWorld(id);
        if (world == null) {
            return Optional.empty();
        }

        if (!WorldTools.chunkLoaded(world, pos)) {
            return Optional.empty();
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof StorageScannerTileEntity) {
            StorageScannerTileEntity scannerTileEntity = (StorageScannerTileEntity) te;
            List<ItemStack> inv = scannerTileEntity.getInventoryForBlock(cpos);
            return Optional.of(new GetContentsInfoPacketClient(inv));
        }

        return Optional.empty();
    }
}
