package dev.theatricalmod.theatrical.client.model;

import com.google.common.collect.HashBasedTable;
import dev.theatricalmod.theatrical.api.CableSide;
import dev.theatricalmod.theatrical.api.CableType;
import java.util.HashMap;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;

public class CableModelData {

    public static final ModelProperty<CableModelData> MODEL_DATA = new ModelProperty<>();

    private CableSide[] sides;
    private HashBasedTable<Direction, CableType, Boolean>[] connections;

    public CableModelData(CableSide[] sides, HashBasedTable<Direction, CableType, Boolean>[] connections) {
        this.sides = sides;
        this.connections = connections;
    }

    public boolean hasSide(int side){
        return sides[side] != null;
    }

    public boolean isConnected(Direction direction, int side, CableType type) {
        return connections[side].get(direction, type);
    }

    public CableSide[] getSides() {
        return sides;
    }

}
