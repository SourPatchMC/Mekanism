package mekanism.common.transmitters;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.math.FloatingLong;
import mekanism.common.lib.transmitter.DynamicNetwork;
import mekanism.common.lib.transmitter.IGridTransmitter;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class TransmitterImpl<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, BUFFER>, BUFFER> extends Transmitter<ACCEPTOR, NETWORK, BUFFER> {

    public TileEntityTransmitter<ACCEPTOR, NETWORK, BUFFER> containingTile;

    public TransmitterImpl(TileEntityTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter) {
        setTileEntity(transmitter);
    }

    @Nonnull
    @Override
    public FloatingLong getCapacityAsFloatingLong() {
        return getTileEntity().getCapacityAsFloatingLong();
    }

    @Override
    public long getCapacity() {
        return getTileEntity().getCapacity();
    }

    @Override
    public World world() {
        return getTileEntity().getWorld();
    }

    @Override
    public Coord4D coord() {
        return new Coord4D(getTileEntity().getPos(), world());
    }

    @Override
    public Coord4D getAdjacentConnectableTransmitterCoord(Direction side) {
        Coord4D sideCoord = coord().offset(side);
        TileEntity potentialTransmitterTile = MekanismUtils.getTileEntity(world(), sideCoord.getPos());
        if (!containingTile.canConnectMutual(side, potentialTransmitterTile)) {
            return null;
        }
        if (potentialTransmitterTile instanceof IGridTransmitter && getTransmissionType().checkTransmissionType((IGridTransmitter<?, ?, ?>) potentialTransmitterTile) &&
            containingTile.isValidTransmitter(potentialTransmitterTile)) {
            return sideCoord;
        }
        return null;
    }

    @Override
    public boolean isCompatibleWith(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> other) {
        if (other instanceof TransmitterImpl) {
            return containingTile.isValidTransmitter(((TransmitterImpl<?, ?, ?>) other).containingTile);
        }
        return true;//allow non-Transmitter impls to connect?
    }

    @Override
    public ACCEPTOR getAcceptor(Direction side) {
        return getTileEntity().getCachedAcceptor(side);
    }

    @Override
    public boolean isValid() {
        TileEntityTransmitter<ACCEPTOR, NETWORK, BUFFER> cont = getTileEntity();
        if (cont == null) {
            return false;
        }
        return !cont.isRemoved() && MekanismUtils.getTileEntity(world(), cont.getPos()) == cont && cont.getTransmitter() == this;
    }

    @Override
    public NETWORK createEmptyNetwork() {
        return getTileEntity().createNewNetwork();
    }

    @Override
    public NETWORK createEmptyNetworkWithID(UUID networkID) {
        return getTileEntity().createNewNetworkWithID(networkID);
    }

    @Override
    public NETWORK getExternalNetwork(Coord4D from) {
        TileEntity tile = MekanismUtils.getTileEntity(world(), from.getPos());
        if (tile instanceof IGridTransmitter) {
            IGridTransmitter<?, ?, ?> transmitter = (IGridTransmitter<?, ?, ?>) tile;
            if (getTransmissionType().checkTransmissionType(transmitter)) {
                return ((IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>) transmitter).getTransmitterNetwork();
            }
        }
        return null;
    }

    @Override
    public void takeShare() {
        containingTile.takeShare();
    }

    @Nullable
    @Override
    public BUFFER releaseShare() {
        return getTileEntity().releaseShare();
    }

    @Override
    public BUFFER getShare() {
        return getTileEntity().getShare();
    }

    @Nullable
    @Override
    public BUFFER getBufferWithFallback() {
        return getTileEntity().getBufferWithFallback();
    }

    @Override
    public NETWORK mergeNetworks(Collection<NETWORK> toMerge) {
        return getTileEntity().createNetworkByMerging(toMerge);
    }

    @Override
    public TransmissionType getTransmissionType() {
        return getTileEntity().getTransmissionType();
    }

    @Override
    public void setRequestsUpdate() {
        getTileEntity().requestsUpdate();
    }

    public TileEntityTransmitter<ACCEPTOR, NETWORK, BUFFER> getTileEntity() {
        return containingTile;
    }

    public void setTileEntity(TileEntityTransmitter<ACCEPTOR, NETWORK, BUFFER> containingPart) {
        this.containingTile = containingPart;
    }
}