package com.modest.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * description
 *
 * @author modest
 * @date 2019/08/09
 */
public class CommUtilsTest {

    @Test
    public void loadProperties() {
        String fileName = "datasource.properties";

        Properties properties = CommUtils.loadProperties(fileName);

        Assert.assertNotNull(properties);
    }
}