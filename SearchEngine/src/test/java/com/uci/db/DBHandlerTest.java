package com.uci.db;

import com.google.common.collect.Lists;
import com.uci.ServerApplication;
import com.uci.constant.Table;
import com.uci.indexer.OneGramIndexer;
import com.uci.indexer.Stemmer;
import com.uci.indexer.TwoGramIndexer;
import com.uci.mode.IndexEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Created by junm5 on 3/5/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@WebAppConfiguration
public class DBHandlerTest {
    @Autowired
    private DBHandler dbHandler;

    @Autowired
    private Stemmer stemmer;

    @Autowired
    private OneGramIndexer indexer;

    @Autowired
    private TwoGramIndexer twoGramIndexer;
//    @Test
//    public void should_clear_document_table() throws Exception {
//        dbHandler.clearAll(Table.ANCHOR);
//    }

    @Test
    public void should_store_list_() throws Exception {
        IndexEntry indexEntry1 = new IndexEntry().setId(1).setTfIdf(0.1);
        IndexEntry indexEntry2 = new IndexEntry().setId(2).setTfIdf(0.2);
        IndexEntry indexEntry3 = new IndexEntry().setId(3).setTfIdf(0.3);
        IndexEntry indexEntry4 = new IndexEntry().setId(4).setTfIdf(0.4);

        List<IndexEntry> list = Lists.newArrayList(indexEntry1, indexEntry2, indexEntry3, indexEntry4);
        dbHandler.put(Table.TERM, "hello", list);
        List<IndexEntry> list1 = (List<IndexEntry>)dbHandler.get(Table.TERM, "hello", List.class);
        System.out.println(list1);

    }
    @Test
    public void should_claer_page_rank() throws Exception {
        dbHandler.clearAll(Table.RANK);
        dbHandler.clearAll(Table.DOCUMENT);
        dbHandler.clearAll(Table.TERM);
    }

    @Test
    public void should_get_() throws Exception{
        String computer = stemmer.stem("computer");
        String games = stemmer.stem("games");
        List<String> nGrams = twoGramIndexer.getNGrams(Lists.newArrayList(computer, games), 2);
        System.out.println(nGrams);
        List<IndexEntry> indexEntities = indexer.getIndexEntities(nGrams.get(0));
        System.out.println(indexEntities);
    }
}