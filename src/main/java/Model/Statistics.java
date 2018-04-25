package Model;

public class Statistics {
    public static void process(Vertex vertex) {
        vertex.stats.process();
        if (vertex.getVehicle() != null) {
            vertex.getVehicle().stats.process();
        }
    }
}
