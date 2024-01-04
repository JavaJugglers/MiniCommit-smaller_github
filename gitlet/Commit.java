package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
    private String timestamp;
    private String parent;
    private String barcode;
    private Map treemap;
    private ArrayDeque tracker;
    private Map Big_brother;
    private ArrayDeque Small_brother;

    private Map tracker_for_added_string;




    public Commit(String message, String parent, Map<String, String> treemap, ArrayDeque tracker, Map Big_brother, ArrayDeque small_brother, Map tracker_for_added_string) {
        this.Big_brother = Big_brother;
        this.tracker_for_added_string = tracker_for_added_string;
        this.Small_brother = small_brother;
        this.tracker = tracker;
        //this.treemap = treemap;
        if(treemap == null) {
            this.treemap = new TreeMap<String, String>();
        } else {
            this.treemap = new TreeMap<String, String>(treemap);
        }
        this.message = message;
        this.parent = parent;
        DateFormat Formatting = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        Formatting.setTimeZone(TimeZone.getTimeZone("PST"));

        if (this.parent == "None") {
            this.timestamp = Formatting.format(new Date(0)); /* https://stackoverflow.com/questions/308683/how-can-i-get-the-current-date-and-time-in-utc-or-gmt-in-java */
            this.barcode = Utils.sha1(this.message, this.parent, this.timestamp);
        } else {
            this.timestamp = Formatting.format(new Date());
            this.barcode = Utils.sha1(this.message, this.parent, this.timestamp);
        }
    }

    public Map get_Big_brother() {
        return this.Big_brother;
    }
    public ArrayDeque get_Small_brother() {
        return this.Small_brother;
    }
    public String get_barcode() {
        return this.barcode;
    }
    public ArrayDeque get_tracker() {
        return this.tracker;
    }
    public Map get_treemap() {
        return this.treemap;
    }

    public String get_parent() {
        return this.parent;
    }

    public String get_timestamp() {
        return this.timestamp;
    }

    public String get_message() {
        return this.message;
    }

    public String getBlobID(String fileName) {
        return (String) treemap.get(fileName);
    }

    public Map get_Tracker_for_added_string() {
        return this.tracker_for_added_string;
    }
}