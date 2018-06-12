package View;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    private int pixelsPerTimer;
    private int animationTime = 2000;
    private int animationSmoothness = 40;

    private Color roadColor;
    private int width;

    private ArrayList<BufferedImage> gridLayers;
    private TreeMap<Integer, VehicleImage> vehicleLayers;

    private ActionListener removeListener;
    private ActionListener newRoadListener;
    private ActionListener newSinkListener;
    private ActionListener newSourceListener;

    boolean drawSink = false;
    boolean drawSource = false;
    boolean deleteMode = false;
    boolean blockDrawing = false;
    boolean startedDrawing = false;

    MapPlanner (int size, int dist, int width){

        setBackground(new Color(75,75,75));
        roadColor = new Color(115,115,115);

        this.width = width;
        this.size = size;
        this.dist = dist;

        pixelsPerTimer = dist/animationSmoothness+1;

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

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Setup
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void setupLayers (){
        gridLayers = new ArrayList<>();
        vehicleLayers = new TreeMap<>();
        //highlightLayers = new ArrayList<>();
        addBackground();
        addLayer(); //Roads
        addLayer(); //Sources & Sinks
    }

    private void addBackground (){
        BufferedImage layer = addLayer();

        Graphics2D graphics2D = (Graphics2D) layer.getGraphics();
        graphics2D.setPaint(roadColor);

        for (int i = dist; i < size * dist + dist; i += dist)
            for (int j = dist; j < size * dist + dist; j += dist)
                graphics2D.fillRect(i-width, j-width, width*2+1, width*2+1);

        repaint();
    }

    private BufferedImage addLayer (){
        BufferedImage bufferedImage = new BufferedImage(size * dist + dist,
                size * dist + dist, BufferedImage.TYPE_INT_ARGB);
        gridLayers.add(bufferedImage);
        return bufferedImage;
    }

    private VehicleImage addVehicleLayer (int id, int r, int g, int b){
        VehicleImage vehicleImage = new VehicleImage(id, r, g, b);
        vehicleLayers.put(id, vehicleImage);
        return vehicleImage;
    }

    private int reposition (int c){
        int ret = (int) Math.round((double)c/(double)dist) * dist;
        if (ret == 0) ret = dist;
        else if (ret > size*dist) ret = size*dist;
        return ret;
    }

    int getAnimationTime() { return animationTime; }

    void setAnimationTime(int animationTime) { this.animationTime = animationTime; }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Animation
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void animate() throws InterruptedException {
        int subTicks = animationSmoothness;
        while (subTicks >= 0){
            final boolean last = subTicks == 0;
            SwingUtilities.invokeLater(() -> animateVehicles(last));
            try {
                TimeUnit.MILLISECONDS.sleep(animationTime/animationSmoothness);
            } catch (InterruptedException e) {
                animateVehicles(true);
                throw e;
            }
            subTicks--;
        }
    }

    private boolean animateVehicles(boolean last){
        boolean destinationReached = true;

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
                    target -= width;
                else if (nextDirection[1] > 0)
                    target -= width;
                else if (nextDirection[1] < 0)
                    target += 1;

                if (direction[0] > 0){
                    vehicle.currentPosition[0] =
                            Math.min(vehicle.currentPosition[0], target);
                    vehicle.currentPosition[1] = getPixelPosition(vehicle.path[3]) + 1;
                    if (vehicle.currentPosition[0] != target)
                        destinationReached = false;
                    if (last)
                        vehicle.currentPosition[0] = target;
                }
                else {
                    vehicle.currentPosition[0] =
                            Math.max(vehicle.currentPosition[0], target);
                    vehicle.currentPosition[1] = getPixelPosition(vehicle.path[3]) - width;
                    if (vehicle.currentPosition[0] != target)
                        destinationReached = false;
                    if (last)
                        vehicle.currentPosition[0] = target;
                }
            }
            else if (direction[0] == 0 && direction[1] != 0){
                int target = getPixelPosition(vehicle.path[3]);

                if (nextDirection[1] > 0)
                    target += 1;
                else if (nextDirection[1] < 0)
                    target -= width;
                else if (nextDirection[0] > 0)
                    target += 1;
                else if (nextDirection[0] < 0)
                    target -= width;

                if (direction[1] > 0){
                    vehicle.currentPosition[1] =
                            Math.min(vehicle.currentPosition[1], target);
                    vehicle.currentPosition[0] = getPixelPosition(vehicle.path[2]) - width;
                    if (vehicle.currentPosition[1] != target)
                        destinationReached = false;
                    if (last)
                        vehicle.currentPosition[1] = target;
                }
                else {
                    vehicle.currentPosition[1] =
                            Math.max(vehicle.currentPosition[1], target);
                    vehicle.currentPosition[0] = getPixelPosition(vehicle.path[2]) + 1;
                    if (vehicle.currentPosition[1] != target)
                        destinationReached = false;
                    if (last)
                        vehicle.currentPosition[1] = target;
                }
            }
        }

        repaint();

        return destinationReached;

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

            boolean newVehicle = false;

            if (!vehicleLayers.containsKey(v[6])) {
                addVehicleLayer(v[6], v[7], v[8], v[9]);
                newVehicle = true;
            }
            VehicleImage vehicle = vehicleLayers.get(v[6]);

            int[] prevDirection = new int[2];
            prevDirection[0] = vehicle.path[2] - vehicle.path[0];
            prevDirection[1] = vehicle.path[3] - vehicle.path[1];

            if (vehicle.path[2] == v[2] && vehicle.path[3] == v[3]){
                vehicle.path[0] = v[2];
                vehicle.path[1] = v[3];
                vehicle.updated = true;
                continue;
            }
            else {
                vehicle.path[0] = v[0];
                vehicle.path[1] = v[1];
            }
            vehicle.path[2] = v[2];
            vehicle.path[3] = v[3];
            vehicle.path[4] = v[4];
            vehicle.path[5] = v[5];

            int[] direction = new int[2];
            direction[0] = v[2] - v[0];
            direction[1] = v[3] - v[1];

            int[] nextDirection = new int[2];
            nextDirection[0] = v[4] - v[2];
            nextDirection[1] = v[5] - v[3];

            if (direction[0] > 0){
                vehicle.currentPosition[0] =
                        getPixelPosition(vehicle.path[0]) + (prevDirection[1] > 0 ? -width : 1);
                vehicle.currentPosition[1] = getPixelPosition(vehicle.path[1]) + 1;
            }
            else if (direction[0] < 0){
                vehicle.currentPosition[0] =
                        getPixelPosition(vehicle.path[0]) + (prevDirection[1] < 0 ? 1 : -width);
                vehicle.currentPosition[1] = getPixelPosition(vehicle.path[1]) - width;
            } else if (direction[1] > 0) {
                vehicle.currentPosition[0] = getPixelPosition(vehicle.path[0]) - width;
                vehicle.currentPosition[1] =
                        getPixelPosition(vehicle.path[1]) + (prevDirection[0] < 0 ? -width : 1);
            } else if (direction[1] < 0){
                vehicle.currentPosition[0] = getPixelPosition(vehicle.path[0]) + 1;
                vehicle.currentPosition[1] =
                        getPixelPosition(vehicle.path[1]) + (prevDirection[0] > 0 ? 1 : -width);
            } else if (newVehicle){
                vehicle.currentPosition[0] = getPixelPosition(vehicle.path[0])
                        + (nextDirection[1] > 0 ? -width : (nextDirection[1] < 0 ? 1 : 1));
                vehicle.currentPosition[1] = getPixelPosition(vehicle.path[1])
                        + (nextDirection[0] > 0 ? 1 : (nextDirection[0] < 0 ? -width : 1));
            }
            vehicle.updated = true;
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
        graphics2D.setColor(roadColor);

        if (lx == rx)
            graphics2D.fillRect(lx-width, uy+width+1, width*2+1, dy-uy-width*2+1);
        else
            graphics2D.fillRect(lx+width+1, uy-width, rx-lx-width*2+1, width*2+1);

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
            graphics2D.fillRect(lx-width, uy+width+1, width*2+1, dy-uy-width*2-1);
        else
            graphics2D.fillRect(lx+width+1, uy-width, rx-lx-width*2-1, width*2+1);

        repaint();
    }

     void drawSpecialVertex (int x, int y, Color color){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        Graphics2D graphics2D = (Graphics2D) gridLayers.get(2).getGraphics();
        graphics2D.setColor(color);
        graphics2D.fillRect(x-width, y-width, width*2+1, width*2+1);

        repaint();
    }

    void removeSpecialVertex (int x, int y){
        x = getPixelPosition(x);
        y = getPixelPosition(y);

        Graphics2D graphics2D = (Graphics2D) gridLayers.get(2).getGraphics();
        graphics2D.setColor(roadColor);
        graphics2D.fillRect(x-width, y-width, width*2+1, width*2+1);

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

        VehicleImage (int id, int r, int g, int b){
            bufferedImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
            graphics2D.setColor(new Color(r,g,b));
            graphics2D.fillRect(0,0,width, width);
            this.id = id;
            path = new int[7];
            Arrays.fill(path, -1);
            currentPosition = new int[2];
        }
    }



}
