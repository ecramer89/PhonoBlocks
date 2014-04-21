import processing.core.*;
import ddf.minim.*;

import java.util.*;
import java.util.List;
import java.awt.*;

public class GraphemeToPhonogramDecoder {
	/*
	 * INVARIANT: -cursor set to 0 when procedure terminates.
	 */
	private static ArrayList<NGram> graphemesToDecode;
	private static ArrayList<Phonogram> decodedPhonograms;
	private static int cursor;

	// useful static variables specific to different decoding ops
	private static int candidateConsonantLEStartIndex;
	private static int candidateConsonantLEEndIndex;

	public static ArrayList<Phonogram> decode(ArrayList<NGram> graphemes) {
		initializeDecodingFields(graphemes);
		recodeGraphemesIntoNGraphs(graphemes);
		turnGraphemesIntoPhonograms();
		return decodedPhonograms;

	}

	private static void initializeDecodingFields(ArrayList<NGram> graphemes) {
		graphemesToDecode = graphemes;
		cursor = 0;
		candidateConsonantLEStartIndex = graphemesToDecode.size()
				- NGram.CONSONANT_LE.cardinality();
		candidateConsonantLEEndIndex = graphemesToDecode.size();
	}

	private static void recodeGraphemesIntoNGraphs(ArrayList<NGram> graphemes) {
		parseConsonantLE();
		parseDigraphs();
	}

	private static void parseConsonantLE() {
		if (consonantLEPossible()) {
			NGram candidate = createCandidateConsonantLE();
			if (candidate != null)
				replaceLast3GraphemesWithCandidate(candidate);
		}
	}

	private static boolean consonantLEPossible() {
		return graphemesToDecode.size() >= NGram.CONSONANT_LE.cardinality();
	}

	private static NGram createCandidateConsonantLE() {
		return NGram.asNGram(createSublistOfGraphemes(
				candidateConsonantLEStartIndex, candidateConsonantLEEndIndex));
	}

	private static List<NGram> createSublistOfGraphemes(int startIndex,
			int endIndex) {
		return graphemesToDecode.subList(startIndex, endIndex);
	}

	private static void replaceLast3GraphemesWithCandidate(NGram candidate) {
		removeGraphemes(candidateConsonantLEStartIndex,
				candidateConsonantLEEndIndex);
		graphemesToDecode.add(candidate);

	}

	private static void removeGraphemes(int startIndex, int endIndex) {
		for (int i = endIndex - 1; i >= startIndex; i--)
			graphemesToDecode.remove(i);
	}

	private static void parseDigraphs() {
		while (digraphPossible()) {
			NGram candidate = NGram.asNGram(createSublistOfGraphemes(cursor,
					cursor + 2));
			if (candidate != null) {
				removeGraphemes(cursor, cursor + 2);
				graphemesToDecode.add(cursor, candidate);
			}
			cursor++;
		}
		resetCursor();
	}

	private static boolean digraphPossible() {
		return cursor < graphemesToDecode.size() - 1;
	}

	private static void resetCursor() {
		cursor = 0;
	}


	private static void turnGraphemesIntoPhonograms() {
		makePhonograms();
		identifyLongVowels();
		getSounds();
	}

	private static void makePhonograms() {
		decodedPhonograms = new ArrayList<Phonogram>();
		Phonogram next;
		for (NGram g : graphemesToDecode) {
			if (g != null) {
				next = new Phonogram(g);
				decodedPhonograms.add(next);
			}
		}

	}
	

	private static void identifyLongVowels() {
		findVowel();
		// respect class invariant
		resetCursor();
	}

	private static void findVowel() {
		while (isNextPhonogram()) {
			Phonogram next = getNextPhonogramAndAdvanceCursor();
			if (next.grapheme.type() == NGram.VOWEL){
				if(!isNextPhonogram()) makeVowelLong(next);
				else findConsonant(next);
		}
		}

	}
	

	private static void makeVowelLong(Phonogram vowel) {
		vowel.sound=vowel.grapheme.getSound(NGram.LONG_VOWEL);
	}

	private static void findConsonant(Phonogram vowel) {
		while (isNextPhonogram()) {
			Phonogram next = getNextPhonogramAndAdvanceCursor();
			if (isConsonant(next))
				findE(vowel);
			if (next.grapheme.type() == NGram.VOWEL_DIGRAPH)
				findVowel();
		}

	}

	// for all n consonant n graphs count as "single consonants"
	private static boolean isConsonant(Phonogram next) {
		return (next.grapheme.type() == NGram.CONSONANT || next.grapheme.type() == NGram.CONSONANT_DIGRAPH);
	}

	private static void findE(Phonogram vowel) {
		if (isNextPhonogram()) {
			Phonogram next = getNextPhonogramAndAdvanceCursor();
			if (isEInfluencer(next))
				handleEInfluence(next, vowel);
			else if (next.grapheme.isVowel())
				findConsonant(next);
			else
				findVowel();
		}
	}

	private static boolean isEInfluencer(Phonogram candidateEInfluence) {
		return candidateEInfluence.grapheme==NGram.E
				&& (!isNextPhonogram() || isConsonant(getNextPhonogramWithoutAdvancingCursor()) || getNextPhonogramWithoutAdvancingCursor().grapheme.type()==NGram.CONSONANT_LE);
	}
	
	private static boolean isNextPhonogram() {
		return cursor < decodedPhonograms.size();
	}
	
	private static Phonogram getNextPhonogramAndAdvanceCursor() {
		return decodedPhonograms.get(cursor++);
	}

	private static Phonogram getNextPhonogramWithoutAdvancingCursor() {
		return decodedPhonograms.get(cursor);
	}

	private static void handleEInfluence(Phonogram e, Phonogram vowel) {
		makeVowelLong(vowel);
		if (!isNextPhonogram())
			e.isSilent = true;
	}
	
	private static void getSounds() {

	}

}
