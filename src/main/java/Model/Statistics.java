package Model;

public class Statistics {
    public static void process(Vertex vertex) {
        if (vertex.stats != null)
            vertex.stats.process();
        if (vertex.getVehicle() != null && vertex.getVehicle().stats != null)
            vertex.getVehicle().stats.process();
    }
}
