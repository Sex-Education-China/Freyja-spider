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
    public static void main(String[] args) {
        //get all the collections
        MongoCollection<Document> collection = database.getCollection("videos");
        BigInteger id = BigInteger.valueOf(getMaxId(collection));
        List<String> url = Tools.exploreURL(20);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        executor.execute(() -> {
            for (String s : url) {
                Result result = Tools.getInfo(Jsoup.parse(Tools.getHTML(s)));
                String title = result.getTitle();
                String link = result.getVideoLink();
                String tag = result.getTags().toString();
                Document document = new Document("id", id.intValue())
                        .append("title", title)
                        .append("link", link)
                        .append("tag", tag);
                collection.insertOne(document);
                id.add(BigInteger.valueOf(1));
            }
        });
        executor.shutdown();
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
            id = 0;
        }
        return maxId;
    }
    public static boolean contains(String str) {
        MongoCollection<Document> collection = database.getCollection("videos");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> cursor = findIterable.iterator();
        boolean contains = false;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            if (document.get("VideoId").toString().contains(str)) {
                contains = true;
                break;
            }
        }
        return contains;
    }
}

