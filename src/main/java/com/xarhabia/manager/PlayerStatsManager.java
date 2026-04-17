package com.xarhabia.manager;

import com.xarhabia.model.PlayerStats;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatsManager {

    private static final HashMap<UUID, PlayerStats> statsMap = new HashMap<>();

    public static PlayerStats getStats(UUID playerId) {
        return statsMap.computeIfAbsent(playerId, id -> new PlayerStats());
    }
}
