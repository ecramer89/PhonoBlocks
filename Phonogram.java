import processing.core.*;
import ddf.minim.*;

import java.util.*;
import java.util.List;
import java.awt.*;

public class Phonogram {

	public NGram grapheme;
	//public Sound sound;
	public boolean isSilent;
	public NGram soundType;
	
	public SpeechSound sound;

	public Phonogram(NGram grapheme_) {
		grapheme = grapheme_;
		isSilent = false;
		setDefaultSoundType();
	}

	private void setDefaultSoundType() {
		  sound=grapheme.defaultSound();
	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((grapheme.asString() == null) ? 0 : grapheme.asString()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phonogram other = (Phonogram) obj;
		if (grapheme == null) {
			if (other.grapheme != null)
				return false;
		} else if (grapheme != other.grapheme || soundType!=other.soundType|| isSilent!=other.isSilent)
			return false;
		return true;
	}

	public void playSound() {
		if(!isSilent)
			sound.playSound();
		
	}

	public boolean needToReloadSound() {
		return sound.needToReloadSound();
	}

	public void loadSound() {
		sound.loadSound();
	}

}
