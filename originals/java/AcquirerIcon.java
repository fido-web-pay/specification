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
     static final double BIG_OUTER_WIDTH  = 100;
     static final double BIG_INNER_WIDTH  = 150;
     static final String BIG_STROKE       = "";
     static final String BIG_FILL         = "";

    StringBuilder svg = new StringBuilder("<svg x='0' y='0' width='1000' height='1000' " +
                                          "xmlns='http://www.w3.org/2000/svg'>\n");

    class Cordinates {
        double x;
        double y;
    }

    Cordinates getCordinates(double gearAngle, double radius, double xDistance) {
        Cordinates cordinates = new Cordinates();
        return cordinates;
    }

    void writePair(Cordinates current, Cordinates next) {
        svg.append(current.x = next.x - current.x)
           .append(',')
           .append(current.y = next.y - current.y)
           .append(' ');
    }

    void addGear(double x, double y, double outerRadius, double innerRadius,
                 int cogs, double shaftRadius, double gearAngle,
                 double outerWidth, double innerWidth,
                 String stroke, String fill) throws Exception {
            svg.append("<path stroke='")
               .append(stroke)
               .append("' fill='")
               .append(fill)
               .append("' d='m");
            Cordinates current = new Cordinates();
            for (int cog = 0; cog < cogs; cog++) {
                writePair(current, getCordinates(gearAngle, innerRadius, -innerWidth / 2));
                writePair(current, getCordinates(gearAngle, outerRadius, -innerWidth / 2));
                writePair(current, getCordinates(gearAngle, outerRadius, innerWidth / 2));
                writePair(current, getCordinates(gearAngle, innerRadius, innerWidth / 2));
                gearAngle += 360 / cogs;
            }
            svg.append("z'/>\n");
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
                BIG_OUTER_WIDTH,
                BIG_INNER_WIDTH,
                BIG_STROKE,
                BIG_FILL);
    }

    public static void main(String[] argc) {
        try {
            new AcquirerIcon(argc[0] + File.separator + "aquirer.svg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
}
