package org.sexedu;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static MongoClient client = new MongoClient("127.0.0.1", 27017);
    static MongoDatabase database = client.getDatabase("bornhub");
    static MongoCollection<Document> collection = database.getCollection("videos");
    static FindIterable<Document> findIterable = collection.find();
    static MongoCursor<Document> cursor = findIterable.iterator();
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        //get all the collections
        MongoCollection<Document> collection = database.getCollection("videos");
        BigInteger id = BigInteger.valueOf(getMaxId(collection));
        List<String> url = Tools.exploreURL(1000);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        executor.execute(() -> {
            for (String s : url) {
                Result result = Tools.getInfo(Jsoup.parse(Tools.getHTML(s)));
                String title = result.getTitle();
                String link = result.getVideoLink().replace("\r\n", "");
                String tag = result.getTags().toString();
                if (!contains(title)) {
                    Document document = new Document("id", id.toString())
                            .append("title", title)
                            .append("link", link)
                            .append("tag", tag);
                    collection.insertOne(document);
                    id.add(BigInteger.valueOf(1));
                }
            }
        });
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        System.out.println("耗时：: " + (System.currentTimeMillis() - start)/1000 + "秒");
    }
    //get max id from mongodb
    public static int getMaxId(MongoCollection<Document> collection) {
        int maxId = Integer.MIN_VALUE;
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> cursor = findIterable.iterator();
        int id =0;
        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                 id = Integer.parseInt(document.get("VideoId").toString());
                if (id>maxId) {
                    maxId = id;
                }
            }
        } catch (Exception e) {
            maxId = 0;
        }
        return maxId;
    }
    public static boolean contains(String str) {
        boolean contains = false;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            if (document.toString().contains(str)) {
                contains = true;
                break;
            }
        }
        return contains;
    }
}

