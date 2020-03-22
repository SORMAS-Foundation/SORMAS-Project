package de.symeda.sormas.ui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

import de.symeda.sormas.ui.utils.AbstractEditForm;

/**
 * Writes the html templates of all forms to files.
 * Diffs between snapshots can be used to spot side effects of refactorings.
 *
 */
public class EditFormLayoutCharting {

	public static void main(String[] args) throws IOException {

		Path javaBase = Paths.get("src/main/java");
		Path startdir = javaBase.resolve("de/symeda/sormas/ui");

		Map<Class<?>, Map<String, String>> layouts = new LinkedHashMap<>();
		
		Files.walkFileTree(startdir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				if (file.toString().endsWith("Form.java")) {
					String className = getClassName(javaBase, file);
					Class<?> clazz;
					try {
						clazz = getClass().getClassLoader().loadClass(className);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
					
					if (clazz != AbstractEditForm.class && AbstractEditForm.class.isAssignableFrom(clazz)) {
						
						Map<String, String> m = new TreeMap<>();
						layouts.put(clazz, m);
						
						Arrays.stream(clazz.getDeclaredFields())
						.filter(f-> f.getName().startsWith("HTML_LAYOUT"))
						.forEach(f -> {
							f.setAccessible(true);
							String htmlLayout;
							try {
								htmlLayout = (String) f.get(null);
							} catch (Exception e) {
								throw new RuntimeException(clazz + ": " + e.getMessage(), e);
							}
							m.put(f.getName(), htmlLayout);
						});
					}
				}
				return super.visitFile(file, attrs);
			}
		});
		
		String irregularForms = layouts.entrySet().stream()
		.filter(e -> e.getValue().isEmpty())
		.map(e -> e.getKey())
		.map(Class::getName)
		.collect(Collectors.joining("\n"));
		
		if (!irregularForms.isEmpty()) {
			throw new RuntimeException("Forms without HTML_LAYOUT constant:\n"+irregularForms);
		}
		
		
		Path destdir = Paths.get("form_layout_snapshots", ISODateTimeFormat.basicDateTimeNoMillis().print(LocalDateTime.now()));

		Files.createDirectories(destdir);
		
		for (Entry<Class<?>, Map<String, String>> e : layouts.entrySet()) {
			
			String className = e.getKey().getSimpleName();
			e.getValue().entrySet().forEach(f -> {
				String suffix =  f.getKey().substring("HTML_LAYOUT".length());
				
				String fileName = className + suffix + ".html";
				
				try {
					Files.write(destdir.resolve(fileName), f.getValue().getBytes(StandardCharsets.UTF_8));
				} catch (IOException e1) {
					throw new UncheckedIOException(e1);
				}
				
			});
			
			
		}

		
		
	}

	private static String getClassName(Path javaBase, Path file) {
		Path relPath = javaBase.relativize(file);
		
		StringBuilder sb = new StringBuilder();
		
		for (Path p : relPath) {
			sb.append(p).append('.');
		}
		return sb.substring(0, sb.length() - 6);
	}

}
