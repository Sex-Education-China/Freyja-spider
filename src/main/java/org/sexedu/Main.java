package org.sexedu;

import org.jsoup.Jsoup;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.sexedu.Tools.*;

public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List<String> url = exploreURL(480);
        int thread = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        for (int i = 0; i < url.size(); i++) {
            String s = url.get(i);
            executorService.execute(() -> {
                Result result = getInfo(Jsoup.parse(getHTML(s)));
                log("标题" + result.getTitle());
                log("播放量" + result.getView());
                log("标题" + result.getTitle());
                log("视频链接" + result.getVideoLink());
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {

        }
            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start) + "ms");
            System.out.println("平均耗时：" + (end - start) / 1000.0 / url.size() + "s");
    }
}

