package net.skillz.util;

import java.io.File;

public class FileUtil {

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