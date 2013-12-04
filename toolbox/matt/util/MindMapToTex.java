package matt.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

public class MindMapToTex {
	
	private static final boolean useArgs = true;
	
	public static void main(String[] args) {
		String pathToMindMap;
		String pathToTexFile;
		
		if (useArgs) {
			assert(args.length >= 2);
			pathToMindMap = args[0];
			pathToTexFile = args[1];
		} else {
			pathToMindMap = "/media/SSD830_DATEN_Q/Kurse/WIP LiverAnatomyExplorer/MyTex/content.mm";
			pathToTexFile = "/media/SSD830_DATEN_Q/Kurse/WIP LiverAnatomyExplorer/MyTex/content.tex";
		}
		
		new MindMapToTex().run(pathToMindMap, pathToTexFile);
		System.out.println("done.");
	}
	
	public void run(String pathToMindMap, String pathToTexFile) {
		assert(new File(pathToMindMap).isFile());
		assert(new File(pathToTexFile).isFile());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(pathToMindMap);
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToTexFile));
			writeToFile(dom.getChildNodes(), bufferedWriter);
			bufferedWriter.close();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void writeToFile(NodeList nl, BufferedWriter bufferedWriter) throws IOException {
		printNodeList(nl, 0, bufferedWriter);
	}
	
	// TODO untangle and refactor
	@SuppressWarnings("unused")
	public void printNodeList(NodeList nl, int depth, BufferedWriter bufferedWriter) throws IOException {
		nodesLoop: for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			NamedNodeMap nnm = node.getAttributes();
			if (node.getNodeName().equals("node") && nnm != null) { // && has attributes
				Node n = nnm.getNamedItem("TEXT");
				if (n != null) { // has TEXT attribute
					if (node.getChildNodes().getLength() == 0) { // has no children: it is content
						handleContentNode(bufferedWriter, depth, n.getNodeValue());
					} else { // has children: it is a caption
						Node firstChildNode = node.getChildNodes().item(1);
						if (firstChildNode.getNodeName().equals("font") && hasAttribute(firstChildNode, "ITALIC")) {
							if (false)
								System.out.println("ignoring italic entry and all childs of it: "+n.getNodeValue());
							continue nodesLoop;
						}
						if (firstChildNode.getNodeName().equals("font") && hasAttribute(firstChildNode, "BOLD")) {
							handleCaptionNode(bufferedWriter, depth, n.getNodeValue());
						} else {
//							System.out.println(n.getNodeValue());
							// treat as comment but process children!
//							handleContentNode(bufferedWriter, depth, n.getNodeValue());
						}
					}
				}
			}
			if (node.getNodeName().equals("richcontent")) {
				String content = node.getTextContent()
					.replaceAll(" {2,}", "")
					.replaceAll("\u00A0", "")
					.replaceAll("\\n", "")
					.replaceAll("\\\\paragEnd", "\n\n");
				content = filterString(content);
				
//				System.out.println(content);
				bufferedWriter.write(content);
				bufferedWriter.newLine();
				bufferedWriter.newLine();
				continue nodesLoop;
			}
			printNodeList(node.getChildNodes(), depth+1, bufferedWriter);
		}
	}
	
	private void handleCaptionNode(BufferedWriter bufferedWriter, int depth, String string) throws IOException {
		boolean silent = string.startsWith("*");
		if (silent)
			string = string.substring(1);
		assertTrue( depth > 0);
		switch (depth) {
		case 1: break;
		case 2: bufferedWriter.write("\\chapter"+(silent?"*":"")+"{"+filterString(string)+"}"); break;
		default: bufferedWriter.write(StringHandling.concat("\t", depth-2)+"\\"
			+StringHandling.concat("sub", depth-3)+"section"+(silent?"*":"")+"{"+filterString(string)+"}"); break;
		}
		bufferedWriter.newLine();
	}
	
	private void handleContentNode(BufferedWriter bufferedWriter, int depth, String string) throws IOException {
		boolean asMaxigBulletPoint = false;
		assertTrue( depth >= 2);
		bufferedWriter.write((asMaxigBulletPoint ? StringHandling.concat("\t", depth-2)+"\\maxig{" : "")
			+filterString(string)+(asMaxigBulletPoint?"}":""));
		// an empty line denotes a paragraph in latex
		bufferedWriter.newLine();
		bufferedWriter.newLine();
	}
	
	private String filterString(String string) {
		return string.replace("ö", "\\\"o").replace("ä", "\\\"a").replace("ü", "\\\"u")
				.replace("Ö", "\\\"O").replace("Ä", "\\\"A").replace("Ü", "\\\"U").replace("ß", "\\ss ");
	}
	
	private boolean hasAttribute(Node node, String name) {
		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null) {
			Node n = nnm.getNamedItem(name);
			if (n != null)
				return true;
		}
		return false;
	}

}
