package com.wy.confuse.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

  
/**  
 *@ClassName: SelfReplaceVisitor 
 *@Description: 混淆类自身替换Visitor
 *@Author: wangy
 *@Since: 2015-12-16  
 *@Version: 1.0  
 */
public class SelfReplaceVisitor extends ASTVisitor {

	private String packageName, className, newClassName;
	private boolean packageFlag = false;
	private boolean classFlag = false;
	private TypeDeclaration typeDeclaration;
	
	public SelfReplaceVisitor(String packageName, String className,
			String newClassName) {
		this.packageName = packageName;
		this.className = className;
		this.newClassName = newClassName;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		if (packageName.equals(node.getName().getFullyQualifiedName())) {
			System.out.println(node.getName());
			packageFlag = true;
			tryChange();
		}
		return false;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		if (className.equals(node.getName().getFullyQualifiedName())) {
			System.out.println(node.getName());
			classFlag = true;
			typeDeclaration = node;
			tryChange();
		}
		return false;
	}
	
	private void tryChange() {
		if (packageFlag && classFlag && typeDeclaration != null) {
			typeDeclaration.getName().setIdentifier(newClassName);
		}
	}
}
