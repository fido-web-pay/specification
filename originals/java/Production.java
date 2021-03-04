import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class Production {

    String originalBase;

    Production(String originalBase) throws Exception {
        this.originalBase = originalBase;
        String template = readOriginal("template.html");
        String statediagram = readOriginal(StateDiagram.FILE_NAME);
        statediagram = "<svg style='display:block;width:500pt;box-shadow:0.2em 0.2em 0.2em #d0d0d0;border-width:1px;border-style:solid;border-color:black' " + statediagram.substring(statediagram.indexOf("<svg ") + 5);
        template = template.replace("@statediagram@", statediagram);
        new FileOutputStream(originalBase + ".." + File.separator + "draft.html").write(template.getBytes("utf-8"));
     }

    static byte[] getByteArrayFromInputStream(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        byte[] buffer = new byte[10000];
        int bytes;
        while ((bytes = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytes);
        }
        is.close();
        return baos.toByteArray();
    }

    String readOriginal(String name) throws Exception {
        return new String(getByteArrayFromInputStream(new FileInputStream(originalBase + name)), "utf-8");
    }

    public static void main(String[] argc) {
        try {
            new Production(argc[0] + File.separator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}