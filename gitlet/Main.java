package gitlet;

import java.io.File;

import static gitlet.Utils.join;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Barack Obama
 */
public class Main {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];

        // check if a .gitlet exists
        switch(firstArg) {
            case "init":
                if (GITLET_DIR.exists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }
                break;
            case "add":
            case "commit":
            case "rm":
            case "log":
            case "global-log":
            case "find":
            case "status":
            case "checkout":
            case "branch":
            case "rm-branch":
            case "reset":
            case "merge":
                if (!GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                break;
            default:
                break;
        }

        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                Repository.add(args);
                break;
            case "commit":
                Repository.commit(args);
                break;
            case "log":
                Repository.log();
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "branch":
                Repository.branch(args);
                break;
            case "reset":
                Repository.resetCmd(args);
                break;
            case "merge":
                Repository.merge(args);
                break;
            case "rm-branch":
                Repository.rm_branch(args);
                break;
            case "rm":
                Repository.rm(args);
                break;
            case "status":
                Repository.status();
                break;
            case "find":
                Repository.find(args);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}