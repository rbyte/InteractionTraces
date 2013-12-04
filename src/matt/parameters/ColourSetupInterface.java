package matt.parameters;

import java.awt.Color;

import matt.ui.MousePath;
import matt.ui.screenElement.Book;
import matt.ui.screenElement.InteractionLog;
import matt.ui.screenElement.Keyword;
import matt.ui.screenElement.KeywordSet;

public interface ColourSetupInterface {
	
	Color edgeColor(float edgeCount, float edgeTotal, long ticksPassedSinceCreation, KeywordSet kwds);
	Color backgroundColour();
	Color kGlowColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, Book currentlyHoveredOverBook);
	Color kBubbleInnerColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log);
	Color kBubbleOutterColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log);
	Color kTextColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, boolean isHoverHighlighted);
	Color backgroundColourOfBookDetailOverlay();
	Color hoverColour();
	Color kTextOnBookspaceColour();
	Color kTextBubbleOnBookspaceColour();
	String bgImageName();
	Color kTextOnBookSpaceColor();
	Color faintOnBackground();
	Color genericHightlightColor();
	Color shadowColour();
	Color hoverColourBooks();
	
}
