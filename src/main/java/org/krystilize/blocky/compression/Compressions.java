package org.krystilize.blocky.compression;

import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser;

import java.io.InputStream;
import java.io.OutputStream;

public enum Compressions implements Compression {
    /**
     * No compression.
     */
    NONE(CompressedProcesser.NONE),
    /**
     * Gzip compression (recommended)
     */
    GZIP(CompressedProcesser.GZIP),
    /**
     * Zlib compression (legacy)
     */
    ZLIB(CompressedProcesser.ZLIB);

    private final @NotNull CompressedProcesser<? extends InputStream, ? extends OutputStream> processor;

    Compressions(@NotNull CompressedProcesser<? extends InputStream, ? extends OutputStream> processor) {
        this.processor = processor;
    }

    @Override
    public @NotNull CompressedProcesser<? extends InputStream, ? extends OutputStream> getProcessor() {
        return processor;
    }
}
