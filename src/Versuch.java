import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Versuch {
    public static void main(String[] args) {
        new Versuch();
    }

    int frameWidth = 1100;
    int frameHeight = 850;

    //E-Feld -----------------------------------------------------------------------------------------------------------
    int eFeldWandDicke = 5; //wird 1 addiert
    int eFeldHeight = 500;
    int eFeldWidth = (int) Math.round(eFeldHeight / 2.0);
    int eFeldSpaltBreite = 150;
    int eFeldSpalBreiteMultiplikator = 20;
    int eFeldTotalWidth = 2 * eFeldWidth + eFeldSpaltBreite;
    int eFeldSymbolCount;
    int eFeldSymbolAbstand;
    int eFeldSymbolSpitze = 15;
    int eFeldMinSymbolAbstand = 2 *eFeldSymbolSpitze;
    //Links:
    long eFeldSpannung = 100;
    double eFeldLinksLadung = eFeldSpannung;
    int eFeldLinksPosX = 200;
    int eFeldLinksPosY = 170;
    //Rechts:
    int eFeldRechtsPosX = eFeldLinksPosX + eFeldSpaltBreite;
    int eFeldRechtsPosY = eFeldLinksPosY;
    double eFeldRechtsLadung = -eFeldLinksLadung;

    //M-Feld -----------------------------------------------------------------------------------------------------------
    double mFeldLadung = -10;
    int mFeldPosX;
    int mFeldPosY;

    int mFeldSymbolCountX;
    int mFeldSymbolCountY;

    int mFeldSymbolSize = 30;
    int mFeldSymbolInnerSize = 4;
    int mFeldSymbolAbstandX;
    int mFeldSymbolAbstandY;
    int mFeldMinSymbolAbstand = 10;

    int mFeldSizeX;
    int mFeldSizeY;

    //Menu -------------------------------------------------------------------------------------------------------------
    JMenuBar bar = new JMenuBar();
    JFrame frame = new JFrame();
    MyPanel panel = new MyPanel(this);
    SinPanel sinPanel = new SinPanel(this);
    JScrollPane scroll;

    JTextField masseText;
    JTextField ladungText;

    Teilchen teilchen = new Teilchen(2, eFeldLinksPosX + eFeldWidth + (eFeldSpaltBreite / 2), eFeldLinksPosY + (eFeldHeight / 2), 0, 0, 3.14);
    int teilchenSize = 10;

    double frequenz = 150000000;
    long zeitlupe = 150000000;
    int fps = 30;
    JButton play;
    JButton umpolen;

    Wechselstrom wechselstrom;
    Rechner rechner;

    JTextField eFeldText = new JTextField(String.valueOf(eFeldSpannung));

    Versuch() {
        frame.setSize(frameWidth, frameHeight);
        frame.setLayout(new GridLayout(1,1));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.add(panel);
        panel.setLayout(null);

        sinPanel.setPreferredSize(new Dimension(eFeldTotalWidth, 100));
        scroll = new JScrollPane(sinPanel);
        sinPanel.setAutoscrolls(true);

        scroll.setLocation(200, 10);
        scroll.setSize(eFeldTotalWidth , 120);

        panel.add(scroll);


        setMFeld();
        setMenu();

        play = new JButton();
        panel.add(play);
        play.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Start".equals(play.getText())) {
                    start();
                    play.setText("Stop");
                } else {
                    play.setText("Start");
                    stop();
                }
            }
        });
        play.setVisible(true);
        play.setSize(100, 100);
        play.setLocation(10, 10);
        play.setText("Start");

        umpolen = new JButton();
        panel.add(umpolen);
        umpolen.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eFeldSpannung *= -1;
                eFeldText.setText(String.valueOf(eFeldSpannung));
                eFeldLinksLadung = eFeldSpannung;
                eFeldRechtsLadung = - eFeldLinksLadung;
                System.out.println("E-Feld Spannung: " + eFeldSpannung);
                panel.repaint();
            }
        });
        umpolen.setVisible(true);
        umpolen.setSize(100, 100);
        umpolen.setLocation(eFeldLinksPosX + eFeldTotalWidth + 90, 10);
        umpolen.setText("umpolen");

        frame.setVisible(true);
        panel.setVisible(true);
    }

    void start()
    {
        sinPanel.notDraw = false;
        sinPanel.times = new ArrayList<>();
        sinPanel.isIn = false;

        wechselstrom = new Wechselstrom(this);
        wechselstrom.start();

        rechner = new Rechner(this);
        rechner.start();

        sinPanel.w = wechselstrom;
        sinPanel.r = rechner;



    }

    void stop()
    {
        wechselstrom.stop = true;
        rechner.stop = true;
        teilchen = new Teilchen(teilchen.ladung, eFeldLinksPosX + eFeldWidth + (eFeldSpaltBreite / 2), eFeldLinksPosY + (eFeldHeight / 2), 0, 0, teilchen.masse);
        panel.repaint();
    }

    void setMenu()
    {
        //Sonsziges Menu -----------------------------------------------------------------------------------------------
        JMenu menuSonstiges = new JMenu("Sonstiges");

        JPanel menuFrequenz = new JPanel();
        JTextField frequenzText = new JTextField(String.valueOf(frequenz));
        frequenzText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double d = 0;
                try {
                    frequenz = Double.parseDouble(frequenzText.getText());
                    d = frequenz;
                } catch (Exception ex) {System.out.println(frequenzText.getText() + " ist keine double Zahl!");}
                if (d == 0) {
                    frequenzText.setText(String.valueOf(frequenz));
                } else {
                    frequenz = d;
                }
            }
        });
        menuFrequenz.add(new JLabel("Frequenz(Hz): "));
        menuFrequenz.add(frequenzText);
        menuSonstiges.add(menuFrequenz);

        JPanel menuZeitlupe = new JPanel();
        //JSlider zeitlupeSlider = new JSlider();
        JTextField zeitlupeText = new JTextField();
        if (zeitlupe < 10) {
            zeitlupeText.setText("0" + String.valueOf(zeitlupe));
        } else {
            zeitlupeText.setText(String.valueOf(zeitlupe));
        }
        zeitlupeText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long l = 0;
                try {
                    l = Long.parseLong(zeitlupeText.getText());
                } catch (Exception ex) {System.out.println(zeitlupeText.getText() + " ist keine ganze Zahl!");}
                if (l == 0) {
                    zeitlupeText.setText(String.valueOf(zeitlupe));
                } else {
                    zeitlupe = l;
                    //zeitlupeSlider.setValue(zeitlupe);
                }
            }
        });
        /*zeitlupeSlider.setMinimum(1);
        zeitlupeSlider.setMaximum(99);
        zeitlupeSlider.setValue(zeitlupe);
        zeitlupeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                zeitlupe = zeitlupeSlider.getValue();
                if (zeitlupe < 10) {
                    zeitlupeText.setText("0" + String.valueOf(zeitlupe));
                } else {
                    zeitlupeText.setText(String.valueOf(zeitlupe));
                }
            }
        });*/
        menuZeitlupe.add(new JLabel("Zeitlupe: "));
        menuZeitlupe.add(zeitlupeText);
        //menuZeitlupe.add(zeitlupeSlider);
        menuSonstiges.add(menuZeitlupe);

        JPanel menuAbstand = new JPanel();
        JTextField abstandText = new JTextField(String.valueOf(eFeldSpaltBreite / eFeldSpalBreiteMultiplikator));
        abstandText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = 0;
                try {
                    i = Integer.parseInt(abstandText.getText());
                } catch (Exception ex) {System.out.println(abstandText.getText() + " ist keine ganze Zahl!");}
                if (i == 0) {
                    abstandText.setText(String.valueOf(eFeldSpaltBreite));
                } else {
                    eFeldSpaltBreite = i * eFeldSpalBreiteMultiplikator;
                    eFeldTotalWidth = 2 * eFeldWidth + eFeldSpaltBreite;
                    eFeldRechtsPosX = eFeldLinksPosX + eFeldSpaltBreite;
                    panel.repaint();
                }
            }
        });
        menuAbstand.add(new JLabel("Abstand(mm): "));
        menuAbstand.add(abstandText);
        menuSonstiges.add(menuAbstand);

        JPanel menuHeight = new JPanel();
        JTextField heightText = new JTextField(String.valueOf(eFeldHeight / (eFeldSpalBreiteMultiplikator)));
        heightText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = 0;
                try {
                    i = Integer.parseInt(heightText.getText());
                } catch (Exception ex) {System.out.println(heightText.getText() + " ist keine ganze Zahl!");}
                if (i == 0) {
                    heightText.setText(String.valueOf(eFeldHeight));
                } else {
                    eFeldHeight = i * eFeldSpalBreiteMultiplikator;
                    eFeldWidth = (int) Math.round(eFeldHeight / 2.0);
                    eFeldTotalWidth = 2 * eFeldWidth + eFeldSpaltBreite;
                    panel.repaint();
                }
            }
        });
        menuHeight.add(new JLabel("Durchmesser(mm): "));
        menuHeight.add(heightText);
        menuSonstiges.add(menuHeight);

        bar.add(menuSonstiges);
        
        
        //Teilchen Menu ------------------------------------------------------------------------------------------------
        JMenu menuTeilchen = new JMenu("Teilchen");

        JPanel menuTeilchenMasse = new JPanel();
        masseText = new JTextField(String.valueOf(teilchen.masse));
        masseText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double d = 0;
                try {
                    d = Double.parseDouble(masseText.getText());
                } catch (Exception ex) {System.out.println(masseText.getText() + " ist keine Kommazahl!");}
                if (d == 0) {
                    masseText.setText(String.valueOf(teilchen.masse));
                } else {
                    teilchen.masse = d;
                }
            }
        });
        menuTeilchenMasse.add(new JLabel("Masse(kg): "));
        menuTeilchenMasse.add(masseText);
        menuTeilchen.add(menuTeilchenMasse);

        JPanel menuTeilchenLadung = new JPanel();
        ladungText = new JTextField(String.valueOf(teilchen.ladung));
        ladungText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double d = 0;
                try {
                    d = Double.parseDouble(ladungText.getText());
                } catch (Exception ex) {System.out.println(ladungText.getText() + " ist keine Kommazahl!");}
                if (d == 0) {
                    ladungText.setText(String.valueOf(teilchen.ladung));
                } else {
                    teilchen.ladung = d;
                }
            }
        });
        menuTeilchenLadung.add(new JLabel("Ladung(C): "));
        menuTeilchenLadung.add(ladungText);
        menuTeilchen.add(menuTeilchenLadung);

        JPanel menuTeilchenPosX = new JPanel();
        JTextField posXText = new JTextField(String.valueOf(teilchen.posX - (eFeldLinksPosX + eFeldWidth + (eFeldSpaltBreite / 2))));
        posXText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double d = 0;
                try {
                    d = Double.parseDouble(posXText.getText());
                    teilchen.posX = d + (eFeldLinksPosX + eFeldWidth + (eFeldSpaltBreite / 2));
                    drawTeilchen(panel.getGraphics(), teilchen);
                } catch (Exception ex) {
                    System.out.println(posXText.getText() + " ist keine Kommazahl!");
                    posXText.setText(String.valueOf(teilchen.posX));
                }
            }
        });
        menuTeilchenPosX.add(new JLabel("X-Position: "));
        menuTeilchenPosX.add(posXText);
        menuTeilchen.add(menuTeilchenPosX);

        JPanel menuTeilchenPosY = new JPanel();
        JTextField posYText = new JTextField(String.valueOf(teilchen.posY - (eFeldLinksPosY + (eFeldHeight / 2))));
        posYText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double d = 0;
                try {
                    d = Double.parseDouble(posYText.getText());
                    teilchen.posY = d + (eFeldLinksPosY + (eFeldHeight / 2));
                    drawTeilchen(panel.getGraphics(), teilchen);
                } catch (Exception eY) {
                    System.out.println(posYText.getText() + " ist keine Kommazahl!");
                    posYText.setText(String.valueOf(teilchen.posY));
                }
            }
        });
        menuTeilchenPosY.add(new JLabel("Y-Position: "));
        menuTeilchenPosY.add(posYText);
        menuTeilchen.add(menuTeilchenPosY);

        JRadioButtonMenuItem menuElektron = new JRadioButtonMenuItem();
        menuElektron.setAction(new AbstractAction("Elektron") {
            @Override
            public void actionPerformed(ActionEvent e) {
                sinPanel.notDraw = true;
                sinPanel.prevTime = 0.0;
                sinPanel.time = 0.0;
                teilchen.masse = 9.1093837015 * Math.pow(10, -31);
                teilchen.ladung = -1.602176634 * Math.pow(10, -19);
                masseText.setText(String.valueOf(teilchen.masse));
                ladungText.setText(String.valueOf(teilchen.ladung));
                frequenz = Math.abs((teilchen.ladung * mFeldLadung) / (2 * Math.PI * teilchen.masse));
                frequenzText.setText(String.valueOf(frequenz));
                zeitlupe = Long.parseLong("300000000000");
                zeitlupeText.setText(String.valueOf(zeitlupe));
                eFeldSpannung = Long.parseLong("500000000000");
                eFeldText.setText(String.valueOf(eFeldSpannung));
            }
        });
        menuElektron.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menuTeilchen.add(menuElektron);

        JRadioButtonMenuItem menuProton = new JRadioButtonMenuItem();
        menuProton.setAction(new AbstractAction("Proton") {
            @Override
            public void actionPerformed(ActionEvent e) {
                sinPanel.notDraw = true;
                sinPanel.prevTime = 0.0;
                sinPanel.time = 0.0;
                teilchen.masse = 1.67262192369 * Math.pow(10, -27);
                teilchen.ladung = 1.602176634 * Math.pow(10, -19);
                masseText.setText(String.valueOf(teilchen.masse));
                ladungText.setText(String.valueOf(teilchen.ladung));
                frequenz = Math.abs((teilchen.ladung * mFeldLadung) / (2 * Math.PI * teilchen.masse));
                frequenzText.setText(String.valueOf(frequenz));
                zeitlupe = Long.parseLong("150000000");
                zeitlupeText.setText(String.valueOf(zeitlupe));
                eFeldSpannung = 100000000;
                eFeldText.setText(String.valueOf(eFeldSpannung));
            }
        });
        menuProton.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menuTeilchen.add(menuProton);

        JRadioButtonMenuItem menuAlpha = new JRadioButtonMenuItem();
        menuAlpha.setAction(new AbstractAction("Alpha Teilchen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                sinPanel.notDraw = true;
                sinPanel.prevTime = 0.0;
                sinPanel.time = 0.0;
                teilchen.masse = 6.6446573357 * Math.pow(10, -27);
                teilchen.ladung = 2 * 1.602176634 * Math.pow(10, -19);
                masseText.setText(String.valueOf(teilchen.masse));
                ladungText.setText(String.valueOf(teilchen.ladung));
                frequenz = Math.abs((teilchen.ladung * mFeldLadung) / (2 * Math.PI * teilchen.masse));
                frequenzText.setText(String.valueOf(frequenz));
                zeitlupe = Long.parseLong("150000000");
                zeitlupeText.setText(String.valueOf(zeitlupe));
                eFeldSpannung = 100000000;
                eFeldText.setText(String.valueOf(eFeldSpannung));
            }
        });
        menuAlpha.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menuTeilchen.add(menuAlpha);

        ButtonGroup b = new ButtonGroup();
        b.add(menuElektron);
        b.add(menuProton);
        b.add(menuAlpha);

        bar.add(menuTeilchen);

        //Felder Menu --------------------------------------------------------------------------------------------------

        JMenu menuFelder = new JMenu("Felder");

        JPanel menuMFeld = new JPanel();
        JTextField mFeldText = new JTextField(String.valueOf(mFeldLadung));
        mFeldText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double d = 0;
                try {
                    mFeldLadung = Double.parseDouble(mFeldText.getText());
                    d = mFeldLadung;
                } catch (Exception ex) {System.out.println(mFeldText.getText() + " ist keine Kommazahl!");}
                if (d == 0) {
                    mFeldText.setText(String.valueOf(mFeldLadung));
                } else {
                    System.out.println("M-Feld Ladung: " + mFeldLadung);
                    panel.repaint();
                }
            }
        });
        menuMFeld.add(new JLabel("Magnetische Flussdichte(T): "));
        menuMFeld.add(mFeldText);
        menuFelder.add(menuMFeld);

        JPanel menuEFeld = new JPanel();
        eFeldText.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long l = 0;
                try {
                    l = Long.parseLong(eFeldText.getText());
                } catch (Exception ex) {System.out.println(eFeldText.getText() + " ist kein Double!");}
                if (l == 0) {
                    eFeldText.setText(String.valueOf(eFeldSpannung));
                } else {
                    eFeldSpannung = l;
                    eFeldLinksLadung = eFeldSpannung;
                    eFeldRechtsLadung = - eFeldLinksLadung;
                    System.out.println("E-Feld Spannung: " + eFeldSpannung);
                    panel.repaint();
                }
            }
        });
        menuEFeld.add(new JLabel("E-Feld Spannung(V): "));
        menuEFeld.add(eFeldText);
        menuFelder.add(menuEFeld);

        bar.add(menuFelder);



        frame.setJMenuBar(bar);
        bar.setVisible(true);
    }

    void setMFeld() {
        if (mFeldLadung != 0){
            mFeldSymbolCountX = (int) Math.round(Math.abs(mFeldLadung) / 10);
            mFeldSymbolCountX = Math.max(mFeldSymbolCountX, 3);
            while (((mFeldSymbolCountX * mFeldSymbolSize) + ((mFeldSymbolCountX - 1) * mFeldMinSymbolAbstand)) > eFeldTotalWidth) { //midestens "mFeldMinSymbolAbstand" abstand zwischen Symbolen
                mFeldSymbolCountX--;
            }
            mFeldSymbolAbstandX = (int) Math.floor(((double) eFeldTotalWidth - (mFeldSymbolCountX * mFeldSymbolSize)) / (mFeldSymbolCountX - 1)) + 1;

            mFeldSymbolCountY = (int) Math.ceil(((double) eFeldHeight - mFeldSymbolSize) / (mFeldSymbolSize + mFeldSymbolAbstandX)) + 1;

            mFeldSymbolAbstandY = (int) Math.floor(((double) eFeldHeight - (mFeldSymbolCountY * mFeldSymbolSize)) / (mFeldSymbolCountY - 1)) + 1;


            mFeldSizeX = (mFeldSymbolCountX * mFeldSymbolSize) + ((mFeldSymbolCountX - 1) * mFeldSymbolAbstandX);
            mFeldSizeY = (mFeldSymbolCountY * mFeldSymbolSize) + ((mFeldSymbolCountY - 1) * mFeldSymbolAbstandY);

            mFeldPosX = eFeldLinksPosX + ((eFeldTotalWidth - mFeldSizeX) / 2);
            mFeldPosY = eFeldLinksPosY + ((eFeldHeight - mFeldSizeY) / 2);
        } else {
            mFeldSymbolCountX = 0;
        }
    }

    void drawTeilchen(Graphics g, Teilchen teilchen) {
        g.fillOval((int) Math.round(teilchen.posX - (teilchenSize / 2)), (int) Math.round(teilchen.posY - (teilchenSize / 2)), teilchenSize, teilchenSize);
    }

    void drawEFeld(Graphics g) {
        Color c = g.getColor();

        g.setColor(eFeldLinksLadung > 0 ? Color.red : Color.blue);
        //g.fillOval(eFeldLinksPosX - eFeldWandDicke, eFeldLinksPosY - eFeldWandDicke, eFeldWidth * 2 + 2 * eFeldWandDicke, eFeldHeight + 2 * eFeldWandDicke);
        g.fillArc(eFeldLinksPosX - eFeldWandDicke, eFeldLinksPosY - eFeldWandDicke, eFeldWidth * 2 + 2 * eFeldWandDicke, eFeldHeight + 2 * eFeldWandDicke, 90, 180);
        g.setColor(eFeldRechtsLadung > 0 ? Color.red : Color.blue);
        //g.fillOval(eFeldRechtsPosX - eFeldWandDicke, eFeldRechtsPosY - eFeldWandDicke, eFeldWidth * 2 + 2 * eFeldWandDicke, eFeldHeight + 2 * eFeldWandDicke);
        g.fillArc(eFeldRechtsPosX - eFeldWandDicke, eFeldRechtsPosY - eFeldWandDicke, eFeldWidth * 2 + 2 * eFeldWandDicke, eFeldHeight + 2 * eFeldWandDicke, 270, 180);

        g.setColor(panel.getBackground());

        g.fillOval(eFeldLinksPosX + 1, eFeldLinksPosY + 1, eFeldWidth * 2 - 2, eFeldHeight - 2);
        g.fillOval(eFeldRechtsPosX + 1, eFeldRechtsPosY + 1, eFeldWidth * 2 - 2, eFeldHeight - 2);

        g.fillRect(eFeldLinksPosX + eFeldWidth, eFeldLinksPosY - eFeldWandDicke, eFeldSpaltBreite, eFeldHeight + 2 * eFeldWandDicke);
        g.fillOval((eFeldLinksPosX + eFeldRechtsPosX) / 2, (eFeldLinksPosY + eFeldRechtsPosY) / 2, eFeldWidth * 2, eFeldHeight);

        g.setColor(eFeldLinksLadung > 0 ? Color.red : Color.blue);
        g.drawLine(eFeldLinksPosX + eFeldWidth, eFeldLinksPosY - eFeldWandDicke, eFeldLinksPosX + eFeldWidth, eFeldLinksPosY + eFeldHeight + eFeldWandDicke - 1);
        g.setColor(eFeldRechtsLadung > 0 ? Color.red : Color.blue);
        g.drawLine(eFeldRechtsPosX + eFeldWidth, eFeldRechtsPosY - eFeldWandDicke, eFeldRechtsPosX + eFeldWidth, eFeldRechtsPosY + eFeldHeight + eFeldWandDicke - 1);
        g.setColor(c);

        //eFeldSymbolCount = (int) Math.round(Math.abs(eFeldLinksLadung) / 10);
        eFeldSymbolCount = 12;
        while (eFeldSymbolCount != eFeldSymbolCount % 100) {
            eFeldSymbolCount /= 10;
        }
        eFeldSymbolCount = Math.max(eFeldSymbolCount, 2);
        while (((eFeldSymbolCount) + ((eFeldSymbolCount - 1) * eFeldMinSymbolAbstand)) > eFeldHeight) { //midestens "eFeldMinSymbolAbstand" abstand zwischen Symbolen
            eFeldSymbolCount--;
        }
        eFeldSymbolAbstand = (int) Math.floor((double) eFeldHeight / (eFeldSymbolCount));

        int posX = eFeldLinksPosX + eFeldWidth;
        int posY = eFeldLinksPosY + eFeldSymbolAbstand / 2;
        for (int i = 0; i < eFeldSymbolCount; ++i) {
            g.drawLine(posX, posY, posX + eFeldSpaltBreite, posY);
            if (eFeldLinksLadung > 0) {
                g.drawLine(posX + eFeldSpaltBreite, posY, posX + eFeldSpaltBreite - eFeldSymbolSpitze, posY - eFeldSymbolSpitze);
                g.drawLine(posX + eFeldSpaltBreite, posY, posX + eFeldSpaltBreite - eFeldSymbolSpitze, posY + eFeldSymbolSpitze);
            } else {
                g.drawLine(posX, posY, posX + eFeldSymbolSpitze, posY + eFeldSymbolSpitze);
                g.drawLine(posX, posY, posX + eFeldSymbolSpitze, posY - eFeldSymbolSpitze);
            }
            posY += eFeldSymbolAbstand;
        }
    }

    void drawMFeld(Graphics g) {
        //g.drawRect(mFeldPosX, mFeldPosY, mFeldSizeX, mFeldSizeY);
        setMFeld();
        if (mFeldLadung <= 0) {
            for (int i = 0; i < mFeldSymbolCountX; ++i) {
                for (int j = 0; j < mFeldSymbolCountY; ++j) {
                    int startX = mFeldPosX + (i * (mFeldSymbolSize + mFeldSymbolAbstandX));
                    int startY = mFeldPosY + (j * (mFeldSymbolSize + mFeldSymbolAbstandY));
                    int endX = startX + mFeldSymbolSize;
                    int endY = startY + mFeldSymbolSize;
                    g.drawLine(startX, startY, endX, endY);

                    startX += mFeldSymbolSize;
                    endX -= mFeldSymbolSize;
                    g.drawLine(startX, startY, endX, endY);
                }
            }
        } else {
            for (int i = 0; i < mFeldSymbolCountX; ++i) {
                for (int j = 0; j < mFeldSymbolCountY; ++j) {
                    int startX = mFeldPosX + (i * (mFeldSymbolSize + mFeldSymbolAbstandX));
                    int startY = mFeldPosY + (j * (mFeldSymbolSize + mFeldSymbolAbstandY));
                    g.drawOval(startX, startY, mFeldSymbolSize, mFeldSymbolSize);
                    g.fillOval(startX + ((mFeldSymbolSize - mFeldSymbolInnerSize) / 2), startY + ((mFeldSymbolSize - mFeldSymbolInnerSize) / 2), mFeldSymbolInnerSize, mFeldSymbolInnerSize);
                }
            }
        }
    }
}