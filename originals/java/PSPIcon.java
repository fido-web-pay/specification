import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class PSPIcon {
    static double WIDTH                  = 1000;
    static double HEIGHT                 = 1000;
    static double RADIUS                 = 70;
    static double STROKE_WIDTH           = 15;
    static String STROKE                 = "grey";

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
    }

    Coordinates center;
    Coordinates current;

    Coordinates getCoordinates(double gearAngle, double radius) {
        return new Coordinates(Math.sin(gearAngle) * radius + center.x,
                               Math.cos(gearAngle) * radius + center.y);
    }

    static String truncateBig(double value) {
        String truncated = String.format("%.1f", value);
        if (truncated.endsWith(".0")) {
            return truncated.substring(0, truncated.length() - 2);
        }
        return truncated;
    }

    void writePair(Coordinates next) {
        svg.append(truncateBig(next.x))
           .append(',')
           .append(truncateBig(next.y))
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

    void addGear(double x, double y, double outerRadius, double innerRadius,
                 int cogs, double shaftRadius, double gearAngle,
                 double outerWidthPercent, double innerWidthPercent,
                 String stroke, String gradientId) throws Exception {
        gearAngle = (Math.PI * gearAngle) / 180;
        svg.append("  <path stroke='")
            .append(stroke)
            .append("' stroke-width='" + truncateBig(COG_STROKE_WIDTH) + "' fill='url(#")
            .append(gradientId)
            .append(")' d='M");
        center = new Coordinates(x, y);
        current = new Coordinates(0, 0);
        double halfCog = Math.PI / cogs;
        double innerWidthRadiansDiv2 =  innerWidthPercent * halfCog;
        double outerWidthRadiansDiv2 =  outerWidthPercent * halfCog;
        for (int cog = 0; cog < cogs; cog++) {
            writePair(getCoordinates(gearAngle - innerWidthRadiansDiv2, innerRadius));
            writePair(getCoordinates(gearAngle - outerWidthRadiansDiv2, outerRadius));
            writePair(getCoordinates(gearAngle + outerWidthRadiansDiv2, outerRadius));
            writePair(getCoordinates(gearAngle + innerWidthRadiansDiv2, innerRadius));
            gearAngle += (Math.PI * 2) / cogs;
        }
        svg.append("z'/>\n")
            .append("  <circle stroke='")
            .append(SHAFT_STROKE)
            .append("' stroke-width='" + truncateBig(SHAFT_STROKE_WIDTH) + "' fill='url(#")
            .append(SHAFT_GRADIENT_ID)
            .append(")' r='")
            .append(truncateBig(shaftRadius))
            .append("' cx='")
            .append(truncateBig(x))
            .append("' cy='")
            .append(truncateBig(y))
            .append("'/>\n");
    }
  
    PSPIcon(String fileName) throws Exception {
        svg = new StringBuilder(
            "<svg width='1000' height='1000' " +
            "xmlns='http://www.w3.org/2000/svg'>\n" +
            "  <title>PSP Symbol</title>\n" +
            "  <!-- Anders Rundgren 2021 -->\n" +
            "  <defs>\n");
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
            truncateBig(STROKE_WIDTH / 2) + 
            "' y='" +
            truncateBig(STROKE_WIDTH / 2) +
            "' width='" +
            truncateBig(WIDTH - STROKE_WIDTH) + "' height='" + 
            truncateBig(HEIGHT - STROKE_WIDTH) + 
            "' rx='" + truncateBig(RADIUS) + "' fill='white' stroke='" + STROKE + "' stroke-width='" +
            truncateBig(STROKE_WIDTH) +
            "'/>\n");
        addGear(BIG_X,
                BIG_Y,
                BIG_OUTER_RADIUS,
                BIG_INNER_RADIUS,
                BIG_COGS,
                BIG_SHAFT_RADIUS,
                BIG_GEAR_ANGLE,
                BIG_OUTER_WIDTH_PERCENT,
                BIG_INNER_WIDTH_PERCENT,
                BIG_STROKE,
                BIG_GRADIENT_ID);
        addGear(SMALL_X,
                SMALL_Y,
                SMALL_OUTER_RADIUS,
                SMALL_INNER_RADIUS,
                SMALL_COGS,
                SMALL_SHAFT_RADIUS,
                SMALL_GEAR_ANGLE,
                SMALL_OUTER_WIDTH_PERCENT,
                SMALL_INNER_WIDTH_PERCENT,
                SMALL_STROKE,
                SMALL_GRADIENT_ID);
        new FileOutputStream(fileName).write(svg.append("</svg>").toString().getBytes("utf-8"));
    }

    public static void main(String[] argc) {
        try {
            new PSPIcon(argc[0] + File.separator + "psp.svg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
}
