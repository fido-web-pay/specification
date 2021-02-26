import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class StateDiagram {

    static double VBAR_HEIGHT = 700;
    static double VBAR_WIDTH = 2;
    static double VBAR_START = 10;
    static double VBAR_DISTANCE = 150;
    static double VBAR_INITIAL = 150;

    static double PROC_WIDTH = 86;
    static double PROC_SLANT = 8;
    static double PROC_HEIGHT = 22;

    static double NUMB_WIDTH = 16;

    static double STROKE_WIDTH = 0.5;

    static double ARROW_WIDTH = 1;
    static double ARROW_HEAD_WIDTH = 5;
    static double ARROW_HEAD_LENGTH = 7;
    static double ARROW_HEAD_GUTTER = 0.5;

    static double DROP_OFFSET = 2;

    String originalBase;
    StringBuilder svg = new StringBuilder ("""
<?xml version='1.0' encoding='utf-8'?>
<svg width='1000' height='1000' xmlns='http://www.w3.org/2000/svg'>
    <title>State Diagram</title>
    <!-- Anders Rundgren 2021 -->
    <defs>
        <filter id='dropShaddow'>
            <feGaussianBlur stdDeviation='1'/>
        </filter>
""");

    int currNum = 0;
    void number(double centerX, double centerY) {
        svg.append("    <rect x='")
           .append(convert(centerX - (NUMB_WIDTH + STROKE_WIDTH) / 2))
           .append("' y='")
           .append(convert(centerY - (NUMB_WIDTH + STROKE_WIDTH) / 2))
           .append("' width='")
           .append(convert(NUMB_WIDTH))
           .append("' height='")
           .append(convert(NUMB_WIDTH))
           .append("' rx='3' fill='white' stroke='black' stroke-width='")
           .append(convert(STROKE_WIDTH))
           .append("'/>\n");
    }

    void processing(int vbar, double y) {
        double rectY = y - (PROC_HEIGHT + STROKE_WIDTH) / 2;
        double startX = center(vbar) - PROC_WIDTH / 2 + PROC_SLANT / 2;
        svg.append("    <path fill='grey' d='M")
           .append(convert(startX + DROP_OFFSET))
           .append(',')
           .append(convert(DROP_OFFSET + rectY))
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
           .append("    <path fill='#fff2cc' stroke='#404040' stroke-width='0.5' d='M")
           .append(convert(startX))
           .append(',')
           .append(convert(rectY))
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
           .append("    <text x='")
           .append(convert(center(vbar) + NUMB_WIDTH / 5))
           .append("' y='")
           .append(convert(rectY + 15))
           .append("' font-family='sans-serif' font-size='12' text-anchor='middle'>Processing</text>\n");
        number(startX - PROC_SLANT / 2 - STROKE_WIDTH / 2, y);
    }

    void arrow(double centerY, int from, int to) {
        double startX = (to > from ? VBAR_WIDTH : -VBAR_WIDTH) / 2;
        double endX = to > from ? -ARROW_HEAD_LENGTH - ARROW_HEAD_GUTTER : ARROW_HEAD_LENGTH + ARROW_HEAD_GUTTER;
        String y = convert(centerY);
        svg.append("    <line x1='")
           .append(convert(center(from) + startX))
           .append("' y1='")
           .append(y)
           .append("' x2='")
           .append(convert(center(to) + endX - startX))
           .append("' y2='")
           .append(y)
           .append("' stroke-width='")
           .append(convert(ARROW_WIDTH))
           .append("' stroke='black' marker-end='url(#arrowHead)'/>\n");

    }

    double center(int i) {
        return VBAR_INITIAL + i * VBAR_DISTANCE;
    }

    StateDiagram(String originalBase) throws Exception {
        this.originalBase = originalBase;
        svg.append("        <marker id='arrowHead' markerWidth='")
           .append(convert(ARROW_HEAD_LENGTH))
           .append("' markerHeight='")
           .append(convert(ARROW_HEAD_WIDTH))
           .append("' refX='0' refY='")
           .append(convert(ARROW_HEAD_WIDTH / 2))
           .append("' orient='auto'>\n            <polygon points='0 0, ")
           .append(convert(ARROW_HEAD_LENGTH))
           .append(" ")
           .append(convert(ARROW_HEAD_WIDTH / 2))
           .append(", 0 ")
           .append(convert(ARROW_HEAD_WIDTH))
           .append("' />\n" +
            "        </marker>\n" +
            "    </defs>\n");
        readOriginal("user.svg");
        for (int i = 0; i < 5; i++) {
            String x = convert(i * VBAR_DISTANCE + VBAR_INITIAL);
            svg.append("    <line x1='")
               .append(x)
               .append("' y1='")
               .append(convert(VBAR_START))
               .append("' x2='")
               .append(x)
               .append("' y2='")
               .append(convert(VBAR_HEIGHT + VBAR_START))
               .append("' stroke-width='")
               .append(convert(VBAR_WIDTH))
               .append("' stroke='red'/>\n");
        }
        processing(1, 100);
        processing(2, 150);
        arrow(150, 3, 4);
        arrow(170, 3, 4);
        arrow(190, 4, 3);
       number(center(3), 150);
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