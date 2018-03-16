/*
* IUncompress.java 
* Created on  202017/2/4 13:22 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.compress;

import java.io.IOException;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public interface IUncompress {
    String uncompress(String str,String charset)  throws IOException;
    String uncompress(String str)  throws IOException;
}
