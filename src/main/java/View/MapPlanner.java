package View;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.IntStream;

/**
 * TODO: Add undo functionality
 *
 * Image layers:
 * 0   - background vertices
 * 1   - road lines
 * 2   - special vertices (sources, sinks)
 * Vehicle layers:
 * 0   - first vehicle
 * 1   - second vehicle
 * ... - ...
 */

class MapPlanner extends JPanel {

    private int size;
    private int dist;
    private int[] latestCoordinates;
    private int curX = -1, curY = -1, prevX, prevY;

    private int timerTicks;
    private int pixelsPerTimer;
    private int animationTime = 2000;
    private int animationSmoothness = 20;

    private ArrayList<BufferedImage> gridLayers;
    private TreeMap<Integer, VehicleImage> vehicleLayers;

    private ActionListener removeListener;
    private ActionListener newRoadListener;
    private ActionListener newSinkListener;
    private ActionListener newSourceListener;

    Timer timer;

    boolean drawSink = false;
    boolean drawSource = false;
    boolean deleteMode = false;
    boolean blockDrawing = false;
    boolean startedDrawing = false;

    MapPlanner (int size, int dist){
        this.size = size;
        this.dist = dist;

        pixelsPerTimer = dist/ animationSmoothness;

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

        timerTicks = 0;
        timer = new Timer(animationTime / animationSmoothness, e -> {
            for (Map.Entry<Integer, VehicleImage> entry : vehicleLayers.entrySet()){
                VehicleImage vehicle = entry.getValue();
                int[] direction = new int[2];
                int[] nextDirection = new int[2];

                direction[0] = vehicle.path[2] - vehicle.path[0];
                direction[1] = vehicle.path[3] - vehicle.path[1];

                nextDirection[0] = vehicle.path[4] - vehicle.path[2];
                nextDirection[1] = vehicle.path[5] - vehicle.path[3];

                vehicle.currentPosition[0] += direction[0] * pixelsPerTimer;
                vehicle.currentPosition[1] += direction[1] * pixelsPerTimer;

                if (direction[1] == 0 && direction[0] != 0) {
                    int target = getPixelPosition(vehicle.path[2]);

                    if (nextDirection[0] > 0)
                        target += 1;
                    else if (nextDirection[0] < 0)
                        target -= 4;
                    else if (nextDirection[1] > 0)
                        target -= 4;
                    else if (nextDirection[1] < 0)
                        target += 1;

                    if (direction[0] > 0){
                        vehicle.currentPosition[0] =
                                Math.min(vehicle.currentPosition[0], target);
                        vehicle.currentPosition[1] = getPixelPosition(vehicle.path[3]) + 1;
                    }
                    else {
                        vehicle.currentPosition[0] =
                                Math.max(vehicle.currentPosition[0], target);
                        vehicle.currentPosition[1] = getPixelPosition(vehicle.path[3]) - 4;
                    }
                }
                else if (direction[0] == 0 && direction[1] != 0){
                    int target = getPixelPosition(vehicle.path[3]);

                    if (nextDirection[1] > 0)
                        target += 1;
                    else if (nextDirection[1] < 0)
                        target -= 4;
                    else if (nextDirection[0] > 0)
                        target += 1;
                    else if (nextDirection[0] < 0)
                        target -= 4;

                    if (direction[1] > 0){
                        vehicle.currentPosition[1] =
                                Math.min(vehicle.currentPosition[1], target);
                        vehicle.currentPosition[0] = getPixelPosition(vehicle.path[2]) - 4;
                    }
                    else {
                        vehicle.currentPosition[1] =
                                Math.max(vehicle.currentPosition[1], target);
                        vehicle.currentPosition[0] = getPixelPosition(vehicle.path[2]) + 1;
                    }
                }
            }

            repaint();

            if (timerTicks++ > animationSmoothness + 5) {
                timerTicks = 0;
                timer.stop();
            }
        });

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Setup
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void setupLayers (){
        gridLayers = new ArrayList<>();
        vehicleLayers = new TreeMap<>();
        addBackground();
        addLayer(); //Roads
        addLayer(); //Sources & Sinks
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
        BufferedImage bufferedImage = new BufferedImage(size * dist + dist,
                size * dist + dist, BufferedImage.TYPE_INT_ARGB);
        gridLayers.add(bufferedImage);
        return bufferedImage;
    }

    private VehicleImage addVehicleLayer (int id){
        VehicleImage vehicleImage = new VehicleImage(id);
        vehicleLayers.put(id, vehicleImage);
        return vehicleImage;
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

    private int[] getPixelPosition(int[] c) {
        for (int i = 0; i < c.length; ++i)
            c[i] = getPixelPosition(c[i]);
        return c;
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

    public void updateVehicles(Collection<int[]> data){
        ArrayList<Integer> toRemove = new ArrayList<>();

        for (Map.Entry<Integer, VehicleImage> entry : vehicleLayers.entrySet()) {
            if (!entry.getValue().updated)
                toRemove.add(entry.getKey());
            entry.getValue().updated = false;
        }

        for (Integer integer : toRemove)
            vehicleLayers.remove(integer);

        for (int[] v : data){
            if (!vehicleLayers.containsKey(v[6]))
                addVehicleLayer(v[6]);
            VehicleImage vi = vehicleLayers.get(v[6]);
            vi.path[0] = v[0];
            vi.path[1] = v[1];
            vi.path[2] = v[2];
            vi.path[3] = v[3];
            vi.path[4] = v[4];
            vi.path[5] = v[5];

            int[] curDirection = new int[2];
            curDirection[0] = v[2] - v[0];
            curDirection[1] = v[3] - v[1];

            int[] nextDirection = new int[2];
            nextDirection[0] = v[4] - v[2];
            nextDirection[1] = v[5] - v[3];

            if (curDirection[0] > 0){
                vi.currentPosition[0] = getPixelPosition(vi.path[0]) + 1;
                vi.currentPosition[1] = getPixelPosition(vi.path[1]) + 1;
            }
            else if (curDirection[0] < 0){
                vi.currentPosition[0] = getPixelPosition(vi.path[0]) - 4;
                vi.currentPosition[1] = getPixelPosition(vi.path[1]) - 4;
            }
            else if (curDirection[1] > 0){
                vi.currentPosition[0] = getPixelPosition(vi.path[0]) - 4;
                vi.currentPosition[1] = getPixelPosition(vi.path[1]) + 1;
            }
            else if (curDirection[1] < 0){
                vi.currentPosition[0] = getPixelPosition(vi.path[0]) + 1;
                vi.currentPosition[1] = getPixelPosition(vi.path[1]) - 4;
            }

            vi.updated = true;
        }

        for (Map.Entry<Integer, VehicleImage> entry : vehicleLayers.entrySet()){
            if (!entry.getValue().updated){
                entry.getValue().path[0] = entry.getValue().path[2];
                entry.getValue().path[1] = entry.getValue().path[3];
                entry.getValue().path[2] = entry.getValue().path[4];
                entry.getValue().path[3] = entry.getValue().path[5];
            }
        }
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

        Graphics2D graphics2D = (Graphics2D) gridLayers.get(1).getGraphics();
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

        Graphics2D graphics2D = (Graphics2D) gridLayers.get(1).getGraphics();
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

        Graphics2D graphics2D = (Graphics2D) gridLayers.get(2).getGraphics();
        graphics2D.setColor(color);
        graphics2D.fillRect(x-4, y-4, 9, 9);

        repaint();
    }

    void removeSpecialVertex (int x, int y){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        Graphics2D graphics2D = (Graphics2D) gridLayers.get(2).getGraphics();
        graphics2D.setColor(Color.lightGray);
        graphics2D.fillRect(x-4, y-4, 9, 9);

        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (BufferedImage bufferedImage : gridLayers)
            g.drawImage(bufferedImage, 0, 0, null);
        for (Map.Entry<Integer, VehicleImage> entry : vehicleLayers.entrySet()) {
            VehicleImage vehicle = entry.getValue();
            g.drawImage(vehicle.bufferedImage, vehicle.currentPosition[0], vehicle.currentPosition[1], null);
        }
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

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Helper classes
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    class VehicleImage {
        BufferedImage bufferedImage;
        int[] path;
        int[] currentPosition;
        int id;
        boolean updated;

        VehicleImage (int id){
            bufferedImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
            graphics2D.setColor(Color.blue);
            graphics2D.fillRect(0,0,4,4);
            this.id = id;
            path = new int[7];
            currentPosition = new int[2];
        }
    }

}
