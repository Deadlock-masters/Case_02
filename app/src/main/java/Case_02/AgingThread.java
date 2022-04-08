package Case_02;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AgingThread extends Thread{
    
    public static final int msb = 67108864; // 2^26
    private static ConcurrentHashMap<Integer, Page> pagination;
    private int NR; // number of references
    private int n;// Number of the page
    private static int oldest;// The oldest page
    private static long oldest_age;// The age of the oldest page
    private int MP;// the number of pages frames
    
    /**
     * Constructor for the class AgingThread
     * @param pagination
     * @param NR 
     * @param MP 
     * @param n
     */
    public AgingThread(ConcurrentHashMap<Integer,Page> pagination, int NR, int MP, int n) {
        this.pagination = pagination;
        this.NR = NR;
        this.MP = MP;
        this.n = n;
        this.oldest = 0;
    }

    /**
     * This method is going to update the age of the pages
     */
    public synchronized static void updateAge() {

        for (Object kv : pagination.entrySet()) {
            Map.Entry<Integer,Page>entry = (Map.Entry) kv;
            int key = (int) entry.getKey();
            Page tempPage = entry.getValue();
            long age = tempPage.getAge();
            age = age >> 1;
            tempPage.setAge(age);
            pagination.replace(key, tempPage);

            if (age < oldest_age) {
                oldest = (int) entry .getKey();
                oldest_age = age;
            }
        }
    }

    public synchronized static void AgePages() throws InterruptedException {
        updateAge();
        
    }

    public void run() {
        try {
            Thread.sleep(10);
            AgePages();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ConcurrentHashMap<Integer,Page> getPagination() {
        return pagination;
    }

    public int eldest() {
        return oldest;
    }
}

