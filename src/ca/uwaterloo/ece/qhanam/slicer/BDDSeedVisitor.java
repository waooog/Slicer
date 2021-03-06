package ca.uwaterloo.ece.qhanam.slicer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * Add any variables or fields that appear in the seed statement
 * to the list of variables that we need to check for.
 * 
 * Strategy:
 * 	BDD & FDDSeed - Track assignments, declarations, objects of method call and arguments of method call.
 * 	FDD & BDDSeed - Track all other variable uses
 * 
 * @author qhanam
 */
public class BDDSeedVisitor extends ASTVisitor {
	LinkedList<String> seedVariables;
	List<Slicer.Options> options;
	
	/**
	 * Create a SeedVisitor
	 * @param seedVariables The list that SeedVisitor will fill with the seed variables.
	 */
	public BDDSeedVisitor(LinkedList<String> seedVariables, List<Slicer.Options> options){
		super();
		this.seedVariables = seedVariables;
		this.options = options;
	}
	
	/**
	 * Add the variable to the node aliases.
	 */
	public boolean visit(QualifiedName node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveBinding();
		
		if(binding == null){
			if(!seedVariables.contains(node.getFullyQualifiedName()))
				seedVariables.add(node.getFullyQualifiedName());
		}
		else if(binding instanceof IVariableBinding){
			if(!seedVariables.contains(binding.getKey()))
				seedVariables.add(binding.getKey());
		}
		
		return false;
	}
	
	/**
	 * Add the variable to the node aliases.
	 */
	public boolean visit(FieldAccess node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveFieldBinding();
		
		if(binding == null){
			if(!seedVariables.contains(node.getName().toString()))
				seedVariables.add(node.getName().toString());
		}
		else if(binding instanceof IVariableBinding){
			if(!seedVariables.contains(binding.getKey()))
				seedVariables.add(binding.getKey());
		}
		
		return false;
	}
	
	/**
	 * Add the variable to the node aliases.
	 */
	public boolean visit(SimpleName node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveBinding();
		
		/* Since we already intercept method calls, we can be sure
		 * that this isn't part of a method call. */
		if(binding == null){
			if(!seedVariables.contains(node.getFullyQualifiedName()))
				seedVariables.add(node.getFullyQualifiedName());
		}
		else if(binding instanceof IVariableBinding){
			if(!seedVariables.contains(binding.getKey()))
				seedVariables.add(binding.getKey());
		}
		
		return false;
	}
	
	/**
	 * We only need to investigate the right hand side of an assignment.
	 */
	public boolean visit(Assignment node){
		node.getRightHandSide().accept(this);
		/* If the operator uses the original variable, then
		 * we need to add it as well (ie. anything operator except
		 * regular assignment) */
		if(node.getOperator() != Assignment.Operator.ASSIGN)
			node.getLeftHandSide().accept(this);
		return false;
	}
	
	/**
	 * Since we don't get method parameters when we visit a SimpleName, we
	 * get them from the actual method invocation.
	 * 
	 * We visit the argument expressions, but not the method call itself.
	 */
	public boolean visit(MethodInvocation node){
 		List<Expression> args = node.arguments();
		for(Expression arg : args){
			arg.accept(this);
		}
		
		if(node.getExpression() != null)
			node.getExpression().accept(this);
		
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(IfStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 * 
	 * TODO: Are we handling this statement type properly?
	 */
	public boolean visit(DoStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(EnhancedForStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(ForStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		List<Expression> initializers = node.initializers();
		for(Expression initializer : initializers){
			initializer.accept(this);
		}
		/* Don't visit the children. */
		return false;
	}

	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(SwitchStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}

	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(SynchronizedStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;	
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(WhileStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
}
