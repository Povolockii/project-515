import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main extends Frame implements WindowListener{

    WorkCanvas arbeit = new WorkCanvas();

    public static void main (String[] args){
        Main win = new Main();
    }

    Main(){
        super("Windowed Application");

        //установить системные цвета
        Color cBkgView = new Color(SystemColor.menu.getRGB());//взять системный цвет
        setBackground(cBkgView);//и установить его как фоновый
        //системные цвета установлены
        //теперь "встраиваем" компонент в область окна
        setLayout(new BorderLayout());//назначаем способ (менеджер) размещения
        add(arbeit, BorderLayout.CENTER);//и добавляем компонент
        pack();//"упаковываем" добавленные компоненты
        //теперь окно программы готово к работе
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();//получение размеров всего экрана
        setBounds((d.width - 300)/2, (d.height - 200)/2, 300,200);//центрировать окно размером 300x200
        show();
        arbeit.init();//и вот только теперь инициализация дублирующего буфера рабочего экрана
        // окно создано - теперь регистрируем Слушателей
        this.addWindowListener(this);
    }

    ////
    //  Методы, унаследованные от интерфейса WindowListener
    //
    public void windowActivated(WindowEvent e){;}
    public void windowClosed(WindowEvent e){
        dispose();
        System.exit(0);
    }
    public void windowClosing(WindowEvent e){
        dispose();
        System.exit(0);
    }
    public void windowDeactivated(WindowEvent e){;}
    public void windowDeiconified(WindowEvent e){;}
    public void windowIconified(WindowEvent e){;}
    public void windowOpened(WindowEvent e){;}


    ////
    //  WorkCanvas - наш рабочий холст, на нём мы будем рисовать
    //
    public class WorkCanvas extends Canvas implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
        Image offScreen;//собственно дублирующий буфер
        Graphics graph;//графический контекст дублирующего буфера

        private char oneKey;
        private int mouseX = 0, mouseY = 0;
        private boolean mouseK[] = {false,false,false};
        private int mouseW = 0;

        WorkCanvas(){
            // регистрируем Слушателей
            this.addKeyListener(this);
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addMouseWheelListener(this);
        }

        public void init(){//только создаёт видеобуфер
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();//получение размеров всего экрана
            offScreen = createImage(d.width, d.height);//создание буфера максимально необходимого размера
            graph = offScreen.getGraphics();//установка графического контекста
            //теперь через graph можно рисовать в буфер
        }

        public void paint(Graphics g){
            if(graph == null) return;
            //прежде всего очистим дублирующий буфер
            Dimension d = this.getSize();//получение текущих размеров окна
            graph.clearRect(0, 0, d.width, d.height);//очистка дублирующего буфера
            //теперь можно рисовать - но мы хотим рисовать не на экран, а в буфер
            //графический контекст экрана g - к нему теперь не обращаемся
            //графический контекст буфера graph - вот через него и будем рисовать
            //точно так же, как раньше рисовали на экран

            graph.drawString("" + oneKey, 100, 50);
            graph.drawString(""+mouseX+","+mouseY
                    +" [ "+mouseK[0]+" , "+mouseK[1]+" , "+mouseK[2]+" ] "
                    +" колесо="+mouseW, 50,75);//и состояние мыши
            graph.drawString("Welcome Windowed Application!", 100, 100);
            // нарисовать стрелочку, указывающую на начало координат
            graph.setColor(new Color(0xff,0xaa,0x44));
            graph.drawLine(1,1, 15,15);
            graph.drawLine(3,3, 3,6);
            graph.drawLine(3,3, 6,3);
            graph.drawString("0,0", 20,20);

            //всё что хотели - нарисовали
            //пришло время перебросить нарисованную в буфере картинку на экран
            g.drawImage(offScreen, 0, 0, this);//поместить изображение на экран
        }

        ////
        //  Методы, унаследованные от интерфейса KeyListener
        //
        public void keyPressed(KeyEvent e){
            oneKey = (char)e.getKeyChar();
            repaint();
        }
        public void keyReleased(KeyEvent e){;}
        public void keyTyped(KeyEvent e){;}

        ////
        //  Методы, унаследованные от интерфейса MouseListener
        //
        public void mousePressed(MouseEvent e){
            switch(e.getButton()){
                case MouseEvent.BUTTON1 : mouseK[0] = true; break;
                case MouseEvent.BUTTON2 : mouseK[1] = true; break;
                case MouseEvent.BUTTON3 : mouseK[2] = true; break;
            }
            repaint();
        }
        public void mouseReleased(MouseEvent e){
            switch(e.getButton()){
                case MouseEvent.BUTTON1 : mouseK[0] = false; break;
                case MouseEvent.BUTTON2 : mouseK[1] = false; break;
                case MouseEvent.BUTTON3 : mouseK[2] = false; break;
            }
            repaint();
        }
        public void mouseClicked(MouseEvent e){;}
        public void mouseEntered(MouseEvent e){;}
        public void mouseExited(MouseEvent e){;}

        ////
        //  Методы, унаследованные от интерфейса MouseMotionListener
        //
        public void mouseMoved(MouseEvent e){
            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }
        public void mouseDragged(MouseEvent e){
            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }

        ////
        //  Методы, унаследованные от интерфейса MouseWheelListener
        //
        public void mouseWheelMoved(MouseWheelEvent e){
            mouseW = e.getWheelRotation();
            repaint();
        }
    }

}
