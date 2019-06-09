package com.loheagn.fememer.render;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.*;
import com.loheagn.fememer.tools.*;

/**
 * render
 */
public class Render {

    public static String render(Map<String, String> objects) {
        try {
            // 初始化
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
            configuration.setDirectoryForTemplateLoading(new File(Values.getWebappPath()));
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Template template = configuration.getTemplate("templates/main.html");
            Writer writer = new StringWriter();
            template.process(objects, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String renderArticle(String title, String author, String bigSource, String tag, String webUrl,
            String source, String content) {
        Map<String, String> objects = new HashMap<String, String>();
        objects.put("title", title);
        objects.put("author", author);
        objects.put("bigSource", bigSource);
        objects.put("source", source);
        objects.put("tag", tag);
        objects.put("webUrl", webUrl);
        objects.put("content", content);
        return render(objects);
    }
}