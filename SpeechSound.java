import ddf.minim.AudioPlayer;
import ddf.minim.Minim;


public enum SpeechSound {
	//long vowels
	AY("ay"),EE("ee"),EYE("eye"),OH("oh"),YOU("you"),OO("oo"),
	//short vowels
	AH("ah"),EH("eh"),IH("ih"),AW("aw"),UH("uh"),
	//consonants
	T("t"),D("d"),B("b"),F("f"),H("h"),L("l"),N("n"),
	//sounds mapped to con. digraphs
	TH("th"),
	//sounds mapped to vow. digraphs
	OY("oy"),
	//final stable consonant le syllables
	BULL("bull"),DULL("dull"),TULL("tull");
	
	private String label;
	private AudioPlayer sound;
	private String soundFileString;
	private boolean needReload;
	
	private SpeechSound(String label_){
		label=label_;
		soundFileString=PhonoBlocks.DATA_FOLDER_PATH+label+".wav";
		loadSound();
	}
	
	public String asString(){
		return label;
	}
	
	public void loadSound(){
		sound=PhonoBlocks.MINIM.loadFile(soundFileString);
		needReload=false;
	}
	
	public void playSound(){
		sound.play();
		needReload=true;
	}
	
	public boolean needToReloadSound(){
		return needReload;
	}
	

	
}
