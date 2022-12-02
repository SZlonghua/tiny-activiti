package com.tiny.activiti.engine.impl.util;

import java.io.IOException;
import java.io.InputStream;

public class IoUtil {

    public static void closeSilently(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ignore) {
            // Exception is silently ignored
        }
    }
}
