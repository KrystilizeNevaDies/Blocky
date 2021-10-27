package org.krystilize.blocky.sponge;

import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.krystilize.blocky.data.SchematicData;

import java.util.HashMap;
import java.util.Map;

public class SpongeSchematicData extends SchematicData {

    int version;

    int[] offset;
    short width;
    short height;
    short length;
    byte[] blocksData;

    SpongeEntity[] entities;
    Map<String, Integer> palette = new HashMap<>();
    SpongeBlock[] spongeBlocks;

    SpongeSchematicData() {
    }

    /**
     * Returns all the blocks within this object
     * @return all blocks
     */
    public @NotNull SpongeBlock[] getAllBlocks() {
        return spongeBlocks;
    }

    /**
     * Returns all the entities within this object
     * @return all entities
     */
    public @NotNull SpongeEntity[] getAllEntities() {
        return entities;
    }

    /**
     * Gets the width of this schematic data object
     * @return the width
     */
    public short getWidth() {
        return width;
    }

    /**
     * Gets the height of this schematic data object
     * @return the height
     */
    public short getHeight() {
        return height;
    }

    /**
     * Gets the length of this schematic data object
     * @return the length
     */
    public short getLength() {
        return length;
    }

    public record SpongeBlock(
            @NotNull String blockString,
            int x,
            int y,
            int z
    ) {
    }

    public record SpongeEntity(
            @NotNull String id,
            double[] pos,
            double[] rotation,
            Map<String, Object> nbtData
    ) {
    }
}
