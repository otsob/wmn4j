
package org.wmn4j.notation.builders;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;


public class ChordBuilderTest {
	
	public ChordBuilderTest() {
	}
	
	private List<NoteBuilder> getCMajorAsNoteBuilders() {
		NoteBuilder first = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.QUARTER);
		
		List<NoteBuilder> cMajor = new ArrayList<>();
		cMajor.add(first);
		cMajor.add(second);
		cMajor.add(third);
		
		return cMajor;
	}

	@Test
	public void testConstructorWithListOfNoteBuilders() {	
		ChordBuilder builder = new ChordBuilder(getCMajorAsNoteBuilders());
		
		Chord chord = builder.build();		
		assertEquals(3, chord.getNoteCount());
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.QUARTER)));
	}
	
	@Test
	public void testConstructorWithListOfNoteBuildersCopiesNoteBuilders() {
		List<NoteBuilder> cMajor = getCMajorAsNoteBuilders();
		
		ChordBuilder builder = new ChordBuilder(cMajor);
		cMajor.get(0).setPitch(Pitch.getPitch(Pitch.Base.B, 0, 4));
		
		Chord chord = builder.build();		
		assertEquals(3, chord.getNoteCount());
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.QUARTER)));
	}
	
	@Test
	public void testConstructorWithSingleNoteBuilderCopiesNoteBuilder() {
		NoteBuilder note = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);		
		ChordBuilder builder = new ChordBuilder(note);
		
		note.setPitch(Pitch.getPitch(Pitch.Base.D, 0, 4));
		
		Chord chord = builder.build();		
		assertEquals(1, chord.getNoteCount());
		assertTrue(chord.contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
	}
}
