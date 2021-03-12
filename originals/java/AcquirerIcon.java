import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class AcquirerIcon {

    static final double COG_STROKE_WIDTH = 5;

     static final double BIG_X            = 500;
     static final double BIG_Y            = 500;
     static final double BIG_OUTER_RADIUS = 250;
     static final double BIG_INNER_RADIUS = 150;
     static final int    BIG_COGS         = 8;
     static final double BIG_SHAFT_RADIUS = 80;
     static final double BIG_GEAR_ANGLE   = 0;
     static final double BIG_OUTER_WIDTH_PERCENT  = 0.1;
     static final double BIG_INNER_WIDTH_PERCENT  = 0.5;
     static final String BIG_STROKE       = "blue";
     static final String BIG_FILL         = "red";

    StringBuilder svg = new StringBuilder("<svg x='0' y='0' width='1000' height='1000' " +
                                          "xmlns='http://www.w3.org/2000/svg'>\n");

    class Cordinates {
        double x;
        double y;
        Cordinates(double x, double y) {
        System.out.println("x=" + x + " y=" + y);
            this.x = x;
            this.y = y;
        }
    }

    Cordinates center;
    Cordinates current;

    Cordinates getCordinates(double gearAngle, double radius) {
        return new Cordinates(Math.sin(gearAngle) * radius + center.x,
                              Math.cos(gearAngle) * radius + center.y);
    }

    void writePair(Cordinates next) {
        svg.append(next.x)
           .append(',')
           .append(next.y)
           .append(' ');    /*
        svg.append(next.x - current.x)
           .append(',')
           .append(next.y - current.y)
           .append(' ');
        current.x += next.x;
        current.y += next.y;
        */
    }

    void addGear(double x, double y, double outerRadius, double innerRadius,
                 int cogs, double shaftRadius, double gearAngle,
                 double outerWidthPercent, double innerWidthPercent,
                 String stroke, String fill) throws Exception {
            svg.append("<path stroke='")
               .append(stroke)
               .append("' fill='")
               .append(fill)
               .append("' d='M");
            center = new Cordinates(x, y);
            current = new Cordinates(0, 0);
            double halfCog = Math.PI / cogs;
            double innerWidthRadiansDiv2 =  innerWidthPercent * halfCog;
            double outerWidthRadiansDiv2 =  outerWidthPercent * halfCog;
           for (int cog = 0; cog < cogs; cog++) {
                writePair(getCordinates(gearAngle - innerWidthRadiansDiv2, innerRadius));
                writePair(getCordinates(gearAngle - outerWidthRadiansDiv2, outerRadius));
                writePair(getCordinates(gearAngle + outerWidthRadiansDiv2, outerRadius));
                writePair(getCordinates(gearAngle + innerWidthRadiansDiv2, innerRadius));
                  System.out.println(gearAngle);
                gearAngle += (Math.PI * 2) / cogs;
            }
            svg.append("z'/>\n");
            System.out.println(svg.toString());
        }
  
    AcquirerIcon(String fileName) throws Exception {
        System.out.println(fileName);
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
                BIG_FILL);
        svg.append("</svg>");
        new FileOutputStream(fileName).write(svg.toString().getBytes("utf-8"));
    }

    public static void main(String[] argc) {
        try {
            new AcquirerIcon(argc[0] + File.separator + "acquirer.svg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
}
