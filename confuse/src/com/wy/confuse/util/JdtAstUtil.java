package com.wy.confuse.util;

import java.io.File;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**  
 *@ClassName: JdtAstUtil 
 *@Description: AST通用类
 *@Author: wangy
 *@Since: 2015-12-16  
 *@Version: 1.0  
 */
public class JdtAstUtil {
	
	public final static Map<String, String> FORMAT_OPTIONS = JavaCore.getOptions();
	
	static {
		FORMAT_OPTIONS.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
        FORMAT_OPTIONS.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
        FORMAT_OPTIONS.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
        /*        
		FORMAT_OPTIONS.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        FORMAT_OPTIONS.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        FORMAT_OPTIONS.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "100");
        FORMAT_OPTIONS.put(DefaultCodeFormatterConstants.FORMATTER_JOIN_LINES_IN_COMMENTS, DefaultCodeFormatterConstants.FALSE);
        // change the option to wrap each enum constant on a new line
        FORMAT_OPTIONS.put(
            DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
            DefaultCodeFormatterConstants.createAlignmentValue(
            true,
            DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
            DefaultCodeFormatterConstants.INDENT_ON_COLUMN));
        */
	}
	
	/** 
	 * @Title: getCompilationUnitFromFile 
	 * @Description: 获取java文件编译单元
	 * @param file
	 * @return
	 * @throws 
	 */
	public static CompilationUnit getCompilationUnitFromFile(File file) {
		return JdtAstUtil.getCompilationUnitFromChars(FileUtil.readFile(file).toCharArray());
	}
	
	/** 
	 * @Title: getCompilationUnitFromFile 
	 * @Description: 根据source获取java文件编译单元，可识别上下文
	 * @param file
	 * @param sources
	 * @return
	 * @throws 
	 */
	public static CompilationUnit getCompilationUnitFromFile(File file, String[] sources, String[] encodings) {
		return JdtAstUtil.getCompilationUnitFromChars(FileUtil.readFile(file).toCharArray(), sources, encodings);
	}
	
	public static CompilationUnit getCompilationUnitFromChars(char[] chars, String[] sources, String[] encodings) {

		ASTParser astParser = ASTParser.newParser(AST.JLS4);
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		astParser.setSource(chars);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
	    astParser.setCompilerOptions(JdtAstUtil.FORMAT_OPTIONS); //设置编译选项
	    
	    if (sources != null && sources.length > 0) {
	    	String[] classpath = {};
	    	astParser.setUnitName("");
			astParser.setEnvironment(classpath, sources, encodings, true);
	    }

		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));
		result.recordModifications();
		return result;
	}
	
	public static CompilationUnit getCompilationUnitFromChars(char[] chars) {

		ASTParser astParser = ASTParser.newParser(AST.JLS4);
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		astParser.setSource(chars);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
	    astParser.setCompilerOptions(JdtAstUtil.FORMAT_OPTIONS); //设置编译选项
		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));
		result.recordModifications();
		return result;
	}
	
	public static void writeToFile(CompilationUnit comp, File file) {
		Document document = new Document(FileUtil.readFile(file));
		TextEdit edits = comp.rewrite(document, JdtAstUtil.FORMAT_OPTIONS);
		try {
			edits.apply(document);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
//		System.out.println(document.get());
		FileUtil.writeFile(file, document.get(), "UTF-8");
	}
	
}
