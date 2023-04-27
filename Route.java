import java.io.*;
import java.util.*;

public class Route {
    public static void main(String[] args) {
        // Read in command line arguments
        String sourceAirport = args[0];
        String destinationAirport = args[1];
        int beginningTime = Integer.parseInt(args[2]);

        // Read in airports and flights data from input files
        List<String> airports = readAirports("airports.txt");
        List<Flight> flights = readFlights("flights.txt");

        // Create a temporal graph
        TemporalGraph graph = new TemporalGraph(airports, flights);

        // Initialize a priority queue with the source airport
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        Vertex sourceVertex = graph.getVertex(sourceAirport);
        sourceVertex.dvalue = beginningTime;
        pq.add(sourceVertex);

        // Run modified Dijkstra's algorithm
        while (!pq.isEmpty()) {
            Vertex u = pq.poll();

            for (Edge e : u.edges) {
                if (e.departureTime >= u.dvalue) {
                    Vertex v = e.destination;
                    int newDvalue = e.arrivalTime;

                    if (newDvalue < v.dvalue) {
                        v.dvalue = newDvalue;
                        pq.remove(v);
                        pq.add(v);
                    }
                }
            }
        }

        // Output the earliest possible arrival time at the destination airport
        Vertex destinationVertex = graph.getVertex(destinationAirport);
        System.out.println(destinationVertex.dvalue);
    }

    public static List<String> readAirports(String filename) {
        List<String> airports = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                airports.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return airports;
    }

    public static List<Flight> readFlights(String filename) {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("flights.txt"))) {
            // Skip the first two lines
            int numFlights = Integer.parseInt(br.readLine());
            br.readLine();
            String line;
            int flightsProcessed = 0; // Add this variable
            while ((line = br.readLine()) != null && flightsProcessed < numFlights) { // Modify loop condition
        
                String[] parts = line.split("\\s+");
                String airline = parts[0].trim();
                String flightNumber = parts[1].trim();
                String source = parts[2].trim();
                String destination = parts[3].trim();
                int departureTime = Integer.parseInt(parts[4].trim());
                int arrivalTime = Integer.parseInt(parts[5].trim());
                int distance = Integer.parseInt(parts[6].trim());
                flights.add(new Flight(source, destination, departureTime, arrivalTime));
                System.out.println(parts);
        
                flightsProcessed++; // Increment flightsProcessed
            }
            return flights; // Move return statement outside the try block
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        return flights;
    }
}

class Flight {
    String source;
    String destination;
    int departureTime;
    int arrivalTime;

    public Flight(String source, String destination, int departureTime, int arrivalTime) {
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
}

class TemporalGraph {
    Map<String, Vertex> vertices;

    public TemporalGraph(List<String> airports, List<Flight> flights) {
        vertices = new HashMap<>();

        // Create a vertex for each airport
        for (String airport : airports) {
            vertices.put(airport, new Vertex(airport));
        }

        // Create an edge for each flight
        for (Flight flight : flights) {
            Vertex source = vertices.get(flight.source);
            Vertex destination = vertices.get(flight.destination);
            source.edges.add(new Edge(destination, flight.departureTime, flight.arrivalTime));
        }
    }

    public Vertex getVertex(String airport) {
        return vertices.get(airport);
    }
}

class Vertex implements Comparable<Vertex> {
    String airport;
    int dvalue;
    List<Edge> edges;

    public Vertex(String airport) {
        this.airport = airport;
        this.dvalue = Integer.MAX_VALUE;
        this.edges = new ArrayList<>();
    }

    @Override
    public int compareTo(Vertex other) {
        return Integer.compare(this.dvalue, other.dvalue);
    }
}

class Edge {
    Vertex destination;
    int departureTime;
    int arrivalTime;

    public Edge(Vertex destination, int departureTime, int arrivalTime) {
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
}