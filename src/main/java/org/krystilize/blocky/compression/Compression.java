package org.krystilize.blocky.compression;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser;

import java.io.InputStream;
import java.io.OutputStream;

public interface Compression {

    @ApiStatus.Internal
    @NotNull CompressedProcesser<? extends InputStream, ? extends OutputStream> getProcessor();
}
