import java.io.*;
import java.util.*;
 
public class Main {
 
    static class Segment {
        long need;
        long gain;
        int building;
        int end;
 
        Segment(long need, long gain, int building, int end) {
            this.need = need;
            this.gain = gain;
            this.building = building;
            this.end = end;
        }
    }
 
    static class Building {
        int m;
        long[] a;
        long[] b;
 
        ArrayList<Segment> segments = new ArrayList<>();
 
        Building(int m) {
            this.m = m;
            a = new long[m];
            b = new long[m];
        }
    }
 
    public static void main(String[] args) throws Exception {
 
        FastScanner fs = new FastScanner(System.in);
        StringBuilder out = new StringBuilder();
 
        int t = fs.nextInt();
 
        while (t-- > 0) {
 
            int n = fs.nextInt();
            long capital = fs.nextLong();
 
            Building[] buildings = new Building[n];
 
            // Read input
            for (int i = 0; i < n; i++) {
 
                int m = fs.nextInt();
 
                buildings[i] = new Building(m);
 
                for (int j = 0; j < m; j++) {
                    buildings[i].a[j] = fs.nextLong();
                }
 
                for (int j = 0; j < m; j++) {
                    buildings[i].b[j] = fs.nextLong();
                }
            }
 
            /*
             * Step 1:
             * Divide every building into the shortest
             * segments whose total gain >= 0.
             */
            for (int i = 0; i < n; i++) {
 
                Building building = buildings[i];
 
                long balance = 0;
                long need = 0;
 
                for (int j = 0; j < building.m; j++) {
 
                    // Capital needed before this floor
                    need = Math.max(
                        need,
                        building.a[j] - balance
                    );
 
                    // Gain/loss after building this floor
                    balance += building.b[j] - building.a[j];
 
                    // We have found the shortest
                    // non-negative segment
                    if (balance >= 0) {
 
                        building.segments.add(
                            new Segment(
                                need,
                                balance,
                                i,
                                j + 1
                            )
                        );
 
                        // Start constructing the next segment
                        balance = 0;
                        need = 0;
                    }
                }
            }
 
            /*
             * Step 2:
             * Put the first available segment of every
             * building into a priority queue.
             */
            PriorityQueue<Segment> pq =
                new PriorityQueue<>(
                    (s1, s2) -> {
                        if (s1.need != s2.need) {
                            return Long.compare(s1.need, s2.need);
                        }
                        return Integer.compare(
                            s1.building,
                            s2.building
                        );
                    }
                );
 
            // Pointer to the next segment of each building
            int[] ptr = new int[n];
 
            for (int i = 0; i < n; i++) {
 
                if (!buildings[i].segments.isEmpty()) {
                    pq.add(buildings[i].segments.get(0));
                }
            }
 
            /*
             * Current height reached in every building
             * during the money-making phase.
             */
            int[] height = new int[n];
 
            /*
             * Greedily build every affordable
             * non-negative segment.
             */
            while (!pq.isEmpty()) {
 
                Segment cur = pq.peek();
 
                // Cheapest available segment is too expensive.
                // Therefore no other segment can be built.
                if (cur.need > capital) {
                    break;
                }
 
                pq.poll();
 
                // Build this whole segment
                capital += cur.gain;
 
                height[cur.building] = cur.end;
 
                // Expose the next segment of this building
                ptr[cur.building]++;
 
                Building building = buildings[cur.building];
 
                if (ptr[cur.building] < building.segments.size()) {
 
                    pq.add(
                        building.segments.get(
                            ptr[cur.building]
                        )
                    );
                }
            }
 
            /*
             * Step 3:
             *
             * Now capital is as large as possible.
             *
             * For every building independently,
             * continue building floor-by-floor.
             */
            int answerHeight = 0;
            int answerBuilding = 0;
 
            for (int i = 0; i < n; i++) {
 
                Building building = buildings[i];
 
                int h = height[i];
                long money = capital;
 
                while (h < building.m &&
                       money >= building.a[h]) {
 
                    money -= building.a[h];
                    money += building.b[h];
 
                    h++;
                }
 
                if (h > answerHeight) {
                    answerHeight = h;
                    answerBuilding = i;
                }
            }
 
            // Building index is 1-based
            out.append(answerHeight)
               .append(' ')
               .append(answerBuilding + 1)
               .append('
');
        }
 
        System.out.print(out);
    }
 
    // Fast input
    static class FastScanner {
 
        private final InputStream in;
        private final byte[] buffer = new byte[1 << 16];
        private int ptr = 0;
        private int len = 0;
 
        FastScanner(InputStream in) {
            this.in = in;
        }
 
        private int read() throws IOException {
 
            if (ptr >= len) {
                len = in.read(buffer);
                ptr = 0;
 
                if (len <= 0) {
                    return -1;
                }
            }
 
            return buffer[ptr++];
        }
 
        long nextLong() throws IOException {
 
            int c;
 
            do {
                c = read();
            } while (c <= ' ');
 
            long sign = 1;
 
            if (c == '-') {
                sign = -1;
                c = read();
            }
 
            long result = 0;
 
            while (c > ' ') {
                result = result * 10 + (c - '0');
                c = read();
            }
 
            return result * sign;
        }
 
        int nextInt() throws IOException {
            return (int) nextLong();
        }
    }
}