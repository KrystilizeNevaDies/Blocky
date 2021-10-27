package org.krystilize.blocky;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser;
import org.jglrxavpok.hephaistos.nbt.NBTReader;
import org.jglrxavpok.hephaistos.nbt.NBTWriter;
import org.krystilize.blocky.compression.Compression;
import org.krystilize.blocky.data.SchematicData;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Schematics {

    /**
     * Creates a schematic using the specified file and schema
     * @param file the file to read from
     * @param schema the schema to use
     * @return the schematic
     */
    public static <D extends SchematicData> @NotNull Schematic<D> file(
            @NotNull File file,
            @NotNull SchematicSchema<D> schema
    ) {
        return new Schematic<>(
                (compression) -> new NBTReader(file, compression.getProcessor()),
                (compression) -> new NBTWriter(file, compression.getProcessor()),
                schema
        );
    }

    /**
     * Creates a read-only schematic using the specified nbt reader and schema
     * @param reader the reader to read from
     * @param schema the schema to use
     * @return the read-only schematic
     */
    public static <D extends SchematicData> @NotNull Schematic<D> nbt(
            @NotNull NBTReader reader,
            @NotNull SchematicSchema<D> schema
    ) {
        return nbt(reader, null, schema);
    }

    /**
     * Creates a write-only schematic using the specified nbt writer and schema
     * @param writer the writer to write to
     * @param schema the schema to use
     * @return the write-only schematic
     */
    public static <D extends SchematicData> @NotNull Schematic<D> nbt(
            @NotNull NBTWriter writer,
            @NotNull SchematicSchema<D> schema
    ) {
        return nbt(null, writer, schema);
    }

    /**
     * Creates a schematic using the specified nbt writer, nbt reader and schema
     * @param reader the reader to read from
     * @param writer the writer to write to
     * @param schema the schema to use
     * @return the schematic
     */
    public static <D extends SchematicData> @NotNull Schematic<D> nbt(
            @Nullable NBTReader reader,
            @Nullable NBTWriter writer,
            @NotNull SchematicSchema<D> schema
    ) {
        return new Schematic<>(
                (compression) -> reader,
                (compression) -> writer,
                schema
        );
    }
}
