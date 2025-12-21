package com.jaquadro.minecraft.storagedrawers.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

import java.util.function.LongConsumer;

public final class GridIndex3D {
    // Map cellKey -> bucket of packed BlockPos longs
    private final Long2ObjectOpenHashMap<LongArrayList> buckets = new Long2ObjectOpenHashMap<>();

    private static boolean sphereIntersectsAabb(BlockPos center, long r2, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

        // This is the standard branchless sphere intersection test.
        // If we wanted to know if the sphere is contained within the box, we only would have to check if the
        // center + radius is inside the box in each axis.
        // For intersection, we need to test overlap, and it's possible for the center to be outside the box,
        // but the sphere to still overlap the box.
        // So we compute how far it is from the sphere center to the nearest point on the boundary.
        // If the center is in the box, it will end up contributing nothing to the distance (dx,dy,dz) because the
        // clamped point will be the center.
        // If it isn't, it will contribute how far away from the box boundary it is, and we know they still overlap
        // if the distance is less than the radius squared
        int qx = Math.clamp(center.getX(), minX, maxX);
        int qy = Math.clamp(center.getY(), minY, maxY);
        int qz = Math.clamp(center.getZ(), minZ, maxZ);

        int dx = center.getX() - qx;
        int dy = center.getY() - qy;
        int dz = center.getZ() - qz;

        // The 
        long d2 = (long) dx * dx + (long) dy * dy + (long) dz * dz;
        return d2 <= r2;
    }

    public void add(BlockPos pos) {
        var sectionPos = SectionPos.of(pos);

        long key = sectionPos.asLong();
        LongArrayList list = buckets.get(key);
        if (list == null) {
            list = new LongArrayList();
            buckets.put(key, list);
        }
        list.add(pos.asLong());
    }

    public boolean remove(BlockPos pos) {
        var packedBlockPos = pos.asLong();
        var sectionPos = SectionPos.of(pos);

        long key = sectionPos.asLong();
        LongArrayList list = buckets.get(key);
        if (list == null) return false;

        // Remove can be made O(1) with a side-index, but we aren't loading/unloading chunks
        // fast enough to be a big deal here. Queries are much more common.
        for (int i = 0; i < list.size(); i++) {
            if (list.getLong(i) == packedBlockPos) {
                int last = list.size() - 1;
                list.set(i, list.getLong(last));
                list.removeLong(last);
                if (list.isEmpty()) buckets.remove(key);
                return true;
            }
        }
        return false;
    }

    public void querySphere(BlockPos center, int radius, LongConsumer found) {
        long r2 = (long) radius * (long) radius;

        // Compute block-space bounds, then section-space bounds
        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minY = center.getY() - radius;
        int maxY = center.getY() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;

        int cellCoordMinX = SectionPos.blockToSectionCoord(minX);
        int cellCoordMaxX = SectionPos.blockToSectionCoord(maxX);
        int cellCoordMinY = SectionPos.blockToSectionCoord(minY);
        int cellCoordMaxY = SectionPos.blockToSectionCoord(maxY);
        int cellCoordMinZ = SectionPos.blockToSectionCoord(minZ);
        int cellCoordMaxZ = SectionPos.blockToSectionCoord(maxZ);

        // This triple-nested loop is only iterating over cells.  Most cells will be eliminated.
        // It seems expensive but is not, as it will only look at chunks that are in range
        // and will eliminate almost all chunks very quickly without looking at any points
        // inside them.
        // The expensive tests are actually the per-point tests, which we minimize.
        // If we wanted to maximize speed but make it less general,
        // we could simply only scan the chunks that are loaded only, etc.

        for (int cx = cellCoordMinX; cx <= cellCoordMaxX; cx++) {
            int cellMinX = SectionPos.sectionToBlockCoord(cx);
            int cellMaxX = cellMinX + SectionPos.SECTION_MAX_INDEX;

            for (int cy = cellCoordMinY; cy <= cellCoordMaxY; cy++) {
                int cellMinY = SectionPos.sectionToBlockCoord(cy);
                int cellMaxY = cellMinY + SectionPos.SECTION_MAX_INDEX;

                for (int cz = cellCoordMinZ; cz <= cellCoordMaxZ; cz++) {
                    long key = SectionPos.of(cx, cy, cz).asLong();
                    LongArrayList list = buckets.get(key);
                    if (list == null) continue;

                    int cellMinZ = SectionPos.sectionToBlockCoord(cz);
                    int cellMaxZ = cellMinZ + SectionPos.SECTION_MAX_INDEX;

                    // Sphere-AABB prune against the cell bounds
                    if (!sphereIntersectsAabb(center, r2, cellMinX, cellMinY, cellMinZ, cellMaxX, cellMaxY, cellMaxZ)) {
                        continue;
                    }

                    // Scan bucket points, check if within radius
                    for (int i = 0; i < list.size(); i++) {
                        long p = list.getLong(i);
                        int x = BlockPos.getX(p);
                        int y = BlockPos.getY(p);
                        int z = BlockPos.getZ(p);

                        int dx = x - center.getX();
                        int dy = y - center.getY();
                        int dz = z - center.getZ();

                        long d2 = (long) dx * dx + (long) dy * dy + (long) dz * dz;
                        if (d2 <= r2) {
                            found.accept(p);
                        }
                    }
                }
            }
        }
    }

}
