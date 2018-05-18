package Model;

import java.util.Random;

public class Source extends Vertex {
    private long fill;
    private long limit;
    private float probability;

    Source(int x, int y, Vertex.VertexType type, long limit, float probability) {
        super(x, y, type);
        setLimit(limit);
        setProbability(probability);
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getLimit() { return limit; }

    public void setProbability(float probability) {
        if (probability < 0 || probability > 1)
            throw new IllegalArgumentException();
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
    }

    public Vehicle spawnVehicle(Sink sink){
        Vehicle vehicle = new Vehicle(this, sink);
        setVehicle(vehicle);
        fill++;
        return vehicle;
    }

    private float throwDice() { return new Random().nextFloat(); }

    public boolean canSpawnVehicle(){ return fill < limit && !hasVehicle() && throwDice() <= probability; }

}
