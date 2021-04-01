import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class ValidatorIcon {
    static double WIDTH                  = 1000;
    static double HEIGHT                 = 1000;
    static double RADIUS                 = 70;
    static double STROKE_WIDTH           = 15;
    static String STROKE                 = "grey";

    static double SHACKLE_X              = 650;
    static double SHACKLE_Y              = 706;
    static double SHACKLE_WIDTH          = 140;
    static double SHACKLE_HEIGHT         = 160;
    static double SHACKLE_X_RADIUS       = 50;
    static double SHACKLE_Y_RADIUS       = 50;
   
    static double LOCK_WIDTH             = 260;
    static double LOCK_HEIGHT            = 160;
 
    static double SERVER_Y               = 86;
    static double SERVER_WIDTH           = 830;
    static double SERVER_HEIGHT          = 180;
    static double SERVER_POWER_ON        = 32;

    StringBuilder svg;

    class Coordinates {
        double x;
        double y;
        Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }

        Coordinates(Coordinates coordinates) {
            this(coordinates.x, coordinates.y);
        }

        StringBuilder write() {
            return new StringBuilder()
                .append(PSPIcon.truncateBig(x))
                .append(',')
                .append(PSPIcon.truncateBig(y));
        }
        
        StringBuilder writePlusSpace() {
            return write().append(' ');
        }

        Coordinates move(double xRelative, double yRelative) {
            x += xRelative;
            y += yRelative;
            return this;
        }
    }

    Coordinates center;
    Coordinates current;

     void shackle(String color, double strokeWidth, String additional) {
        Coordinates xLowerLeft = new Coordinates(SHACKLE_X - SHACKLE_WIDTH / 2,
                                                 SHACKLE_Y + SHACKLE_HEIGHT / 2);
        svg.append("  <path fill='none' stroke='")
           .append(color)
           .append("' stroke-width='")
           .append(PSPIcon.truncateBig(strokeWidth))
           .append('\'')
           .append(additional)
           .append(" d='M")
           .append(xLowerLeft.writePlusSpace())
           .append('L')
           .append(xLowerLeft.move(0, SHACKLE_Y_RADIUS - SHACKLE_HEIGHT).writePlusSpace())
           .append('C')
           .append(new Coordinates(xLowerLeft).move(0,
                                                    - SHACKLE_Y_RADIUS / 2).writePlusSpace())
           .append(new Coordinates(xLowerLeft).move(SHACKLE_X_RADIUS / 2,
                                                    - SHACKLE_Y_RADIUS).writePlusSpace())
           .append(new Coordinates(xLowerLeft).move(SHACKLE_X_RADIUS,
                                                    - SHACKLE_Y_RADIUS).writePlusSpace())
           .append('L')
           .append(new Coordinates(xLowerLeft).move(SHACKLE_WIDTH - SHACKLE_X_RADIUS, 
                                                    - SHACKLE_Y_RADIUS).writePlusSpace())
           .append('C')
           .append(new Coordinates(xLowerLeft).move(SHACKLE_WIDTH - SHACKLE_X_RADIUS / 2,
                                                    - SHACKLE_Y_RADIUS).writePlusSpace())
           .append(new Coordinates(xLowerLeft).move(SHACKLE_WIDTH,
                                                    - SHACKLE_Y_RADIUS / 2).writePlusSpace())
           .append(new Coordinates(xLowerLeft).move(SHACKLE_WIDTH,
                                                    0).writePlusSpace())
           .append('L')
           .append(xLowerLeft.move(SHACKLE_WIDTH, 
                                    SHACKLE_HEIGHT - SHACKLE_Y_RADIUS).write())
       .append("'/>\n");
    }

    void ventilation(double x, double y) {
         srv.append("  <rect fill='#dae3f3' height='")
            .append(PSPIcon.truncateBig(SERVER_HEIGHT * 0.7))
            .append("' stroke='#7f7f7f' stroke-width='10' width='20' x='")
            .append(PSPIcon.truncateBig(x + SERVER_POWER_ON * 5))
            .append("' y='")
            .append(PSPIcon.truncateBig(y + SERVER_HEIGHT * 0.15))
            .append("'/>\n");
    }
 
    StringBuilder srv = new StringBuilder();

    void server(double y) {
        double x = (WIDTH - SERVER_WIDTH) / 2;
        srv.append("  <rect fill='url(#serverGloss)' x='")
           .append(PSPIcon.truncateBig(x))
           .append("' y='")
           .append(PSPIcon.truncateBig(y))
           .append("' width='")
           .append(PSPIcon.truncateBig(SERVER_WIDTH))
           .append("' height='")
           .append(PSPIcon.truncateBig(SERVER_HEIGHT))
           .append("' " +
                   "rx='40' " + 
                   "stroke='#7f7f7f' stroke-width='10'/>\n" +
                   "  <circle cx='")
           .append(PSPIcon.truncateBig(x + SERVER_POWER_ON * 2))
           .append("' cy='")
           .append(PSPIcon.truncateBig(y + SERVER_POWER_ON * 2))
           .append("' fill='url(#powerOn)' r='")
           .append(PSPIcon.truncateBig(SERVER_POWER_ON))
           .append("' stroke='#333333' stroke-width='5'/>\n");
         for (int i = 0; i < 3 ; i++) {
            ventilation(x, y);
            x += 70;
        }
    }

    void padLock() {
        shackle("#808080", 60, "");
        shackle("#b0b0b0", 40, "");
        shackle("white", 14, " filter='url(#shackleGlow)'");

        svg.append("  <rect fill='url(#goldenLock)' x='")
           .append(PSPIcon.truncateBig(SHACKLE_X - LOCK_WIDTH / 2))
           .append("' y='")
           .append(PSPIcon.truncateBig(SHACKLE_Y + SHACKLE_HEIGHT / 2))
           .append("' width='")
           .append(PSPIcon.truncateBig(LOCK_WIDTH))
           .append("' height='")
           .append(PSPIcon.truncateBig(LOCK_HEIGHT))
           .append("' rx='25' stroke='#7f7f7f' stroke-width='10'/>\n");
    }

    void rack(double x, double y) {
        svg.append("   <line stroke='#4ca7e0' x1='")
           .append(PSPIcon.truncateBig(x))
           .append("' y1='")
           .append(PSPIcon.truncateBig(SERVER_Y))
           .append("' x2='")
           .append(PSPIcon.truncateBig(x))
           .append("' y2='")
           .append(PSPIcon.truncateBig(y))
           .append("' stroke-width='25'/>\n");
    }
  
    ValidatorIcon(String fileName) throws Exception {
        svg = new StringBuilder(
            "<svg width='1000' height='1000' " +
            "xmlns='http://www.w3.org/2000/svg'>\n" +
            "  <title>Validator Symbol</title>\n" +
            "  <!-- Anders Rundgren 2021 -->\n" +
            "  <defs>\n" +
            "    <filter id='shackleGlow'>\n" +
            "      <feGaussianBlur stdDeviation='2'/>\n" +
            "    </filter>\n" +
            "    <linearGradient id='goldenLock' x1='0' x2='1' y1='0.3' y2='0.6'>\n" +
            "      <stop offset='0.1' stop-color='#f0d100'/>\n" +
            "      <stop offset='0.5' stop-color='#f7f5dc'/>\n" +
            "      <stop offset='0.9' stop-color='#f0d100'/>\n" +
            "    </linearGradient>\n" +
            "    <linearGradient id='serverGloss' x1='0' x2='1' y1='0.3' y2='0.7'>\n" +
            "      <stop offset='0' stop-color='#d2ddef'/>\n" +
            "      <stop offset='0.5' stop-color='#edf2ff'/>\n" +
            "      <stop offset='1' stop-color='#d2ddef'/>\n" +
            "    </linearGradient>\n" +
            "    <radialGradient cx='0.5' cy='0.5' id='powerOn' r='0.5'>\n" +
            "      <stop offset='0' stop-color='#eaebef'/>\n" +
            "      <stop offset='1' stop-color='#ff5656'/>\n" +
            "    </radialGradient>\n");

        svg.append(
            "  </defs>\n  <rect x='" +
            PSPIcon.truncateBig(STROKE_WIDTH / 2) + 
            "' y='" +
            PSPIcon.truncateBig(STROKE_WIDTH / 2) +
            "' width='" +
            PSPIcon.truncateBig(WIDTH - STROKE_WIDTH) + "' height='" + 
            PSPIcon.truncateBig(HEIGHT - STROKE_WIDTH) + 
            "' rx='" + PSPIcon.truncateBig(RADIUS) + "' fill='white' stroke='" + STROKE + "' stroke-width='" + 
            PSPIcon.truncateBig(STROKE_WIDTH) +
            "'/>\n");

        double y = SERVER_Y;
        for (int i = 0; i < 4; i++) {
            server(y);
            y += SERVER_HEIGHT * 1.2;
        }
        rack(SERVER_POWER_ON * 5, y);
        rack(WIDTH - SERVER_POWER_ON * 5, y);
        svg.append(srv);

        padLock();

        new FileOutputStream(fileName).write(svg.append("</svg>").toString().getBytes("utf-8"));
    }

    public static void main(String[] argc) {
        try {
            new ValidatorIcon(argc[0] + File.separator + "validator.svg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
}
