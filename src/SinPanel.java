import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SinPanel extends JPanel
{
    int ball = 4;
    Versuch versuch;
    ArrayList<Double> times = new ArrayList<>();
    boolean isIn = false;
    boolean green = false;
    Rechner r;
    Wechselstrom w;
    int sWidth = 100;
    double time = 0.0;
    double prevTime = 0.0;
    boolean notDraw = false;

    SinPanel(Versuch versuch)
    {
        super();
        this.versuch = versuch;
        //setBackground(Color.white);
        r = versuch.rechner;
        w = versuch.wechselstrom;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!notDraw) {
            if (r != null && isIn != r.inEFeld) {
                times.add(w.time);
                isIn = r.inEFeld;
            }
            if (w != null) {
                time = w.time;
            }
            int fWidht = 0;
            green = false;
            int pos = 0;
            for (double d = 0; d < time * versuch.frequenz; d += 0.001) {
                if (pos < times.size() && d >= times.get(pos) * versuch.frequenz && d - 0.001 < times.get(pos) * versuch.frequenz) {
                    ++pos;
                    green = !green;
                }
                g.setColor(green ? Color.decode("#10d010") : Color.decode("#303030"));
                g.fillOval((int) Math.round(d * sWidth), (int) Math.round(((double) (getHeight() - ball) / 2) * (Math.sin(d * 2 * Math.PI) + 1)), ball, ball);
                fWidht = (int) Math.round(d * sWidth) + ball + 10;
            }
            if (prevTime != time) {
                fWidht = Math.max(fWidht, versuch.eFeldTotalWidth);
                setPreferredSize(new Dimension(fWidht, 100));
                JScrollBar scroll = versuch.scroll.getHorizontalScrollBar();
                scroll.setMaximum(fWidht);
                scroll.setValue(fWidht);

                prevTime = time;
            }
        }
    }
}
