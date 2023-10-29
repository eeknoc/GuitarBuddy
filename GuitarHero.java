import java.awt.*;

public class GuitarHero {
	// set up panel and graphics objects
	private static final int WIDTH = 768;
	private static final int HEIGHT = 256;
	private static DrawingPanel panel = new DrawingPanel(WIDTH, HEIGHT);
	private static Graphics2D g = panel.getGraphics();

	private static final int DRAW_SAMPLE_RATE = 20; // draw at a rate of 20/sec
	private static final int AUDIO_PER_DRAW = StdAudio.SAMPLE_RATE / DRAW_SAMPLE_RATE;

	private static final int PLAY_TIME = 10; // target 60 seconds display window
	private static final int X_RANGE = DRAW_SAMPLE_RATE * PLAY_TIME;

	static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
	static final int RANGEOFKEYS = 37;
	static final double CONCERT_A = 440.0;

	// general scaling method
	public static double scale(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {
		return (oldValue - oldMin) / (oldMax - oldMin) * (newMax - newMin) + newMin;
	}

	// helpers for scaling to window
	public static int scaleToHeight(double oldValue) {
		return (int) (scale(oldValue, -1, 1, 0, HEIGHT));
	}

	public static int scaleToWidth(double oldValue) {
		return (int) (scale(oldValue, 0, X_RANGE, 0, WIDTH));
	}

	public static void main(String[] args) {

		// Create an array of 37 GuitarString objects
		GuitarString[] strings = new GuitarString[RANGEOFKEYS];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = new GuitarString(CONCERT_A * Math.pow(2, (i-24) / 12.0));
		}

		
		// Use keyboard.indexOf(key) to figure out which key was typed
		panel.onKeyDown((key) -> {
			if (keyboard.indexOf(key) != -1) {
				strings[keyboard.indexOf(key)].pluck();
			}
		});

		
		// fence post
		double xprev = 0, yprev = 0;

		while (true) {
			// compute the superposition of the samples for duration
			double sample = 0;
			for (int i = 0; i < strings.length; i++) {
				sample += strings[i].sample();
			}

			// send the result to standard audio
			StdAudio.play(sample);

			// advance the simulation of each guitar string by one step
			for (int i = 0; i < strings.length; i++) {
				strings[i].tic();
			}

			// Decide if we need to draw.
			// Audio sample rate is StdAudio.SAMPLE_RATE per second
			// Draw sample rate is DRAW_SAMPLE_RATE
			// Hence, we draw every StdAudio.SAMPLE_RATE / DRAW_SAMPLE_RATE
			if (strings[0].time() % AUDIO_PER_DRAW == 0) {
				g.setColor(Color.RED);
				g.drawLine(scaleToWidth(xprev),
						scaleToHeight(yprev),
						scaleToWidth(xprev + 1),
						scaleToHeight(sample));
				xprev++;
				yprev = sample;
				
				// check if wrapped around
				if (xprev > X_RANGE) {
					xprev = 0;

					// clear the image
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, WIDTH, HEIGHT);
				}
			}
		}
	}
}