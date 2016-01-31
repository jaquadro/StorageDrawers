/*package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import com.dynious.refinedrelocation.api.APIUtils;
import com.dynious.refinedrelocation.api.tileentity.ISortingMember;
import com.dynious.refinedrelocation.api.tileentity.handlers.ISortingMemberHandler;
import net.minecraft.tileentity.TileEntity;

public class TileSortingTrim extends TileEntity implements ISortingMember {

    private ISortingMemberHandler sortingHandler = APIUtils.createSortingMemberHandler(this);
    private boolean isFirstTick = true;

    @Override
    public ISortingMemberHandler getHandler() {
        return sortingHandler;
    }

    @Override
    public void updateEntity() {
        if(isFirstTick) {
            sortingHandler.onTileAdded();
            isFirstTick = false;
        }
        super.updateEntity();
    }

    @Override
    public void invalidate() {
        sortingHandler.onTileRemoved();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        sortingHandler.onTileRemoved();
        super.onChunkUnload();
    }

}
*/