package com.loheagn.fememer;
import java.net.URL;

import org.jsoup.*;
import org.jsoup.nodes.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main ( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        String html = "<html><head><title>First parse</title></head>"
  + "<body><p>Parsed HTML into a doc.</p></body></html>";
Document doc = Jsoup.connect("http://www.baidu.com").get();
System.out.println(doc);


    }
}
