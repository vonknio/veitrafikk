package Model;

class Statistics {
    /**
     * Update all statistics related to given vertex for current timetick.
     */
    static void process(Vertex vertex) {
        if (vertex.stats != null)
            vertex.stats.process();
        if (vertex.getVehicle() != null && vertex.getVehicle().stats != null)
            vertex.getVehicle().stats.process();
    }
}
