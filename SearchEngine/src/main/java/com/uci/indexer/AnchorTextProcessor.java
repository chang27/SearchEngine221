package com.uci.indexer;

import com.uci.io.MyFileReader;
import com.uci.io.MyFileWriter;
import com.uci.mode.URLPath;
import com.uci.service.DBHandler;
import com.uci.utils.SysPathUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by junm5 on 3/1/17.
 */
@Service
public class AnchorTextProcessor {

    private Map<String, Set<String>> archors = new HashMap<>();

    @Autowired
    private BookUrlRepository bookUrlRepository;

    private String prefix = SysPathUtil.getSysPath() + "/WEBPAGES_RAW/";
    private String anchorText = SysPathUtil.getSysPath() + "/conf/anchor.txt";
    private final String SPLITOR = "######";
    @Autowired
    private DBHandler dbHandler;

    private final String ANHOR_KEY = "ANCHOR";

    public void parseAnchorTextFromFile() {
        List<URLPath> urlPaths = bookUrlRepository.getURLPaths();
        for (URLPath urlPath : urlPaths) {
            String path = prefix + urlPath.getPath();
            MyFileReader myFileReader = new MyFileReader(path);
            String html = myFileReader.readAll();
            if (html != null && !html.isEmpty()) {
                try {
                    Document doc = Jsoup.parse(html);
                    add(doc, urlPath.getUrl());
                } catch (Exception exp) {
                    System.out.println("parsing failed: " + path);
                    exp.printStackTrace();
                } finally {
                    myFileReader.close();
                }

            }
        }
    }

    public void saveIntoRedis() {
        for (String key : archors.keySet()) {
            dbHandler.put(ANHOR_KEY + key, getAnchorText(archors.get(key)));
        }
    }

    public void saveAnchorTextIntoFile() {
        MyFileWriter.createFile(anchorText);
        MyFileWriter myFileWriter = null;
        try {
            myFileWriter = new MyFileWriter(anchorText, true);
            for (String key : archors.keySet()) {
                Set<String> set = archors.get(key);
                String text = new StringBuffer().append(key).append(SPLITOR).append(getAnchorText(set)).toString();
                myFileWriter.writeLine(text);
                myFileWriter.flush();
                System.out.println("storing anchor text: " + key);
            }
            myFileWriter.flush();

        } finally {
            myFileWriter.close();
        }
    }

    public void add(String url, String text) {
        Set<String> set = archors.get(url);
        if (set == null) {
            set = new HashSet<>();
            archors.put(url, set);
        }
        set.add(text);
    }

    public void add(Document document, String url) {
        Map<String, String> outgoingLinks = Htmlparser.getOutgoingLinks(document, url);
        for (String key : outgoingLinks.keySet()) {
            add(key, outgoingLinks.get(key));
        }
    }

    public String getAnchorText(Set<String> set) {
        return set == null ?
                null : set.stream().reduce((x, y) -> (x + y).replace("\n", " ")).get();
    }

    public String getAnchorText(String url) {
        Set<String> set = archors.get(url);
        return getAnchorText(set);
    }

    public String getAnchorTextFromRedis(String url) {
        return dbHandler.get(ANHOR_KEY + url, String.class);
    }


}
