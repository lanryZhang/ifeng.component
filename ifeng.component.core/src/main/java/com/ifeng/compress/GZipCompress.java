/*
* GZipCompress.java 
* Created on  202017/2/4 13:23 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.compress;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class GZipCompress implements ICompress ,IUncompress{
    @Override
    public String compress(String str,String charset)  throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        FileInputStream fis = new FileInputStream(new File("D://1.txt"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        String res = "";
        try {
            gzip.write(str.getBytes());
            gzip.close();
            res = out.toString(charset);
        }finally {
            if (null != gzip) {
                gzip.close();
            }
            if (null != out){
                out.close();
            }
        }
        return res;
    }

    @Override
    public String uncompress(String str, String charset) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str
                .getBytes(charset));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        try {
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        }finally {
            if (null != gunzip) {
                gunzip.close();
            }
            if (null != in){
                in.close();
            }
            if (null != out){
                out.close();
            }
        }
        return out.toString();
    }

    @Override
    public String uncompress(String str) throws IOException {
        return uncompress(str,"ISO-8859-1");
    }

    @Override
    public String compress(String str) throws IOException {
        return compress(str,"ISO-8859-1");
    }
}
