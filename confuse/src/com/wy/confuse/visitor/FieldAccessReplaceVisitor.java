/**  
 *@Copyright: Copyright (c) 2014
 *@Company: Iwell
 */
package com.wy.confuse.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * @ClassName: FieldAccessReplaceVisitor
 * @Description: 全局变量调用替换visitor
 * @Author: wangy
 * @Since: 2015-12-16
 * @Version: 1.0
 */
public class FieldAccessReplaceVisitor extends ASTVisitor {

	private String changeClassName;
	private CompilationUnit changeClassUnit;
	private VariableDeclarationFragment fieldFragment;
	private String replaceName;
	private String bindingReplaceStr;

	public FieldAccessReplaceVisitor(String changeClassName,
			CompilationUnit changeClassUnit, VariableDeclarationFragment fieldFragment,
			String replaceName, String bindingReplaceStr) {
		this.changeClassName = changeClassName;
		this.changeClassUnit = changeClassUnit;
		this.fieldFragment = fieldFragment;
		this.replaceName = replaceName;
		this.bindingReplaceStr = bindingReplaceStr;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		// 判断是否为变量
		if (binding instanceof IVariableBinding) {
			IVariableBinding variableBinding = (IVariableBinding)binding;
			// 判断是否为域（全局变量）
			if (variableBinding.isField()) {
				ITypeBinding declaringClass = variableBinding.getDeclaringClass();
				// 如果不是全局变量，返回
				if (declaringClass == null) {
					return true;
				}
				// 域声明类是否为目标类
				while (declaringClass.getDeclaringClass() != null) {
					declaringClass = declaringClass.getDeclaringClass();
				}
				if (declaringClass != null && changeClassName.equals(declaringClass.getBinaryName())) {
					String bindingKey = variableBinding.getKey().replaceAll(bindingReplaceStr, "");
					if (fieldFragment.equals(changeClassUnit.findDeclaringNode(bindingKey))) {
						node.setIdentifier(replaceName);
					}
				}
			}
		}
		return false;
	}

}
