package org.krystilize.blocky.mcedit;

import org.jetbrains.annotations.NotNull;
import org.krystilize.blocky.data.SchematicData;

/**
 * A class that holds schematic data that is used by the mcedit format
 */
public class MCEditSchematicData extends SchematicData {
    byte[] blockData;
    short width;
    short height;
    short length;

    short[] blockStateIds;
    int[] blockPosX;
    int[] blockPosY;
    int[] blockPosZ;
    MCEditBlock[] mcEditBlocks;

    MCEditSchematicData() {
    }

    /**
     * Returns all the blocks within this object
     * @return the list of all blocks
     */
    public @NotNull MCEditBlock[] getAllBlocks() {
        if (mcEditBlocks == null) {
            mcEditBlocks = new MCEditBlock[blockStateIds.length];
            for (int i = 0; i < blockStateIds.length; i++) {
                mcEditBlocks[i] = new MCEditBlock(
                        blockStateIds[i],
                        blockPosX[i],
                        blockPosY[i],
                        blockPosZ[i]
                );
            }
        }
        return mcEditBlocks;
    }

    /**
     * Gets the blockStateID at the specified index
     * @param index the index
     * @return the blockStateID
     */
    public short getBlockID(int index) {
        return blockStateIds[index];
    }

    /**
     * Gets the block x position at the specified index
     * @param index the index
     * @return the block x position
     */
    public int getBlockX(int index) {
        return blockPosX[index];
    }

    /**
     * Gets the block y position at the specified index
     * @param index the index
     * @return the block y position
     */
    public int getBlockY(int index) {
        return blockPosY[index];
    }

    /**
     * Gets the block z position at the specified index
     * @param index the index
     * @return the block z position
     */
    public int getBlockZ(int index) {
        return blockPosZ[index];
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public short getLength() {
        return length;
    }

    public record MCEditBlock(
            short blockStateId,
            int x,
            int y,
            int z
    ) {
    }
}
