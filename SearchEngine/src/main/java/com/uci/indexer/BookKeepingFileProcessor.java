package com.uci.indexer;

import com.uci.io.MyFileReader;
import com.uci.mode.URLPath;
import com.uci.service.DBHandler;
import com.uci.utils.SysPathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * parsing html file into document and generating index file
 * Created by junm5 on 2/25/17.
 */
@Service
public class BookKeepingFileProcessor {

    @Autowired
    private BookUrlRepository bookUrlRepository;

    @Autowired
    private Indexer indexer;

    @Autowired
    private TextProcessor textProcessor;

    @Autowired
    private DBHandler dbRepository;

    private int i = 0;

    //    private String prefix = SysPathUtil.getSysPath() + "/SearchEngine/WEBPAGES_RAW/";
    private String prefix = SysPathUtil.getSysPath() + "/WEBPAGES_RAW/";

    //1 - 18660
    public void readFileIntoDocument() {
        while (bookUrlRepository.hashNext()) {
            URLPath urlPath = bookUrlRepository.next();
            String path = prefix + urlPath.getPath();
            MyFileReader myFileReader = new MyFileReader(path);

            String html = myFileReader.readAll();
            if (html != null && !html.isEmpty()) {
                try {
                    com.uci.mode.Document document = Htmlparser.generateDocument(html, urlPath.getUrl());
                    if (document != null) {
                        i++;
                        document.setId(i);
                        buildDocumentIndex(document);
//                        dbRepository.put(String.valueOf(i), document);
                        System.out.println("generate document index i = " + i);
                    }
                } catch (Exception exp) {
                    System.out.println("parsing failed: " + path);
                    exp.printStackTrace();
                } finally {
                    myFileReader.close();
                }
            }
        }
        System.out.println("saving indexing.....");
        indexer.calculateTFIDF();
        indexer.saveIndexes();
    }

    private void buildDocumentIndex(com.uci.mode.Document document) {
        String input = new StringBuffer().append(document.getTitle())
                .append("#").append(document.getText()).toString();
        List<String> tokens = textProcessor.getTokens(input);
        List<String> stemstop = textProcessor.stemstop(tokens);
        indexer.indexize(document.getId(), stemstop);
    }
}
