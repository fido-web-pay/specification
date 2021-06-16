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

    String esadFilter(String json) throws Exception {
        int i = json.indexOf("&quot;encryptedAuthorization&quot;");
        if (i < 0) throw new IOException("no keyword");
        i = json.indexOf("&quot;", i + 32);
        int j = json.indexOf("&quot;", i + 1);
        return json.substring(0, i + 10) + "&#x2022;&#x2022;&#x2022;" + json.substring(j - 4);
    }

    String processCodeTxt(String fileName) throws Exception {
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
                buf.append("&quot;");
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
        template = template.replace("@browser-PR.txt@", processCodeTxt("browser-PR.txt"));
        template = template.replace("@AD.txt@", processCodeTxt("AD.txt"));
        template = template.replace("@SAD.txt@", processCodeTxt("SAD.txt"));
        template = template.replace("@ESAD.txt@", processCodeTxt("ESAD.txt"));
        template = template.replace("@PRCD.txt@", processCodeTxt("PRCD.txt"));
        template = template.replace("@FWP-assertion.json@", processCodeTxt("FWP-assertion.json"));
        template = template.replace("@PSP-request.json@", esadFilter(processCodeTxt("PSP-request.json")));
        template = template.replace("@ISSUER-request.json@", esadFilter(processCodeTxt("ISSUER-request.json")));
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
