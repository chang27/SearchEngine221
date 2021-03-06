package com.uci.indexer;

import com.google.common.base.Strings;
import com.uci.io.MyFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by junm5 on 1/17/17.
 */
@Component
public class TextProcessor {

    @Autowired
    private StopWordsFilter stopWordsFilter;

    @Autowired
    private Stemmer stemmer;

    private String PATTERN = "[a-z0-9A-Z]+";
    private Pattern compile = Pattern.compile(PATTERN);

    public List<String> tokenize(String filePath) {
        if (filePath == null || filePath.equals("")) {
            System.out.println("Input file is null or empty");
            return new ArrayList();
        }
        MyFileReader fileReader = null;
        try {
            fileReader = new MyFileReader(filePath);
            //readLines all data from files
            String txt = fileReader.readAll().trim();
            return getTokens(txt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileReader.close();
        }
        return new ArrayList();
    }

    public void print(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList(map.entrySet());
        Comparator<Map.Entry<String, Integer>> valueComparator = (o1, o2) -> o2.getValue() - o1.getValue();
        Collections.sort(list, valueComparator);

        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    public Map<String, Integer> buildFreMap(List<String> input) {
        HashMap<String, Integer> res = new HashMap();
        if (input == null || input.isEmpty()) {
            return res;
        }
        for (String token : input) {
            res.put(token, res.getOrDefault(token, 0) + 1);
        }
        return res;
    }

    public List<String> getTokens(String input) {
        Matcher matcher = compile.matcher(input.toLowerCase());
        List<String> res = new ArrayList();
        while (matcher.find()) {
            res.add(matcher.group());
        }
        return res;
    }

    public List<String> getTokensByUrl(String url) {
        List<String> tokens = new ArrayList<>();
        try {
            URL ur = new URL(url);
            String path = ur.getPath();
            tokens.addAll(getTokens(path));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public List<String> stemstop(List<String> tokens) {
        List<String> res = new ArrayList<>();
        tokens = stopWordsFilter.filter(tokens);
        for (String s : tokens) {
            String out = stemmer.stem(s);
            res.add(out);
        }
        return res;
    }


}