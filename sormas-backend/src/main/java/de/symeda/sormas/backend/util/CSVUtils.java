package de.symeda.sormas.backend.util;

import java.io.FileReader;
import java.io.FileWriter;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class CSVUtils {
	
	private static final char DEFAULT_SEPARATOR = ';';
	
	public static CSVReader createCSVReader(FileReader fileReader) {
		return new CSVReaderBuilder(fileReader).withCSVParser(new CSVParserBuilder().withSeparator(DEFAULT_SEPARATOR).build()).build();
	}
	
	public static CSVWriter createCSVWriter(FileWriter fileWriter) {
		return new CSVWriter(fileWriter, DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
	}
	
}
