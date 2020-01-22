import java.awt.*;

public class Wechselstrom extends Thread
{
    boolean stop;
    Versuch versuch;
    double time = 0;

    Wechselstrom(Versuch versuch) {
        this.versuch = versuch;
    }

    @Override
    public void run() {
        super.run();
        stop = false;

        while (!stop) {
            try {
                Thread.sleep(1000 / versuch.fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (versuch.frequenz != 0) {
                time += (double) 1 / (versuch.fps * versuch.zeitlupe);
                versuch.eFeldLinksLadung = Math.sin(time * versuch.frequenz * 2 * Math.PI) * versuch.eFeldSpannung;
                versuch.eFeldRechtsLadung = versuch.eFeldLinksLadung * -1;
            }
                versuch.panel.repaint();
        }
        if (versuch.frequenz != 0) {
            versuch.eFeldLinksLadung *= -1;
            versuch.eFeldRechtsLadung *= -1;
            versuch.panel.repaint();
        }
    }
}
