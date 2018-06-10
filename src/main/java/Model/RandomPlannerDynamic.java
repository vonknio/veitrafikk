package Model;

/**
 * Planner that chooses a new destination uniformly at random from vehicles' neighbours.
 */
class RandomPlannerDynamic extends RandomPlannerStatic {
    @Override
    public boolean isDynamic() { return true; }
}