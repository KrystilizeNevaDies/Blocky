package org.krystilize.blocky.mcedit;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.collections.ImmutableByteArray;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTReader;
import org.jglrxavpok.hephaistos.nbt.NBTWriter;
import org.krystilize.blocky.SchematicSchema;
import org.krystilize.blocky.error.SchematicReadError;

import java.util.Objects;

@ApiStatus.Internal
public enum MCEditSchema implements SchematicSchema<MCEditSchematicData> {
    INSTANCE;

    // https://github.com/EngineHub/WorldEdit/blob/version/5.x/src/main/java/com/sk89q/worldedit/schematic/MCEditSchematicFormat.java
    @Override
    public void read(@NotNull NBTReader reader, @NotNull MCEditSchematicData data) throws Throwable {
        // Get Main NBT
        NBTCompound nbtTag = (NBTCompound) reader.read();

        if (!nbtTag.containsKey("Blocks")) {
            throw new SchematicReadError("Blocks nbt section not found in mcedit schematic. Is this an mcedit schematic?");
        }

        readSizes(nbtTag, data);
        readBlocksData(nbtTag, data);
        readBlockPositions(data);
        // TODO: Read tile entities
    }

    @Override
    public void write(@NotNull MCEditSchematicData data, @NotNull NBTWriter writer) throws Throwable {
        // TODO: Write
    }

    @Override
    public MCEditSchematicData createData() {
        return new MCEditSchematicData();
    }


    private void readSizes(@NotNull NBTCompound nbtTag, @NotNull MCEditSchematicData data) throws SchematicReadError {
        // Get dimensions
        Short width = nbtTag.getShort("Width");
        Short height = nbtTag.getShort("Height");
        Short length = nbtTag.getShort("Length");

        if (width == null || height == null || length == null) {
            throw new SchematicReadError("Width, Height or Length tag was not found in sponge schematic");
        }

        data.width = width;
        data.height = height;
        data.length = length;
    }


    private void readBlocksData(@NotNull NBTCompound nbtTag, @NotNull MCEditSchematicData data) throws Throwable {
        // Check materials
        // Blocks
        String materials = nbtTag.getString("Materials");
        if (materials == null) {
            throw new SchematicReadError("Materials tag in block data not found.");
        }
        if (!materials.equals("Alpha")) {
            throw new SchematicReadError("Materials tag in mcedit schematic block data is not supported.");
        }

        ImmutableByteArray blockId = nbtTag.getByteArray("Blocks");
        if (blockId == null) {
            throw new SchematicReadError("Block section in mcedit schematic was not found.");
        }

        ImmutableByteArray blocksData = nbtTag.getByteArray("Data");
        if (blocksData == null) {
            throw new SchematicReadError("Block data section in mcedit schematic was not found.");
        }
        data.blockData = blocksData.copyArray();

        @NotNull ImmutableByteArray addBlockId = new ImmutableByteArray();
        if (nbtTag.containsKey("AddBlocks")) {
            addBlockId = Objects.requireNonNull(nbtTag.getByteArray("AddBlocks"));
        }

        // Read block data
        short[] blockStateIds = new short[blockId.getSize()];
        for (int index = 0; index < blockId.getSize(); index++) {
            if ((index >> 1) >= addBlockId.getSize()) {
                blockStateIds[index] = (short) (blockId.get(index) & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blockStateIds[index] = (short) (((addBlockId.get(index >> 1) & 0x0F) << 8) + (blockId.get(index) & 0xFF));
                } else {
                    blockStateIds[index] = (short) (((addBlockId.get(index >> 1) & 0xF0) << 4) + (blockId.get(index) & 0xFF));
                }
            }
        }

        data.blockStateIds = blockStateIds;
    }

    public void readBlockPositions(@NotNull MCEditSchematicData data) {
        final float width = data.width;
        final float height = data.height;
        final float length = data.length;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int i = (int) (y * width * length + z * width + x);
                    data.blockPosX[i] = x;
                    data.blockPosY[i] = y;
                    data.blockPosZ[i] = z;
                }
            }
        }
    }
}
