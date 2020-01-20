public class Wechselstrom extends Thread
{
    boolean stop;
    Versuch versuch;

    Wechselstrom(Versuch versuch) {
        this.versuch = versuch;
    }

    @Override
    public void run() {
        super.run();
        stop = false;
        double time = 0;
        while (!stop) {
            try {
                Thread.sleep(1000 / versuch.fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time += (double)1 / (versuch.fps * versuch.zeitlupe);
            versuch.eFeldLinksLadung = Math.sin(time * versuch.frequenz * 2 * Math.PI) * versuch.eFeldSpannung;
            versuch.eFeldRechtsLadung = versuch.eFeldLinksLadung * -1;
            versuch.panel.repaint();
        }
        versuch.eFeldLinksLadung *= -1;
        versuch.eFeldRechtsLadung *= -1;
        versuch.panel.repaint();
    }
}
