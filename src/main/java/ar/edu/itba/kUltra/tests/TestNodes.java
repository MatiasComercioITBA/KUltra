package ar.edu.itba.kUltra.tests;

import ar.edu.itba.kUltra.nodes.*;
import ar.edu.itba.kUltra.symbols.ParameterListSymbol;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class TestNodes {
	public static void main(String[] args) {

		final ProgramNode programNode = new ProgramNode(generateMethods(), generateMainBody());

		programNode.compileAs("TestNodes", "compiled");
	}

	private static NodeList<MethodNode> generateMethods() {

		final BodyNode helloWorldBodyNode = new BodyNode();
		helloWorldBodyNode.add(new ReturnNode(null));
		helloWorldBodyNode.add(new MethodCallNode("puts",
				putsArgument("Hello world from 'helloWorld' method!\n")));

		final MethodNode helloWorldMethodNode = new MethodNode("void", "helloWorld", new ParameterListSymbol(), helloWorldBodyNode);

		final NodeList<MethodNode> methodNodes = new NodeList<>();
		methodNodes.add(helloWorldMethodNode);
		return methodNodes;
	}

	private static BodyNode generateMainBody() {
		final BodyNode mainBodyNode = new BodyNode();

		/* test logical nodes */
		generateTestsForLogicalNodes(mainBodyNode);

		/* geti test */
		final NodeList<ExpressionNode> putsArgs2 = new NodeList<>();
		putsArgs2.add(new MethodCallNode("geti", new NodeList<>()));
		mainBodyNode.add(new MethodCallNode("puti", putsArgs2));

		final NodeList<ExpressionNode> putiArgs = new NodeList<>();
		putiArgs.add(new VariableNode("getiInput"));
		mainBodyNode.add(new MethodCallNode("puti", putiArgs));
		mainBodyNode.add(new AssignmentNode("getiInput", new MethodCallNode("geti", new NodeList<>())));
		mainBodyNode.add(new DeclarationNode("Integer", "getiInput"));

		/* helloWorld call */
		mainBodyNode.add(generateHelloWorldCall());

		final VariableNode i = new VariableNode("i");
		/* while statement */
		final ExpressionNode whileCondition = new RelationalNode(Type.INT_TYPE, GeneratorAdapter.LT, i, new LiteralNode<>(18));

		final BodyNode whileBodyNode = new BodyNode();
		whileBodyNode.add(new MethodCallNode("puts", putsArgument("\n")));
		whileBodyNode.add(new MethodCallNode("puti", putsArgument(i)));
		whileBodyNode.add(new AssignmentNode("i", new ArithmeticNode(GeneratorAdapter.ADD, i, new LiteralNode<>(1))));

		final StatementNode whileNode = new WhileNode(whileCondition, whileBodyNode);
		mainBodyNode.add(whileNode);
		/* END of while statement */

				/*
			if (i < 18) {
				printGetInput(mainBodyNode);
			} else {
				mainBodyNode.add(new MethodCallNode("puts", putsArgument("i>= 18\n")));
			}
		 */

		final ExpressionNode condition = new RelationalNode(Type.INT_TYPE, GeneratorAdapter.LT, i, new LiteralNode<>(18));

		final BodyNode ifBodyNode = new BodyNode();
		printGetInput(ifBodyNode);


		final BodyNode elseBodyNode = new BodyNode();
		elseBodyNode.add(new MethodCallNode("puts", putsArgument("i>= 18\n")));
//
		final StatementNode ifNode = new IfNode(condition, ifBodyNode, elseBodyNode);
		mainBodyNode.add(ifNode);
//		printGetInput(mainBodyNode);

		final AssignmentNode getsAssignment = new AssignmentNode("s", new MethodCallNode("gets", null));
		mainBodyNode.add(getsAssignment);

		/* get input and print it with another message */
		mainBodyNode.add(new DeclarationNode("String", "s"));

		/* puts("\n"); */
		mainBodyNode.add(new MethodCallNode("puts", putsArgument("\n")));

		final NodeList<ExpressionNode> argumentNodes = new NodeList<>();
		argumentNodes.add(i);
		final MethodCallNode putsMethod = new MethodCallNode("puti", argumentNodes);
		mainBodyNode.add(putsMethod);

		final VariableNode a1 = new VariableNode("a1");
		final VariableNode a2 = new VariableNode("a2");
		final ArithmeticNode addNode = new ArithmeticNode(GeneratorAdapter.ADD, a1, a2);
		final AssignmentNode assignmentNode = new AssignmentNode("i", addNode);
		mainBodyNode.add(assignmentNode);

		final LiteralNode<Integer> a2Value = new LiteralNode<>(3);
		final AssignmentNode a2Assignment = new AssignmentNode("a2", a2Value);
		mainBodyNode.add(a2Assignment);

		final LiteralNode<Integer> a1Value = new LiteralNode<>(5);
		final AssignmentNode a1Assignment = new AssignmentNode("a1", a1Value);
		mainBodyNode.add(a1Assignment);

		final DeclarationNode a2Declare = new DeclarationNode("Integer", "a2");
		mainBodyNode.add(a2Declare);

		final DeclarationNode a1Declare = new DeclarationNode("Integer", "a1");
		mainBodyNode.add(a1Declare);

		final DeclarationNode iDeclare = new DeclarationNode("Integer", "i");
		mainBodyNode.add(iDeclare);

		return mainBodyNode;
	}

	private static void generateTestsForLogicalNodes(final BodyNode mainBodyNode) {

		/* integrity test */
		/*
			if (!(! ((true && false) || true))) {
				sout("logic is ok");
			} else {
				sout("sth is wrong here...");
			}
		 */
		final BodyNode ifBodyNode = new BodyNode();
		ifBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nintegrity test logic is ok\n\n"))); // expecting true

		final BodyNode elseBodyNode = new BodyNode();
		elseBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nsth is wrong with the integrity test...\n\n")));
		mainBodyNode.add(
				new IfNode(
						/* condition */
						new NotLogicalNode(new NotLogicalNode(
								new BinaryLogicalNode(GeneratorAdapter.OR,
										new BinaryLogicalNode(GeneratorAdapter.AND,
												new LiteralNode<>(true), new LiteralNode<>(false)
										), new LiteralNode<>(true)
								)
						)),
						/* true body */
						ifBodyNode
						,
						/* false body */
						elseBodyNode
				)
		);



		/***********************************************************************/

		/* not test */
		final BodyNode notIfBodyNode = new BodyNode();
		notIfBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nnot logic is ok\n\n")));

		final BodyNode notElseBodyNode = new BodyNode();
		notElseBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nsth is wrong with not...\n\n")));
		mainBodyNode.add(new IfNode(new NotLogicalNode(new LiteralNode<>(false)), notIfBodyNode, notElseBodyNode));

		/* and test */
		final BodyNode andIfBodyNode = new BodyNode();
		andIfBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nsth is wrong with and...\n\n"))); // expecting false

		final BodyNode andElseBodyNode = new BodyNode();
		andElseBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nand logic is ok\n\n")));
		mainBodyNode.add(new IfNode(new BinaryLogicalNode(GeneratorAdapter.AND, new LiteralNode<>(true), new LiteralNode<>(false)), andIfBodyNode, andElseBodyNode));


		/* or test */
		final BodyNode orIfBodyNode = new BodyNode();
		orIfBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nor logic is ok\n\n"))); // expecting true

		final BodyNode orElseBodyNode = new BodyNode();
		orElseBodyNode.add(new MethodCallNode("puts", putsArgument("\n\nsth is wrong with or...\n\n")));
		mainBodyNode.add(new IfNode(new BinaryLogicalNode(GeneratorAdapter.OR, new LiteralNode<>(false), new LiteralNode<>(true)), orIfBodyNode, orElseBodyNode));

	}

	private static StatementNode generateHelloWorldCall() {
		return new MethodCallNode("helloWorld", null);
	}

	private static void printGetInput(final BodyNode statementNodes) {
		/* puts("\n"); */
		statementNodes.add(new MethodCallNode("puts", putsArgument("\n")));
		/* prints what was captured (it was saved at "s") */
		statementNodes.add(new MethodCallNode("puts", putsArgument(new VariableNode("s"))));
		/* puts("\nThis is what was captured: "); */
		statementNodes.add(new MethodCallNode("puts", putsArgument("\nThis is what was captured: ")));
	}

	private static NodeList<ExpressionNode> putsArgument(final Object o) {
		final NodeList<ExpressionNode> argumentNodes = new NodeList<>();
		argumentNodes.add(new LiteralNode<>(o));
		return argumentNodes;
	}

	private static NodeList<ExpressionNode> putsArgument(final VariableNode variableNode) {
		final NodeList<ExpressionNode> argumentNodes = new NodeList<>();
		argumentNodes.add(variableNode);
		return argumentNodes;
	}
}
