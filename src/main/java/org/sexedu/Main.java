package org.sexedu;

import cn.hutool.http.HttpUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.Jsoup;

import java.util.List;

import static org.sexedu.Tools.*;

public class Main {
    public static void main(String[] args) {
        List<String> url = exploreURL();
        for (int i = 0; i < url.size(); i++) {
            String s = url.get(i);
            Result result = getInfo(Jsoup.parse(getHTML(s)));
            System.out.println("标题："+result.getTitle());
            System.out.println("播放量:"+result.getView());
            System.out.println("标签:"+result.getTags().toString());
            System.out.println("视频地址:"+result.getVideoLink());

        }
    }
}

