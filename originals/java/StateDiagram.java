import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class StateDiagram {

    static double VBAR_HEIGHT = 200;
    static double VBAR_WIDTH = 2;
    static double VBAR_START = 10;
    static double VBAR_DISTANCE = 50;
    static double VBAR_INITIAL = 150;

    static double PROC_WIDTH = 82;
    static double PROC_SLANT = 8;
    static double PROC_HEIGHT = 22;

    static double DROP_OFFSET = 2;

    String originalBase;
    StringBuilder svg = new StringBuilder ("""
<?xml version='1.0' encoding='utf-8'?>
<svg width='1000' height='300' xmlns='http://www.w3.org/2000/svg'>
    <title>State Diagram</title>
    <!-- Anders Rundgren 2021 -->
    <defs>
        <filter id='dropShaddow'>
            <feGaussianBlur stdDeviation='1'/>
        </filter>
    </defs>
""");

    void processing(int vbar, double y) {
        double startX = center(vbar) - PROC_WIDTH / 2 + PROC_SLANT / 2;
        svg.append("<path fill='grey' d='M")
           .append(convert(startX + DROP_OFFSET))
           .append(',')
           .append(convert(DROP_OFFSET + y))
           .append(" l")
           .append(convert(PROC_WIDTH))
           .append(',')
           .append("0 l")
           .append(convert(-PROC_SLANT))
           .append(',')
           .append(convert(PROC_HEIGHT))
           .append(" l")
           .append(convert(-PROC_WIDTH))
           .append(",0Z' filter='url(#dropShaddow)'/>\n")
           .append("<path fill='#fff2cc' stroke='#404040' stroke-width='0.5' d='M")
           .append(convert(startX))
           .append(',')
           .append(convert(y))
           .append(" l")
           .append(convert(PROC_WIDTH))
           .append(',')
           .append("0 l")
           .append(convert(-PROC_SLANT))
           .append(',')
           .append(convert(PROC_HEIGHT))
           .append(" l")
           .append(convert(-PROC_WIDTH))
           .append(",0Z'/>\n")
           .append("<text x='")
           .append(center(vbar))
           .append("' y='")
           .append(convert(y + 15))
           .append("' font-family='sans-serif' font-size='12' text-anchor='middle'>Processing</text>\n");
           /*
        <path fill='#fff2cc' stroke='#404040' stroke-width='0.5' d='M5,1 l82,0 l-4,22 l-82,0Z'/>
        <text x='44' y='16' font-family='sans-serif' font-size='12' text-anchor='middle'>Processing</text>
    </symbol>
    <use href='#process'/>
    <use href='#process' x='100' y='50'/>
    */
    }

    double center(int i) {
        return VBAR_INITIAL - VBAR_WIDTH / 2 + i * VBAR_DISTANCE;
    }

    StateDiagram(String originalBase) throws Exception {
        this.originalBase = originalBase;
        readOriginal("user.svg");
        for (int i = 0; i < 5; i++) {
            svg.append("    <rect x='")
               .append(convert(center(i)))
               .append("' y='")
               .append(convert(VBAR_START))
               .append("' width='")
               .append(convert(VBAR_WIDTH))
               .append("' height='")
               .append(convert(VBAR_HEIGHT))
               .append("' fill='red'/>\n");
        }
        processing(1, 100);
        processing(2, 150);
        new FileOutputStream(originalBase + "statediagram.svg").write(svg.append("</svg>").toString().getBytes("utf-8"));
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

    String convert(double d) {
        String s = Double.toString(d);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s; 
    }

    public static void main(String[] argc) {
        try {
            new StateDiagram(argc[0] + File.separator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}