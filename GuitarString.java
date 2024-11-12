public class GuitarString {

    private RingBuffer buffer; // ring buffer
    private static final double DECAY = 0.996;
    private int ticCount = 0;

    // create a guitar string of the given frequency
    public GuitarString(double frequency) {
        buffer = new RingBuffer((int)(StdAudio.SAMPLE_RATE / frequency));
        for (int i = 0; i < (int)(StdAudio.SAMPLE_RATE / frequency); i++) {
            buffer.enqueue(0.0);
        }
    }

    // create a guitar string with size & initial values given by the array
    public GuitarString(double[] init) {
        buffer = new RingBuffer(init.length);
        for (int i = 0; i < init.length; i++) {
            buffer.enqueue(init[i]);
        }
    }

    // pluck the guitar string by replacing the buffer with white noise
    public void pluck() {
        for (int i = 0; i < buffer.size(); i++) {
            buffer.dequeue();
            buffer.enqueue(Math.random() - 0.5);
        }
    }

    // advance the simulation one time step
    public void tic() {
        double front = buffer.dequeue();
        buffer.enqueue((front + buffer.peek()) / 2 * DECAY);
        ticCount++;
    }

    // return the current sample
    public double sample() {
        return buffer.peek();
    }

    // return number of times tic was called
    public int time() {
        return ticCount;
    }

    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        double[] samples = { .2, .4, .5, .3, -.2, .4, .3, .0, -.1, -.3 };  
        GuitarString testString = new GuitarString(samples);
        for (int i = 0; i < N; i++) {
            int t = testString.time();
            double sample = testString.sample();
            System.out.printf("%6d %8.4f\n", t, sample);
            testString.tic();
        }
    }
}
