import java.util.ArrayList;

public class Point {
    public double xCoor;
    public double yCoor;
    public int name;

    /**
     *
     * @param name name of the nodes.
     * @param inputX x coordinates of the nodes.
     * @param inputY y coordinates of the nodes.
     */
    Point(int name,double inputX, double inputY){
        this.xCoor = inputX;
        this.yCoor = inputY;
        this.name = name;
    }

    /**
     * method to calculate distance between two nodes.
     */
    public double distanceTo(Point other) {
        double dx = this.xCoor- other.xCoor;
        double dy = this.yCoor - other.yCoor;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public String toString(){
        return "" + name;
    }
}
