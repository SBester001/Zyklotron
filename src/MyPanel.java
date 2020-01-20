import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel {

    Versuch versuch;
    int t = 0;

    MyPanel(Versuch versuch)
    {
        super();
        this.versuch = versuch;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        versuch.drawEFeld(g);
        versuch.drawMFeld(g);
        versuch.drawTeilchen(g, versuch.teilchen);
    }
}
