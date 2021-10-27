package org.krystilize.blocky.sponge;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.collections.ImmutableByteArray;
import org.jglrxavpok.hephaistos.collections.ImmutableIntArray;
import org.jglrxavpok.hephaistos.nbt.*;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;
import org.krystilize.blocky.NBTUtils;
import org.krystilize.blocky.SchematicSchema;
import org.krystilize.blocky.error.SchematicReadError;
import org.krystilize.blocky.error.SchematicWriteError;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.StreamSupport;

public enum SpongeSchema implements SchematicSchema<SpongeSchematicData> {
    INSTANCE;
    // Sizes

    @Override
    public void read(@NotNull NBTReader reader, @NotNull SpongeSchematicData data) throws Throwable {
        // Get Main NBT
        NBTCompound nbt = (NBTCompound) reader.read();

        Integer version = nbt.getInt("Version");
        data.version = version == null ? 2 : version;

        // TODO: Read and Check Data Version

        readSizes(nbt, data);
        readBlockPalette(nbt, data);

        // TODO: Read block data
        readBlocks(data);

        // TODO: Read Biome Palette

        // TODO: Read BiomeData

        readEntities(nbt, data);
    }

    @Override
    public void write(@NotNull SpongeSchematicData data, @NotNull NBTWriter writer) throws Throwable {
        MutableNBTCompound nbtTag = new MutableNBTCompound();

        // Set Version
        nbtTag.setInt("Version", data.version);

        // TODO: Write Data entities

        writeSizes(nbtTag, data);
        writeBlockPalette(nbtTag, data);

        // TODO: Write Block entities

        // TODO: Write entities

        // TODO: Write Biome Palette

        // TODO: Write BiomeData

        writer.writeNamed("Schematic", nbtTag.toCompound());
    }

    private void readEntities(@NotNull NBTCompound nbt, SpongeSchematicData data) throws Throwable {
        NBTList<NBT> entities = nbt.getList("Entities");

        if (entities == null) {
            return;
        }

        data.entities = entities.asListView().stream()
                .filter(NBTCompound.class::isInstance)
                .map(NBTCompound.class::cast)
                .filter(entity -> entity.containsKey("Id") && entity.containsKey("Pos") && entity.containsKey("Rotation"))
                .map(entityTag -> {
                    String id = entityTag.getString("Id");
                    NBTList<NBTNumber<?>> pos = entityTag.getList("Pos");
                    NBTList<NBTNumber<?>> rot = entityTag.getList("Rotation");

                    // Create assertions as filter requires not null
                    assert id != null;
                    assert pos != null;
                    assert rot != null;

                    // TODO: Check for position and rotation

                    return new SpongeSchematicData.SpongeEntity(
                            id,
                            new double[] { toNum(pos.get(0)), toNum(pos.get(1)), toNum(pos.get(2)) },
                            new double[] { toNum(pos.get(0)), toNum(pos.get(1)) },
                            NBTUtils.toJavaMap(entityTag)
                    );
                })
                .toArray(SpongeSchematicData.SpongeEntity[]::new);
    }

    private double toNum(@NotNull NBTNumber<?> nbtNumber) {
        return nbtNumber.getValue().doubleValue();
    }


    private void readSizes(@NotNull NBTCompound nbt, SpongeSchematicData data) throws Throwable {
        // Get dimensions
        Short width = nbt.getShort("Width");
        Short height = nbt.getShort("Height");
        Short length = nbt.getShort("Length");

        if (width == null || height == null || length == null) {
            throw new SchematicWriteError("Width, Height or Length tag was not found in sponge schematic");
        }

        data.width = width;
        data.height = height;
        data.length = length;


        // Get offset
        ImmutableIntArray offset = nbt.getIntArray("Offset");
        if (offset == null || offset.getSize() != 3) {
            data.offset = new int[] {0, 0, 0};
        } else {
            data.offset = offset.copyArray();
        }
    }

    private void readBlockPalette(@NotNull NBTCompound nbt, SpongeSchematicData data) throws Throwable {
        // Get Max Palette
        // Blocks
        Integer maxPalette = nbt.getInt("PaletteMax");
        if (maxPalette == null) {
            throw new SchematicReadError("PaletteMax tag not found in sponge schematic");
        }

        // Get Palette
        NBTCompound nbtPalette = (NBTCompound) nbt.get("Palette");
        if (nbtPalette == null) {
            throw new SchematicReadError("Palette tag not found in sponge schematic");
        }

        // Is Palette same size
        Set<String> keys = nbtPalette.getKeys();
        if (keys.size() != maxPalette) {
            throw new SchematicReadError("Palette size does not match PaletteMax tag");
        }

        // Create map from nbtPalette
        for (String key : keys) {
            Integer value = nbtPalette.getInt(key);
            if (value == null) {
                throw new SchematicReadError("Palette key: " + key + " was not found");
            }

            data.palette.put(key, value);
        }

        // Sort Palette map by values
        data.palette = data.palette.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll
                );

        // Get block data
        ImmutableByteArray bytes = nbt.getByteArray("BlockData");
        if (bytes == null || bytes.getSize() == 0) {
            throw new SchematicReadError("BlockData tag in sponge schematic was either not found or had a length of zero.");
        }
        data.blocksData = bytes.copyArray();
    }
    // https://github.com/EngineHub/WorldEdit/blob/303f5a76b2df70d63480f2126c9ef4b228eb3c59/worldedit-core/src/main/java/com/sk89q/worldedit/extent/clipboard/io/SpongeSchematicReader.java#L261-L297

    private void readBlocks(SpongeSchematicData data) throws Throwable {
        int index = 0;
        int i = 0;
        int value;
        int varintLength;
        List<String> paletteKeys = new ArrayList<>(data.palette.keySet());
        data.spongeBlocks = new SpongeSchematicData.SpongeBlock[data.blocksData.length];

        while (i < data.blocksData.length) {
            value = 0;
            varintLength = 0;

            while (true) {
                value |= (data.blocksData[i] & 127) << (varintLength++ * 7);
                if (varintLength > 5) {
                    throw new SchematicReadError("Incorrect var int length");
                }
                if ((data.blocksData[i] & 128) != 128) {
                    i++;
                    break;
                }
                i++;
            }

            // Offset is not needed
            int x = (index % (data.width * data.length)) % data.width
                    //- offset[0]
                    ;
            int y = index / (data.width * data.length)
                    //- offset[1]
                    ;
            int z = (index % (data.width * data.length)) / data.width
                    //- offset[2]
                    ;

            String block = paletteKeys.get(value);

            data.spongeBlocks[index] = new SpongeSchematicData.SpongeBlock(
                    block,
                    x,
                    y,
                    z
            );

            index++;
        }
    }

    private void writeSizes(@NotNull MutableNBTCompound nbt, SpongeSchematicData data) throws Throwable {
        // Set Width
        nbt.setShort("Width", data.width);

        // Set Height
        nbt.setShort("Height", data.height);

        // Set Length
        nbt.setShort("Length", data.length);

        // Set offset
        nbt.setIntArray("Offset", data.offset);
    }

    private void writeBlockPalette(@NotNull MutableNBTCompound nbtTag, SpongeSchematicData data) throws Throwable {
        // Generate Palette
        int paletteMax = 0;
        Map<String, Integer> palette = new HashMap<>();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(data.width * data.height * data.length);

        for (SpongeSchematicData.SpongeBlock spongeBlock : data.spongeBlocks) {
            String name = spongeBlock.blockString();

            int blockId;
            if (palette.containsKey(name)) {
                blockId = palette.get(name);
            } else {
                blockId = paletteMax;
                palette.put(name, blockId);
                paletteMax++;
            }

            while ((blockId & -128) != 0) {
                buffer.write(blockId & 127 | 128);
                blockId >>>= 7;
            }
            buffer.write(blockId);
        }

        // Set PaletteMax
        nbtTag.setInt("PaletteMax", paletteMax);

        // Palette items to NBTTag
        MutableNBTCompound paletteItems = new MutableNBTCompound();
        palette.forEach(paletteItems::setInt);

        // Set Palette
        nbtTag.set("Palette", paletteItems.toCompound());

        // Set Block Data
        nbtTag.setByteArray("BlockData", buffer.toByteArray());
    }

    @Override
    public SpongeSchematicData createData() {
        return new SpongeSchematicData();
    }
}
