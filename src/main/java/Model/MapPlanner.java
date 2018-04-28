package Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TODO: Add undo functionality
 */
public class MapPlanner extends JComponent {

    private int size;
    private int dist;
    private Image image;
    private Graphics2D graphics2D;
    private Grid grid;
    public boolean startedDrawing = false;
    public boolean blockDrawing = false;
    public boolean drawSource = false;
    public boolean drawSink = false;

    private int curX = -1, curY = -1, prevX, prevY;

    public MapPlanner (Grid grid, int size, int dist){

        this.size = size;
        this.dist = dist;
        this.grid = grid;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                if (blockDrawing)
                    return;

                prevX = curX;
                prevY = curY;
                curX = e.getX();
                curY = e.getY();

                if (drawSource){
                    drawSource(curX, curY);
                    drawSource = false;
                    return;
                }

                if (drawSink){
                    drawSink(curX, curY);
                    drawSink = false;
                    return;
                }

                if (startedDrawing)
                    drawRoad(curX, curY, prevX, prevY);

                startedDrawing = !startedDrawing;

            }
        });

    }

    protected void paintComponent(Graphics g) {

        if (image == null)
            setupImage();

        g.drawImage(image, 0, 0, null);

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

    private int getGridPosition (int c){
        return (int) Math.round((double)c/(double)dist) - 1;
    }

     void drawRoad (int x2, int y2, int x1, int y1){

        x1 = reposition(x1);
        x2 = reposition(x2);
        y1 = reposition(y1);
        y2 = reposition(y2);

        if ((x1 != x2 && y1 != y2) || (x1 == x2 && y1 == y2))
            return;

        graphics2D.drawLine(x1, y1, x2, y2);
        repaint();

        grid.addLongRoad(getGridPosition(x1), getGridPosition(y1), getGridPosition(x2), getGridPosition(y2));

    }

     void drawSource (int x, int y){

        x = reposition(x);
        y = reposition(y);

        graphics2D.setPaint(Color.orange);
        graphics2D.fillRect(x-6, y-6, 13, 13);
        graphics2D.setPaint(Color.black);

        repaint();

        grid.addSource(getGridPosition(x), getGridPosition(y));

    }

     void drawSink (int x, int y){

        x = reposition(x);
        y = reposition(y);

        graphics2D.setPaint(Color.blue);
        graphics2D.fillRect(x-6, y-6, 13, 13);
        graphics2D.setPaint(Color.black);

        repaint();

        grid.addSink(getGridPosition(x), getGridPosition(y));

    }

}
