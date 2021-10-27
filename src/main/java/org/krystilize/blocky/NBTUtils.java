package org.krystilize.blocky;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NBTUtils {
    /**
     * Converts an nbtcompound to a java map.
     *
     * @param compound the nbt to convert
     * @return the java map
     */
    public static @NotNull Map<String, Object> toJavaMap(@NotNull NBTCompound compound) {
        final Map<String, Object> javaMap = new HashMap<>();
        compound.forEach((key, nbt) -> javaMap.put(key, toJava(nbt)));
        return javaMap;
    }

    private static final Map<Integer, Function<NBT, Object>> NBT_MAPPINGS = Map.ofEntries(
            Map.entry(NBTType.TAG_Byte.getOrdinal(), nbt -> ((NBTByte) nbt).getValue()),
            Map.entry(NBTType.TAG_Byte_Array.getOrdinal(), nbt -> ((NBTByteArray) nbt).getValue()),
            Map.entry(NBTType.TAG_Compound.getOrdinal(), nbt -> toJavaMap((NBTCompound) nbt)),
            Map.entry(NBTType.TAG_Double.getOrdinal(), nbt -> ((NBTDouble) nbt).getValue()),
            Map.entry(NBTType.TAG_Float.getOrdinal(), nbt -> ((NBTFloat) nbt).getValue()),
            Map.entry(NBTType.TAG_Int.getOrdinal(), nbt -> ((NBTInt) nbt).getValue()),
            Map.entry(NBTType.TAG_Int_Array.getOrdinal(), nbt -> ((NBTIntArray) nbt).getValue().copyArray()),
            Map.entry(NBTType.TAG_List.getOrdinal(), nbt -> toJavaList((NBTList<? extends NBT>) nbt)),
            Map.entry(NBTType.TAG_Long.getOrdinal(), nbt -> ((NBTLong) nbt).getValue()),
            Map.entry(NBTType.TAG_Long_Array.getOrdinal(), nbt -> ((NBTLongArray) nbt).getValue().copyArray()),
            Map.entry(NBTType.TAG_Short.getOrdinal(), nbt -> ((NBTShort) nbt).getValue()),
            Map.entry(NBTType.TAG_String.getOrdinal(), nbt -> ((NBTString) nbt).getValue())
    );

    private static @Nullable Object toJava(NBT nbt) {
        return NBT_MAPPINGS.get(nbt.getID().getOrdinal()).apply(nbt);
    }

    private static @NotNull List<Object> toJavaList(NBTList<? extends NBT> list) {
        List<Object> javaList = new ArrayList<>();
        list.forEach(nbt -> {
            javaList.add(toJava(nbt));
        });
        return javaList;
    }
}
