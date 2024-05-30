import java.util.ArrayList;

class Ant {
    ArrayList<Integer> tour; // Tour represented by node indices
    boolean[] visited; // Track visited nodes
    int currentCity; // Current city index
    double tourLength; // Length of the tour

    /**
     * creating a ant.
     * @param numCities the number of node which is taken from file.
     */
    public Ant(int numCities) {
        tour = new ArrayList<>();
        visited = new boolean[numCities];
        for (int i = 0; i < numCities; i++) {
            visited[i] = false;
        }
        currentCity = 0;
        tourLength = 0.0;
    }

    /**
     * Method to move to the next node
     * @param city which visited from ant.
     */
    public void visitCity(int city) {
        if (currentCity != -1) {
            tourLength += MusaKaanGuney.distance[currentCity][city];
        }
        tour.add(city);
        visited[city] = true;
        currentCity = city;
    }

    /**
     * Method to calculate tour length and return the tour
     */
    public ArrayList<Integer> getTour() {
        tourLength += MusaKaanGuney.distance[currentCity][tour.get(0)]; // Return to the starting city
        tour.add(tour.get(0));
        return tour;
    }
}