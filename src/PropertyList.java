import com.dd.plist.*;
import java.io.File;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PropertyList {
    private static String pathChar = System.getProperty("file.separator");

    private static String hiveDir() {
        return System.getProperty("user.home") + pathChar + "Library" + pathChar + "Preferences" + pathChar;
    }

    private static String keyName(String key) {
        if(key.contains(" ") || key.contains("-") ) {
            return "\"" + key + "\"";
        } else {
            return key;
        }
    }

    private static boolean fileExists(String FileUrl) {
        if ((new File(FileUrl)).exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static void Log(String message) {
        System.out.println(message);
    }

    private static String generalError(String filePath, String defaultPair) {
        Date date = new Date();
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return format.format(date) + " defaults[" + compilerStackLine() + ":707]\n" +
                "The domain/default pair of (" + filePath +", " + defaultPair + ") does not exist.";
    }

    private static NSDictionary rootReader(String fileUrl) {
        NSDictionary root;

        if(fileUrl.toLowerCase().endsWith(".plist") || fileUrl.toLowerCase().contains(pathChar)) {
            // flat file
            if(!fileUrl.toLowerCase().endsWith(".plist")) { fileUrl += ".plist"; }
            if(fileExists(fileUrl)) {
                try {
                    root = (NSDictionary)PropertyListParser.parse(new File(fileUrl));
                    if(root != null) {
                        return root;
                    } else {
                        return new NSDictionary();
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    return new NSDictionary();
                }
            } else {
                return new NSDictionary();
            }
        } else {
            // hive file
            File hivePtr = new File(hiveDir());

            if(!hivePtr.exists()) {
                if(!hivePtr.mkdirs()) {
                    System.out.println("Failure creating hive storage.");
                    return new NSDictionary();
                } else {
                    return new NSDictionary();
                }
            } else {
                String hiveFile = hiveDir() + fileUrl + ".plist";
                File hiveFilePtr = new File(hiveFile);

                if(hiveFilePtr.exists()) {
                    try {
                        return (NSDictionary)PropertyListParser.parse(hiveFilePtr);
                    } catch (Exception ex) {
                        System.out.println("Error opening hive file.\n" + hiveFile + "\n" + ex.getMessage());
                        return null;
                    }
                } else {
                    return new NSDictionary();
                }
            }

        }
    }

    private static boolean rootWriter(NSDictionary root, String fileUrl) {

        if(fileUrl.toLowerCase().endsWith(".plist") || fileUrl.toLowerCase().contains(pathChar)) {
            // flat file
            if(!fileUrl.toLowerCase().endsWith(".plist")) { fileUrl += ".plist"; }
            try {
                if(root != null) {
                    PropertyListParser.saveAsBinary(root, new File(fileUrl));
                    return true;
                } else {
                    Log("Catastrophic error..?");
                    return false;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return false;
            }
        } else {
            // hive file
            File hivePtr = new File(hiveDir());

            if(!hivePtr.exists()) {
                if(!hivePtr.mkdirs()) {
                    System.out.println("Failure creating hive storage.");
                    return false;
                }
            } else {
                String hiveFile = hiveDir() + fileUrl + ".plist";

                try {
                    PropertyListParser.saveAsBinary(root, new File(hiveFile));
                    return true;
                } catch (Exception ex) {
                    System.out.println("Error opening hive file.\n" + ex.getMessage());
                    return false;
                }
            }

        }
        return false;
    }
    
    public static String compilerStackLine() {
        StackTraceElement stk = new RuntimeException().getStackTrace()[1];
        return "" + stk.getLineNumber();
    }

    public static String ReadKey(String keyName, String fileUrl) {
        // TODO:100 Polish "ReadKey()"
        if(!new File(fileUrl).exists() &&
                !new File(fileUrl + ".plist").exists() &&
                !new File(hiveDir() + fileUrl + ".plist").exists()
                ) {
            return generalError(fileUrl, keyName);
        } else {
            NSDictionary root = rootReader(fileUrl);
            Object keyValue = root.objectForKey(keyName);

            if(keyValue != null) {
                if(keyValue.getClass() == NSString.class) {
                    NSString tmpStr = (NSString)keyValue;
                    return tmpStr.toString();
                } else if(keyValue.getClass() == NSObject.class) {
                    return "[NSObject] " + keyValue.toString();
                } else if(keyValue.getClass() == NSData.class) {
                    return "[NSData] " + keyValue.toString().replaceAll("com.dd.plist.NSData", "") + "\n";
                } else if(keyValue.getClass() == NSArray.class) {
                    NSArray nsArr = (NSArray)keyValue;
                    String _return = "";
                    _return += "[NSArray] " + keyName + "\n{\n";

                    for(Integer i = 0; i < nsArr.count(); i++) {
                        _return += "\t[" + i + "] = " + nsArr.objectAtIndex(i) + "\n";
                    }

                    _return += "}\n";

                    return _return;
                } else if(keyValue.getClass() == NSNumber.class) {
                    return "[NSNumber] " + keyValue + "\n";
                } else if(keyValue.getClass() == NSDate.class) {
                    NSDate date = (NSDate)keyValue;
                    return "[NSDate] " + date.toString() + "\n";
                } else {
                    return "Property list type is not managed: " + keyValue.getClass().getName() + "\n";
                }
            } else {
                return generalError(fileUrl, keyName);

            }
        }
    }

    public static String ReadAllKeys(String fileUrl) {
        NSDictionary root;
        String stringDictionary = "";

        root = rootReader(fileUrl);
        stringDictionary += "{\n";

        for(String key: root.allKeys()) {
            Object keyValue = root.objectForKey(key);
            Class keyType = keyValue.getClass();

            if(keyType == NSDictionary.class) {

            } else if(keyType == NSArray.class) {
                NSArray array = (NSArray)keyValue;
                stringDictionary += "\t" + keyName(key) + " =\t (\n";
                for(int i = 0; i < array.count(); i++) {
                    stringDictionary += "\t\t" + array.objectAtIndex(i).toString();
                    if(i == array.count()) {
                        stringDictionary += "\n";
                    } else {
                        stringDictionary += ",\n";
                    }
                }
                stringDictionary += "\t)\n";
            } else {
                stringDictionary += "\t" + keyName(key) + " = " + keyValue.toString() + ";\n";
            }
        }

        stringDictionary += "}\n";
        return stringDictionary;

    }

    public static boolean WriteString(String fileUrl, String keyName, String keyValue) {
        NSDictionary root = rootReader(fileUrl);
        
        root.put(keyName, keyValue);

        return rootWriter(root,fileUrl);
    }

    public static boolean WriteData(String fileUrl, String keyName, String keyValue) {
        // TODO:6 WriteData()??
        NSDictionary root = rootReader(fileUrl);
        // root.put();
        return rootWriter(null, fileUrl);
    }

    public static boolean WriteInt(String fileUrl, String keyName, String keyValue) {
        NSDictionary root = rootReader(fileUrl);
        int i = Integer.parseInt(keyValue);

        root.put(keyName, i);
        
        return rootWriter(root, fileUrl);
    }

    public static boolean WriteFloat(String fileUrl, String keyName, String keyValue) {
        NSDictionary root = rootReader(fileUrl);
        double d = Double.parseDouble(keyValue);

        root.put(keyName, d);

        return rootWriter(root, fileUrl);
    }

    public static boolean WriteBool(String fileUrl, String keyName, String keyValue) {
        NSDictionary root = rootReader(fileUrl);
        boolean bool = false;

        if(keyValue.toLowerCase().equals("yes") || keyValue.toLowerCase().equals("true")) {
            bool = true;
        } else {
            bool = false;
        }

        root.put(keyName, bool);

        return rootWriter(root, fileUrl);
    }

    public static boolean WriteDate(String fileUrl, String keyName, String keyValue) {
        DateFormat masterFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZZZ");
        Date finalDate = null;
        boolean running = true;

        // formats to try
        ArrayList<DateFormat> fmts = new ArrayList<DateFormat>();
        fmts.add(new SimpleDateFormat("MM-dd-yy"));
        fmts.add(new SimpleDateFormat("MM-dd-yyyy"));
        fmts.add(new SimpleDateFormat("yyyy-MM-dd"));
        fmts.add(new SimpleDateFormat("MM/dd/yy"));
        fmts.add(new SimpleDateFormat("MM/dd/yyyy"));
        fmts.add(new SimpleDateFormat("yyyy/MM/dd"));
        // catch all (seems to be same as mac)
        fmts.add(new SimpleDateFormat("y"));

        int formatIndex = 0;
        int formatLast = fmts.size();

        while (running) {
            if(formatIndex <= formatLast) {
                try {
                    DateFormat tryFormat = (SimpleDateFormat)fmts.get(formatIndex);
                    finalDate = tryFormat.parse(keyValue);
                    running = false;
                } catch (Exception e) {
                    // throw away
                }
            } else {
                running = false;
            }
            formatIndex++;
        }

        if(finalDate != null) {
            NSDictionary root = rootReader(fileUrl);
            root.put(keyName, finalDate);
            return rootWriter(root, fileUrl);
        } else {
            System.out.println("Cannot parse any of the information provided bub, sorry.");
            return false;
        }

    }

    public static boolean WriteArray(String fileUrl, String keyName, String[] keyValue) {
        NSDictionary root = rootReader(fileUrl);
        NSArray array = new NSArray(keyValue.length);
        NSObject objValue;

        for(int i = 0; i < keyValue.length; i++) {
            objValue = new NSString(keyValue[i]);
            array.setValue(i, objValue);
        }

        root.put(keyName, array);

        return rootWriter(root, fileUrl);
    }

    public static boolean WriteArrayAdd(String fileUrl, String keyName, String[] keyValues) {
        return false;
    }

    public static boolean WriteDictionary(String fileUrl, String keyName, Object keyValue) {
        return false;
    }

    public static boolean WriteDictionaryAdd(String fileUrl, String keyName, String[] keyValues) {
        return false;
    }


}
