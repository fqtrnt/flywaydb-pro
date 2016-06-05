package com.github.flywaydb.utils;

import static org.flywaydb.core.internal.util.StringUtils.tokenizeToStringArray;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

	private static final String BASE_PACKAGE = "com.zxelec";

	public static <T extends Annotation> List<Class<?>> findClassByAnnotation(Class<T> class1) {
		List<Class<?>> classsFromPackage = getClasssFromPackage(BASE_PACKAGE);
		List<Class<?>> migs = new ArrayList<Class<?>>();
		for (Class<?> classz : classsFromPackage) {
			T annotation = classz.getAnnotation(class1);
			if (null != annotation) migs.add(classz);
		}
		return migs;
	}

	public static <T extends Annotation> List<T> findAnnotations(Class<T> classz) {
		List<Class<?>> findClassByAnnotation = findClassByAnnotation(classz);
		List<T> annos = new ArrayList<T>();
		for (Class<?> clss : findClassByAnnotation) {
			T annotation = clss.getAnnotation(classz);
			if (null == annotation) continue;
			annos.add(annotation);
		}
		return annos;
	}

	/**
	 * 获得包下面的所有的class
	 *
	 * @param pack
	 *          package完整名称
	 * @return List包含所有class的实例
	 */
	public static List<Class<?>> getClasssFromPackage(String pack) {
		List<Class<?>> clazzs = new ArrayList<Class<?>>();

		// 是否循环搜索子包
		boolean recursive = true;
		// 包名字
		String packageName = pack;
		// 包名对应的路径名称
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findClassInPackageByFile(packageName, filePath, recursive, clazzs);
				} else if ("jar".equals(protocol)) {
					clazzs.addAll(getClasssFromJarFile(url));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return clazzs;
	}

	/**
	 * 在package对应的路径下找到所有的class
	 *
	 * @param packageName
	 *          package名称
	 * @param filePath
	 *          package对应的路径
	 * @param recursive
	 *          是否查找子package
	 * @param clazzs
	 *          找到class以后存放的集合
	 */
	public static void findClassInPackageByFile(String packageName,
			String filePath, final boolean recursive, List<Class<?>> clazzs) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 在给定的目录下找到所有的文件，并且进行条件过滤
		File[] dirFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
				boolean acceptClass = file.getName().endsWith("class");// 接受class文件
				return acceptDir || acceptClass;
			}
		});

		for (File file : dirFiles) {
			if (file.isDirectory()) {
				findClassInPackageByFile(packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, clazzs);
			} else {
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try {
					clazzs.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + "." + className));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从jar文件中读取指定目录下面的所有的class文件
	 *
	 * @param jarPath
	 *          jar文件存放的位置
	 * @param filePath
	 *          指定的文件目录
	 * @return 所有的的class的对象
	 */
	public static List<Class<?>> getClasssFromJarFile(URL url) throws Exception {
		List<Class<?>> clazzs = new ArrayList<Class<?>>();
		String filePath = tokenizeToStringArray(URLDecoder.decode(url.getFile(), "UTF-8"), "!")[1];
		filePath = filePath.replaceFirst("/", "");
		List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
		JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
		Enumeration<JarEntry> ee = jarFile.entries();
		while (ee.hasMoreElements()) {
			JarEntry entry = (JarEntry) ee.nextElement();
			// 过滤我们出满足我们需求的东西
			if (entry.getName().startsWith(filePath)
					&& entry.getName().endsWith(".class")) {
				jarEntryList.add(entry);
			}
		}
		for (JarEntry entry : jarEntryList) {
			String className = entry.getName().replace('/', '.');
			className = className.substring(0, className.length() - 6);

			try {
				clazzs.add(Thread.currentThread().getContextClassLoader()
						.loadClass(className));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return clazzs;
	}
}
