package net.levelz.screen.widget;

import net.levelz.level.restriction.PlayerRestriction;
import net.minecraft.text.MutableText;

import java.util.Map;

public class RestrictionBook {

    public final Map<Integer, PlayerRestriction> restriction;
    public final MutableText title;
    public final int code;

    public RestrictionBook(Map<Integer, PlayerRestriction> restriction, MutableText title, int code) {
        this.restriction = restriction;
        this.title = title;
        this.code = code;
    }
}
