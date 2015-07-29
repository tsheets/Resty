package us.monoid.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static us.monoid.web.Resty.*;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import us.monoid.web.Resty;

public class RestyXmlTest {

	@Test
	public void testXml() throws Exception {
		Resty r = new Resty();
		NodeList nl = r.xml("http://rss.slashdot.org/Slashdot/slashdotGamesatom").get("feed/entry/link");
		assertNotNull(nl);
		for (int i = 0, len = nl.getLength(); i < len; i++) {
			System.out.println(((Element)nl.item(i)).getAttribute("href"));
		}
	}
	
	@Test
	public void testXml2Text() throws Exception {
		Resty r = new Resty();
		String text = r.xml("http://rss.slashdot.org/Slashdot/slashdotDevelopersatom").toString();
		System.out.println(text);
		assertTrue(text.contains("Slashdot: Developers"));
	}
}
