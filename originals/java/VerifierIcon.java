import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class VerifierIcon {
    static double WIDTH                  = 1000;
    static double HEIGHT                 = 1000;
    static double RADIUS                 = 70;
    static double STROKE_WIDTH           = 15;
    static String STROKE                 = "grey";

    static double SHACKLE_X              = 680;
    static double SHACKLE_Y              = 520;
    static double SHACKLE_WIDTH          = 220;
    static double SHACKLE_HEIGHT         = 200;
    static double SHACKLE_X_RADIUS       = 70;
    static double SHACKLE_Y_RADIUS       = 70;
   
    static double LOCK_WIDTH             = 390;
    static double LOCK_HEIGHT            = 240;
    /*
    <rect fill="url(#svg_8)" height="383.05085" id="svg_7" rx="40" stroke="#7f7f7f" stroke-width="10" width="830.50847" x="84.40678" y="145.42373"/>

    */
    static double SERVER_X               = 84;
    static double SERVER_Y               = 146;
    static double SERVER_WIDTH           = 830;
    static double SERVER_HEIGHT          = 384;

    static final double SHAFT_STROKE_WIDTH = 15;
    static final String SHAFT_STROKE       = "grey";
    static final String SHAFT_GRADIENT_ID  = "shaftGid";
    static final double SHAFT_GRADIENT_INITIAL_STOP = 0.5;
    static final double SHAFT_GRADIENT_STOP  = 1;
    static final String SHAFT_GRADIENT_STOP_COLOR   = "#8080ff";
    static final String SHAFT_GRADIENT_INNER_COLOR  = "white";

    static final double COG_STROKE_WIDTH = 20;

    static final double BIG_X            = 390;
    static final double BIG_Y            = 610;
    static final double BIG_OUTER_RADIUS = 300;
    static final double BIG_INNER_RADIUS = 190;
    static final int    BIG_COGS         = 12;
    static final double BIG_SHAFT_RADIUS = 80;
    static final double BIG_GEAR_ANGLE   = 15;
    static final double BIG_OUTER_WIDTH_PERCENT  = 0.15;
    static final double BIG_INNER_WIDTH_PERCENT  = 0.8;
    static final String BIG_STROKE       = "#009a00";
    static final String BIG_GRADIENT_ID  = "bigGearGid";
    static final double BIG_GRADIENT_INITIAL_STOP  = 0;
    static final double BIG_GRADIENT_STOP  = 0.8;
    static final String BIG_GRADIENT_STOP_COLOR   = "#c4eb4e";
    static final String BIG_GRADIENT_INNER_COLOR  = "white";

    static final double SMALL_X            = BIG_X + 330;
    static final double SMALL_Y            = BIG_Y - 330;
    static final double SMALL_OUTER_RADIUS = 200;
    static final double SMALL_INNER_RADIUS = 100;
    static final int    SMALL_COGS         = 6;
    static final double SMALL_SHAFT_RADIUS = 50;
    static final double SMALL_GEAR_ANGLE   = -12.5;
    static final double SMALL_OUTER_WIDTH_PERCENT  = 0.15;
    static final double SMALL_INNER_WIDTH_PERCENT  = 0.90;
    static final String SMALL_STROKE       = "#f27b2b";
    static final String SMALL_GRADIENT_ID  = "smallGearGid";
    static final double SMALL_GRADIENT_INITIAL_STOP  = 0.3;
    static final double SMALL_GRADIENT_STOP  = 1;
    static final String SMALL_GRADIENT_STOP_COLOR  = "#eccb1e";
    static final String SMALL_GRADIENT_INNER_COLOR  = "white";

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

    Coordinates getCoordinates(double gearAngle, double radius) {
        return new Coordinates(Math.sin(gearAngle) * radius + center.x,
                              Math.cos(gearAngle) * radius + center.y);
    }

    void writePair(Coordinates next) {
        svg.append(next.x)
           .append(',')
           .append(next.y)
           .append(' '); 
    }

    void radialGradient(String gradientId,
                        double initialStop,
                        double stop,
                        String stopColor,
                        String innerColor) {
        svg.append("    <radialGradient cx='0.5' cy='0.5' id='")
            .append(gradientId)
            .append("' r='0.5' spreadMethod='pad'>\n" +
                    "      <stop offset='")
            .append(initialStop)
            .append("' stop-color='")
            .append(innerColor)
            .append("'/>\n" +
                    "      <stop offset='")
            .append(stop)
            .append("' stop-color='")
            .append(stopColor)
            .append("'/>\n" +
                    "    </radialGradient>\n"); 
    }

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

    void padLock() {
        svg.append("  <rect fill='url(#serverGloss)' x='")
           .append(PSPIcon.truncateBig(SERVER_X))
           .append("' y='")
           .append(PSPIcon.truncateBig(SERVER_Y))
           .append("' width='")
           .append(PSPIcon.truncateBig(SERVER_WIDTH))
           .append("' height='")
           .append(PSPIcon.truncateBig(SERVER_HEIGHT))
           .append("' rx='45' stroke='#7f7f7f' stroke-width='10'/>\n");

        shackle("black", 70, "");
        shackle("#b0b0b0", 64, "");
        shackle("white", 16, " filter='url(#shackleGlow)'");

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
  
    VerifierIcon(String fileName) throws Exception {
        svg = new StringBuilder(
            "<svg width='1000' height='1000' " +
            "xmlns='http://www.w3.org/2000/svg'>\n" +
            "  <title>Verifier Symbol</title>\n" +
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
            "      <stop offset='0' stop-color='#dae3f3'/>\n" +
            "      <stop offset='0.5' stop-color='#dae3f3' stop-opacity='0.6'/>\n" +
            "      <stop offset='1' stop-color='#dae3f3'/>\n" +
            "    </linearGradient>\n");
        radialGradient(BIG_GRADIENT_ID,
                       BIG_GRADIENT_INITIAL_STOP,
                       BIG_GRADIENT_STOP,
                       BIG_GRADIENT_STOP_COLOR,
                       BIG_GRADIENT_INNER_COLOR);
        radialGradient(SMALL_GRADIENT_ID,
                       SMALL_GRADIENT_INITIAL_STOP,
                       SMALL_GRADIENT_STOP,
                       SMALL_GRADIENT_STOP_COLOR,
                       SMALL_GRADIENT_INNER_COLOR);
        radialGradient(SHAFT_GRADIENT_ID,
                       SHAFT_GRADIENT_INITIAL_STOP,
                       SHAFT_GRADIENT_STOP,
                       SHAFT_GRADIENT_STOP_COLOR,
                       SHAFT_GRADIENT_INNER_COLOR);
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
        padLock();
        new FileOutputStream(fileName).write(svg.append("</svg>").toString().getBytes("utf-8"));
    }

    public static void main(String[] argc) {
        try {
            new VerifierIcon(argc[0] + File.separator + "verifier.svg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
}
