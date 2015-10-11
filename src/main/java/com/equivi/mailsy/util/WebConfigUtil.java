package com.equivi.mailsy.util;

import com.equivi.mailsy.service.constant.dEmailerWebPropertyKey;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tsagita on 11/8/14.
 */
public class WebConfigUtil {
    private final static Logger LOG = Logger.getLogger(WebConfigUtil.class);

    private static Properties properties;
    private static InputStream is;

    static {
        properties = new Properties();
        try {
            String propertyLocation = System.getProperty("propertyLocation");
            is = new FileInputStream(propertyLocation);
            properties.load(is);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    public static String getValue(dEmailerWebPropertyKey emailerWebPropertyKey) {
        return properties.getProperty(emailerWebPropertyKey.getKeyName());
    }
}
