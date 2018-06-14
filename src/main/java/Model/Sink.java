package Model;

import java.awt.*;
import java.util.Random;

class Sink extends Vertex {

    private static Color[] colors = new Color[]{
            new Color(41,187,156),
            new Color(93,213,143),
            new Color(96,173,224),
            new Color(154,92,180),
            new Color(240,195,48),
            new Color(228,126,48),
            new Color(229,77,66)
    };

    Sink(int x, int y, Vertex.VertexType type) { super(x, y, type); }

    private Color color;

    public Color getColor() { return color; }

    public void setColor(){
        color = colors[new Random().nextInt(colors.length)];
    }
}
