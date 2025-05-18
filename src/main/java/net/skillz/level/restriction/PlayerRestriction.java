package net.skillz.level.restriction;

import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * @param skillLevelRestrictions skillid, lvl
 */
public record PlayerRestriction(int id, Map<Identifier, Integer> skillLevelRestrictions) {

}
