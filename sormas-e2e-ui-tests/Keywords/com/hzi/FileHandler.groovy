package com.hzi


import java.nio.file.Files
import java.nio.file.Paths

public class FileHandler {

	static boolean existFile(path, fileName) {
		return Files.exists(Paths.get(path, fileName))
	}

	static boolean removeFile(path, fileName) {
		return Files.deleteIfExists(Paths.get(path, fileName))
	}
}
