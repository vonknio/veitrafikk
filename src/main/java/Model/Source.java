package Model;
import java.util.Random;

class Source extends Vertex {
    private long fill;
    private long limit;
    private float probability;

    Source(int x, int y, Vertex.VertexType type, long limit, float probability) {
        super(x, y, type);
        setLimit(limit);
        setProbability(probability);
    }

    void setLimit(long limit) {
        this.limit = limit;
    }

    long getLimit() { return limit; }

    void setProbability(float probability) {
        if (probability <= 0 || probability > 1)
            throw new IllegalArgumentException();
        this.probability = probability;
    }

    float getProbability() {
        return probability;
    }

    Vehicle spawnVehicle(Sink sink){
        Vehicle vehicle = new Vehicle(this, sink, sink.getColor());
        setVehicle(vehicle);
        fill++;
        return vehicle;
    }

    private float throwDice() { return new Random().nextFloat(); }

    boolean canSpawnVehicle(){ return willSpawnAgain() && !hasVehicle() && throwDice() <= probability; }

    boolean willSpawnAgain() {
        return fill < limit;
    }

}
