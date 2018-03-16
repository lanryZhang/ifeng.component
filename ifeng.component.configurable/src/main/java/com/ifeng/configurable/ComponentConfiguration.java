/*
* ComponentConfiguration.java 
* Created on  202016/12/9 14:05 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.configurable;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import sun.security.x509.IPAddressName;

import java.io.*;
import java.util.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ComponentConfiguration extends Configuration {
    protected Properties properties = new Properties();
    private Logger logger = Logger.getLogger(ComponentConfiguration.class);
    private String configPath;
    private static HashMap<String,List<String>> rootMap = new HashMap<>();
    private static HashMap<String,HashMap<String, Context>> map;

    public static HashMap<String, HashMap<String, Context>> getMap() {
        return map;
    }

    public ComponentConfiguration(String path) {
        this.configPath = path;
    }

    public ComponentConfiguration(){}



    public List<String> getAllInstances(String path) {
        this.configPath = path;
        return rootMap.get(configPath);

//        if (!rootSection.isEmpty()) {
//            Object obj = properties.get(rootSection);
//            if (obj != null) {
//                return Arrays.asList(obj.toString().split(",|\\s+"));
//            }
//        }
//        return null;
    }

    public List<String> getAllInstances() {
        return getAllInstances(configPath);
    }
    @Override
    protected void analysisProperties() {
        InputStream inputStream = null;
        try {
            if (new File(configPath).exists()) {
                inputStream = new FileInputStream(new File(configPath));
            } else {
                inputStream = ComponentConfiguration.class.getClassLoader().getResourceAsStream(StringUtils.strip(configPath, "/"));
            }
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    public HashMap<String, Context> load(String path) {
        this.configPath = path;
        return load();
    }

    public HashMap<String, Context> load() {
        synchronized (ComponentConfiguration.class) {
            if (null == map ){
                map = new HashMap<>();
            }
            String rootSection = "";

            if (!map.containsKey(configPath)) {
                analysisProperties();
                Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();

                HashMap<String,Context> innerMap = new HashMap<>();
                while (iterator.hasNext()) {
                    Map.Entry en = iterator.next();
                    if (rootSection.isEmpty() || en.getKey().toString().length() < rootSection.length()) {
                        rootSection = en.getKey().toString();
                    }
                }

                if (!rootSection.isEmpty()) {
                    Object obj = properties.get(rootSection);
                    if (obj != null) {
                        rootMap.put(configPath, Arrays.asList(obj.toString().split(",|\\s+")));

                    }
                }

                String root = rootSection + ".";
                iterator = properties.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry en = iterator.next();
                    String key = en.getKey().toString();
                    if (key.length() < root.length()) {
                        continue;
                    }
                    key = key.substring(root.length(), key.length());

                    String instanceName = key.substring(0, key.indexOf("."));
                    Context context = innerMap.get(instanceName);
                    if (context == null) {
                        context = new Context();
                        innerMap.put(instanceName, context);
                    }
                    String innerKey = key.substring(key.indexOf(".") + 1, key.length());

                    context.put(innerKey, en.getValue());
                }
                map.put(configPath,innerMap);
            }
        }
        properties.clear();
        return map.get(configPath);
    }
}
