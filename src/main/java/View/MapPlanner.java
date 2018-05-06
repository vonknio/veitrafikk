package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * TODO: Add undo functionality
 * TODO: Change the way erasing source and sink is done
 */

/**
 * Image layers:
 * 0   - vertices
 * 1   - lines
 * 2   - special vertices
 * ... - vehicles
 */

class MapPlanner extends JPanel {

    boolean drawSink = false;
    boolean drawSource = false;
    boolean deleteMode = false;
    boolean blockDrawing = false;
    boolean startedDrawing = false;

    private int size;
    private int dist;
    private int[] latestCoordinates;
    private int curX = -1, curY = -1, prevX, prevY;
    private ArrayList<BufferedImage> layers;

    private ActionListener newRoadListener;
    private ActionListener newSinkListener;
    private ActionListener newSourceListener;
    private ActionListener removeListener;

    MapPlanner (int size, int dist){
        this.size = size;
        this.dist = dist;

        setupLayers();

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

                if (deleteMode){
                    int[] road = getClosestRoad(curX, curY);
                    if (road == null)
                        return;
                    setGridCoordinates(road[0], road[1], road[2], road[3]);
                    notifyAboutRemove();
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

    private void setupLayers (){
        layers = new ArrayList<>();
        addBackground();
        addLayer(); //Roads
        addLayer(); //Sources
        addLayer(); //Sinks
    }

    private void addBackground (){

        BufferedImage layer = addLayer();

        Graphics2D graphics2D = (Graphics2D) layer.getGraphics();
        graphics2D.setPaint(Color.lightGray);

        for (int i = dist; i < size * dist + dist; i += dist)
            for (int j = dist; j < size * dist + dist; j += dist)
                graphics2D.fillRect(i-4, j-4, 9, 9);

        repaint();

    }

    private BufferedImage addLayer (){
        layers.add(new BufferedImage(size * dist + dist,
                size * dist + dist, BufferedImage.TYPE_INT_ARGB));
        return layers.get(layers.size()-1);
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

    private int[] getClosestRoad (int x, int y){
        int[] ret = new int[4];
        ret[0] = reposition(x);
        ret[1] = reposition(y);

        int dx = x - ret[0];
        int dy = y - ret[1];

        if (Math.abs(dx) > Math.abs(dy)){
            if (dx > 0)
                ret[2] = reposition(ret[0] + dist);
            else
                ret[2] = reposition(ret[0] - dist);
            ret[3] = ret[1];
        }
        else {
            if (dy > 0)
                ret[3] = reposition(ret[1] + dist);
            else
                ret[3] = reposition(ret[1] - dist);
            ret[2] = ret[0];
        }

        if (ret[0] == ret[2] && ret[1] == ret[3])
            return null;

        return ret;
    }

    private void setGridCoordinates(int... args) {
        latestCoordinates = IntStream.of(args).map(x -> getGridPosition(reposition(x))).toArray();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Draw
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void drawUnitRoad (int x1, int y1, int x2, int y2) {
        x1 = getPixelPosition(x1);
        x2 = getPixelPosition(x2);
        y1 = getPixelPosition(y1);
        y2 = getPixelPosition(y2);

        int lx = Math.min(x1, x2);
        int rx = Math.max(x1, x2);
        int uy = Math.min(y1, y2);
        int dy = Math.max(y1, y2);

        Graphics2D graphics2D = (Graphics2D) layers.get(1).getGraphics();
        graphics2D.setColor(Color.lightGray);

        if (lx == rx)
            graphics2D.fillRect(lx-4, uy+5, 9, dy-uy-9);
        else
            graphics2D.fillRect(lx+5, uy-4, rx-lx-9, 9);

        repaint();
    }

    void drawRoad (int x1, int y1, int x2, int y2) {
        if ((x1 != x2 && y1 != y2) || (x1 == x2 && y1 == y2))
            return;

        int lx = Math.min(x1, x2);
        int rx = Math.max(x1, x2);
        int uy = Math.min(y1, y2);
        int dy = Math.max(y1, y2);

        if (lx == rx)
            for (int i = 0; i < dy-uy; ++i)
                drawUnitRoad(lx, uy+i, lx, uy+i+1);
        else
            for (int i = 0; i < rx-lx; ++i)
                drawUnitRoad(lx+i, uy, lx+i+1, uy);

        repaint();
    }

    void removeRoad (int x1, int y1, int x2, int y2){
        x1 = getPixelPosition(x1);
        y1 = getPixelPosition(y1);
        x2 = getPixelPosition(x2);
        y2 = getPixelPosition(y2);

        int lx = Math.min(x1, x2);
        int rx = Math.max(x1, x2);
        int uy = Math.min(y1, y2);
        int dy = Math.max(y1, y2);

        Graphics2D graphics2D = (Graphics2D) layers.get(1).getGraphics();
        graphics2D.setColor(getBackground());

        if (lx == rx)
            graphics2D.fillRect(lx-4, uy+5, 9, dy-uy-9);
        else
            graphics2D.fillRect(lx+5, uy-4, rx-lx-9, 9);

        repaint();
    }

     void drawSpecialVertex (int x, int y, Color color){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        Graphics2D graphics2D = (Graphics2D) layers.get(2).getGraphics();
        graphics2D.setColor(color);
        graphics2D.fillRect(x-4, y-4, 9, 9);

        repaint();
    }

    void removeSpecialVertex (int x, int y){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        Graphics2D graphics2D = (Graphics2D) layers.get(2).getGraphics();
        graphics2D.setColor(Color.lightGray);
        graphics2D.fillRect(x-4, y-4, 9, 9);

        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (BufferedImage bufferedImage : layers)
            g.drawImage(bufferedImage, 0, 0, null);
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Add listeners
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    void addNewRoadListener(ActionListener listener) { newRoadListener = listener; }

    void addNewSourceListener(ActionListener listener) { newSourceListener = listener; }

    void addNewSinkListener(ActionListener listener) { newSinkListener = listener; }

    void addRemoveListener(ActionListener listener) { removeListener = listener; }

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

    private void notifyAboutRemove() {
        if (removeListener != null)
            removeListener.actionPerformed(null);
    }

}
