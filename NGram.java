import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum NGram  {
	CONSONANT_DIGRAPH(2), VOWEL_DIGRAPH(2), VOWEL(1), 
    CONSONANT(1), CONSONANT_LE(3), 
    TH("TH", CONSONANT_DIGRAPH, new SpeechSound[] {SpeechSound.TH}), 
    AA("AA", VOWEL_DIGRAPH, new SpeechSound[] {SpeechSound.AY}), 
    AE("AE",VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.AY}), 
    AI("AI", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.AY}), AU("AU", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.AW}), 
    EE("EE", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.EE}), EA("EA", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.EE}), 
    EI("EI",VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.AY}), IE("IE", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.EE}), 
    OO("OO", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.OO}), OI("OI", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.OY}), 
    OE("OE",VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.OH}), OU("OU", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.OO}), 
    UE("UE", VOWEL_DIGRAPH,new SpeechSound[] {SpeechSound.YOU}), 
    BLE("BLE", CONSONANT_LE,new SpeechSound[] {SpeechSound.BULL}), 
    DLE("DLE", CONSONANT_LE,new SpeechSound[] {SpeechSound.DULL}), 
    TLE("TLE", CONSONANT_LE,new SpeechSound[] {SpeechSound.TULL}), 
    B("B", CONSONANT,new SpeechSound[] {SpeechSound.B}), D("D", CONSONANT,new SpeechSound[] {SpeechSound.D}), 
    F("F", CONSONANT,new SpeechSound[] {SpeechSound.F}), H("H",CONSONANT,new SpeechSound[] {SpeechSound.H}), 
    T("T", CONSONANT,new SpeechSound[] {SpeechSound.T}), L("L", CONSONANT,new SpeechSound[] {SpeechSound.L}), 
    N("N", CONSONANT,new SpeechSound[] {SpeechSound.N}), 
    A("A", VOWEL,new SpeechSound[] {SpeechSound.AH,SpeechSound.AY}), E("E", VOWEL,new SpeechSound[] {SpeechSound.EH,SpeechSound.EE}), 
    I("I",VOWEL,new SpeechSound[] {SpeechSound.IH,SpeechSound.EYE}), 
    O("O", VOWEL,new SpeechSound[] {SpeechSound.AW,SpeechSound.OH}), U("U",VOWEL,new SpeechSound[] {SpeechSound.UH,SpeechSound.YOU});
    
    //versions of vowel sounds
    public static final int SHORT_VOWEL=0;
    public static final int LONG_VOWEL=1;
    public static final int R_INFLUENCE_VOWEL=2;
    //versions of consonant sounds
    public static final int HARD_CONSONANT=0;
    public static final int SOFT_CONSONANT=1;
    
    public static final int NUM_VOWELS = 5;
	public static final int NUM_CONSONANTS = 7;


	//public static final int CONSONANTLE_GRAPH_LENGTH = 3;
    
    private String asString;
	private NGram type;
    private SpeechSound[] sounds;
	private int cardinality;

	private NGram(String asString_, NGram type_, SpeechSound[] sounds_) {
		asString = asString_;
		type = type_;
		sounds=sounds_;
		cardinality=type.cardinality;
	}

	private NGram(int cardinality_) {
		asString = "";
		cardinality = cardinality_;
	}
	
	public SpeechSound defaultSound(){
		return sounds[0];
	}
	
	public SpeechSound getSound(int soundIndex){
		return sounds[soundIndex];
	}

	public String asString() {
		return asString;
	}

	public NGram type() {
		return type;
	}
	
	public void updateType(NGram newType){
		type=newType;
	}

	public static NGram asNGram(String graphemeString) {
		graphemeString = graphemeString.toUpperCase().trim();
		for (NGram ng : NGram.values())
			if (ng.asString.equals(graphemeString))
				return ng;

		return null;
	}

	public static NGram asNGram(Collection<NGram> graphemes) {
		StringBuffer graphemeStringBuffer = new StringBuffer();
		for (NGram g : graphemes)
			graphemeStringBuffer.append(g.asString());
		String graphemeString = graphemeStringBuffer.toString().trim()
				.toUpperCase();
		return asNGram(graphemeString);
	}

	public static ArrayList<NGram> allVowels() {
		ArrayList<NGram> result = new ArrayList<NGram>();
		for (NGram ng : values())
			if (ng.type == VOWEL)
				result.add(ng);
		return result;
	}

	public static ArrayList<NGram> allConsonants() {
		ArrayList<NGram> result = new ArrayList<NGram>();
		for (NGram ng : values())
			if (ng.type == CONSONANT)
				result.add(ng);
		return result;
	}

	public Sound asSound() {
		// TODO Auto-generated method stub
		return null;
	}

	public static ArrayList<NGram> nGramValues() {
		ArrayList<NGram> result = new ArrayList<NGram>();
		for (NGram ng : values()) {
			if (ng.type != null)
				result.add(ng);
		}
		return result;
	}
	
	public static ArrayList<NGram> allConsonantLE() {
		ArrayList<NGram> result = new ArrayList<NGram>();
		for (NGram ng : values()) {
			if (ng.type == CONSONANT_LE)
				result.add(ng);
		}
		return result;
	}
	
	public static ArrayList<NGram> allConsonantDigraph() {
		ArrayList<NGram> result = new ArrayList<NGram>();
		for (NGram ng : values()) {
			if (ng.type == CONSONANT_DIGRAPH)
				result.add(ng);
		}
		return result;
	}
	
	public static ArrayList<NGram> allVowelDigraph() {
		ArrayList<NGram> result = new ArrayList<NGram>();
		for (NGram ng : values()) {
			if (ng.type == VOWEL_DIGRAPH)
				result.add(ng);
		}
		return result;
	}
	
	public static ArrayList<NGram> allLetters(){
		ArrayList<NGram> all1Grams=allConsonants();
		all1Grams.addAll(allVowels());
		return all1Grams;
		
	}
	
	

	public boolean isConsonant() {
		return type.type == CONSONANT;
	}

	public boolean isVowel() {
		return type == VOWEL;
	}

	public int cardinality() {
		return cardinality;
	}

}