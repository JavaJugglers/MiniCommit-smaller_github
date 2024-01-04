package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Groot implements Serializable { //keeps track of staging-area, whatever that means.
    private Map treemap;

    public Groot() {
        Map<String, String> treemap = new TreeMap<String, String>();
        this.treemap = treemap;
    }
    public Map get_treemap() {
        return this.treemap;
    }
}
