import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class StateDiagram {

    static final String FILE_NAME = "statediagram.svg";

    static final double ACTOR_TOP_Y = 32;
    static final double ACTOR_HEIGHT = 40;

    static final double PROC_WIDTH = 86;
    static final double PROC_SLANT = 8;
    static final double PROC_HEIGHT = 22;

    static final double VBAR_WIDTH = 1.2;
    static final double VBAR_START_Y = ACTOR_TOP_Y + ACTOR_HEIGHT - 5;
    static final double VBAR_X_DISTANCE = 120;
    static final double VBAR_START_X = PROC_WIDTH / 2 + 20;
    static final String VBAR_COLOR = "#cc2020";

    static final double NUMB_WIDTH = 14;

    static final double STROKE_WIDTH = 0.5;

    static final double ARROW_WIDTH = 0.8;
    static final double ARROW_HEAD_WIDTH = 5;
    static final double ARROW_HEAD_LENGTH = 7;
    static final double ARROW_HEAD_GUTTER = 0.5;

    static final double DROP_OFFSET = 2;

    static final int FONT_SIZE = 12;
    static final double FONT_OFFSET = 4;

    static final double HEADER_Y = 20;

    static final double UI_WIDTH = 86;
    static final double UI_HEIGHT = 50;
    static final double UI_BORDER = 5;
 
    static final String CLICKABLE_COLOR = "blue";

    static final double SEQ_Y_DISTANCE = 32;
    static final double SEQ_Y_SLACK_AFTER_UI = 0;

    String originalBase;

    double seqY = 96;

    StringBuilder svg = new StringBuilder ();

/*
    <filter id='dropArbitrary'>
      <feOffset result='offOut' in='SourceAlpha' dx='3' dy='3'/>
      <feFlood flood-color='black' result='color' flood-opacity='0.4'/>
      <feComposite in='color' in2='offOut' operator='in' result='sombra'/>
      <feGaussianBlur result='blurOut' in='sombra' stdDeviation='1'/>
      <feBlend in='SourceGraphic' in2='blurOut' mode='normal'/>
    </filter>
*/

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

    void processing(int vbar, String url) {
        double rectY = seqY - (PROC_HEIGHT + STROKE_WIDTH) / 2;
        double startX = center(vbar) - PROC_WIDTH / 2 + PROC_SLANT / 2;
        svg.append("  <path fill='black' opacity='0.4' d='M")
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
        text(center(vbar) + NUMB_WIDTH / 5, seqY, "Processing");
        number(startX - PROC_SLANT / 2 - STROKE_WIDTH / 2, seqY, url);
        seqY += SEQ_Y_DISTANCE + SEQ_Y_SLACK_AFTER_UI;
    }

    void dashedArrow(int from, int to, String label, String url) {
        _arrow(from, to, label, " stroke-dasharray='4'", url);
    }

    void arrow(int from, int to, String label, String url) {
        _arrow(from, to, label, "", url);
    }

    void _arrow(int from, int to, String label, String additional, String url) {
        double startX = (to > from ? VBAR_WIDTH : -VBAR_WIDTH) / 2;
        double endX = to > from ? -ARROW_HEAD_LENGTH - ARROW_HEAD_GUTTER : ARROW_HEAD_LENGTH + ARROW_HEAD_GUTTER;
        String arrowY = convert(seqY);
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
        text(textX, seqY - FONT_SIZE * 0.8, label);
        number(center(from), seqY, url);
        seqY += SEQ_Y_DISTANCE;
    }

    double center(int i) {
        return VBAR_START_X + i * VBAR_X_DISTANCE;
    }

    String embedded;

    double findArgument(String property) throws Exception {
        char delim = '"';
        int start = embedded.indexOf(property + "=\"");
        if (start < 0) {
            start = embedded.indexOf(property + "='");
            if (start < 0) {
                throw new IOException("Did not find: " + property);
            }
            delim = '\''; 
        }
        int valueStart = start + property.length() + 2;
        int valueStop = embedded.indexOf(delim, valueStart);
        double argument = Double.valueOf(embedded.substring(valueStart, valueStop));
        embedded = embedded.substring(0, start) + "\u0000" + embedded.substring(valueStop + 1);
        return argument;
    }
    void actor(String fileName, String label, int vbar) throws Exception {
        embedded = readOriginal(fileName).replace('"', '\'');
        double width = findArgument("width");
        double height = findArgument("height");
        int start = embedded.indexOf('\u0000');
        int stop = embedded.lastIndexOf('\u0000');
        embedded = embedded.substring(0, start) + "viewBox='0 0 " + convert(width) + " " + convert(height) + "'" +
            embedded.substring(stop + 1);
         double embeddedWidth = ACTOR_HEIGHT * width / height;
        svg.append("<svg x='")
           .append(convert(center(vbar) - embeddedWidth / 2))
           .append("' y='")
           .append(ACTOR_TOP_Y)
           .append("' width='")
           .append(convert(embeddedWidth))
           .append("' height='")
           .append(ACTOR_HEIGHT)
           .append("'")
          .append(embedded.substring(embedded.indexOf("<svg") + 4));
        _text(center(vbar), HEADER_Y, label, FONT_SIZE * 1.2, "black");

     }

    StateDiagram(String originalBase) throws Exception {
        this.originalBase = originalBase;

        actor("user.svg",     "User",     0);
        actor("browser.svg",  "Browser",  1);
        actor("merchant.svg", "Merchant", 2);
        actor("acquirer.svg", "Acquirer", 3);
        actor("issuer.svg",   "Issuer",   4);

        arrow(2, 1, "PREQ", "#preq");

        double uiY = seqY + (UI_HEIGHT - PROC_HEIGHT) / 2;
        double uiTop = uiY - (UI_HEIGHT + STROKE_WIDTH) / 2;
        double uiX = center(1) - (UI_WIDTH + STROKE_WIDTH) / 2;
        svg.append("  <rect x='")
           .append(convert(uiX + DROP_OFFSET))
           .append("' y='")
           .append(convert(uiTop + DROP_OFFSET))
           .append("' width='")
           .append(convert(UI_WIDTH))
           .append("' height='")
           .append(convert(UI_HEIGHT))
           .append("' fill='black' opacity='0.4' filter='url(#dropShaddow)'/>\n")
           .append("  <rect x='")
           .append(convert(uiX))
           .append("' y='")
           .append(convert(uiTop))
           .append("' width='")
           .append(convert(UI_WIDTH))
           .append("' height='")
           .append(convert(UI_HEIGHT))
           .append("' fill='white' stroke-width='")
           .append(convert(STROKE_WIDTH))
           .append("' stroke='black'/>\n")
           .append("  <rect x='")
           .append(convert(uiX))
           .append("' y='")
           .append(convert(uiTop))
           .append("' width='")
           .append(convert(UI_WIDTH))
           .append("' height='")
           .append(convert(UI_BORDER))
           .append("' fill='#deeafc' stroke-width='")
           .append(convert(STROKE_WIDTH))
           .append("' stroke='black'/>\n");  
        text(center(1), uiY, "Payment UI");
        number(uiX, uiTop + (UI_BORDER + STROKE_WIDTH) / 2, "#paymentui");
        seqY += SEQ_Y_DISTANCE + SEQ_Y_SLACK_AFTER_UI + UI_HEIGHT - PROC_HEIGHT;
        dashedArrow(0, 1, "Authorization", "#userauth");
        processing(1, "#fpwcore");
        arrow(1, 2, "PRES", "#pres");
        processing(2, "#presproc");
        arrow(2, 3, "AREQ", "#areq");
        processing(3, "#areqproc");
        arrow(3, 4, "IREQ", "#ireq");
        processing(4, "#ireqproc");
  //      dashedArrow(4, 3, "Out of Scope", "#outofscope");
 
        seqY -= PROC_HEIGHT * 0.2;
        StringBuilder preludium = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n" +
                                                    "<svg viewBox='0 0 ")
            .append(convert(center(4) + PROC_WIDTH / 2 + 20))
            .append(" ")
            .append(convert(seqY + 10))
            .append("' xmlns='http://www.w3.org/2000/svg'>\n" +
  "    <title>FIDO Web Pay - State Diagram</title>\n" +
  "    <!-- Anders Rundgren 2021 -->\n" +
  "    <defs>\n" +
  "    <filter id='dropShaddow'>\n" +
  "      <feGaussianBlur stdDeviation='1.3'/>\n" +
  "    </filter>\n")
            .append("    <marker id='arrowHead' markerWidth='")
            .append(convert(ARROW_HEAD_LENGTH))
            .append("' markerHeight='")
            .append(convert(ARROW_HEAD_WIDTH))
            .append("' refX='0' refY='")
            .append(convert(ARROW_HEAD_WIDTH / 2))
            .append("' orient='auto' markerUnits='userSpaceOnUse'>\n" +
            "      <polygon points='0 0, ")
            .append(convert(ARROW_HEAD_LENGTH))
            .append(" ")
            .append(convert(ARROW_HEAD_WIDTH / 2))
            .append(", 0 ")
            .append(convert(ARROW_HEAD_WIDTH))
            .append("'/>\n" +
               "    </marker>\n" +
               "  </defs>\n");
        for (int i = 0; i < 5; i++) {
            String x = convert(i * VBAR_X_DISTANCE + VBAR_START_X);
            preludium.append("  <line x1='")
                 .append(x)
                 .append("' y1='")
                 .append(convert(VBAR_START_Y))
                 .append("' x2='")
                 .append(x)
                 .append("' y2='")
                 .append(convert(seqY))
                 .append("' stroke-width='")
                 .append(convert(VBAR_WIDTH))
                 .append("' stroke='" + VBAR_COLOR + "'/>\n");
        }
        svg.insert(0, preludium);

        new FileOutputStream(originalBase + FILE_NAME).write(svg.append("</svg>").toString().getBytes("utf-8"));
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
