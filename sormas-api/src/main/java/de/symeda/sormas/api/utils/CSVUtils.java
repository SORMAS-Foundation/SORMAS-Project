package de.symeda.sormas.api.utils;

import java.io.Reader;
import java.io.Writer;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class CSVUtils {
	
	private static final char DEFAULT_SEPARATOR = ';';
	
	public static CSVReader createCSVReader(Reader reader) {
		return new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().withSeparator(DEFAULT_SEPARATOR).build()).build();
	}
	
	public static CSVWriter createCSVWriter(Writer writer) {
		return new CSVWriter(writer, DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
	}
	
}
