package Case_02;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;

public class UpdaterThread extends Thread{
    private ConcurrentHashMap<Integer,Page> pagination;
    private ArrayList<Integer> RAM;
    private CyclicBarrier barrier;
    private ArrayList<Integer> pageRefs;
    private int NR;
    private int MP;
    private int failures;
    private int count;

    public UpdaterThread(ConcurrentHashMap<Integer, Page> pagination, CyclicBarrier barrier, ArrayList<Integer> pageRefs, int MP){
        this.pagination = pagination;
        this.barrier = barrier;
        this.pageRefs = pageRefs;
        this.MP = MP;
        this.NR = pageRefs.size();
        this.failures = 0;
        this.count = 0;
        this.RAM = new ArrayList<Integer>();
    }

    @Override
    public void run() {
        for (int i = 0; i < NR; i++) {
            int pageN = pageRefs.get(i);
            try {
                updatePagination(pageN);
                Thread.sleep(20);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private synchronized void updatePagination(int pageN) throws InterruptedException {
        Page page = pagination.get(pageN);

        AgingThread t = new AgingThread(pagination, NR, MP, pageN);
        
        t.start();

        if (!page.getLoaded())
        {
            page.load();

            if (RAM.size() < MP)
            {
                RAM.add(pageN);
            }
            else
            {
                int eldest = t.eldest();

                pagination = t.getPagination();
                
                Page p = pagination.get(eldest);
                p.unload();
                RAM.remove(eldest);
                RAM.add(pageN);
                pagination.replace(eldest, p);
            }
            pagination.replace(pageN, page);

            failures ++;
        }

        pagination.replace(pageN, page);
    }

    public int getFailures()
    {
        return failures;
    }

    public int getLoadedPages()
    {
        return count;
    }

}
