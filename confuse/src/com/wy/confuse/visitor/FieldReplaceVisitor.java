/**  
 *@Copyright: Copyright (c) 2014
 *@Company: Iwell
 */
package com.wy.confuse.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

  
/**  
 *@ClassName: FieldReplaceVisitor 
 *@Description: 全局变量替换visitor
 *@Author: wangy
 *@Since: 2015-12-16  
 *@Version: 1.0  
 */
public class FieldReplaceVisitor extends ASTVisitor {

	private Set<VariableDeclarationFragment> fieldFragmentSet = new HashSet<VariableDeclarationFragment>();
	private Map<IBinding, List<SimpleName>> fieldMap = new HashMap<IBinding, List<SimpleName>>();
	private String changeClassName;
	
	public FieldReplaceVisitor(String changeClassName) {
		this.changeClassName = changeClassName;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		List<VariableDeclarationFragment> fieldFragmentList = node.fragments();
		fieldFragmentSet.addAll(fieldFragmentList);
		return true;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		// 判断是否为变量
		if (binding instanceof IVariableBinding) {
			IVariableBinding variableBinding = (IVariableBinding)binding;
			// 判断是否为域（全局变量）
			if (variableBinding.isField()) {
				// 判断域声明是否在本类中
				ITypeBinding declaringClass = variableBinding.getDeclaringClass();
				// 如果不是全局变量，返回
				if (declaringClass == null) {
					return true;
				}
				if (fieldMap.get(variableBinding) == null) {
					fieldMap.put(variableBinding, new ArrayList<SimpleName>());
				}
				fieldMap.get(variableBinding).add(node);
			}
		}
		return true;
	}
	
	public Set<VariableDeclarationFragment> getFieldFragmentSet() {
		return fieldFragmentSet;
	}

	public Map<IBinding, List<SimpleName>> getFieldMap() {
		return fieldMap;
	}
	
}
