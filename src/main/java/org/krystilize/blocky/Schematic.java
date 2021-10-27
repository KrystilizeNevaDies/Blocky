package org.krystilize.blocky;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTReader;
import org.jglrxavpok.hephaistos.nbt.NBTWriter;
import org.krystilize.blocky.compression.Compression;
import org.krystilize.blocky.data.SchematicData;
import org.krystilize.blocky.error.SchematicReadError;
import org.krystilize.blocky.error.SchematicWriteError;

public class Schematic<D extends SchematicData> {

    private final @NotNull SchematicSchema<D> schema;
    private final @NotNull ExceptionHandledFunction<Compression, NBTReader> readerFunction;
    private final @NotNull ExceptionHandledFunction<Compression, NBTWriter> writerFunction;
    private @Nullable NBTWriter currentWriter;

    Schematic(
            @NotNull ExceptionHandledFunction<Compression, NBTReader> readerFunction,
            @NotNull ExceptionHandledFunction<Compression, NBTWriter> writerFunction,
            @NotNull SchematicSchema<D> schema
    ) {
        this.schema = schema;
        this.readerFunction = readerFunction;
        this.writerFunction = writerFunction;
    }

    @ApiStatus.Internal
    @NotNull SchematicSchema<D> getSchema() {
        return schema;
    };

    @ApiStatus.Internal
    @NotNull NBTReader getReader(@NotNull Compression compression) throws Throwable {
        NBTReader reader = readerFunction.get(compression);
        if (reader == null) {
            throw new SchematicReadError("Schematic#getReader was called on a write-only schematic");
        }
        return reader;
    };

    @ApiStatus.Internal
    @NotNull NBTWriter getWriter(@NotNull Compression compression) throws Throwable {
        if (this.currentWriter == null) {
            this.currentWriter = writerFunction.get(compression);
            if (this.currentWriter == null) {
                throw new SchematicWriteError("Schematic#getWriter was called on a read-only schematic");
            }
        }
        return currentWriter;
    };

    @ApiStatus.Internal
    void flush() throws Throwable {
        if (currentWriter == null) {
            throw new SchematicWriteError("Schematic#flush was called while the NBTWriter was null");
        }
        currentWriter.close();
        this.currentWriter = null;
    };

    @FunctionalInterface
    protected interface ExceptionHandledFunction<V, R> {
        R get(@NotNull V value) throws Throwable;
    }
}
