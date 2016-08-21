package com.wy.confuse.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

  
/**  
 *@ClassName: VariableNameReplaceVisitor 
 *@Description: 变量名字替换Visitor，不包含全局域
 *@Author: wangy
 *@Since: 2015-12-16  
 *@Version: 1.0  
 */
public class VariableNameReplaceVisitor extends ASTVisitor {
	
	private Map<IBinding, List<SimpleName>> variableMap = new HashMap<IBinding, List<SimpleName>>();
	
	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		// 判断是否为变量
		if (binding instanceof IVariableBinding) {
			IVariableBinding variableBinding = (IVariableBinding)binding;
			// 判断是否为域（全局变量）
			if (!variableBinding.isField()) {
				if (variableMap.get(variableBinding) == null) {
					variableMap.put(variableBinding, new ArrayList<SimpleName>());
				}
				variableMap.get(variableBinding).add(node);
			}
		}
		return false;
	}

	public Map<IBinding, List<SimpleName>> getVariableMap() {
		return variableMap;
	}
	
}
