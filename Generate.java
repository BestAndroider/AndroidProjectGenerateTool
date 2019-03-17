
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Generate {
	//当前java文件所在的路径，即这个工具所在的根目录。
	public final static String ROOT_PATH = Generate.class.getResource("").getPath();
	//模板文件所在的路径。
	public final static String FTL_PATH = ROOT_PATH+"/marker";
	
	//项目根目录。
	public static String PROJECT_ROOT = "";
	
	//包名
	public static String PACKGE_NAME = "";
	
	//所有依赖库拼接而成的字符串，以#分割。
	public static String DEPENDENCIES_STR_IN = "";
	
	//项目根目录下build.gradle所需要的配置(通常以classpath开头)
	public static String DEPENDENCIES_STR_OUT = "";
	
	//项目根目录下build.gradle所需要的仓库配置。（如 jcenter()、mavenCentral()）
	public static String DEPENDENCIES_REPOSITORIES = "";
	
	public static void main(String[] args) throws Exception {
		//传递进来三个参数，第一个是项目名，第二个是包名，第三个是所有的依赖库拼接而成的字符串。
		PROJECT_ROOT = args[0];
		PACKGE_NAME = args[1];
		
		createBuildGradle();

		createAppBuildGradle(PROJECT_ROOT+"/app");
		createAndroidManifest();
		createIgnoreAndProguardFiles();
		createTestFiles();
		createMainActivity();
	}
	
	//生成module的build.gradle文件
	private static void createAppBuildGradle(String appPath) throws Exception {
		Template template = getTemplate("build.gradle1.ftl");
		File file = new File(appPath+"/build.gradle");
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(file);
		Map<Object, Object> map = new HashMap<>();

		map.put("packageName", PACKGE_NAME);
		template.process(map, fileWriter);
		fileWriter.close();
	}

	/*
	 * 生成项目的build.gradle
	 * */
	public static void createBuildGradle() throws Exception{
		// 在模板文件目录中找到名称为name的文件
		Template template = getTemplate("build.gradle.ftl");
		FileWriter fileWriter = new FileWriter(new File(PROJECT_ROOT+"/build.gradle"));
		
		Map<Object, Object> map = new HashMap<>();
		map.put("gradlePluginVersion", "3.2.1");
		template.process(map, fileWriter);
		fileWriter.close();
	}
	
	/*
	 * 生成AndroidManifest文件
	 * */
	
	public static void createAndroidManifest() throws Exception {
		Template template = getTemplate("AndroidManifest.xml.ftl");
		FileWriter fileWriter = new FileWriter(new File(PROJECT_ROOT+"/app/src/main/AndroidManifest.xml"));
		Map<Object, Object> map = new HashMap<>();
		map.put("packageName", PACKGE_NAME);
		template.process(map, fileWriter);
		fileWriter.close();
	}
	
	
	/*创建混淆文件和忽略文件*/
	
	public static void createIgnoreAndProguardFiles() throws Exception{
		//project下的忽略文件
		Template template = getTemplate(".gitignore.ftl");
		File projectIgnoreFile = new File(PROJECT_ROOT+"/.gitignore");
		if(!projectIgnoreFile.exists()) {
			projectIgnoreFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(projectIgnoreFile);
		Map<Object, Object> map = new HashMap<>();
		template.process(map, fileWriter);
		
		
		//module下的忽略文件
		Template template1 = getTemplate(".gitignore1.ftl");
		File moduleIgnoreFile = new File(PROJECT_ROOT+"/app/.gitignore");
		if(!moduleIgnoreFile.exists()) {
			moduleIgnoreFile.createNewFile();
		}
		FileWriter fileWriter1 = new FileWriter(moduleIgnoreFile);
		Map<Object, Object> map1 = new HashMap<>();
		template1.process(map1, fileWriter1);
		fileWriter1.close();
		
		//创建混淆文件
		Template template2 = getTemplate("proguard-rules.pro.ftl");
		File proguardFile = new File(PROJECT_ROOT+"/app/proguard-rules.pro");
		if(!proguardFile.exists()) {
			proguardFile.createNewFile();
		}
		FileWriter fileWriter2 = new FileWriter(proguardFile);
		Map<Object, Object> map2 = new HashMap<>();
		template2.process(map2, fileWriter2);
		
		//关流。
		fileWriter.close();
		fileWriter1.close();
		fileWriter2.close();
		
	}
	
	/*
	 * 创建Test文件
	 * */
	public static void createTestFiles() throws Exception{
		
		//创建ExampleUnitTest类
		Template template = getTemplate("ExampleUnitTest.java.ftl");
		String [] packages = PACKGE_NAME.split("\\.");
		String testFilePath = PROJECT_ROOT+"/app/src/test/java";
		for(int i=0;i<packages.length;i++) {
			testFilePath = testFilePath+"/"+packages[i];
		}
		File testFile = new File(testFilePath+"/ExampleUnitTest.java");
		if(!testFile.exists()) {
			testFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(testFile);
		Map<Object, Object> map = new HashMap<>();
		map.put("packageName", PACKGE_NAME);
		template.process(map, fileWriter);
		
		
		//创建ExampleInstrumentedTest类
		Template template1 = getTemplate("ExampleInstrumentedTest.java.ftl");
		String androidTestFilePath = PROJECT_ROOT+"/app/src/androidTest/java";
		for(int i=0;i<packages.length;i++) {
			androidTestFilePath = androidTestFilePath+"/"+packages[i];
		}
		File androidTestFile = new File(androidTestFilePath+"/ExampleInstrumentedTest.java");
		if(!androidTestFile.exists()) {
			androidTestFile.createNewFile();
		}
		FileWriter fileWriter1 = new FileWriter(androidTestFile);
		Map<Object, Object> map1 = new HashMap<>();
		map1.put("packageName", PACKGE_NAME);
		template1.process(map1, fileWriter1);
		fileWriter.close();
		fileWriter1.close();
	}
	

	public static void createMainActivity() throws Exception{
		Template template = getTemplate("MainActivity.java.ftl");
		String [] packages = PACKGE_NAME.split("\\.");
		String mainActivityPath = PROJECT_ROOT+"/app/src/main/java";
		for(int i=0;i<packages.length;i++) {
			mainActivityPath = mainActivityPath+"/"+packages[i];
		}
		File mainActivityFile = new File(mainActivityPath+"/MainActivity.java");
		if(!mainActivityFile.exists()) {
			mainActivityFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(mainActivityFile);
		Map<Object, Object> map = new HashMap<>();
		map.put("packageName", PACKGE_NAME);
		template.process(map, fileWriter);
		fileWriter.close();
	}
	
	
	public static Template getTemplate(String templateName) throws Exception{
		Configuration configuration = new Configuration();
		configuration.setDirectoryForTemplateLoading(new File(FTL_PATH));
		Template template = configuration.getTemplate(templateName);
		return template;
	}

}
