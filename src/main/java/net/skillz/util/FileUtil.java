package net.skillz.util;

import net.minecraft.util.Identifier;

import java.io.File;

public class FileUtil {

    public static Identifier pathToId(Identifier path) {
        return new Identifier(path.getNamespace(), FileUtil.getBaseName(path.getPath()));
    }

    public static String getBaseName(String filename) {
        if (filename == null)
            return null;

        String name = new File(filename).getName();
        int extPos = name.lastIndexOf('.');

        if (extPos < 0)
            return name;

        return name.substring(0, extPos);
    }
}