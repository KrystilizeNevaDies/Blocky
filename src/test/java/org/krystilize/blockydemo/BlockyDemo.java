package org.krystilize.blockydemo;

import org.krystilize.blocky.Blocky;
import org.krystilize.blocky.Schematic;
import org.krystilize.blocky.Schematics;
import org.krystilize.blocky.compression.Compressions;
import org.krystilize.blocky.data.Schemas;
import org.krystilize.blocky.sponge.SpongeSchematicData;

import java.io.File;

public class BlockyDemo {
    private static final Blocky BLOCKY = Blocky.builder()
            .compression(Compressions.GZIP)
            .build();

    public static void main(String[] args) {
        // Specify type and source
        File input = new File("schem/stage.schem");
        Schematic<SpongeSchematicData> schematic = Schematics.file(input, Schemas.SPONGE);

        // Read the data
        SpongeSchematicData data = BLOCKY.read(schematic);

        // Print all entities
        for (SpongeSchematicData.SpongeEntity entity : data.getAllEntities()) {
            System.out.println(entity);
        }

        // Write the data back into a different file
        File output = new File("schem/stage1.schem");
        Schematic<SpongeSchematicData> outputSchematic = Schematics.file(output, Schemas.SPONGE);

        BLOCKY.write(data, outputSchematic);
    }
}
