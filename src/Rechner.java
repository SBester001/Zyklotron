public class Rechner extends Thread {
    boolean stop;
    Versuch versuch;

    Rechner(Versuch versuch) {
        this.versuch = versuch;
    }

    @Override
    public void run() {
        super.run();

        int time = 0;
        stop = false;
        double startX = versuch.teilchen.posX;
        double startY = versuch.teilchen.posY;
        double t = 0;

        double y0 = startY;
        double x0 = startX;

        double vx = 0;
        double vy = 0;

        while (!stop) {
            boolean inMFeld = (versuch.teilchen.posX > versuch.mFeldPosX) && (versuch.teilchen.posX < (versuch.mFeldPosX + versuch.mFeldSizeX)) && (versuch.teilchen.posY > versuch.mFeldPosY) && (versuch.teilchen.posY < (versuch.mFeldPosY + versuch.mFeldSizeY));
            boolean inEFeld = (versuch.teilchen.posX > (versuch.eFeldLinksPosX + versuch.eFeldWidth)) && (versuch.teilchen.posX < (versuch.eFeldLinksPosX + versuch.eFeldWidth + versuch.eFeldSpaltBreite)) && (versuch.teilchen.posY > versuch.eFeldLinksPosY) && (versuch.teilchen.posY < (versuch.eFeldLinksPosY + versuch.eFeldHeight));
            double posX = 0;
            double posY = 0;

            double B = versuch.mFeldLadung;
            double d = (double)versuch.eFeldSpaltBreite / (1000 * versuch.eFeldSpalBreiteMultiplikator);
            double q = versuch.teilchen.ladung;
            double m = versuch.teilchen.masse;

            double U = versuch.eFeldLinksLadung;

            if (!inEFeld) {
                U = 0;
            }
            t = (double)1 / (versuch.fps * versuch.zeitlupe);

            double temp = (((B * d * vy + U) * Math.sin((B * q * t) / m)) / (B * d)) + vx * Math.cos((B * q * t) / m);
            vy = -1 * ((-1 * (B * d * vy + U) * Math.cos((B * q * t) / m) + B * d * vx * Math.sin((B * q * t) / m) + U) / (B * d));
            vx = temp;

            versuch.teilchen.speedX = vx;
            versuch.teilchen.speedY = vy;

            y0 = versuch.teilchen.posY;
            x0 = versuch.teilchen.posX;

            if (!inMFeld || B == 0) {
                posY = vy * t + y0;
                posX = ((q * U * t * t) / (2 * m * d)) + vx * t + x0;
            } else {
                posX = (B * B * d * q * x0 - m * (B * d * vy + U) * Math.cos((B * q * t) / m) + B * d * m * vx * Math.sin((B * q * t) / m) + B * d * m * vy + m * U) / (B * B * d * q);
                posY = (B * (B * d * q * y0 - d * m * vx - q * t * U) + m * (B * d * vy + U) * Math.sin((B * q * t) / m) + B * d * m * vx * Math.cos((B * q * t) / m)) / (B * B * d * q);
            }

            double tempX = versuch.teilchen.posX;
            double tempY = versuch.teilchen.posY;

            double mLinksX = versuch.eFeldLinksPosX + (double)versuch.eFeldHeight / 2;
            double mLinksY = versuch.eFeldLinksPosY + (double)versuch.eFeldHeight / 2;
            double mRechtsX = versuch.eFeldRechtsPosX + (double)versuch.eFeldHeight / 2;
            double mRechtsY = versuch.eFeldRechtsPosY + (double)versuch.eFeldHeight / 2;
            boolean inLinks = Math.abs(Math.sqrt(Math.pow(posX - mLinksX, 2) + Math.pow(posY - mLinksY, 2))) < (double)versuch.eFeldHeight / 2;
            boolean inRechts = Math.abs(Math.sqrt(Math.pow(posX - mRechtsX, 2) + Math.pow(posY - mRechtsY, 2))) < (double)versuch.eFeldHeight / 2;

            if (!inRechts && !inLinks && !inEFeld) {
                inLinks = Math.abs(Math.sqrt(Math.pow(tempX - mLinksX, 2) + Math.pow(tempY - mLinksY, 2))) < (double)versuch.eFeldHeight / 2;
                inRechts = Math.abs(Math.sqrt(Math.pow(tempX - mRechtsX, 2) + Math.pow(tempY - mRechtsY, 2))) < (double)versuch.eFeldHeight / 2;
                if (inLinks || inRechts || inEFeld) {
                    if (!((posX > (versuch.eFeldLinksPosX + versuch.eFeldWidth)) && (posX < (versuch.eFeldLinksPosX + versuch.eFeldWidth + versuch.eFeldSpaltBreite)) && (tempX > (versuch.eFeldLinksPosX + versuch.eFeldWidth)) && (tempX < (versuch.eFeldLinksPosX + versuch.eFeldWidth + versuch.eFeldSpaltBreite)))) {
                        stop = true;
                        posX = tempX;
                        posY = tempY;
                    }
                }
            }

            versuch.teilchen.posX = posX;
            versuch.teilchen.posY = posY;


            versuch.drawTeilchen(versuch.panel.getGraphics(), versuch.teilchen);
            versuch.panel.repaint((int) Math.round(tempX - versuch.teilchenSize), (int) Math.round(tempY - versuch.teilchenSize), versuch.teilchenSize * 2, versuch.teilchenSize * 2);

            try {Thread.sleep(1000 / versuch.fps);} catch (InterruptedException e) {e.printStackTrace();}
        }
    }
}
