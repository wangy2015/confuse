/**  
 *@Copyright: Copyright (c) 2014
 *@Company: Iwell
 */
package com.wy.confuse.invoke;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.wy.confuse.util.FileUtil;
import com.wy.confuse.util.JdtAstUtil;
import com.wy.confuse.visitor.FieldAccessReplaceVisitor;
import com.wy.confuse.visitor.FieldReplaceVisitor;
import com.wy.confuse.visitor.VariableNameReplaceVisitor;

/**
 * @ClassName: ReplaceInvoker
 * @Description: 替换调用类
 * @Author: wangy
 * @Since: 2015-12-16
 * @Version: 1.0
 */
public class ReplaceInvoker {
	private String[] sources;
	private String[] encodings;
	private String[] includeClasses;
	private String[] excludeClasses;
	private File changeClassFile;
	private String bindingReplaceStr;
	private String changeClassName;
	private CompilationUnit changeClassUnit;
	private List<File> files = new ArrayList<File>();
	int i = 0;

	public ReplaceInvoker(String[] sources, String[] encodings, String[] includeClasses, String[] excludeClasses) {
		this.sources = sources;
		this.encodings = encodings;
		this.includeClasses = includeClasses;
		this.excludeClasses = excludeClasses;
		for (String source : sources) {
			List<File> sourceFiles = FileUtil.getFileListBySuffixName(source, ".java");
			if (sourceFiles != null && sourceFiles.size() > 0) {
				files.addAll(sourceFiles);
			}
		}
	}
	
	public void replace() {
		for (File file : files) {
			changeClassFile = file;
			bindingReplaceStr = file.getPath().replaceAll(".java", "").replaceAll("\\\\", "\\\\\\\\") + "~";
			changeClassUnit = JdtAstUtil.getCompilationUnitFromFile(file, sources, encodings);
			changeClassName = FileUtil.getClassFullName(file.getPath(), sources);

			if (includeClasses == null || Arrays.asList(includeClasses).contains(changeClassName)) {
				// 替换局部变量
//				this.replaceVariableName();
				if (excludeClasses != null && !Arrays.asList(excludeClasses).contains(changeClassName)) {
					// 替换全局变量
					this.replaceField();
				}
			}
		}
	}


	/** 
	 * @Title: replaceField 
	 * @Description: 替换全局变量
	 * @return
	 * @throws 
	 */
	public int replaceField() {

		FieldReplaceVisitor fieldVisitor = new FieldReplaceVisitor(changeClassName);
		changeClassUnit.accept(fieldVisitor);
		Set<VariableDeclarationFragment> fieldFragmentSet = fieldVisitor.getFieldFragmentSet();
		System.out.println("混淆类：" + changeClassName);
		for (VariableDeclarationFragment fieldFragment : fieldFragmentSet) {
			String fieldName = "f" + i;
			// 替换其它类的引用
			this.replaceFieldAccess(fieldFragment, fieldName);
			// 替换本类中的引用
			List<SimpleName> names = fieldVisitor.getFieldMap().get(fieldFragment.resolveBinding());
			if (names == null) {
				System.out.println("fieldAccess：" + fieldFragment);
				System.out.println("Binding：" + fieldFragment.resolveBinding());
			}
			for (SimpleName name : names) {
				name.setIdentifier(fieldName);
			}
			// 替换变量声明
			fieldFragment.getName().setIdentifier(fieldName);
			i++;
		}
		JdtAstUtil.writeToFile(changeClassUnit, changeClassFile);
		
		return i;
	}

	/** 
	 * @Title: replaceFieldAccess 
	 * @Description: 替换全局变量引用
	 * @param fieldFragment
	 * @param fieldName
	 * @throws 
	 */
	private void replaceFieldAccess(VariableDeclarationFragment fieldFragment, String fieldName) {
		for (File referenceClassFile : files) {
			if (!changeClassFile.equals(referenceClassFile)) {
				CompilationUnit referenceClassUnit = JdtAstUtil.getCompilationUnitFromFile(referenceClassFile, sources, encodings);
				FieldAccessReplaceVisitor fieldAccessVisitor = new FieldAccessReplaceVisitor(
						changeClassName, changeClassUnit, fieldFragment, fieldName, bindingReplaceStr);
				referenceClassUnit.accept(fieldAccessVisitor);
				JdtAstUtil.writeToFile(referenceClassUnit, referenceClassFile);
			}
		}
	}

	/**
	 * @Title: replaceMethod
	 * @Description: 替换方法
	 * @return 替换次数
	 * @throws
	 */
	public int replaceMethod() {
		return 0;
	}
	
	/**
	 * @Title: replaceVariableName
	 * @Description: 替换非全局变量名称
	 * @return 替换次数
	 * @throws
	 */
	public int replaceVariableName() {
		int i = 0;
		VariableNameReplaceVisitor visitor = new VariableNameReplaceVisitor();
		changeClassUnit.accept(visitor);
		Set<IBinding> bindingSet = visitor.getVariableMap().keySet();
//		System.out.println(bindingSet);
		for (IBinding binding : bindingSet) {
			ASTNode node = changeClassUnit.findDeclaringNode(binding);
//			System.out.println(node.getClass());
			if (node instanceof VariableDeclaration) {
				VariableDeclaration variableDeclaration = (VariableDeclaration) node;
				variableDeclaration.getName().setIdentifier("a" + i);
				List<SimpleName> names = visitor.getVariableMap().get(binding);
				for (SimpleName name : names) {
					name.setIdentifier("a" + i);
				}
			}
			i++;
		}
		JdtAstUtil.writeToFile(changeClassUnit, changeClassFile);
		return i;
	}

}
