import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class SpeechSoundColorMap {

	public static final Color SILENT = new Color(0);

	private static SpeechSoundColorMap speechSoundColorMap;
	private static HashMap<SpeechSound, Color> map;
	// note if we wish the color->sound assignments to change, switch the
	// concrete type to HashSet
	// (versus LinkedHashSet which maintains order)
	private static Set<Color> colors;
	private Iterator<Color> colorGenerator;

	private SpeechSoundColorMap() {
		map = new HashMap<SpeechSound, Color>();
		generateColors();
		putSoundsAndColorsInMap();
	}

	private void putSoundsAndColorsInMap() {
		int nxtSound = 0;
		SpeechSound[] speechSounds = SpeechSound.values();
		for (Color c : colors) {
			SpeechSound next = speechSounds[nxtSound];
			map.put(next, c);
			nxtSound++;
		}
	}

	private void generateColors() {
		colors = new LinkedHashSet<Color>();
		initializeAndCalibrateColorGenerator();
		for (int i = 0; i < SpeechSound.values().length; i++)
			colors.add(colorGenerator.next());
	}

	private void initializeAndCalibrateColorGenerator() {
		int numLevelsPerChannel = calculateNumLevelsPerChannelRequired();
		System.out.println(numLevelsPerChannel);
		colorGenerator = Color.WHITE.colorGenerator(numLevelsPerChannel);
	}

	private int calculateNumLevelsPerChannelRequired() {
		// num unique colors generated by colorGenerator given by
		// numLevelsPerChannel^3.
		double numUniqueColors = SpeechSound.values().length;
		// numLevelsPerChannel^3 >= numUniqueColors.
		// 1/3 treated as 0, so we take to power of "nearly there" .33.
		double reciprocalOfNumberOfChannels = .33;
		return roundUp(Math.pow(numUniqueColors, reciprocalOfNumberOfChannels));
	}

	private int roundUp(double arg) {
		return ((int) arg) + 1;
	}

	public static Color getColorForSpeechSound(SpeechSound sound) {
		if (speechSoundColorMap == null)
			speechSoundColorMap = new SpeechSoundColorMap();
        
		if(sound==null) return null;
        
		return map.get(sound);
	}

}
