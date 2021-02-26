import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class StateDiagram {

    static double USER_TOP_Y = 26;
    static double USER_HEIGHT = 60;

    static double VBAR_HEIGHT = 700;
    static double VBAR_WIDTH = 2;
    static double VBAR_START_Y = USER_TOP_Y + USER_HEIGHT - 5;
    static double VBAR_DISTANCE = 150;
    static double VBAR_START_X = 150;
    static String VBAR_COLOR = "#cc2020";

    static double PROC_WIDTH = 86;
    static double PROC_SLANT = 8;
    static double PROC_HEIGHT = 22;

    static double NUMB_WIDTH = 14;

    static double STROKE_WIDTH = 0.5;

    static double ARROW_WIDTH = 1;
    static double ARROW_HEAD_WIDTH = 5;
    static double ARROW_HEAD_LENGTH = 7;
    static double ARROW_HEAD_GUTTER = 0.5;

    static double DROP_OFFSET = 2;

    static int FONT_SIZE = 12;
    static double FONT_OFFSET = 4;

    static double HEADER_Y = FONT_SIZE;

    static String CLICKABLE_COLOR = "blue";
 
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

    int currNum = 1;
    void number(double centerX, double centerY, String url) {
        int n = currNum++;
        double width = NUMB_WIDTH;
        double fontX = centerX;
        if (n > 9) {
            width *= 1.3;
        }
        if (n == 1 || (n > 9 && n != 11 &&  n < 20)) {
            fontX -= FONT_SIZE / 10;
        }
       svg.append("  <a href='")
           .append(url)
           .append("'>\n  <rect x='")
           .append(convert(centerX - (width + STROKE_WIDTH) / 2))
           .append("' y='")
           .append(convert(centerY - (NUMB_WIDTH + STROKE_WIDTH) / 2))
           .append("' width='")
           .append(convert(width))
           .append("' height='")
           .append(convert(NUMB_WIDTH))
           .append("' rx='3' fill='white' stroke='")
           .append(CLICKABLE_COLOR)
           .append("' stroke-width='")
           .append(convert(STROKE_WIDTH))
           .append("'/>\n");
        _text(fontX, centerY, String.valueOf(n), FONT_SIZE, CLICKABLE_COLOR);
        svg.append("  </a>\n");
    }

    void text(double centerX, double centerY, String text) {
        _text(centerX, centerY, text, FONT_SIZE, "black");
    }

    void _text(double centerX, double centerY, String text, double fontSize, String color) {
        svg.append("  <text x='")
           .append(convert(centerX))
           .append("' y='")
           .append(convert(centerY + FONT_OFFSET))
           .append("' fill='")
           .append(color)
           .append("' font-family='sans-serif' font-size='")
           .append(convert(fontSize))
           .append("' text-anchor='middle'>")
           .append(text)
           .append("</text>\n");
    }

    void header(int vbar, String text) {
        _text(center(vbar), HEADER_Y, text, FONT_SIZE * 1.2, "black");
    }

    void processing(int vbar, double y, String url) {
        double rectY = y - (PROC_HEIGHT + STROKE_WIDTH) / 2;
        double startX = center(vbar) - PROC_WIDTH / 2 + PROC_SLANT / 2;
        svg.append("  <path fill='#a0a0a0' d='M")
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
           .append("  <path fill='#fff2cc' stroke='#404040' stroke-width='0.5' d='M")
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
           .append(",0Z'/>\n");
        text(center(vbar) + NUMB_WIDTH / 5, y, "Processing");
        number(startX - PROC_SLANT / 2 - STROKE_WIDTH / 2, y, url);
    }

    void dashedArrow(double centerY, int from, int to, String label, String url) {
        _arrow(centerY, from, to, label, " stroke-dasharray='4'", url);
    }

    void arrow(double centerY, int from, int to, String label, String url) {
        _arrow(centerY, from, to, label, "", url);
    }

    void _arrow(double centerY, int from, int to, String label, String additional, String url) {
        double startX = (to > from ? VBAR_WIDTH : -VBAR_WIDTH) / 2;
        double endX = to > from ? -ARROW_HEAD_LENGTH - ARROW_HEAD_GUTTER : ARROW_HEAD_LENGTH + ARROW_HEAD_GUTTER;
        String arrowY = convert(centerY);
        double textX = (center(to) + center(from)) / 2;
        svg.append("  <line x1='")
           .append(convert(center(from) + startX))
           .append("' y1='")
           .append(arrowY)
           .append("' x2='")
           .append(convert(center(to) + endX - startX))
           .append("' y2='")
           .append(arrowY)
           .append("' stroke-width='")
           .append(convert(ARROW_WIDTH))
           .append("' stroke='black' marker-end='url(#arrowHead)'")
           .append(additional)
           .append("/>\n");
        text(textX, centerY - FONT_SIZE * 0.8, label);
        number(center(from), centerY, url);
    }

    double center(int i) {
        return VBAR_START_X + i * VBAR_DISTANCE;
    }

    StateDiagram(String originalBase) throws Exception {
        this.originalBase = originalBase;
        svg.append("    <marker id='arrowHead' markerWidth='")
           .append(convert(ARROW_HEAD_LENGTH))
           .append("' markerHeight='")
           .append(convert(ARROW_HEAD_WIDTH))
           .append("' refX='0' refY='")
           .append(convert(ARROW_HEAD_WIDTH / 2))
           .append("' orient='auto'>\n        <polygon points='0 0, ")
           .append(convert(ARROW_HEAD_LENGTH))
           .append(" ")
           .append(convert(ARROW_HEAD_WIDTH / 2))
           .append(", 0 ")
           .append(convert(ARROW_HEAD_WIDTH))
           .append("' />\n" +
            "    </marker>\n" +
            "  </defs>\n");
        for (int i = 0; i < 5; i++) {
            String x = convert(i * VBAR_DISTANCE + VBAR_START_X);
            svg.append("  <line x1='")
               .append(x)
               .append("' y1='")
               .append(convert(VBAR_START_Y))
               .append("' x2='")
               .append(x)
               .append("' y2='")
               .append(convert(VBAR_HEIGHT + VBAR_START_Y))
               .append("' stroke-width='")
               .append(convert(VBAR_WIDTH))
               .append("' stroke='" + VBAR_COLOR + "'/>\n");
        }
        String embedded = readOriginal("user.svg")
           .replace("height='128'\n   width='128'", "viewBox='0 0 128 128'");
        svg.append("<svg x='")
           .append(convert(center(0) - USER_HEIGHT / 2))
           .append("' y='")
           .append(USER_TOP_Y)
           .append("' width='")
           .append(USER_HEIGHT)
           .append("' height='")
           .append(USER_HEIGHT)
           .append("'")
           .append(embedded.substring(embedded.indexOf("<svg") + 4));

        embedded = readOriginal("browser.svg")
           .replace("<svg width='80' height='80'", "<svg viewBox='0 0 80 80'");
        svg.append("<svg x='")
           .append(convert(center(1) - USER_HEIGHT / 2))
           .append("' y='")
           .append(USER_TOP_Y)
           .append("' width='")
           .append(USER_HEIGHT)
           .append("' height='")
           .append(USER_HEIGHT)
           .append("'")
          .append(embedded.substring(embedded.indexOf("<svg") + 4));
        processing(1, 100, "#init");
        processing(2, 150, "#browserui");
        arrow(150, 3, 4, "KURT", "#kurt");
        arrow(170, 3, 4, "Yes this!", "#something");
        arrow(190, 4, 3, "blah", "#else");
        dashedArrow(210, 3, 4, "Auth", "#userauth");
        header(0, "User");
        header(1, "Browser");
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