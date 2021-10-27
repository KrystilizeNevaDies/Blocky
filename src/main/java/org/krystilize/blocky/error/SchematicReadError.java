package org.krystilize.blocky.error;

import org.jetbrains.annotations.NotNull;

public class SchematicReadError extends Throwable {
    public SchematicReadError(@NotNull String errorMessage) {
        super(errorMessage);
    }
}
