package org.krystilize.blocky;

import org.jetbrains.annotations.NotNull;
import org.krystilize.blocky.compression.Compression;
import org.krystilize.blocky.compression.Compressions;
import org.krystilize.blocky.data.SchematicData;

import java.util.function.Function;

/**
 * This object holds all the options used when reading/writing a schematic file.
 */
public final class Blocky {

    private final @NotNull Compression compression;

    private Blocky(@NotNull Builder<Blocky> builder) {
        this.compression = builder.readCompression;
    }

    /**
     * Creates a builder of the blocky object.
     * <br><br>
     * This builder allows you to specify any options you will need while using schematics.
     * @return the builder
     */
    public static Builder<Blocky> builder() {
        return new Builder<>(Blocky::new);
    }

    /**
     * Reads the specified schematic into the specified schematic data
     * @param schematic the schematic to read from
     * @param data the schematic data to write into
     */
    public <D extends SchematicData> void read(@NotNull Schematic<D> schematic, @NotNull D data) {
        SchematicSchema<D> schema = schematic.getSchema();
        try {
            schema.read(schematic.getReader(compression), data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the specified schematic into a new schematic data object
     * @param schematic the schematic to read from
     */
    public <D extends SchematicData> D read(@NotNull Schematic<D> schematic) {
        D data = schematic.getSchema().createData();
        read(schematic, data);
        return data;
    }

    /**
     * Reads the specified schematic data the specified schematic and flushes afterwards
     * @param data the schematic data to read from
     * @param schematic the schematic to write into
     */
    public <D extends SchematicData> void write(@NotNull D data, @NotNull Schematic<D> schematic) {
        this.write(data, schematic, true);
    }

    /**
     * Reads the specified schematic data the specified schematic
     * @param data the schematic data to read from
     * @param schematic the schematic to write into
     * @param flush whether the data should be flushed afterwards
     */
    public <D extends SchematicData> void write(@NotNull D data, @NotNull Schematic<D> schematic, boolean flush) {
        SchematicSchema<D> schema = schematic.getSchema();
        try {
            schema.write(data, schematic.getWriter(compression));
            if (flush) {
                schematic.flush();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static class Builder<B extends Blocky> {

        final @NotNull Function<Builder<B>, B> blockyConstructor;
        @NotNull Compression readCompression = Compressions.GZIP;
        @NotNull Compression writeCompression = Compressions.GZIP;

        private Builder(@NotNull Function<Builder<B>, B> blockyConstructor) {
            this.blockyConstructor = blockyConstructor;
        }

        /**
         * Sets the compression used for read and write
         * @param compression the compression used for read and write
         * @return the builder
         */
        public Builder<B> compression(@NotNull Compression compression) {
            this.readCompression = compression;
            this.writeCompression = compression;
            return this;
        }

        /**
         * Sets the compression used for read
         * @param compression the compression used for read
         * @return the builder
         */
        public Builder<B> readCompression(@NotNull Compression compression) {
            this.readCompression = compression;
            return this;
        }

        /**
         * Sets the compression used for write
         * @param compression the compression used for write
         * @return the builder
         */
        public Builder<B> writeCompression(@NotNull Compression compression) {
            this.writeCompression = compression;
            return this;
        }

        /**
         * Creates a new Blocky object with this builder.
         * @return the blocky object
         */
        public @NotNull B build() {
            return blockyConstructor.apply(this);
        }
    }
}
