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

public class ColourSetupNight implements ColourSetupInterface {
	
//	Color.getHSBColor(136f/255f, 153f/255f, 255f/255f);
//	Color.getHSBColor(162f/255f, 255f/255f, 69f/255f);
	
	// only one chance
	// kuler.adobe.com/#themeID/1698297
	@SuppressWarnings("unused")
	private static final Color oocBase = new Color(34, 31, 38);
	@SuppressWarnings("unused")
	private static final Color oocDark1 = new Color(51, 45, 47);
	private static final Color oocDark1Grey = new Color(47, 47, 47);
	private static final Color oocDark2 = new Color(71, 71, 65);
	private static final Color oocOcker = new Color(163, 134, 57);
	private static final Color oocRed = new Color(166, 61, 52);
	
	// Kodachrome
	// kuler.adobe.com/#themeID/1795310
	@SuppressWarnings("unused")
	private static final Color kcBase = new Color(33, 35, 43);
	@SuppressWarnings("unused")
	private static final Color kcLight = new Color(212, 209, 172);
	@SuppressWarnings("unused")
	private static final Color kcRed = new Color(163, 36, 36);
	@SuppressWarnings("unused")
	private static final Color kcOcker = new Color(198, 148, 62);
	private static final Color kcOckerPale = new Color(247, 232, 174);
	
	// violet highlighting
	@SuppressWarnings("unused")
	private static final Color violetBase = new Color(56, 26, 45);
	@SuppressWarnings("unused")
	private static final Color violetBase5 = new Color(99, 42, 67);
	
	//orange highlighting
	@SuppressWarnings("unused")
	private static final Color orangeBase5 = new Color(255, 101, 23);
	
	// some important colour
	private static final Color black = new Color(0, 0, 0);
	@SuppressWarnings("unused")
	private static final Color white = new Color(255, 255, 255);
	
	@Override public Color genericHightlightColor() { return oocOcker; }
	@Override public Color backgroundColour() { return oocDark1Grey; }
	@Override public Color shadowColour() { return black; }
	@Override public Color faintOnBackground() { return PToolbox.setLightnessMultiply(backgroundColour(), 1.1f); }
	
	@Override public String bgImageName() { return "body_bg_black_noAlpha_020.jpg"; }
	@Override public Color hoverColour() { return new Color(255, 255, 255, 200); }
	@Override public Color hoverColourBooks() { return new Color(255, 255, 255, 240); }
//	@Override public Color hoverColour() { return PToolbox.setAlpha(orangeBase5, 150); }
//	@Override public Color hoverColour() { return PToolbox.setLightnessMultiply(orangeBase5, 1.0f); }
	@Override public Color kTextOnBookSpaceColor() { return PToolbox.setLightnessMultiply(oocRed, 1f); }
//	@Override public Color hoverColour() { return PToolbox.setLightnessMultiply(oocRed, 1f); }
	
	@Override public Color backgroundColourOfBookDetailOverlay() { return new Color(255, 255, 255, 180); }
	
	public static final float edgeTimeoutMS = 300000;
	public static final float edgeColourTimeoutMS = 100000;
	
	@Override public Color edgeColor(float edgeCount, float edgeTotal, long ticksPassedSinceCreation, KeywordSet kwds) {
		Color old = PToolbox.setLightness(oocOcker, 0.8f);
		Color recent = oocRed;
//		Color recent = new Color(150, 0, 0);
		double colourFactor = (ticksPassedSinceCreation > edgeColourTimeoutMS ? 0 : (1-ticksPassedSinceCreation/edgeColourTimeoutMS));
		assertTrue(0 <= colourFactor && colourFactor <= 1);
		float alphaFactor = (ticksPassedSinceCreation > edgeTimeoutMS ? 0 : (1-ticksPassedSinceCreation/edgeTimeoutMS));
		assertTrue(0 <= alphaFactor && alphaFactor <= 1);
		return new Color(
			Util.linearInterpolation(old.getRed(), recent.getRed(), colourFactor),
			Util.linearInterpolation(old.getGreen(), recent.getGreen(), colourFactor),
			Util.linearInterpolation(old.getBlue(), recent.getBlue(), colourFactor),
//			(int) (20+80*alphaFactor+100*colourFactor)
			(int) (20+100*alphaFactor+130*colourFactor)
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
			return PToolbox.setLightnessMultiply(kcOckerPale, 0.3f);
		}
	}
	
	@Override public Color kBubbleInnerColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
		return k.isSelected()
			? PToolbox.setLightnessMultiply(oocRed, 1.2f)
			: PToolbox.setSaturationMultiply(
				PToolbox.setLightnessMultiply(oocOcker, 0.66f+clickWeightOfKeyword(k, log)/3f),
				clickWeightOfKeyword(k, log)
			);
	}
	
	@Override public Color kBubbleOutterColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log) {
		return k.isSelected()
			? oocDark1Grey
			: oocDark1Grey;
	}
	
	@Override public Color kTextOnBookspaceColour() { return PToolbox.setAlpha(oocOcker, 130); }
	@Override public Color kTextBubbleOnBookspaceColour() { return PToolbox.setAlpha(oocOcker, 220); }
	
	@Override public Color kTextColour(Keyword k, MousePath mousePath, InteractionLog<Keyword> log, boolean isHoverHighlighted) {
		float gradient = k.getSchweifProximityAndUpdate(mousePath);
		float factor = (float) Math.max(gradient, Math.pow(
				k.getRadiusPercentage() < 0.2 ? 0 : (k.getRadiusPercentage() > 0.4 ? 1 : k.getRadiusPercentage())
				, 2)/2f);
		
		// considers 3 things: the general importance of the keyword,
		// the click weight (clicks received relative to other keywords),
		// and whether it has been selected recently
		factor = (float) Util.max(factor, kTextFadeoutTicksRatio(k, log), clickWeightOfKeyword(k, log)*0.8f);
		if (isHoverHighlighted)
			factor = 1f;
		return k.isSelected()
			? PToolbox.setLightness(oocRed, 1)
			: PToolbox.mingleColours(PToolbox.setAlpha(oocDark2, (int) (100*factor)), kcOckerPale, factor);
	}
	
	private long kTextFadeoutTicks() { return 100000; }
	
	// this is, if a keyword has been clicked on recently, still show the text for a while
	@Post(Params.returnIn0to1) public float kTextFadeoutTicksRatio(Keyword k, InteractionLog<Keyword> log) {
		TimestampedObject<Keyword> t = log.getMostCurrentVbutNotBeyondCutoff(k, 50);
		return t == null ? 0 : (1-(float) t.getTimeFactor(kTextFadeoutTicks()))/1.7f;
	}
	
	

}
