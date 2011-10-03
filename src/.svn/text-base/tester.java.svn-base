import com.dd.plist.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class tester {
    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("y");
        DateFormat masterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

        try{
            String ds = "";

            for(int i = 0; i < args.length; i++) {
                ds += args[i] + " ";
            }


            Date d = df.parse(ds);
            System.out.println(masterDate.format(d));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
