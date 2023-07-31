package com.increff.pos.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

public class IOUtil {

    public static void closeQuietly(Closeable c) {
        if (Objects.isNull(c)) {
            return;
        }

        try {
            c.close();
        } catch (IOException e) {
            // do nothing
        }
    }

}