/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 07.12.14
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SimpleApplet extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
    //апплет выводит заданным цветом состояние клавиатуры и мыши
    //анимирует спрайт и проигрывает фоновую музыку
    String text;//текст, который надо вывести
    Color colorText;//цвет текста
    Image offScreen;//собственно дублирующий буфер
    Graphics graph;//графический контекст дублирующего буфера
    Thread appThread = null;
    Image pic[] = new Image[28];//кадры для анимации, расположенные по порядку
    int frame = 0;//номер текущего кадра, нужен для анимации
    MediaTracker tracker = new MediaTracker(this);
    AudioClip soundTrack = null;//звук
    char oneKey;
    int mouseX = 0, mouseY = 0;
    boolean mouseK[] = {false,false,false};
    int mouseW = 0;

    public void init(){
        this.setBackground(new Color(0x888888));//установлен фоновый цвет = серый 50%
        //теперь попробуем считать значения параметров
        //заданные апплету при вызове из html-документа
        text = getParameter("text");
        if(text == null)
            text = "Ничего не введено";
        String colorValue = getParameter("color");
        if(colorValue == null)
            colorValue = "000000";//цветом по умолчанию будет чёрный
        colorText = stringToColor(colorValue);
        offScreen = createImage(320, 200);//создание дублирующего буфера размером 320x200 пикселей
        graph = offScreen.getGraphics();//получить графический контекст для него
        for(int i = 0; i < 28; i++){
            pic[i] = getImage(getCodeBase(),"resource/fregat" + i + ".gif");
            tracker.addImage(pic[i], i);
            try{tracker.waitForID(i);}catch(InterruptedException e){}
            //следующая строка обязательно нужна для Windows, если загружается несколько изображений
            pic[i].flush();//нормальные операционные системы этого не требуют - впрочем и не ругаются
        }
        //загрузить космическую музыку
        soundTrack = getAudioClip(getCodeBase(), "resource/spacemusic.au");
        //регистрация Слушателей
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    public void start(){
        if(appThread == null){
            appThread =  new Thread(this);
            appThread.start();
        }
        if(soundTrack != null)
            soundTrack.loop();//запустить воспроизведение звука
    }

    public void stop(){
        appThread = null;
        if(soundTrack != null)
            soundTrack.stop();//остановить воспроизведение звука
    }

    public void run(){
        while(appThread != null){
            //здесь производим все необходимые действия
            if(frame < 27)
                frame++;//увеличить номер кадра
            else
                frame = 0;//обнулить
            //всё подсчитали? тогда пора перерисовать изображение
            repaint();
            try{//начинается часть, которая может вызвать исключительную ситуацию
                Thread.sleep(50);//усыпляем поток на 50 миллисекунд
            }catch(InterruptedException e) {}//именно так -
            // - фактическая обработка исключения нам не нужна, но нужно соблюсти правила
        }
    }

    public void paint(Graphics g){
        //прежде всего очистим дублирующий буфер
        Dimension d = this.getSize();//получение текущих размеров окна
        graph.clearRect(0, 0, d.width, d.height);//очистка дублирующего буфера
        //теперь можно рисовать - но мы хотим рисовать не на экран, а в буфер
        //графический контекст экрана g - к нему теперь не обращаемся
        //графический контекст буфера graph - вот через него и будем рисовать
        //точно так же, как раньше рисовали на экран

        graph.setColor(colorText);//устанавливаем цвет
        graph.drawString(text + " - keyboard = " + oneKey, 30, 30);//выводим текст
        graph.drawImage(pic[frame],
                100 - pic[frame].getWidth(null)/2,//скорректировать центр спрайта
                100 - pic[frame].getHeight(null)/2,
                this);
        graph.drawString("" + mouseX + "," + mouseY
                + " [ " + mouseK[0] + " , " + mouseK[1] + " , " + mouseK[2] + " ] "
                + " колесо=" + mouseW, 50, 50);//и состояние мыши

        //всё что хотели - нарисовали
        //пришло время перебросить нарисованную в буфере картинку на экран
        g.drawImage(offScreen, 0, 0, this);//поместить изображение на экран
    }

    public void update(Graphics g){
        paint(g);
    }

    private Color stringToColor(String colorString){
        //метод принимает строку, содержащую цвет в формате "rrggbb"
        int red = (Integer.decode("0x" + colorString.substring(0, 2))).intValue();
        int green = (Integer.decode("0x" + colorString.substring(2, 4))).intValue();
        int blue = (Integer.decode("0x" + colorString.substring(4, 6))).intValue();
        //а возвращает цвет
        return new Color(red, green, blue);
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