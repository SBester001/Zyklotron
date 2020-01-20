public class Teilchen {
    double posX;
    double posY;

    double speedX;
    double speedY;

    double ladung;
    double masse;

    Teilchen(double ladung, int posX, int posY, int speedX, int speedY, double masse)
    {
        this.ladung = ladung;
        this.posX = posX;
        this.posY = posY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.masse = masse;
    }
}
