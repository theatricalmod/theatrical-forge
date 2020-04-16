package dev.theatricalmod.theatrical.api;

import net.minecraft.util.Direction;

public interface IAcceptsCable {

    CableType[] getAcceptedCables(Direction side);

}
