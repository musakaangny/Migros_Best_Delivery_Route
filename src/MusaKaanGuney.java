import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MusaKaanGuney {
    static int chosenMethod = 2; // to choose method to use
    static int chosenGraph = 2; // to choose graph for Ant Colony Optimization.
    static double[][] distance; // Distance matrix between cities
    static int numCities; // Number of nodes
    static double[][] pheromones; // Pheromone levels between nodes
    static double shortestDistance = Double.MAX_VALUE;
    static ArrayList<Point> shortestRoute = new ArrayList<>();


    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering(); // Use for faster animations

        int M = 180; // Number of ants
        int N = 250; // Number of iterations
        double pheromoneIntensity = 0.1;
        double degradationConstant = 0.6; // degradation rate
        double alpha = 0.9; // Alpha parameter
        double beta = 1.7; // Beta parameter
        double Q = 0.001; // Pheromone quantity
        /**
         * reading a input file.
         */
        String fileName = "input.txt";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.printf("%s can not be found.", fileName);
            System.exit(1);
        }
        Scanner inputFile = null;
        try {
            inputFile = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        /**
         * points which is taken from file are listed in Arraylist..
         */
        ArrayList<Point> houses = new ArrayList<>();
        String[] migrosC = inputFile.nextLine().split(",");
        double xm = Double.parseDouble(migrosC[0]);
        double ym = Double.parseDouble(migrosC[1]);
        Point startingPoint = new Point(1,xm,ym);
        Point lastPoint = new Point(1,xm, ym);
        houses.add(startingPoint);
        int counter = 2;
        while (inputFile.hasNextLine()) {
            String[] coor = inputFile.nextLine().split(",");
            double x = Double.parseDouble(coor[0]);
            double y = Double.parseDouble(coor[1]);
            Point points = new Point(counter,x,y);
            houses.add(points);
            counter++;
        }
        inputFile.close();
        /**
         * chosenMethod 1 is brute-force method.
         * Drawing shortesRoute by using StdDraw.
         * Giving console output which include method, the shortest distance, taken time and the shortest route.
         */
        if (chosenMethod == 1) {
            double startingTime = System.currentTimeMillis();
            generatePermutations(houses, startingPoint, lastPoint);
            shortestRoute.add(lastPoint);
            StdDraw.setCanvasSize(750, 750); // set the size of the drawing canvas
            StdDraw.setXscale(0, 1.0); // set the scale of the coordinate system
            StdDraw.setYscale(0, 1.0);
            StdDraw.clear(StdDraw.WHITE); // clear the background
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
            StdDraw.line(shortestRoute.getFirst().xCoor,shortestRoute.getFirst().yCoor, shortestRoute.get(1).xCoor, shortestRoute.get(1).yCoor);

            for (int i = 1; i < shortestRoute.size()-1; i++){
                StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                StdDraw.line(shortestRoute.get(i).xCoor,shortestRoute.get(i).yCoor, shortestRoute.get(i+1).xCoor, shortestRoute.get(i+1).yCoor);
                StdDraw.setPenColor(StdDraw.GRAY); // draw ball on the screen
                StdDraw.filledCircle(shortestRoute.get(i).xCoor, shortestRoute.get(i).yCoor, 0.015);
                StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                StdDraw.text(shortestRoute.get(i).xCoor, shortestRoute.get(i).yCoor, String.valueOf(shortestRoute.get(i).name));
            }
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // draw ball on the screen
            StdDraw.filledCircle(shortestRoute.getFirst().xCoor, shortestRoute.getFirst().yCoor, 0.015);
            StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
            StdDraw.text(shortestRoute.getFirst().xCoor, shortestRoute.getFirst().yCoor, String.valueOf(shortestRoute.getFirst().name));
            StdDraw.show();
            double endTime = System.currentTimeMillis();
            double takenTime = endTime - startingTime;
            System.out.println("Method: Brute-Force Method");
            System.out.println("Shortest Distance: " + shortestDistance);
            System.out.println("Shortest Path: " +  shortestRoute);
            System.out.println("Time it takes to find the shortest path: " + takenTime / 1000 + " seconds.");
        }
        /**
         *   chosenMethod 2 is ant colony optimization method.
         *   Drawing shortesRoute by using StdDraw.
         *   Giving console output which include method, the shortest distance, taken time and the shortest route.
         */
        else if (chosenMethod == 2){
            double startingTime = System.currentTimeMillis();
            // Generate or read coordinates of cities (houses and Migros)
            numCities = counter-1;

            // Compute distances between cities
            distance = new double[numCities][numCities];
            for (int i = 0; i < numCities; i++) {
                for (int j = 0; j < numCities; j++) {
                    distance[i][j] = houses.get(i).distanceTo(houses.get(j));
                }
            }

            // Initialize pheromone levels
            initializePheromones(pheromoneIntensity);

            // Main loop
            ArrayList<Ant> ants = new ArrayList<>();
            for (int iteration = 0; iteration < N; iteration++) {
                for (int i = 0; i < M; i++) {
                    ants.add(new Ant(numCities));
                }

                // Construct tours
                for (Ant ant : ants) {
                    while (ant.tour.size() < numCities + 1) {
                        int nextCity = selectNextCity(ant, beta, alpha);
                        ant.visitCity(nextCity);
                    }
                }

                // Update pheromone levels
                updatePheromones(ants,degradationConstant,Q);
            }
            // Find the best tour
            Ant bestAnt = null;
            double bestTourLength = Double.MAX_VALUE;
            for (Ant ant : ants) {
                if (ant.tourLength < bestTourLength) {
                    bestAnt = ant;
                    bestTourLength = ant.tourLength;
                }
            }

            // Output the best tour
            ArrayList<Point> bestTour = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(bestAnt).tour.size(); i++){
                Ant finalBestAnt = bestAnt;
                int finalI = i;
                bestTour.add(houses.stream().filter(a ->
                    a.name == finalBestAnt.tour.get(finalI)+1
                ).findAny().orElse(null));
            }
            ArrayList<Point> bestRoute = new ArrayList<>();
            for (int i = 0; i<numCities + 1; i++){
                bestRoute.add(bestTour.get(i));
            }
            StdDraw.setCanvasSize(750, 750); // set the size of the drawing canvas
            StdDraw.setXscale(0, 1.0); // set the scale of the coordinate system
            StdDraw.setYscale(0, 1.0);
            StdDraw.clear(StdDraw.WHITE); // clear the background
            /**
             * choosing which graphs to display
             */
            if (chosenGraph == 1){
                for (int m = 0; m < pheromones.length; m++) {
                    for (int n = m + 1; n < pheromones.length; n++) {
                        StdDraw.setPenRadius(0.0005 * pheromones[m][n]);
                        StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                        StdDraw.line(houses.get(m).xCoor, houses.get(m).yCoor, houses.get(n).xCoor, houses.get(n).yCoor);
                    }
                }
                for (Point house : houses) {
                    StdDraw.setPenColor(StdDraw.GRAY); // draw ball on the screen
                    StdDraw.filledCircle(house.xCoor, house.yCoor, 0.015);
                    StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                    StdDraw.text(house.xCoor, house.yCoor, String.valueOf(house.name));
                }
            }
            else if (chosenGraph == 2){
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                StdDraw.line(bestTour.getFirst().xCoor,bestTour.getFirst().yCoor, bestTour.get(1).xCoor, bestTour.get(1).yCoor);

                for (int i = 1; i < bestRoute.size()-1; i++){
                    StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                    StdDraw.line(bestRoute.get(i).xCoor,bestRoute.get(i).yCoor, bestRoute.get(i+1).xCoor, bestRoute.get(i+1).yCoor);
                    StdDraw.setPenColor(StdDraw.GRAY); // draw ball on the screen
                    StdDraw.filledCircle(bestRoute.get(i).xCoor, bestRoute.get(i).yCoor, 0.015);
                    StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                    StdDraw.text(bestRoute.get(i).xCoor, bestRoute.get(i).yCoor, String.valueOf(bestRoute.get(i).name));
                }
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // draw ball on the screen
                StdDraw.filledCircle(bestRoute.getFirst().xCoor, bestRoute.getFirst().yCoor, 0.015);
                StdDraw.setPenColor(StdDraw.BLACK); // draw ball on the screen
                StdDraw.text(bestRoute.getFirst().xCoor, bestRoute.getFirst().yCoor, String.valueOf(bestRoute.getFirst().name));
            }
            StdDraw.show();
            double endTime = System.currentTimeMillis();
            double takenTime = endTime - startingTime;
            System.out.println("Method: Ant Colony Optimization Approach");
            System.out.println("Shortest Distance: " + bestTourLength);
            System.out.println("Shortest Path: " + bestRoute);
            System.out.println("Time it takes to find the shortest path: " + takenTime / 1000 + " seconds.");
        }
    }

    /** method to create permutations.
     * @param points all nodes which include migros and houses.
     * @param startingPoint starting node to start route (Migros)
     * @param lastPoint last node to finish route (Migros)
     */
    public static void generatePermutations(ArrayList<Point> points, Point startingPoint, Point lastPoint) {
        ArrayList<Point> remainingPoints = new ArrayList<>(points);
        remainingPoints.remove(startingPoint);
        ArrayList<Point> currentPermutation = new ArrayList<>();
        currentPermutation.add(startingPoint);
        generatePermutationsHelper(remainingPoints, currentPermutation, lastPoint);
    }

    /** helper method to create permutations.
     * @param remainingPoints points which not visited.
     * @param currentPermutation points which include points respectively.
     * @param lastPoint last node to finish route (Migros)
     */
    private static void generatePermutationsHelper(ArrayList<Point> remainingPoints, ArrayList<Point> currentPermutation, Point lastPoint) {
        if (remainingPoints.isEmpty()) {
            if (currentPermutation.size() > 1) {
                if (calculateTotalDistance(currentPermutation) < shortestDistance) {
                    shortestDistance = calculateTotalDistance(currentPermutation);
                    shortestRoute = new ArrayList<>(currentPermutation);
                }
            }
        }

        for (int i = 0; i < remainingPoints.size(); i++) {
            Point currentPoint = remainingPoints.get(i);
            ArrayList<Point> newRemainingPoints = new ArrayList<>(remainingPoints);
            newRemainingPoints.remove(i);

            currentPermutation.add(currentPoint);
            generatePermutationsHelper(newRemainingPoints, currentPermutation, lastPoint);
            currentPermutation.remove(currentPermutation.size() - 1);
        }
    }

    /** method to calculate path distances.
     * @param path path to calculate distances between path's nodes.
     */
    public static double calculateTotalDistance(ArrayList<Point> path) {
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += path.get(i).distanceTo(path.get(i + 1));
        }
        totalDistance += path.getLast().distanceTo(path.getFirst());
        return totalDistance;
    }

    /** starting matrix which demonstrates pheromone densities.
     * @param pheromoneIntensity initial intensity of pheromone which is 0.1.
     */
    public static void initializePheromones(double pheromoneIntensity) {
        pheromones = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] = pheromoneIntensity;
            }
        }
    }

    /** Method to update pheromone levels after each iteration
     *
     * @param ants all ants to update pheromone density.
     * @param degradationConstant parameter to pheromone update.
     * @param Q parameter to pheromone update.
     */
    public static void updatePheromones(ArrayList<Ant> ants, double degradationConstant, double Q) {
        // Pheromone deposit by ants
        for (Ant ant : ants) {
            ArrayList<Integer> tour = ant.getTour();
            for (int i = 0; i < numCities; i++) {
                int house1 = tour.get(i);
                int house2 = tour.get(i + 1);
                pheromones[house1][house2] += Q / ant.tourLength;
                pheromones[house2][house1] += Q / ant.tourLength;
            }
        }
        // Evaporation
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] *= (degradationConstant);
            }
        }
    }

    /**
     * Method to select the next node for an ant based on pheromone levels and distances
     * @param ant ant which goes to next node.
     * @param beta constant to take a power
     * @param alpha constant to take a power
     * @return
     */
    public static int selectNextCity(Ant ant, double beta, double alpha) {
        int currentCity = ant.currentCity;
        double[] probabilities = new double[numCities];
        double sum = 0.0;
        for (int i = 0; i < numCities; i++) {
            if (!ant.visited[i]) {
                probabilities[i] = Math.pow(pheromones[currentCity][i], alpha) *
                        Math.pow(1.0 / distance[currentCity][i], beta);
                sum += probabilities[i];
            }
        }

        // Roulette wheel selection
        double rand = Math.random() * sum;
        double partialSum = 0.0;
        for (int i = 0; i < numCities; i++) {
            partialSum += probabilities[i];
            if (partialSum >= rand) {
                return i;
            }
        }
        // In case of rounding errors
        return -1;
    }
}