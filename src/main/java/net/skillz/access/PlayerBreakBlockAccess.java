package net.skillz.access;

public interface PlayerBreakBlockAccess {

    public void setInventoryBlockBreakable(boolean breakable);

    public void setAbstractBlockBreakDelta(float breakingDelta);

    public float getBreakingAbstractBlockDelta();
}
