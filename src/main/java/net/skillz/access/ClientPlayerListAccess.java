package net.skillz.access;

import java.util.Map;
import java.util.UUID;

public interface ClientPlayerListAccess {

    int skillz$getLevel();

    void skillz$setLevel(int level);

    Map<UUID, Integer> skillz$getLevelMap();
}
