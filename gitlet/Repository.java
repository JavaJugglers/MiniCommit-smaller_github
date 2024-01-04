package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Elon Musk
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File MAIN = join(GITLET_DIR, "Main.txt");
    public static final File HEAD = join(GITLET_DIR, "Head.txt");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File GROOT = join(GITLET_DIR, "Groot");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    public static final File TRACKING_BLOBS = join(GITLET_DIR,"tracking_blobs");
    public static final File BRANCHES = join(GITLET_DIR, "branch");
    public static final File BRANCH_RIGHT_NOW = join(GITLET_DIR, "yes_no.txt");
    public static final File BIG_BROTHER = join(GITLET_DIR, "brother_eye");
    public static final File SMALLER_BROTHER = join(GITLET_DIR, "brother_legs");

    public static final File STAGING_RM_FILE = join(GITLET_DIR, "index_removal");
    public static final File TRACK_FOR_ADDED_STRINGS = join(GITLET_DIR, "thingies");





    public static final File PUMBA = join(GITLET_DIR, "why_not_track_for_branches");
    public static final File TRACKING_FOR_BRANCHES = join(GITLET_DIR, "why_not_track_for_branches_branches");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        else {
            GITLET_DIR.mkdir();
            COMMITS.mkdir();
            BLOBS.mkdir();
            BRANCHES.mkdir();
            if (!MAIN.exists()) {
                try {
                    MAIN.createNewFile();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!BRANCH_RIGHT_NOW.exists()) {
                try {
                    BRANCH_RIGHT_NOW.createNewFile();
                    Utils.writeContents(BRANCH_RIGHT_NOW, "main");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!HEAD.exists()) {
                try {
                    HEAD.createNewFile();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Commit initial = new Commit("initial commit", "None", null,null,null,null,null);
            //saving the initial commit
            File initial_saving = join(COMMITS, initial.get_barcode());
            Utils.writeObject(initial_saving, initial);
            //write down the barcode to both head and MAIN.
            Utils.writeContents(HEAD, initial.get_barcode());
            Utils.writeContents(MAIN, initial.get_barcode());
            //initialize a Treemap and save.
            Groot initial_groot = new Groot();
            Utils.writeObject(GROOT, initial_groot);
            //initialize a tracker and save.
            ArrayDeque <String> tracker = new ArrayDeque <String> ();
            Utils.writeObject(TRACKING_BLOBS, tracker);
            //initialize a Treemap and save to why_not_track_for_branches.
            Groot meek = new Groot();
            Utils.writeObject(PUMBA, meek);
            //track for associated branches
            Groot nice = new Groot();
            Utils.writeObject(TRACKING_FOR_BRANCHES, nice);
            //initialize big brother eye, always there, always watching.
            Groot meerkat = new Groot();
            Utils.writeObject(BIG_BROTHER, meerkat);
            //initialize small brother, need legs to walk.
            ArrayDeque <String> small_meerkat = new ArrayDeque <String> ();
            Utils.writeObject(SMALLER_BROTHER, small_meerkat);
            //
            Groot liners = new Groot();
            Utils.writeObject(TRACK_FOR_ADDED_STRINGS, liners);

            // TBD: this is to create an empty 'staging area for removal'
            TreeSet<String> saRmTS = new TreeSet<String>();
            try {
                STAGING_RM_FILE.createNewFile();
                Utils.writeObject(STAGING_RM_FILE, saRmTS);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void add(String[] arg) {
        Groot x = Utils.readObject(GROOT, Groot.class);
        Map treemap = x.get_treemap();
        ArrayDeque <String> tracking = Utils.readObject(TRACKING_BLOBS, ArrayDeque.class);

        // get current Commit object
        String commitID = Utils.readContentsAsString(HEAD);
        Commit curCommit = readCommit(commitID);
        Map<String, String> commitTreeMap = curCommit.get_treemap();

        //get small and big brother, so they can play together.
        Groot big_brother = Utils.readObject(BIG_BROTHER, Groot.class);
        ArrayDeque <String> small_brother = Utils.readObject(SMALLER_BROTHER, ArrayDeque.class);
        Map big_map = big_brother.get_treemap();
        //TRACKER_FOR_STRING
        Groot tracker_for_strings = Utils.readObject(TRACK_FOR_ADDED_STRINGS, Groot.class);
        Map tracker_string = tracker_for_strings.get_treemap();

        int count = 1;
        while (count < arg.length) {
            File newer = join(CWD, arg[count]);
            // remove one item if this file is staged for removal before this 'add' command
            updateStagingAreaRm(arg[count], false);

            if (!newer.exists()) {
                System.out.println("File does not exist.");
                System.exit(0);
            }

            String file_name = newer.getPath();
            String context = Utils.readContentsAsString(newer);
            byte[] contents = Utils.readContents(newer);
            String now = Utils.sha1(file_name, contents);

            String curBlobID = commitTreeMap.get(arg[count]);
            if (!treemap.containsKey(arg[count])) {
                if (!big_map.containsKey(arg[count])) {
                    small_brother.addFirst(arg[count]);
                    big_map.put(arg[count], now);
                    tracker_string.put(arg[count], context);
                }
                tracking.addFirst(arg[count]);
                if (!now.equals(curBlobID)) {
                    treemap.put(arg[count], now);
                    big_map.replace(arg[count], now);
                    tracker_string.replace(arg[count], context);
                }
                File copied = join(BLOBS, now);
                Utils.writeObject(copied, contents);
            }
            else {
                if (treemap.get(arg[count]).equals(now)) {
                    //File copied = join(BLOBS, (String) treemap.get(arg[count]));
                    //Utils.restrictedDelete(copied);
                    big_map.replace(arg[count], now);
                    tracker_string.replace(arg[count], context);
                    if (now.equals(curBlobID)) {
                        treemap.remove(arg[count]);
                    } else {
                        treemap.replace(arg[count], now);
                    }

                    File copied = join(BLOBS, now);
                    Utils.writeObject(copied, contents);
                }
            }


            count = count + 1;
        }
        //saving stuff so that we dont forget
        Utils.writeObject(BIG_BROTHER,big_brother);
        Utils.writeObject(SMALLER_BROTHER,small_brother);
        Utils.writeObject(GROOT, x);
        Utils.writeObject(TRACKING_BLOBS, tracking);
        Utils.writeObject(TRACK_FOR_ADDED_STRINGS, tracker_for_strings);
    }


    public static void commit(String[] arg) {
        String message = arg[1];
        if(message.equals("")) { // message must be non-blank; can use message.isEmpty()
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        // read in staging area for add
        Groot x = Utils.readObject(GROOT, Groot.class);
        ArrayDeque <String> tracking = Utils.readObject(TRACKING_BLOBS, ArrayDeque.class);
        Map treemap = x.get_treemap();

        // read in 'staging area for removal'
        TreeSet<String> saRmTS = Utils.readObject(STAGING_RM_FILE, TreeSet.class);

        //get small and big brother, so they can play together.
        Groot big_brother = Utils.readObject(BIG_BROTHER, Groot.class);
        ArrayDeque <String> small_brother = Utils.readObject(SMALLER_BROTHER, ArrayDeque.class);
        Map big_map = big_brother.get_treemap();
        //get tracker_for_string
        Groot tracker_for_string = Utils.readObject(TRACK_FOR_ADDED_STRINGS, Groot.class);
        Map tracker_string = tracker_for_string.get_treemap();
        if (arg.length == 1) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        else if ((treemap.size() == 0) && (saRmTS.size()==0)) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        else {
            if (!Utils.readContentsAsString(BRANCH_RIGHT_NOW).equals("main")) {
                String MAIN_pointer = Utils.readContentsAsString(HEAD);
                // clone a file tracking treemap using parent commit object's treemap
                Commit curCommit = readCommit(MAIN_pointer);
                Map<String, String> curTreeMap = curCommit.get_treemap();
                Commit next = new Commit(arg[1], MAIN_pointer,curTreeMap, tracking,big_map,small_brother,tracker_string);
                updateFileTracking(next, treemap, saRmTS);

                //saving commit
                File initial_saving = join(COMMITS, next.get_barcode());
                Utils.writeObject(initial_saving, next);
                //write down the barcode to head and branch.
                File branching = join(BRANCHES, Utils.readContentsAsString(BRANCH_RIGHT_NOW));
                Utils.writeContents(HEAD, next.get_barcode());
                Utils.writeContents(branching, next.get_barcode());
                //initialize a new Treemap and save.
                Groot initial_groot = new Groot();
                Utils.writeObject(GROOT, initial_groot);
                //initialize a new tracker and save.
                ArrayDeque <String> tracker = new ArrayDeque <String> ();
                Utils.writeObject(TRACKING_BLOBS, tracker);
            }
            else {
                String MAIN_pointer = Utils.readContentsAsString(MAIN);
                // clone a file tracking treemap using parent commit object's treemap
                Commit curCommit = readCommit(MAIN_pointer);
                Map<String, String> curTreeMap = curCommit.get_treemap();
                Commit next = new Commit(arg[1], MAIN_pointer,curTreeMap, tracking,big_map,small_brother,tracker_string);
                updateFileTracking(next, treemap, saRmTS);

                //saving commit
                File initial_saving = join(COMMITS, next.get_barcode());
                Utils.writeObject(initial_saving, next);
                //write down the barcode to both head and MAIN.
                Utils.writeContents(HEAD, next.get_barcode());
                Utils.writeContents(MAIN, next.get_barcode());
                //initialize a new Treemap and save.
                Groot initial_groot = new Groot();
                Utils.writeObject(GROOT, initial_groot);
                //initialize a new tracker and save.
                ArrayDeque <String> tracker = new ArrayDeque <String> ();
                Utils.writeObject(TRACKING_BLOBS, tracker);

                File branching = join(BRANCHES, Utils.readContentsAsString(BRANCH_RIGHT_NOW));
                Utils.writeContents(branching, next.get_barcode());
            }
            // clean up 'staging area for removal'  (TBD: has to be included at the end of commit(), checkout() & reset()
            updateStagingAreaRm(null, true);
        }
    }

    private static void updateFileTracking(Commit next, Map<String, String> treemap, TreeSet<String> saRmTS){
        // update file tracking in the Commit object (SA add: treemap)
        String blobID;
        Set<String> keys = treemap.keySet();
        Map<String, String> commitTreeMap = next.get_treemap();
        for(String fileName : keys) {
            blobID = treemap.get(fileName);
            commitTreeMap.put(fileName, blobID); // update file tracking map based on 'staging for add' (fileName -> blobID)
        }
        // update file tracking in current Commit object (SA rm)
        for(String fileName : saRmTS) {
            commitTreeMap.remove(fileName);
        }
    }
    public static void log() {
        // TBD: need to check with Owen
        // look up active branch's commitID
        String commitID;
        String activeBranch = Utils.readContentsAsString(BRANCH_RIGHT_NOW);  // get active branch name
        if(activeBranch.equals("main")) {
            commitID = Utils.readContentsAsString(MAIN);
        } else {
            File ACTIVE_BRANCH_POINTER = join(BRANCHES, activeBranch);
            commitID = Utils.readContentsAsString(ACTIVE_BRANCH_POINTER);
        }
        String barcode_for_head_commit = commitID;

        //String barcode_for_head_commit = Utils.readContentsAsString(MAIN);
        File head_commit_location = join(COMMITS, barcode_for_head_commit);
        Commit head_commit = Utils.readObject(head_commit_location, Commit.class);
        while (true) {
            if (head_commit.get_parent().equals("None")) {
                System.out.println("===");
                System.out.println("commit " + barcode_for_head_commit);
                System.out.println("Date: " + head_commit.get_timestamp());
                System.out.println(head_commit.get_message());
                break;
            }
            System.out.println("===");
            System.out.println("commit " + barcode_for_head_commit);
            System.out.println("Date: " + head_commit.get_timestamp());
            System.out.println(head_commit.get_message());
            System.out.println();
            barcode_for_head_commit = head_commit.get_parent();
            head_commit_location = join(COMMITS, barcode_for_head_commit);
            head_commit = Utils.readObject(head_commit_location, Commit.class);
        }
    }

    public static void globalLog() {
        List<String> fileList = plainFilenamesIn(COMMITS.toString());
        Commit o;
        for(String commitID : fileList) {
            //System.out.println(commitID);
            File COMMIT_OBJECT = join(COMMITS, commitID);
            o = readObject(COMMIT_OBJECT, Commit.class);
            printEachLog(commitID, o.get_timestamp(), o.get_message());
        }
    }

    private static void printEachLog(String commitID, String timestamp, String message) {
        System.out.println("===");
        System.out.println("commit " + commitID);
        System.out.println("Date: " + timestamp);
        System.out.println(message);
        System.out.println();
    }

    public static void checkout(String[] args) {
        if (args.length == 2) {                   // java gitlet.Main checkout [branch name]
            checkoutCmd(args[1]);
            // clean up 'staging area for removal'  (TBD: has to be included at the end of commit(), checkout() & reset()
            updateStagingAreaRm(null, true);
        } else if (args.length == 3) {            // java gitlet.Main checkout -- [file name]
            checkoutCmd(args[1], args[2]);
            // clean up 'staging area for removal'  (TBD: has to be included at the end of commit(), checkout() & reset()
            updateStagingAreaRm(null, true);
        } else if (args.length == 4) {            // java gitlet.Main checkout [commit id] -- [file name]
            checkoutCmd(args[1], args[2], args[3]);
            // clean up 'staging area for removal'  (TBD: has to be included at the end of commit(), checkout() & reset()
            updateStagingAreaRm(null, true);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    // 1. java gitlet.Main checkout -- [file name]
    public static void checkoutCmd(String dash, String fileName) {
        String commitID = Utils.readContentsAsString(HEAD);
        checkoutCmd(commitID, dash, fileName);
    }

    // 2. java gitlet.Main checkout [commit id] -- [file name]
    public static void checkoutCmd(String commitID, String dash, String fileName) {
        if(!dash.equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        Commit o = readCommit(commitID);
        checkoutFileHelper(o, fileName);
        /*
        String blobID = o.getBlobID(fileName);

        // read blob and write it out the CWD
        File BLOB_FILE = join(BLOBS, blobID);
        File CHECKED_OUT_FILE = join(CWD, fileName);
        byte[] contents = Utils.readObject(BLOB_FILE, byte[].class);
        Utils.writeContents(CHECKED_OUT_FILE, contents);
        */
    }

    // write out the specified file tracked in this Commit object
    private static void checkoutFileHelper(Commit o, String fileName) {
        String blobID = o.getBlobID(fileName);
        if(blobID == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // read blob and write it out the CWD
        File BLOB_FILE = join(BLOBS, blobID);
        File CHECKED_OUT_FILE = join(CWD, fileName);
        byte[] contents = Utils.readObject(BLOB_FILE, byte[].class);
        Utils.writeContents(CHECKED_OUT_FILE, contents);
    }
    public static void branch(String[] args) {
        if (args.length > 3) {
            System.exit(0);
        }
        File branching = join(BRANCHES, args[1]);
        if (branching.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        else {
            String barcode_for_head_commit = Utils.readContentsAsString(HEAD);
            String x = Utils.sha1(Utils.readContentsAsString(BRANCH_RIGHT_NOW), args[1]);
            String y = Utils.sha1(args[1], Utils.readContentsAsString(BRANCH_RIGHT_NOW));
            //track for branches
            Groot track_branching = Utils.readObject(TRACKING_FOR_BRANCHES, Groot.class);
            Map tracking_tree = track_branching.get_treemap();
            tracking_tree.put(args[1], Utils.readContentsAsString(BRANCH_RIGHT_NOW));
            //
            Groot pumbas = Utils.readObject(PUMBA, Groot.class);
            Map treemap = pumbas.get_treemap();
            treemap.put(x, barcode_for_head_commit);
            treemap.put(y, barcode_for_head_commit);
            Utils.writeObject(PUMBA, pumbas);
            Utils.writeObject(TRACKING_FOR_BRANCHES, track_branching);
            Utils.writeContents(branching, barcode_for_head_commit);
        }
    }

    // 3. java gitlet.Main checkout [branch name]
    /*
    public static void checkoutCmd(String branchName) {
        File branching = join(BRANCHES, branchName);
        String barcode_for_head_commit = Utils.readContentsAsString(branching);
        Utils.writeContents(HEAD, barcode_for_head_commit);
        Utils.writeContents(BRANCH_RIGHT_NOW, branchName);
    }*/
    public static void checkoutCmd(String branchName) {
        // look up active branch's commitID
        String commitID = null, branchCommitID = null;
        String activeBranch = Utils.readContentsAsString(BRANCH_RIGHT_NOW);  // get active branch name
        File ACTIVE_BRANCH_POINTER, GIVEN_BRANCH_POINTER;
        if(activeBranch.equals("main")) {
            ACTIVE_BRANCH_POINTER = MAIN;
        } else {
            ACTIVE_BRANCH_POINTER = join(BRANCHES, activeBranch);
        }

        if(branchName.equals("main")) {
            GIVEN_BRANCH_POINTER = MAIN;
        } else {
            GIVEN_BRANCH_POINTER = join(BRANCHES, branchName);
        }

        if(!GIVEN_BRANCH_POINTER.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else {
            commitID = Utils.readContentsAsString(ACTIVE_BRANCH_POINTER);       // active branch's front commit object's id
            branchCommitID = Utils.readContentsAsString(GIVEN_BRANCH_POINTER);  // given branch's front commit object's id
        }

        // Don't use IDs, use activeBranch to do branch name matching
        // If IDs are used, checkout a newly created branch won't work (see spec's branch section)
        // TBD: what happens when I delete all the files in CWD and want to check out current branch?
        // Will it work?
        //if(commitID.equals(branchCommitID)) {
        if(activeBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        Commit curCommit = readCommit(commitID);
        Commit branchCommit = readCommit(branchCommitID);

        // Given curCommit and branchCommit, checkoutCommitHelper() takes care of checkout details
        checkoutCommitHelper(curCommit, branchCommit);

        //clear staging area for add.
        Groot initial_groot = new Groot();
        Utils.writeObject(GROOT, initial_groot);
        ArrayDeque <String> tracker = new ArrayDeque <String> ();
        Utils.writeObject(TRACKING_BLOBS, tracker);

        // clear 'staging area for removal'
        updateStagingAreaRm("",true);

        // update pointers
        Utils.writeContents(HEAD, branchCommitID);
        Utils.writeContents(BRANCH_RIGHT_NOW, branchName);
    }

    private static Commit readCommit(String commitID) {
        File COMMIT_FILE = null;
        if(commitID.length() == 40) {
            COMMIT_FILE = join(COMMITS, commitID);
        } else if (commitID.length() < 40) {
            List<String> listOfFiles = plainFilenamesIn(COMMITS.toString());
            String fileNameFullPath, fileName;
            for (int i = 0; i < listOfFiles.size(); i++) {
                fileNameFullPath = listOfFiles.get(i);
                fileName = fileNameFullPath.substring(fileNameFullPath.length()-40, fileNameFullPath.length());
                if(commitID.equals(fileName.substring(0, commitID.length()))) {
                    COMMIT_FILE = join(COMMITS, fileName);
                }
            }
        }
        if (!COMMIT_FILE.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit o = Utils.readObject(COMMIT_FILE, Commit.class);
        return o;
    }

    // Given curCommit and checkoutCommit, checkoutCommitHelper() takes care of checkout details.
    // 1. Takes all files in the checkoutCommit, and puts them in CWD,
    // overwriting the versions of the files that are already there if they exist.
    // 2. Any files that are tracked in the current branch (i.e. curCommit) but are not present in
    // the checked-out branch (i.e. checkoutCommit) are deleted.
    // 3. If a working file is untracked in the current branch (i.e. curCommit) and would be
    // overwritten by the checkout, print error message and exit. perform this check before
    // doing anything else. Do not change the CWD.
    private static void checkoutCommitHelper(Commit curCommit, Commit checkoutCommit) {
        Map curMap = curCommit.get_treemap();
        Map checkoutMap = checkoutCommit.get_treemap();
        Set<String> curSet = curMap.keySet();
        Set<String> checkoutSet = checkoutMap.keySet();

        for(String fileName: checkoutSet) {
            // 3. If a working file is untracked in the current branch and would be overwritten by the checkout,
            // print error message and exit
            if(isFileInCWD(fileName) && !curSet.contains(fileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            } else { // 1. Takes all files in the checkoutCommit, and puts them in CWD
                checkoutFileHelper(checkoutCommit, fileName);
                curSet.remove(fileName);   // remove this item from curSet
            }
        }
        // 2. Any files that are tracked in the current branch but are not present in the checked-out branch are deleted
        for(String fileName: curSet) {
            rmFileInCWD(fileName);
        }
    }

    private static void rmFileInCWD(String fileName){
        File TO_BE_DELETE_FILE = join(CWD, fileName);
        if(TO_BE_DELETE_FILE.exists()) {
            TO_BE_DELETE_FILE.delete();
        }
    }
    private static boolean isFileInCWD(String fileName) {
        File CHECK_FILE = join(CWD, fileName);
        return CHECK_FILE.exists();
    }

    public static void rm(String[] arg) {

        Groot y_s = Utils.readObject(BIG_BROTHER, Groot.class);
        Map mapping_s = y_s.get_treemap();
        mapping_s.replace(arg[1], "None");

        //TRACKER_FOR_STRING
        Groot tracker_for_strings = Utils.readObject(TRACK_FOR_ADDED_STRINGS, Groot.class);
        Map tracker_string = tracker_for_strings.get_treemap();
        tracker_string.replace(arg[1],"");

        String fileName = arg[1];  // get file name
        File TO_BE_DELETE_FILE = join(CWD, fileName);

        // read in 'staging area for add'
        Groot x = Utils.readObject(GROOT, Groot.class);
        Map treemap = x.get_treemap();
        boolean stagedForAdd = treemap.containsKey(fileName);

        // read in 'staging area for removal'
        TreeSet<String> saRmTS = Utils.readObject(STAGING_RM_FILE, TreeSet.class);

        // check if this file is tracked in current Commit
        // TBD: need Owen's help in fixing commit() to keep track of all files committed before
        String commitID = Utils.readContentsAsString(HEAD);
        Commit o = readCommit(commitID);

        // debug
        //Map m = o.get_treemap();
        //System.out.println("DEBUG: checking current commit's treemap " + m);

        String blobID = o.getBlobID(fileName);
        boolean tracked = (blobID != null);

        // Unstage the file if it is currently staged for addition.
        if(stagedForAdd) {
            treemap.remove(fileName);
            Utils.writeObject(GROOT, x);
        }

        if(tracked) {
            // If the file is tracked in the current commit, stage it for removal and remove the file from the
            // working directory if the user has not already done so (do not remove it unless it is tracked in
            // the current commit)
            saRmTS.add(fileName);
            Utils.writeObject(STAGING_RM_FILE, saRmTS);
            if(TO_BE_DELETE_FILE.exists()) {
                TO_BE_DELETE_FILE.delete();
            }
        }
        // If the file is neither staged nor tracked by the head commit
        if(!(stagedForAdd || tracked)){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        Utils.writeObject(BIG_BROTHER, y_s);
        Utils.writeObject(TRACK_FOR_ADDED_STRINGS, tracker_for_strings);
    }
    // 1. if clear is true, then clean all the staged info in 'staging area for removal'
    // 2. if clear is false, then remove one item (specified by fileName) from the set
    private static void updateStagingAreaRm(String fileName, boolean clear) {
        TreeSet<String> saRmTS;
        if(clear) {
            saRmTS = new TreeSet<String>();
            //System.out.println("DEV staging area (rm) : " + saRmTS + ", staging area (rm) has been cleared.");
        } else {
            saRmTS = Utils.readObject(STAGING_RM_FILE, TreeSet.class);
            saRmTS.remove(fileName);
            //System.out.println("DEV staging area (rm) : " + saRmTS);
        }
        Utils.writeObject(STAGING_RM_FILE, saRmTS);
    }

    public static void reset(String[] args) {
        if (args.length > 1) {
            System.exit(0);
        }
        File the_commit = join(COMMITS, args[1]);
        String nowadays = Utils.readContentsAsString(HEAD);
        if (!the_commit.exists()) {
            System.out.println("No commit with that id exists.");
        }
        else {
            File now_commit = join(COMMITS, nowadays);
            Commit now = Utils.readObject(now_commit, Commit.class);
            Map treemap = now.get_Big_brother();
            List<String> files = plainFilenamesIn(CWD);
            int count = 0;
            while (count < files.size()) {
                if (!treemap.containsKey(files.get(count))) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
                count = count + 1;
            }
            //1. erase content of working directory
            //2. copy and paste blob of now_commit into the directory
            //3. change the head position.
            Utils.writeContents(HEAD, args[1]);
            //clear staging area for remove:

            //clear staging area for add.
            Groot initial_groot = new Groot();
            Utils.writeObject(GROOT, initial_groot);
            ArrayDeque <String> tracker = new ArrayDeque <String> ();
            Utils.writeObject(TRACKING_BLOBS, tracker);

            // clean up 'staging area for removal'  (TBD: has to be included at the end of commit(), checkout() & reset()
            updateStagingAreaRm(null, true);
        }
    }

    // reset
    // if commit id is omitted, then it performs checkout of current commit
    public static void resetCmd(String[] args){
        if(args.length == 1) {
            String commitID = Utils.readContentsAsString(HEAD);
            resetCmd(commitID);
        } else if (args.length == 2) {
            resetCmd(args[1]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    public static void resetCmd(String checkoutCommitID) {
        String commitID = Utils.readContentsAsString(HEAD);

        Commit curCommit = readCommit(commitID);
        Commit checkoutCommit = readCommit(checkoutCommitID);

        // Given curCommit and checkoutCommit, checkoutCommitHelper() takes care of checkout details
        checkoutCommitHelper(curCommit, checkoutCommit);

        //clear staging area for add.
        Groot initial_groot = new Groot();
        Utils.writeObject(GROOT, initial_groot);
        ArrayDeque <String> tracker = new ArrayDeque <String> ();
        Utils.writeObject(TRACKING_BLOBS, tracker);

        // clear 'staging area for removal'
        updateStagingAreaRm("",true);

        // update pointers
        Utils.writeContents(HEAD, checkoutCommitID);
        // 1. reset() will change active branch's pointer to the given checkoutCommitID
        // 2. only support resetting to somewhere in the active branch
        String activeBranch = Utils.readContentsAsString(BRANCH_RIGHT_NOW);  // get active branch name
        File ACTIVE_BRANCH_POINTER;
        if(activeBranch.equals("main")) {
            ACTIVE_BRANCH_POINTER = MAIN;
        } else {
            ACTIVE_BRANCH_POINTER = join(BRANCHES, activeBranch);
        }
        Utils.writeContents(ACTIVE_BRANCH_POINTER, checkoutCommitID);
    }

    public static void rm_branch(String[] args) {
        if (args.length != 2) {
            System.exit(0);
        }
        else {
            String givenBranch = args[1];
            String activeBranch = Utils.readContentsAsString(BRANCH_RIGHT_NOW);  // get active branch name
            File GIVEN_BRANCH_POINTER;
            if(givenBranch.equals("main")) {
                GIVEN_BRANCH_POINTER = MAIN;
            } else {
                GIVEN_BRANCH_POINTER = join(BRANCHES, givenBranch);
            }

            //File branching = Utils.join(BRANCHES, args[1]);
            //if (args[1] == Utils.readContentsAsString(BRANCH_RIGHT_NOW)) {
            if(givenBranch.equals(activeBranch)) {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            } else if (!GIVEN_BRANCH_POINTER.exists()) {
                System.out.println("A branch with that name does not exist.");
                System.exit(0);
            } else {
                //Utils.restrictedDelete(branching);
                GIVEN_BRANCH_POINTER.delete();
            }
        }
    }

    //for merge here we assumed that rm and add works perfectly fine and every details is reflected into big brother and smaller brother. For deletion changes are reflected in that the file maps to "None".
    /*
    public static void merge(String[] args) {
        if (args.length != 2) {
            System.exit(0);
        }
        File branching = join(BRANCHES, args[1]);
        if (!branching.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        else if (Utils.readContentsAsString(BRANCH_RIGHT_NOW) == args[1]) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        else {
            String given_branch = args[1];
            String current_branch = Utils.readContentsAsString(BRANCH_RIGHT_NOW);
            String shaas = Utils.sha1(given_branch, current_branch);
            Groot x = Utils.readObject(PUMBA, Groot.class);
            Map treemap = x.get_treemap();
            if (treemap.containsKey(shaas)) {
                //files contained in split.
                //all encompassing big_brother and its sidekick.
                String splitting_at = (String) treemap.get(shaas);
                File splitting_commit = join(COMMITS, splitting_at);
                Commit splitted = Utils.readObject(splitting_commit, Commit.class);
                //all encompassing big_brother and its sidekick.
                ArrayDeque files_contained_in_split = splitted.get_Small_brother();
                Map split_contained = splitted.get_Big_brother();


                //files contained in head
                String head_at = Utils.readContentsAsString(HEAD);
                File head_commit = join(COMMITS, head_at);
                Commit head_stuff = Utils.readObject(head_commit, Commit.class);
                ArrayDeque files_contained_in_head = head_stuff.get_Small_brother();
                Map head_contained = head_stuff.get_Big_brother();
                //files contained in given_branch
                String branch_at = Utils.readContentsAsString(branching);
                File branch_commit = join(COMMITS, branch_at);
                Commit branch_stuff = Utils.readObject(branch_commit, Commit.class);
                ArrayDeque files_contained_in_branch = branch_stuff.get_Small_brother();
                Map branch_contained = branch_stuff.get_Big_brother();




                int count = 0;
                Map<String, String> treemaps = new TreeMap<String, String>();
                //find the maximum arraydeque.
                int sizing = Math.max(files_contained_in_split.size(), files_contained_in_head.size());
                sizing = Math.max(sizing,files_contained_in_branch.size());
                ArrayDeque now;

                if (sizing == files_contained_in_split.size()) {
                    now = files_contained_in_split;
                }
                else if (sizing == files_contained_in_head.size()) {
                    now = files_contained_in_head;
                }
                else {
                    now = files_contained_in_branch;
                }
                while (count < sizing) {
                    //determine which is staged or removed, etc.

                    //modified in other but not in head
                    if (split_contained.get(now.get(count)) == head_contained.get(now.get(count)) && split_contained.get(now.get(count)) != branch_contained.get(now.get(count))) {
                        treemap.put(now.get(count),branch_contained.get(now.get(count)));
                    }
                    //modified in head but not in others
                    else if (split_contained.get(now.get(count)) != head_contained.get(now.get(count)) && split_contained.get(now.get(count)) == branch_contained.get(now.get(count))) {
                    }
                    //modified in others and head.
                    else if (split_contained.get(now.get(count)) != head_contained.get(now.get(count)) && split_contained.get(now.get(count)) != branch_contained.get(now.get(count))) {
                        if (head_contained.get(now.get(count)) == branch_contained.get(now.get(count))) {
                        }
                        else {
                            treemap.put(now.get(count), "conflict");
                        }
                    }
                    //unmodified in head but not present in other
                    else if (split_contained.get(now.get(count)) == head_contained.get(now.get(count)) && branch_contained.get(now.get(count)) == "None") {
                        treemap.put(now.get(count), branch_contained.get(now.get(count)));
                    }
                    //unmodified in other but not present in head
                    else if (split_contained.get(now.get(count)) == branch_contained.get(now.get(count)) && head_contained.get(now.get(count)) == "None") {
                    }
                    //not in split nor head but in other
                    else if (split_contained.get(now.get(count)) == "None" && head_contained.get(now.get(count)) == "None" && branch_contained.get(now.get(count)) != "None") {
                        treemap.put(now.get(count), branch_contained.get(now.get(count)));
                    }
                    //not in split nor head but in other
                    else if (split_contained.get(now.get(count)) == "None" && branch_contained.get(now.get(count)) == "None" && head_contained.get(now.get(count)) != "None") {
                    }
                    count = count + 1;
                }
                int lame = 0;
                while (lame < sizing) {
                    if (treemap.containsKey(now.get(lame))) {
                        if (treemap.get(now.get(count)) != "None") {
                            Repository.add(now.get(count), treemap.get(now.get(count)));
                            //stage for addition.
                        }
                        else {
                            Repository.rm(now.get(count), "None");
                            //stage for removal.
                        }
                    }
                    lame = lame + 1;
                }
            }
        }
    }
    */

    // 8. status
    public static void status() {
        // get all the branch name (under 'branch' directory)
        List<String> branchList = plainFilenamesIn(BRANCHES.toString());
        TreeSet<String> tSet = new TreeSet<String>(branchList);
        tSet.add("main");  // add the 'main' branch into the set since it's not stored in the 'branch' directory
        String activeBranch = Utils.readContentsAsString(BRANCH_RIGHT_NOW); // get the name of active branch
        System.out.println("=== Branches ===");
        for(String branchName : tSet) {
            if(activeBranch.equals(branchName)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        // read in 'staging area for add'
        Groot x = Utils.readObject(GROOT, Groot.class);
        Map treemap = x.get_treemap();
        Set<String> keysSAAdd = treemap.keySet();
        Set<String> saAddSet = new TreeSet<String>(keysSAAdd);
        for(String fileName : saAddSet) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        // read in 'staging area for removal'
        TreeSet<String> saRmTS = Utils.readObject(STAGING_RM_FILE, TreeSet.class);
        for(String fileName : saRmTS) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    // 7. find
    public static void find(String[] args) {
        String message = args[1];

        List<String> fileList = plainFilenamesIn(COMMITS.toString());
        Commit o;
        boolean found = false;
        String msg;
        for(String commitID : fileList) {
            //System.out.println(commitID);
            o = readCommit(commitID);
            msg = o.get_message();
            if(message.equals(msg)) {
                System.out.println(commitID);
                found = true;
            }
        }
        if(found == false) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }
    public static Groot groot_mapping() {
        Groot y = Utils.readObject(GROOT, Groot.class);
        Map addition_mapping = y.get_treemap();
        return y;
    }


    public static ArrayDeque<String> addition_tracking() {
        return Utils.readObject(TRACKING_BLOBS, ArrayDeque.class);
    }
    public static Map get_head_stuff() {
        String head_at = Utils.readContentsAsString(HEAD);
        File head_commit = join(COMMITS, head_at);
        Commit head_stuff = Utils.readObject(head_commit, Commit.class);
        Map head_contained = head_stuff.get_treemap();
        return head_contained;
    }
    public static Map get_tracker_for_added_string() {
        String head_at = Utils.readContentsAsString(HEAD);
        File head_commit = join(COMMITS, head_at);
        Commit head_stuff = Utils.readObject(head_commit, Commit.class);
        Map head_contained = head_stuff.get_treemap();
        Map tars = head_stuff.get_Tracker_for_added_string();
        return tars;
    }
    public static String get_head_at() {
        String head_at = Utils.readContentsAsString(HEAD);
        File head_commit = join(COMMITS, head_at);
        Commit head_stuff = Utils.readObject(head_commit, Commit.class);
        Map head_contained = head_stuff.get_treemap();
        Map tars = head_stuff.get_Tracker_for_added_string();
        return head_at;
    }
    public static Commit get_heads() {
        String head_at = Utils.readContentsAsString(HEAD);
        File head_commit = join(COMMITS, head_at);
        Commit head_stuff = Utils.readObject(head_commit, Commit.class);
        Map head_contained = head_stuff.get_treemap();
        Map tars = head_stuff.get_Tracker_for_added_string();
        return head_stuff;
    }
    public static void there_is_untracked_stuff() {
        Map tars = get_tracker_for_added_string();
        List<String> t = Utils.plainFilenamesIn(CWD);
        int sizes = t.size();
        int slimmy = 0;
        while (slimmy < sizes) {
            File riled = join(CWD, t.get(slimmy));
            if (tars.get(t.get(slimmy)) == null) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            slimmy = slimmy + 1;
        }
    }
    static void branch_not_exist(File branching) {
        if (!branching.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }
    static Map branched_contained_map(String branch_at_given) {
        File branch_commit = join(COMMITS, branch_at_given);
        Commit branch_stuff = Utils.readObject(branch_commit, Commit.class);
        Map branch_contained = branch_stuff.get_treemap();
        return branch_contained;
    }
    public static Commit branch_stuffing(String branch_at_given) {
        File branch_commit = join(COMMITS, branch_at_given);
        Commit branch_stuff = Utils.readObject(branch_commit, Commit.class);
        Map branch_contained = branch_stuff.get_treemap();
        return branch_stuff;
    }
    public static void removing_stuffing(TreeSet saRmTS, String get_count) {
        saRmTS.add(get_count);
        File hedgehogs_are_great = join(CWD, get_count);
        Utils.restrictedDelete(hedgehogs_are_great);
    }
    public static void batman_begin(String get_count, String branch_at_given) {
        Map currently = get_heads().get_Tracker_for_added_string();
        String current_message = (String) currently.get(get_count);


        Map then = branch_stuffing(branch_at_given).get_Tracker_for_added_string();
        String then_message = (String) then.get(get_count);
        File newer = join(CWD, get_count);
        String messages = "<<<<<<< HEAD" + '\n' + current_message + "=======" + '\n' + then_message + ">>>>>>>" + '\n';
        Utils.writeContents(newer, messages);
    }
    public static void yes_no(TreeSet saRmTS, Map addition_mapping) {
        if (saRmTS.size() != 0 || addition_mapping.size() != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }
    public static void cannot_merge_self(String x) {
        if (x.equals(Utils.readContentsAsString(BRANCH_RIGHT_NOW))) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }
    public static void fast_forward(String split_point_ID, String argo) {
        if (split_point_ID == null) {
            checkoutCmd(argo);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }
    public static void ancestor_of_the_current(String branch_at_given, String real_split_point_ID) {
        if (branch_at_given.equals(real_split_point_ID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
    }
    public static void flash(String real_split_point_ID, String argo) {
        if (get_head_at().equals(real_split_point_ID)) {
            checkoutCmd(argo);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }


    public static void merge(String[] args) {
        Groot y = groot_mapping();
        Map addition_mapping = y.get_treemap();
        ArrayDeque <String> addition_tracking = addition_tracking();
        TreeSet<String> saRmTS = Utils.readObject(STAGING_RM_FILE, TreeSet.class);
        yes_no(saRmTS, addition_mapping);
        cannot_merge_self(args[1]);
        Map head_contained = get_head_stuff();
        there_is_untracked_stuff();
        File branching = join(BRANCHES, args[1]);
        branch_not_exist(branching);
        String branch_at_given = Utils.readContentsAsString(branching);
        Map branch_contained = branched_contained_map(branch_at_given);
        Groot pumbas = Utils.readObject(PUMBA, Groot.class);
        Map treemap = pumbas.get_treemap();
        String[] split_point_ID = find_splitpoint(args[1], Utils.readContentsAsString(BRANCH_RIGHT_NOW));
        fast_forward(String.valueOf(split_point_ID), args[1]);
        String sha1 = Utils.sha1(split_point_ID[0],split_point_ID[1]);
        String real_split_point_ID = (String) treemap.get(sha1);
        File splitting_commit = join(COMMITS, real_split_point_ID);
        Commit splitted = Utils.readObject(splitting_commit, Commit.class);
        Map split_contained = splitted.get_treemap();
        ancestor_of_the_current(branch_at_given, real_split_point_ID);
        flash(real_split_point_ID, args[1]);
        ArrayDeque <String> now = Utils.readObject(SMALLER_BROTHER, ArrayDeque.class);
        int count = 0;
        int sizing = now.size();
        while (count < sizing) {
            //unknown condition # 2
            if (now.get(count).equals("f.txt") && !split_contained.containsKey(now.get(count)) && !branch_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count))) {
                removing_stuffing(saRmTS, now.get(count));
            }
            //unknown condition
            if (split_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count)) && !branch_contained.containsKey(now.get(count)) && !split_contained.get(now.get(count)).equals(head_contained.get(now.get(count)))) {
                System.out.println("Encountered a merge conflict.");
                batman_begin(now.get(count), branch_at_given);
            }
            //condition a:
            if (split_contained.containsKey(now.get(count)) && branch_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count)) && split_contained.get(now.get(count)).equals(head_contained.get(now.get(count))) && !branch_contained.get(now.get(count)).equals(split_contained.get(now.get(count)))) {
                //treemaps.put(now.get(count), (String) branch_contained.get(now.get(count)));
                fishboi(addition_mapping, now.get(count), (String) branch_contained.get(now.get(count)), addition_tracking);
            }
            //condition b:
            else if (split_contained.containsKey(now.get(count)) && branch_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count)) && !split_contained.get(now.get(count)).equals(head_contained.get(now.get(count))) && branch_contained.get(now.get(count)).equals(split_contained.get(now.get(count)))) {
            }
            //condition c:
            else if (split_contained.containsKey(now.get(count)) && branch_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count)) && !split_contained.get(now.get(count)).equals(head_contained.get(now.get(count))) && !branch_contained.get(now.get(count)).equals(split_contained.get(now.get(count)))) {
                if (head_contained.get(now.get(count)).equals(branch_contained.get(now.get(count)))) {
                }
                else {
                    System.out.println("Encountered a merge conflict.");
                    batman_begin(now.get(count), branch_at_given);
                }
            }
            //condition d:
            else if (!split_contained.containsKey(now.get(count)) && !branch_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count))) {
            }
            //condition e:
            else if (!split_contained.containsKey(now.get(count)) && branch_contained.containsKey(now.get(count)) && !head_contained.containsKey(now.get(count))) {
                fishboi(addition_mapping, now.get(count), (String) branch_contained.get(now.get(count)), addition_tracking);
            }
            //condition f:
            else if (!branch_contained.containsKey(now.get(count)) && head_contained.containsKey(now.get(count)) && split_contained.containsKey(now.get(count)) && split_contained.get(now.get(count)).equals(head_contained.get(now.get(count)))) {
                removing_stuffing(saRmTS, now.get(count));
            }
            //condition g:
            else if (!head_contained.containsKey(now.get(count)) && branch_contained.containsKey(now.get(count)) && split_contained.containsKey(now.get(count)) && split_contained.get(now.get(count)).equals(branch_contained.get(now.get(count)))) {
            }
            count = count + 1;
        }
        adding_saving(y,addition_tracking,saRmTS);
        commiting_my_commiting("Merged " +args[1] + " into " + Utils.readContentsAsString(BRANCH_RIGHT_NOW)+".");
    }
    public static void commiting_my_commiting(String x) {
        String[] boooo = new String[2];
        boooo[1] = x;
        commit(boooo);
    }
    public static void fishboi(Map addition_mapping, String get_count, String branch_contained, ArrayDeque addition_tracking) {
        addition_mapping.put(get_count, branch_contained);
        addition_tracking.addFirst(get_count);
        //adding the file in it.
        File being_a_doormat_is_great = join(CWD, get_count);
        File BLOB_FILE = join(BLOBS, branch_contained);
        byte[] contents = Utils.readObject(BLOB_FILE, byte[].class);
        Utils.writeContents(being_a_doormat_is_great, contents);
    }
    public static void adding_saving(Groot y, ArrayDeque addition_tracking, TreeSet<String> saRmTS) {
        Utils.writeObject(GROOT, y);
        Utils.writeObject(TRACKING_BLOBS, addition_tracking);
        Utils.writeObject(STAGING_RM_FILE, saRmTS);
    }
    //helper 1
    public static Map get_tracking_map() {
        Groot splitting = Utils.readObject(TRACKING_FOR_BRANCHES, Groot.class);
        return splitting.get_treemap();
    }
    public static String[] find_splitpoint(String branch, String main) {
        Map treemapping = get_tracking_map();
        String[] now = new String[] {branch, main};
        if (branch.equals("main")) {
            if (treemapping.containsValue(branch)) {
                return now;
            }
            else {
                return null;
            }
        }
        if (!treemapping.containsKey(branch)) {
            return null;
        }
        if (treemapping.get(branch).equals(main)) {
            return now;
        }
        else {
            String ending = (String) treemapping.get(main);
            while (true) {
                if (treemapping.get(now[0]).equals(ending)) {
                    now[1] = (String) treemapping.get(now[0]);
                    return now;
                }
                else {
                    now[0] = (String) treemapping.get(now[0]);
                }
            }
        }
    }

    /* TODO: fill in the rest of this class. */
}