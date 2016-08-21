package com.wy.confuse.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

  
/**  
 *@ClassName: MethodReplaceVisitor 
 *@Description: 方法替换Visitor
 *@Author: wangy
 *@Since: 2015-12-16  
 *@Version: 1.0  
 */
public class MethodReplaceVisitor extends ASTVisitor {
	
	String changeClassFullName;
	private Map<IMethodBinding, List<MethodInvocation>> methodMap = new HashMap<IMethodBinding, List<MethodInvocation>>();
	
	public MethodReplaceVisitor(String changeClassFullName) {
		this.changeClassFullName = changeClassFullName;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		// 判断方法声明是否为目标类
		if (changeClassFullName.equals(methodBinding.getDeclaringClass().getBinaryName())) {
			if (methodMap.get(methodBinding) == null) {
				methodMap.put(methodBinding, new ArrayList<MethodInvocation>());
			}
			methodMap.get(methodBinding).add(node);
			System.out.println("MethodInvocation: " + node);
			System.out.println("binding: " + methodBinding);
		}
		return true;
	}

	public Map<IMethodBinding, List<MethodInvocation>> getMethodMap() {
		return methodMap;
	}

}
