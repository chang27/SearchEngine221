package com.uci.pr;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.uci.mode.Page;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by junm5 on 3/3/17.
 */
public class PageRank {
    //PR(A) = (1-d) + d ∑ (PR(Ti)/C(Ti))
    private static final double d = 0.85;

    public static double calculatePR(Page page) {
        if (page == null || page.isVisited()) {
            return 0;
        }
        List<Page> inputPages = page.getInputPages();
        double res = 1 - d;
        if (inputPages == null) {
            page.setScore(res);
            return res;
        }
        for (Page temp : inputPages) {
            res += d * calculatePR(temp) / temp.getOutputNumber();
        }
        page.setScore(res);
        return res;
    }

    public static void main(String[] args) {
        Page page = new Page("A");

        Page t1 = new Page("t1");
        Page t2 = new Page("t2");
        Page t3 = new Page("t3");

        t1.setOutputNumber(1);
        t2.setOutputNumber(1);
        t3.setOutputNumber(1);

        page.setInputPages(Lists.newArrayList(t1, t2, t3));
        Set<Page> set = new HashSet();


        for (Page page1 : set) {
            System.out.println(page1.getScore());
        }
    }

}
