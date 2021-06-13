import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class CreateDocument  {

    String originalBase;

    String sequenceDiagram;

    void removeDuplicate(String what) throws Exception {
        int i = sequenceDiagram.lastIndexOf("<title>" + what);
        if (i < 0) {
            throw new IOException("SVG err");
        }
        int start = sequenceDiagram.indexOf("<defs", i);
        int stop = sequenceDiagram.indexOf("</defs>", i);
        sequenceDiagram = sequenceDiagram.substring(0, start) + sequenceDiagram.substring(stop + 7);
    }

    String processCborTxt(String fileName) throws Exception {
        StringBuilder buf = new StringBuilder();
        for (char c : readOriginal(fileName).toCharArray()) {
            switch (c) {
            case '\n':
                buf.append("<br>");
                break;
            case '<':
                buf.append("&lt;");
                break;
            case '>':
                buf.append("&gt;");
                break;
            case '&':
                buf.append("&amp;");
                break;
            case '\"':
                buf.append("&#034;");
                break;
            case '\'':
                buf.append("&#039;");
                break;
            case ' ':
                buf.append("&nbsp;");
                break;
            default:
                buf.append(c);
                break;
            }
        }
        return buf.toString();
    }

    CreateDocument (String originalBase) throws Exception {
        this.originalBase = originalBase;
        String template = readOriginal("template.html");
        sequenceDiagram = readOriginal(SequenceDiagram.STANDARD_SEQUENCE_FILE);
        sequenceDiagram = "<svg style='display:block;width:500pt' class='box' " + 
                          sequenceDiagram.substring(sequenceDiagram.indexOf("<svg ") + 5);
        template = template.replace("@sequencediagram@", sequenceDiagram);
        sequenceDiagram = readOriginal(SequenceDiagram.DELEGATED_SEQUENCE_FILE);
        sequenceDiagram = "<svg style='display:block;width:400pt' class='box' " + 
                          sequenceDiagram.substring(sequenceDiagram.indexOf("<svg ") + 5);
        removeDuplicate("Merchant");
        removeDuplicate("PSP");
        removeDuplicate("Issuer");
        removeDuplicate("FIDO Web Pay - Sample Sequence Diagram");
        template = template.replace("@delegateddiagram@", sequenceDiagram);
        template = template.replace("@AD.txt@", processCborTxt("AD.txt"));
        template = template.replace("@SAD.txt@", processCborTxt("SAD.txt"));
        template = template.replace("@ESAD.txt@", processCborTxt("ESAD.txt"));
        template = template.replace("@PRCD.txt@", processCborTxt("PRCD.txt"));
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
            new CreateDocument (argc[0] + File.separator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
