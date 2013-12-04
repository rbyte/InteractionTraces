package matt.parameters;

import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.contract4j5.contract.Post;

import matt.ui.MousePath;
import matt.ui.PToolbox;
import matt.ui.screenElement.Book;
import matt.ui.screenElement.InteractionLog;
import matt.ui.screenElement.Keyword;
import matt.ui.screenElement.KeywordSet;
import matt.ui.screenElement.TimestampedObject;
import matt.util.Util;

public class ColourSetupDribble implements ColourSetupInterface {
	
//	Color.getHSBColor(136f/255f, 153f/255f, 255f/255f);
//	Color.getHSBColor(162f/255f, 255f/255f, 69f/255f);
	
	// dribble
	// http://kuler.adobe.com/#themeID/1859505
	private static final Color dribbleDarkBlue = new Color(31, 76, 83);
	private static final Color dribbleLightBlue = new Color(112, 183, 186);
	private static final Color dribbleRed = new Color(241, 67, 63);
	private static final Color dribbleCream = new Color(231, 225, 212);
	// my addition
	private static final Color dribbleCreamOrange = new Color(242, 201, 149);
	private static final Color dribbleWhite = new Color(255, 255, 255);
	
	@Override public Color genericHightlightColor() { return dribbleLightBlue; }
	@Override public Color backgroundColour() { return dribbleWhite; }
	@Override public Color shadowColour() { return dribbleWhite; }
	@Override public Color faintOnBackground() { return new Color(220, 220, 220); }
	
	@Override public String bgImageName() { return "body_bg_010.jpg"; }
	@Override public Color hoverColour() { return PToolbox.setLightnessMultiply(dribbleCreamOrange, 0.8f); }
	@Override public Color hoverColourBooks() { return PToolbox.setLightnessMultiply(dribbleCreamOrange, 0.8f); }
	@Override public Color kTextOnBookSpaceColor() { return PToolbox.setLightnessMultiply(dribbleCreamOrange, 0.8f); }
	
	@Override public Color backgroundColourOfBookDetailOverlay() { return new Color(255, 255, 255, 200); }
	
	public static final float edgeTimeoutMS = 300000;
	public static final float edgeColourTimeoutMS = 100000;
	
	@Override public Color edgeColor(float edgeCount, float edgeTotal, long ticksPassedSinceCreation, KeywordSet kwds) {
		Color old = PToolbox.setLightness(dribbleCream, 0.7f);
		Color recent = PToolbox.setLightness(dribbleLightBlue, 1f);
		double colourFactor = (ticksPassedSinceCreation > edgeColourTimeoutMS ? 0 : (1-ticksPassedSinceCreation/edgeColourTimeoutMS));
		if (edgeTotal-kwds.getSelected().length+1 <= edgeCount)
			colourFactor = 1;
		assertTrue(0 <= colourFactor && colourFactor <= 1);
		float alphaFactor = (ticksPassedSinceCreation > edgeTimeoutMS ? 0 : (1-ticksPassedSinceCreation/edgeTimeoutMS));
		assertTrue(0 <= alphaFactor && alphaFactor <= 1);
		return new Color(
			Util.linearInterpolation(old.getRed(), recent.getRed(), colourFactor),
			Util.linearInterpolation(old.getGreen(), recent.getGreen(), colourFactor),
			Util.linearInterpolation(old.getBlue(), recent.getBlue(), colourFactor),
			(int) (60+90*alphaFactor+90*colourFactor)
		);
	}
	
	@Post(Params.returnIn0to1) public float clickWeightOfKeyword(Keyword k, InteractionLog<Keyword> log) {
		int kClickedCount = log.getClickedCount(k);
		int logSize = log.size();
		int getMaxCount = log.getMaxCount(k);
		float lowTraceCountCutoff = logSize < 30 ? logSize/30f : 1;
		return (float) (logSize == 0 ? 0 : Math.pow(kClickedCount / (float) getMaxCount, 1))*lowTraceCountCutoff;
	}
	
	@Override public Color kGlowColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, Book currentlyHoveredOverBook) {
		if (k.getAssociatedActiveBooks().contains(currentlyHoveredOverBook)) {
			return hoverColour();
		} else {
			return PToolbox.setLightnessMultiply(dribbleCreamOrange, 0.3f);
		}
	}
	
	@Override public Color kBubbleInnerColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
		return k.isSelected()
			? PToolbox.setLightnessMultiply(dribbleCream, 1f)
			: dribbleWhite
			;
	}
	
	@Override public Color kBubbleOutterColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
		return k.isSelected()
			? PToolbox.setLightnessMultiply(dribbleLightBlue, 1.5f)
			: dribbleCream;
	}
	
	@Override public Color kTextOnBookspaceColour() { return PToolbox.setAlpha(dribbleDarkBlue, 150);}
	@Override public Color kTextBubbleOnBookspaceColour() { return PToolbox.setAlpha(dribbleDarkBlue, 210); }
	
	@Override public Color kTextColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, boolean isHoverHighlighted) {
		float gradient = k.getSchweifProximityAndUpdate(mousePath);
		float factor = (float) Math.max(gradient, Math.pow(
				k.getRadiusPercentage() < 0.2 ? 0 : (k.getRadiusPercentage() > 0.4 ? 1 : k.getRadiusPercentage())
				, 2)/2f);
		factor = (float) Math.max(factor, kTextFadeoutTicksRatio(k, log));
		if (isHoverHighlighted)
			factor = 0.6f;
		return k.isSelected()
			? PToolbox.setLightness(dribbleRed, 0.8f)
			: PToolbox.mingleColours(PToolbox.setAlpha(dribbleDarkBlue, (int) (100*factor)), dribbleDarkBlue, factor);
	}
	
	private long kTextFadeoutTicks() { return 150000; }
	
	@Post(Params.returnIn0to1) public float kTextFadeoutTicksRatio(Keyword k, InteractionLog<Keyword> log) {
		TimestampedObject<Keyword> t = log.getMostCurrentVbutNotBeyondCutoff(k, 50);
		return t == null ? 0 : (1-(float) t.getTimeFactor(kTextFadeoutTicks()))/2.5f;
	}
	
	

}
