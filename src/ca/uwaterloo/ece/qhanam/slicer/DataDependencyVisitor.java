package ca.uwaterloo.ece.qhanam.slicer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.Block;

/**
 * Checks whether this ASTNode (Statement) contains a data dependency from
 * the given list.
 * @author qhanam
 */
public class DataDependencyVisitor extends DependencyVisitor {
	
	/* The list of all possible variables and their aliases at this point in the CFG. */
	private LinkedList<String> aliases;
	
	/* The slicer options. */
	private List<Slicer.Options> options;
	
	/**
	 * Create DataDependencyVisitor
	 * @param 
	 */
	public DataDependencyVisitor(LinkedList<String> aliases){
		super();
		this.aliases = aliases;
	}
	
	/**
	 * The first thing we do is add the variable to the node aliases if it is
	 * present in a statement.
	 */
	public boolean visit(SimpleName node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveBinding();
		
		/* Make sure this is a variable.
		 * If we are just analyzing one source file,
		 * we won't have binding info... so do our 
		 * best effort at matching variables. */
		if(binding == null){
			if(!(node.getParent() instanceof MethodInvocation)){
				if(this.aliases.contains(node.getFullyQualifiedName()))
					this.result = true;
			}
		}
		else if(binding instanceof IVariableBinding){
			/* If this variable is in the alias list, then this statement 
			 * is a data dependency. */
			if(this.aliases.contains(binding.getKey())){
				this.result = true;
			}
		}
		
		return true;
	}
	
	public boolean visit(Block node){
		return false;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(IfStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(DoStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(EnhancedForStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(ForStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			List<Expression> initializers = node.initializers();
			for(Expression initializer : initializers){
				initializer.accept(this);
			}
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}

	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(SwitchStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	

	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(SynchronizedStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(WhileStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
}
