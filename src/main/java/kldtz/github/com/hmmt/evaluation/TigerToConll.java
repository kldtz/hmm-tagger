package kldtz.github.com.hmmt.evaluation;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TigerToConll {
	private static final Logger logger = LoggerFactory.getLogger(TigerToConll.class);
	
	private TigerToConll() {}

	public static void extractTokenTagPairs(Path tigerCorpus, Path conllOutputPath) {
		XMLEventReader eventReader = null;
		try (FileInputStream stream = new FileInputStream(tigerCorpus.toString());
				InputStream gzipInputStream = new GZIPInputStream(stream);) {
			XMLInputFactory factory = XMLInputFactory.newFactory();
			eventReader = factory.createXMLEventReader(gzipInputStream, "iso-8859-1");
			extractTokenTagPairs(eventReader, conllOutputPath);
		} catch (XMLStreamException | IOException e) {
			logger.error("Exception while processing Tiger corpus", e);
		} finally {
			if (eventReader != null) {
				try {
					eventReader.close();
				} catch (XMLStreamException e) {
					logger.warn("Could not close XMLEventReader", e);
				}
			}
		}
	}

	private static void extractTokenTagPairs(XMLEventReader eventReader, Path conllOutputPath) throws XMLStreamException, IOException {
		try(FileWriter fileWriter = new FileWriter(conllOutputPath.toString(), true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				PrintWriter writer = new PrintWriter(bufferedWriter);) {
			while(eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					processStartElement(writer, event);
				} else if (event.isEndElement()) {
					processEndElement(writer, event);
				}
			}
		} 		
	}

	private static void processEndElement(PrintWriter writer, XMLEvent event) {
		EndElement endElement = event.asEndElement();
		if (endElement == null) {
			throw new IllegalArgumentException("The given corpus has an unexpected format");
		}
		String endElementName = endElement.getName().getLocalPart();
		if ("s".equals(endElementName)) {
			writer.append("\n");
		}
	}

	private static void processStartElement(PrintWriter writer, XMLEvent event) {
		StartElement startElement = event.asStartElement();
		String startElementName = startElement.getName().getLocalPart();
		if ("t".equals(startElementName)) {
			Attribute wordAttribute = startElement.getAttributeByName(new QName("word"));
			Attribute posAttribute = startElement.getAttributeByName(new QName("pos"));
			if (wordAttribute == null || posAttribute == null) {
				throw new IllegalArgumentException("The given corpus has an unexpected format");
			}
			writer.append(wordAttribute.getValue());
			writer.append("\t");
			writer.append(posAttribute.getValue());
			writer.append("\n");
		}
	}

	public static void main(String[] args) {
		try {
			Path tigerCorpus = Paths.get("data/private/tiger_2-2.xml.gz");
			Path conllOutputPath = Paths.get("data/private/tiger.conll");
			Files.deleteIfExists(conllOutputPath);
			TigerToConll.extractTokenTagPairs(tigerCorpus, conllOutputPath);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
