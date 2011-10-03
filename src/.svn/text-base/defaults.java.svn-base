import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

class runnerErrors {
    protected static void errorUnexpectedArgument(String faultyArg) {
        Date date = new Date();
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(format.format(date) + " defaults[" + PropertyList.compilerStackLine() + ".707] Unexpected argument " +
        faultyArg + "; leaving defaults unchanged");
    }

    protected static void errorWrongUsage() {
        System.out.println("usage: defaults (write|read) [filename|domain] property-name (-type) property-value");
    }

    protected static void errorRandomDictionaryFail() {
        Date date = new Date();
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(format.format(date) + " defaults[" + PropertyList.compilerStackLine() + ".707]\n" +
                "Rep argument is not a dictionary\n" +
                "Defaults have not been changed.");
    }
}

public class defaults {
    public static void main(String[] args) {
        if(args.length == 0) {
            runnerErrors.errorWrongUsage();
        }else if(args.length == 2) {
            // read whole dictionary
            System.out.println(PropertyList.ReadAllKeys(args[1]));
        } else if(args[0].equalsIgnoreCase("read")) {
            if(args.length > 3) {
                runnerErrors.errorUnexpectedArgument(args[3]);
            } else {
                String value = PropertyList.ReadKey(args[2], args[1]);
                System.out.println(value);
            }
        } else if(args[0].equalsIgnoreCase("write")) {
            // ex 'write filename/domain key1 val1'
            // 'write filename/domain -type ...
            String domainPtr = args[1];
            String keyName = args[2];
            
            if(args.length == 3) {
                runnerErrors.errorRandomDictionaryFail();
            } else if(args.length == 4) {
                String optionKey = args[3].toLowerCase();

                if(optionKey.equals("-string") ||
                        optionKey.equals("-data") ||
                        optionKey.equals("-int") ||
                        optionKey.equals("-float") ||
                        optionKey.equals("-bool") ||
                        optionKey.equals("-date") ||
                        optionKey.equals("-array") ||
                        optionKey.equals("-array-add") ||
                        optionKey.equals("-dict") ||
                        optionKey.equals("-dict-add")
                        ) {
                    runnerErrors.errorWrongUsage();
                } else {
                    PropertyList.WriteString(args[1], args[2], args[3]);
                }

            } else {
                // this should be the "data defined" arguments.. parsing ahead..
                String optionKey = args[3].toLowerCase();

                if(optionKey.equals("-string")) {
                    PropertyList.WriteString(domainPtr, keyName, args[4]);
                    
                } else if(optionKey.equals("-data")) {
                    // TODO:1 Figured out WTF data is TODO:2 Call data
                    
                } else if(optionKey.toLowerCase().equals("-int")) {
                    PropertyList.WriteInt(domainPtr, keyName, args[4]);

                } else if(optionKey.toLowerCase().equals("-float")) {
                    PropertyList.WriteFloat(domainPtr, keyName, args[4]);

                } else if(optionKey.toLowerCase().equals("-bool")) {
                    PropertyList.WriteBool(args[1], keyName, args[4]);

                } else if(optionKey.toLowerCase().equals("-date")) {
                    PropertyList.WriteDate(domainPtr, keyName, args[4]);
                    
                } else if(optionKey.toLowerCase().equals("-array") && !args[3].toLowerCase().contains("-add")) {
                    // build/verify array:
                    String[] arrayValues = null;
                    System.arraycopy(args, 4, arrayValues, 0, args.length - 4);
                    PropertyList.WriteArray(domainPtr, keyName, arrayValues);

                } else if(args[3].toLowerCase().equals("-array-add")) {
                    // TODO:2 Verify array exists and append
                } else if(args[3].toLowerCase().equals("-dict") && !args[3].toLowerCase().contains("-add")) {
                    // TODO:3 write new dict
                } else if(args[3].toLowerCase().equals("-dict-add")) {
                    // TODO:4 Verify existing Dict and call dict
                } else {
                    // TODO:5 See if this is viable
                    System.out.println("How did we get to this line? figure it out.");
                }
            }
        } else {
            runnerErrors.errorWrongUsage();
        }
    }
}
