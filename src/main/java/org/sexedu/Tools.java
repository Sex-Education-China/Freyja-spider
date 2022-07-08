package org.sexedu;

import cn.hutool.http.HttpUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */){
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String getURL(String regex, String data) {
        //sb存放正则匹配的结果
        StringBuffer sb = new StringBuffer();
        //编译正则字符串
        Pattern p = Pattern.compile(regex);
        //利用正则去匹配
        Matcher matcher = p.matcher(data);
        //如果找到了我们正则里要的东西
        while (matcher.find()) {
            //保存到sb中，"\r\n"表示找到一个放一行，就是换行
            sb.append(matcher.group() + "\r\n");
        }
        return sb.toString();
    }
    public static String getHTML(String url) {
        String str = HttpUtil.createGet(url)
                .cookie("PHPSESSID=jmjifou28acrclhuil9oj0d5j6; kt_ips=205.198.104.201%2C46.20.109.22")
                .header("Host", "jable.tv")
                .setHttpProxy("127.0.0.1", 7890)
                .execute().body();
        return str;
    }
    public static List<String> exploreURL(int time) {
        List<String> result = new LinkedList();
        Map<String,String> header = new HashMap<>();
        header.put("Host","jable.tv");
        header.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        header.put("Cookie","PHPSESSID=jmjifou28acrclhuil9oj0d5j6; kt_ips=205.198.104.201%2C46.20.109.22");
        Document doc = Jsoup.parse(getHTML("https://jable.tv/new-release//"));
        Elements elements = doc.select(".cover-md>a");
        for (Element element : elements) {
            String url = element.attr("href");
            result.add(url);
        }
        int n = 0;
        int page = 1;
        while (n<=time) {
            String url = "https://jable.tv/new-release/?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=release_year&from=" + page +  "&_=1656735210112";
            Document doc1 = Jsoup.parse(getHTML(url));
            Elements elements1 = doc1.select(".cover-md>a");
            for (Element element : elements1) {
                String url1 = element.attr("href");
                result.add(url1);
                ++n;
                if (n>time) {
                    break;
                }
            }
        }
        List<String> realResult = new LinkedList();
        int a = 0;
        while (true) {
            realResult.add(result.get(a));
            a++;
            if (a>=result.size()-25) {
                break;
            }
        }
        return realResult;
    }
    public static Result getInfo(Document doc) {
        List<String> tag = new LinkedList<>();
        Result result = new Result();
        Elements tags = doc.select(".h6-md>a");
        for (Element element : tags) {
            tag.add(element.text());
        }
        Elements title = doc.select(".header-left>h4");
        String origin = doc.select(".mr-3").text();
        int view = Integer.parseInt(stripNonDigits(origin.substring(1)));

        Elements scripts = doc.select("script");
        String lines[] = scripts.toString().split("\n");
        String url = "";
        for (int i = 0 ;i<lines.length;i++) {
            if(lines[i].contains("hlsUrl")) {
                url = getURL("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]",lines[i]);
                break;
            }
        }
        result.setVideoLink(url);
        result.setView(view);
        result.setTitle(title.text());
        result.setTags(tag);
        return result;
    }
    public static void log(String str) {
        Logger.getLogger("").fine(str);
    }
}
@ToString
@Getter
@Setter
class Result {
    private List<String> tags;
    private String title;
    private int view;
    private String videoLink;
}
