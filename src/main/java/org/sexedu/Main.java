package org.sexedu;

import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        long start = System.currentTimeMillis();
        List<String> url = Tools.exploreURL(1000);
        ExecutorService executor = Executors.newFixedThreadPool(config.Config().getThread());
        System.out.println("开始爬取！");
        final int[] i = {0};
        executor.execute(() -> {
            for (String s : url) {
                Result result = Tools.getInfo(Jsoup.parse(Tools.getHTML(s)));
                String title = result.getTitle();
                String tag = result.getTags().toString();
                String preview = result.getPreview();
                Map<String,Object> params = new TreeMap<>();
                params.put("title",title);
                params.put("tag",tag);
                params.put("link",s);
                params.put("preview",preview);
                HttpUtil.post("http://127.0.0.1:8080/video/add",params);
                i[0]++;
            }
        });
        executor.shutdown();
        while (!executor.isTerminated()) {
            if (i[0] % 20 == 0) {
                System.out.println("已爬取" + i[0] + "个视频");
                float time = (System.currentTimeMillis() - start) / 1000;
                System.out.println("速度：" + (i[0] / time + "个/秒"));
                Thread.sleep(1000*10);;
            }
        }

    }
}

