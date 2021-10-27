package org.krystilize.blocky.data;

import org.krystilize.blocky.SchematicSchema;
import org.krystilize.blocky.mcedit.MCEditSchema;
import org.krystilize.blocky.mcedit.MCEditSchematicData;
import org.krystilize.blocky.sponge.SpongeSchema;
import org.krystilize.blocky.sponge.SpongeSchematicData;

public class Schemas {

    public static final SchematicSchema<MCEditSchematicData> MC_EDIT = MCEditSchema.INSTANCE;

    public static final SchematicSchema<SpongeSchematicData> SPONGE = SpongeSchema.INSTANCE;
}
