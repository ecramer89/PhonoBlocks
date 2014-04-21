import java.util.Iterator;

public class Color {
	
	public static final Color WHITE=new Color(255);
	public static final int NUM_CHANNELS=3;
	
	public float r;
	public float g;
	public float b;
	public float intensity;

	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(float intensity) {
		this(intensity, intensity, intensity);
		this.intensity = intensity;

	}
	
	public ColorGenerator colorGenerator(int numLevels) {
		return new ColorGenerator(numLevels);	
	}

	private class ColorGenerator implements Iterator<Color> {
		
		public static final int LIMIT=255;

		private static final int DEFAULT_LEVELS_PER_CHANNEL = 3;

		private static final float DEFAULT_STEP = LIMIT/DEFAULT_LEVELS_PER_CHANNEL;
		
		private int rCursor;
		private int gCursor;
		private int bCursor;
		
		private int numLevelsPerChannel;
		private float step;

		public ColorGenerator(int numLevels) {
			rCursor = gCursor = bCursor = 0;
			setNumLevelsPerChannel(numLevels);
		}
		
		
		private void setNumLevelsPerChannel(int numLevels){
			if(numLevels>0) {
				numLevelsPerChannel=numLevels;
				step=LIMIT/numLevels;
			}
			numLevelsPerChannel=DEFAULT_LEVELS_PER_CHANNEL;
			step=DEFAULT_STEP;
		}

		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public Color next() {
			return incrementB();
		}
		
		private Color incrementR(){
			if(rCursor<numLevelsPerChannel)
				rCursor++;
			else 
				rCursor=0;
			return makeColor();
			
		}
		
		private Color incrementG(){
			if(gCursor<numLevelsPerChannel){
				gCursor++;
			    return makeColor();
			}
			else gCursor=0;
			return incrementR();
		}
		
		private Color incrementB(){
			if(bCursor<numLevelsPerChannel){
				bCursor++;
			    return makeColor();
			}
			else bCursor=0;
			return incrementG();
		}
		
	
		private Color makeColor() {
			return new Color(rCursor*step,bCursor*step,gCursor*step);
		}

		@Override
		public void remove() {
			return;

		}

	}


}
