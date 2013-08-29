package fr.openwide.maven.plugins.localization.properties.consistency.maven.plugin;

import java.nio.charset.Charset;

public enum PropertiesEncoding {
	ISO_8859_1("ISO-8859-1"),
	UTF_8("UTF-8");
	
	private Charset charset;
	
	private PropertiesEncoding(String charsetName) {
		this.charset = Charset.forName(charsetName);
	}
	
	public Charset getCharset() {
		return charset;
	}
}
