package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.IntStream;

/**
 * TODO: Add undo functionality
 */
class MapPlanner extends JComponent {

    private int size;
    private int dist;
    private Image image;
    private Graphics2D graphics2D;
    boolean startedDrawing = false;
    boolean blockDrawing = false;
    boolean drawSource = false;
    boolean drawSink = false;

    private ActionListener newRoadListener;
    private ActionListener newSourceListener;
    private ActionListener newSinkListener;

    private int[] latestCoordinates;

    private int curX = -1, curY = -1, prevX, prevY;

    MapPlanner (int size, int dist){

        this.size = size;
        this.dist = dist;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                if (blockDrawing)
                    return;

                prevX = curX;
                prevY = curY;
                curX = e.getX();
                curY = e.getY();

                if (drawSource){
                    setGridCoordinates(curX, curY);
                    notifyAboutNewSource();
                    drawSource = false;
                    return;
                }

                if (drawSink){
                    setGridCoordinates(curX, curY);
                    notifyAboutNewSink();
                    drawSink = false;
                    return;
                }

                if (startedDrawing) {
                    setGridCoordinates(curX, curY, prevX, prevY);
                    notifyAboutNewRoad();
                }

                startedDrawing = !startedDrawing;

            }
        });
    }

    private void setupImage (){

        image = createImage(size * dist + dist, size * dist + dist);
        graphics2D = (Graphics2D) image.getGraphics();
        clear();

        graphics2D.setPaint(Color.lightGray);

        for (int i = dist; i < size * dist + dist; i += dist)
            for (int j = dist; j < size * dist + dist; j += dist)
                graphics2D.fillRect(i-1, j-1, 3, 3);

        graphics2D.setPaint(Color.black);
        repaint();

    }

    private void clear() {

        graphics2D.setPaint(Color.white);
        graphics2D.fillRect(0, 0, getSize().width, getSize().height);
        graphics2D.setPaint(Color.black);
        repaint();

    }

    private int reposition (int c){

        int ret = (int) Math.round((double)c/(double)dist) * dist;
        if (ret == 0) ret = dist;
        else if (ret > size*dist) ret = size*dist;
        return ret;

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters and setters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    int[] getCoordinates() {
        return latestCoordinates.clone();
    }

    private int getPixelPosition(int c) {
        return (c+1)*dist;
    }

    private int getGridPosition (int c){
        return (int) Math.round((double)c/(double)dist) - 1;
    }

    private void setGridCoordinates(int... args) {
        latestCoordinates = IntStream.of(args).map(x -> getGridPosition(reposition(x))).toArray();
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Draw
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void drawRoad (int x2, int y2, int x1, int y1) {
        x1 = getPixelPosition(x1);
        x2 = getPixelPosition(x2);
        y1 = getPixelPosition(y1);
        y2 = getPixelPosition(y2);

        if ((x1 != x2 && y1 != y2) || (x1 == x2 && y1 == y2))
            return;

        graphics2D.drawLine(x1, y1, x2, y2);
        repaint();
    }

     void drawSource (int x, int y){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        graphics2D.setPaint(Color.orange);
        graphics2D.fillRect(x-6, y-6, 13, 13);
        graphics2D.setPaint(Color.black);

        repaint();
    }

     void drawSink (int x, int y){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        graphics2D.setPaint(Color.blue);
        graphics2D.fillRect(x-6, y-6, 13, 13);
        graphics2D.setPaint(Color.black);

        repaint();
    }

    protected void paintComponent(Graphics g) {

        if (image == null)
            setupImage();

        g.drawImage(image, 0, 0, null);

    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Add listeners
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void addNewRoadListener(ActionListener listener) { newRoadListener = listener; }

    void addNewSourceListener(ActionListener listener) { newSourceListener = listener; }

    void addNewSinkListener(ActionListener listener) { newSinkListener = listener; }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Notify
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void notifyAboutNewRoad() {
        if (newRoadListener != null)
            newRoadListener.actionPerformed(null);
    }

    private void notifyAboutNewSource() {
        if (newSourceListener != null)
            newSourceListener.actionPerformed(null);
    }

    private void notifyAboutNewSink() {
        if (newSinkListener != null)
            newSinkListener.actionPerformed(null);
    }
}
