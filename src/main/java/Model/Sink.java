package Model;

import java.awt.*;
import java.util.Random;

class Sink extends Vertex {

    private static Color[] colors = new Color[]{
            new Color(20, 70, 117),
            new Color(126, 181, 209),
            new Color(37, 164, 141),
            new Color(152, 205, 183),
            new Color(247, 223, 201),
            new Color(170, 37, 41),
            new Color(186, 149, 182),
            new Color(189, 213, 86),
            new Color(251, 223, 104),
            new Color(244, 141, 88),
    };

    Sink(int x, int y, Vertex.VertexType type) { super(x, y, type); }

    private Color color;

    public Color getColor() { return color; }

    public void setColor(){
        color = colors[new Random().nextInt(colors.length)];
    }
}
