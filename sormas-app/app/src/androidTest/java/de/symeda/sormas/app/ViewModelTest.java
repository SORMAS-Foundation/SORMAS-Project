package de.symeda.sormas.app;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.test.InstrumentationRegistry;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class ViewModelTest {

	@Test
	public void isPublicClass() throws IOException, ClassNotFoundException {
		Class[] classes = getClassesOfPackage("de.symeda.sormas.app");

		for (Class clazz : classes) {
			if (ViewModel.class.isAssignableFrom(clazz)) {
				assertTrue("ViewModel class needs to be public: '" + clazz.getSimpleName() + "'", Modifier.isPublic(clazz.getModifiers()));
			}
		}
	}

	private Class[] getClassesOfPackage(String packageName) throws ClassNotFoundException, IOException {
		List<Class> classes = new ArrayList<>();

		Context context = InstrumentationRegistry.getTargetContext();
		PathClassLoader classLoader = (PathClassLoader) context.getClassLoader();
		String packageCodePath = context.getPackageCodePath();
		DexFile df = new DexFile(packageCodePath);
		for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
			String className = iter.nextElement();
			if (className.contains(packageName)) {
				Class<?> aClass = classLoader.loadClass(className);
				classes.add(aClass);
			}
		}

		return classes.toArray(new Class[classes.size()]);
	}
}
