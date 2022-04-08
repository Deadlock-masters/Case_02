package Case_02;

/**
 * In this file we are going to simulate a page in the pagination system that we are going to use
 * 
 * @author Juan Andres Mendez, Isaac David Bermudez
 */

public class Page {

    /* ****************************
     * 
     * Attributes
     * 
     * ***************************/
    private int n; // Number of the page

    private boolean loaded; // If the page is loaded or not
    
    private long age; // The age of the page

    /* ****************************
     * 
     * Constructors
     * 
     * ***************************/        
    
    /**
     * Constructor for the class Pag
     * @param n
     */
    public Page(int n) {
        this.n = n;
        this.loaded = false;
        this.age = -1;
    }

    /* ****************************
     * 
     * Getters
     * ****************************/

    public int getN() {
        return this.n;
    }

    public boolean getLoaded() {
        return this.loaded;
    }

    public long getAge() {
        return this.age;
    }

    /* ********************************
     * 
     * Setters
     * *******************************/

    public void load() {
        this.loaded = true;
        this.age = 67108864;
    }

    public void unload() {
        this.loaded = false;
        this.age = -1;
    }

    public void setAge(long age) {
        this.age = age;
    }

}
