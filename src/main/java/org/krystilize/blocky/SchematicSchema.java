package org.krystilize.blocky;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTReader;
import org.jglrxavpok.hephaistos.nbt.NBTWriter;
import org.krystilize.blocky.data.SchematicData;

@ApiStatus.Internal
public interface SchematicSchema<D extends SchematicData> {
    @ApiStatus.Internal
    void read(@NotNull NBTReader reader, @NotNull D data) throws Throwable;
    @ApiStatus.Internal
    void write(@NotNull D data, @NotNull NBTWriter writer) throws Throwable;
    @ApiStatus.Internal
    D createData();
}
