package de.symeda.sormas.api.utils;

import java.io.Reader;
import java.io.Writer;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class CSVUtils {
	
	public static CSVReader createCSVReader(Reader reader, char separator) {
		return new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build();
	}
	
	public static CSVWriter createCSVWriter(Writer writer, char separator) {
		return new CSVWriter(writer, separator, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
	}
	
}
