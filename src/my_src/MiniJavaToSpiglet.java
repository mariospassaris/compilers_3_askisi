package my_src;

import java.util.*;
import java.lang.Exception;

import javax.swing.Spring;

import my_src.Assume;
import syntaxtree.*;
import visitor.*;

public class MiniJavaToSpiglet extends DepthFirstVisitor
{
	LinkedHashMap<String,LinkedHashMap<String,Fun_or_Ident>> Table;
	LinkedHashMap<String,String> DeclClasses;   //classname,extend class
	
	LinkedHashMap<String,LinkedHashMap<Integer,String>> VTable;
	LinkedHashMap<String,LinkedHashMap<Integer,String>> IdsTable;
	
	LinkedHashMap<String,String> arg = new LinkedHashMap<String,String>();

	String spiglet_code = "";
	String class_name = null;
	String extend_class = null;
	String method_call = null;
	String method_name = null;
	int temp_count;
	int arg_temp_count;
	int label;
	String value = null;
	String expr = null;
	String expr_mes = null;
	String id_string = null;
	boolean flag = false;
	boolean temp_z = false;
	String re_temp;	
	int previous_table_temp;
	boolean look = false;
	int arg_count = 0;
	boolean class_ident = false;
	LinkedHashMap<Integer,String> arguments = new LinkedHashMap<Integer,String>();
	
	public MiniJavaToSpiglet(Goal n, LinkedHashMap<String,LinkedHashMap<String,Fun_or_Ident>> Table1,
	LinkedHashMap<String,String> DeclClasses1,
	LinkedHashMap<String,LinkedHashMap<Integer,String>> VTable1,
	LinkedHashMap<String,LinkedHashMap<Integer,String>> IdsTable1) throws Exception, SemError
	{
		Table = Table1;
		DeclClasses = DeclClasses1;
		VTable = VTable1;
		IdsTable = IdsTable1;
		temp_count = 50;
		arg_temp_count = 0;
		label = 0;
		previous_table_temp = 0;
		System.out.println("here in root");
		n.f0.accept(this);
		System.out.println(spiglet_code);
		n.f1.accept(this);
		System.out.println(spiglet_code);
		
	}
	
	public int AssignTemp()
	{
		temp_count++;
		return temp_count;
	}
	
	public int CurrentTemp()
	{
		return temp_count;
	}
	
	public int IncreaseTemp(int N)
	{
		return (temp_count = temp_count + N);
	}
	
	public int AssignArgTemp()
	{
		arg_temp_count++;
		return arg_temp_count;
	}
	
	public void ResetArgTemp()
	{
		arg_temp_count = 0;
	}
	
	public int CurrentArgTemp()
	{
		return temp_count;
	}
	
	public int AssignLabel()
	{
		label++;
		return label;
	}
	
	public int CurrentLabel()
	{
		return label;
	}
	
	
	/* Main */
	 /**
	    * <PRE>
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> "public"
	    * f4 -> "static"
	    * f5 -> "void"
	    * f6 -> "main"
	    * f7 -> "("
	    * f8 -> "String"
	    * f9 -> "["
	    * f10 -> "]"
	    * f11 -> Identifier()
	    * f12 -> ")"
	    * f13 -> "{"
	    * f14 -> ( VarDeclaration() )*
	    * f15 -> ( Statement() )*
	    * f16 -> "}"
	    * f17 -> "}"
	    * </PRE>
	    */
   public void visit(MainClass n) throws Exception, SemError{
	   System.out.println("here in main");
	   class_name = "MAIN";
	   spiglet_code = "MAIN \n";	       
	   n.f15.accept(this);	       
       spiglet_code +="END\n";
       System.out.println("------FINISH main");
   }
   
   
   /* Type Declaration */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    * </PRE>
    */
   public void visit(TypeDeclaration n) throws Exception, SemError{
	   System.out.println("here in typedeclar");
	   n.f0.accept(this);
   }
   
   
   /* Class Declaration */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    * </PRE>
    */
   public void visit(ClassDeclaration n) throws Exception, SemError{
	   System.out.println("here in class declar");
	   //n.f1.accept(this);
	   class_name = n.f1.f0.toString();
	   System.out.println(class_name);
	   n.f4.accept(this);
   }
   
   //Class extends
 	/**
 	 * Grammar production:
 	 * f0 -> "class"
 	 * f1 -> Identifier()
 	 * f2 -> "extends"
 	 * f3 -> Identifier()
 	 * f4 -> "{"
 	 * f5 -> ( VarDeclaration() )*
 	 * f6 -> ( MethodDeclaration() )*
 	 * f7 -> "}"
 	 */
   public void visit(ClassExtendsDeclaration n) throws Exception, SemError{
	   System.out.println("here in class extend decl");
	   //n.f1.accept(this);
	   class_name = n.f1.f0.toString();
	   extend_class = n.f3.f0.toString();
	   n.f6.accept(this);
   }
 	
   
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    * </PRE>
    */
   public void visit(MethodDeclaration n) throws Exception, SemError{
	   System.out.println("here in method decl");
	   //n.f2.accept(this);
	   method_name = n.f2.f0.toString();
	   arg.clear();
	   ResetArgTemp();
	 
	   /* Save args to TEMPs */
	   arg.put("this","TEMP "+ 0 );   //this
	   
	   /* find number of arguments */
	   n.f4.accept(this);
	   spiglet_code += "\n"+class_name+"_"+method_name+" [ "+arg.size()+" ]"+"\n";
	   IncreaseTemp(CurrentArgTemp());
	   
	   /* Save varDeclarations to TEMPs */
	   n.f7.accept(this);
	   
	   /* Begin Statement */
	   spiglet_code += "BEGIN\n";
	   n.f8.accept(this);
	   
	   value = "right";
	   n.f10.accept(this);
	   spiglet_code += "RETURN\n";
	
	   spiglet_code += "\t";
	  
	   spiglet_code += id_string;
	   	
	   spiglet_code += " \nEND\n";
   }   
   
   
   /* FormalParameter */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> Type()
    * f1 -> Identifier()
    * </PRE>
    */
   public void visit(FormalParameter n) throws Exception, SemError{
	   System.out.println("here in formalparameter");
	   arg.put(n.f1.f0.toString(),"TEMP "+ AssignArgTemp());
   }
   
   
   /**
    * Grammar production:
    * <PRE>
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    * </PRE>
    */
   public void visit(VarDeclaration n) throws Exception, SemError{
	   System.out.println("here in var decl");
	   arg.put(n.f1.f0.toString(),"TEMP "+ AssignTemp());
   }
   
   /* Statement */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    * </PRE>
    */
   public void visit(Statement n) throws Exception, SemError{
	   System.out.println("here in statement");
	   n.f0.accept(this);      //epistrofh expr
   }	
   
   /* AssignmentStatement */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    * </PRE>
    */
   public void visit(AssignmentStatement n) throws Exception, SemError{
	   System.out.println("here in Assign State");
	   	
   		//flag = true;
   		value = "right";
   		n.f2.accept(this);
   		String save2 = new String(id_string);
   		
   		value = "left";
   		n.f0.accept(this);     //epistrofh id_string
   		spiglet_code += " "+save2+"\n";
   		
   		String eq = new String(expr);
   		if( eq != null && eq.equals("this") )
   			spiglet_code += " TEMP 0";
   		//spiglet_code += "\n";
   		expr = new String(id_string);
   }
   
   
   /* ArrayAssignmentStatement */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    * </PRE>
    */
   public void visit(ArrayAssignmentStatement n) throws Exception, SemError{
	   value = "left_array";
	   n.f2.accept(this);
	   String save_expr = new String(id_string);
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT "+save_expr+" 0\n";
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" L"+AssignLabel()+"\n";
	   spiglet_code += "\tERROR\n";
	   spiglet_code += "L"+CurrentLabel()+"\tNOOP\n";
	   
	   value = "left_array";
	   n.f0.accept(this);
	   String save_ident = new String(id_string);
	   if(!temp_z) save_ident = save_ident+" 0";
	   
	   String save_head = "TEMP "+AssignTemp();
	   spiglet_code += "\tHLOAD "+save_head+" "+save_ident+"\n";
	   String save_head_zero = new String(save_head);
	   
	   if(class_ident)
	   {
		   class_ident = false;
		   spiglet_code += "\tHLOAD TEMP "+AssignTemp()+" "+save_head+" 0\n";
		   save_head_zero = "TEMP "+CurrentTemp();
	   }
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 1\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES "+save_expr+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT TEMP "+(CurrentTemp()-1)+" "+save_head_zero+"\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" MINUS TEMP "+(CurrentTemp()-3)+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" L"+AssignLabel()+"\n";
	   spiglet_code += "\tERROR\n";
	   spiglet_code += "L"+CurrentLabel()+"\tNOOP\n";
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES "+save_expr+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS TEMP "+(CurrentTemp()-1)+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+save_head+" TEMP "+(CurrentTemp()-1)+"\n";
	   String last_head = "TEMP "+CurrentTemp();
	   
	   value = "right";
	   n.f5.accept(this);
	   spiglet_code += "\tHSTORE "+last_head+" 0 "+id_string+"\n";
	   
	   expr = "ArrayAssignmentStatement";
   }
   
   
   
   /* IfStatement */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    * </PRE>
    */
   public void visit(IfStatement n) throws Exception, SemError{
	   System.out.println("here in IF state");
	   value = "right";
	   n.f2.accept(this);
	   String exitLabel = "L"+AssignLabel();
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" "+exitLabel+"\n";
	   
	   n.f4.accept(this);
	   String finishLabel = "L"+AssignLabel();
	   spiglet_code += "\tJUMP "+finishLabel+"\n";
	   spiglet_code += exitLabel+"\tNOOP\n";
	   
	   n.f6.accept(this);
	   spiglet_code += finishLabel+"\tNOOP\n";
   }
   
   /* WhileStatement */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * </PRE>
    */
   public void visit(WhileStatement n) throws Exception, SemError{
	   System.out.println("here in WHILE state");
	   String stateLabel = "L"+AssignLabel();
	   spiglet_code += stateLabel+"\tNOOP\n";
	   value = "right";
	   n.f2.accept(this);
	   String exitLabel = "L"+AssignLabel();
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" "+exitLabel+"\n";
	   
	   n.f4.accept(this);
	   spiglet_code +="\t JUMP "+stateLabel+"\n";
	   spiglet_code +=exitLabel+"\tNOOP\n";
   }
   		
   
   /* Print Statement */
   /**
    * <PRE>
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    * </PRE>
    */
   public void visit(PrintStatement n) throws Exception, SemError{
	   System.out.println("here in print");
	   value = "right";
	   n.f2.accept(this);
	   
	   if(look)
	   {
		   look = false;
		   spiglet_code += "\tPRINT "+id_string+"\n";
	   }
	   //else spiglet_code += "\tPRINT TEMP "+CurrentTemp()+"\n";
	   else spiglet_code += "\tPRINT "+id_string+"\n";
	   expr = "print";
   }
   
   
   /* Expression */
   /**
    * <PRE>
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    * </PRE>
    */
   public void visit(Expression n) throws Exception, SemError{
	   System.out.println("here in expression");
	   n.f0.accept(this);
	   //if(expr != null && !class_name.equals("MAIN")) 
   }
   
   /* AndExpression*/
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    * </PRE>
    */
   public void visit(AndExpression n) throws Exception, SemError{
	   value = "right";
	   n.f0.accept(this);
	   String apotel = "TEMP "+AssignTemp();
	   
	   spiglet_code += "\tMOVE "+apotel+" "+id_string+"\n";
	   spiglet_code += "\tCJUMP "+id_string+" L"+AssignLabel()+"\n";
	   
	   value = "right";
	   n.f2.accept(this);
	   
	   spiglet_code += "\tMOVE "+apotel+" "+id_string+"\n";
	   spiglet_code += "L"+CurrentLabel()+"\n";
	   
	   id_string = new String(apotel);
   }
	   
   
   /* CompareExpression*/
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "&lt;"
    * f2 -> PrimaryExpression()
    * </PRE>
    */
   public void visit(CompareExpression n) throws Exception, SemError{
		System.out.println("here in compare");
		n.f2.accept(this);
		//int save1_temp = CurrentTemp();
		String save2 = new String(id_string);
		n.f0.accept(this);
		//int save2_temp = CurrentTemp();
		String save1 = new String(id_string);
	   
	    spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT ";
	   
	    spiglet_code +=" "+save1+" "+save2+"\n";
	    
	    id_string = "TEMP "+CurrentTemp();
	   
	    expr = "compare";
   }
   
   /* MinusExpression */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    * </PRE>
    */
   public void visit(MinusExpression n) throws Exception, SemError{
	    boolean flag_call = false;
	    if(value.equals("call")) flag_call = true; 
	    System.out.println("here in minus");
	    value = "right";
	   	n.f2.accept(this);
	   	//int save1_temp = CurrentTemp();
		String save2 = new String(id_string);
	   	n.f0.accept(this);
	   	//int save2_temp = CurrentTemp();
	   	String save1 = new String(id_string);
		spiglet_code +="\tMOVE TEMP "+AssignTemp()+" MINUS ";
		//n.f0.accept(this);
		spiglet_code +=" "+save1+" "+save2+"\n";
		
		id_string = "TEMP "+CurrentTemp();
		expr = "MINUS";
		if(flag_call)
	    {
		   flag_call = false;
		   arg_count++;
		   arguments.put(arg_count, id_string);
	    }
   }
   
   /* PlusExpression */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    * </PRE>
    */
   public void visit(PlusExpression n) throws Exception, SemError{
	    boolean flag_call = false;
	    if(value.equals("call")) flag_call = true; 
	    System.out.println("here in plus");
	    value = "right";
	    n.f2.accept(this);
	   	//int save1_temp = CurrentTemp();
		String save2 = new String(id_string);
	   	n.f0.accept(this);
	   	//int save2_temp = CurrentTemp();
	   	String save1 = new String(id_string);
		spiglet_code +="\tMOVE TEMP "+AssignTemp()+" PLUS "; 
		
		spiglet_code +=" "+save1+" "+save2+"\n";
		
		id_string = "TEMP "+CurrentTemp();
		expr = "PLUS";
		if(flag_call)
	    {
		   flag_call = false;
		   arg_count++;
		   arguments.put(arg_count, id_string);
	    }
   }
   
   
   /* TimesExpression */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    * </PRE>
    */
   public void visit(TimesExpression n) throws Exception, SemError{
	    boolean flag_call = false;
	    if(value.equals("call")) flag_call = true; 
	    System.out.println("here in times");
	    value = "right";
   	    n.f2.accept(this);
	   	//int save1_temp = CurrentTemp();
		String save2 = new String(id_string);
	   	n.f0.accept(this);
	   	//int save2_temp = CurrentTemp();
	   	String save1 = new String(id_string);
		spiglet_code +="\tMOVE TEMP "+AssignTemp()+" TIMES ";
		//n.f0.accept(this);
		spiglet_code +=" "+save1+" "+save2+"\n";
		
		id_string = "TEMP "+CurrentTemp();
		expr = "TIMES";
		if(flag_call)
	    {
		   flag_call = false;
		   arg_count++;
		   arguments.put(arg_count, id_string);
	    }
   }
   
   
   /* ArrayLookup */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    * </PRE>
    */
   public void visit(ArrayLookup n) throws Exception, SemError{
	   boolean flag_call = false;
	   if(value.equals("call")) flag_call = true;
	   value = "left_array";
	   n.f2.accept(this);
	   String save_expr = new String(id_string);
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT "+save_expr+" 0\n";
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" L"+AssignLabel()+"\n";
	   spiglet_code += "\tERROR\n";
	   spiglet_code += "L"+CurrentLabel()+"\tNOOP\n";
	   
	   
	   value = "left_array";
	   n.f0.accept(this);
	   String save_ident = new String(id_string);
	   if(!temp_z) save_ident = save_ident+" 0";
	   
	   String save_head = "TEMP "+AssignTemp();
	   spiglet_code += "\tHLOAD "+save_head+" "+save_ident+"\n";
	   String save_head_zero = new String(save_head);
	   
	   if(class_ident)
	   {
		   class_ident = false;
		   spiglet_code += "\tHLOAD TEMP "+AssignTemp()+" "+save_head+" 0\n";
		   save_head_zero = "TEMP "+CurrentTemp();
	   }
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 1\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES "+save_expr+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT TEMP "+(CurrentTemp()-1)+" "+save_head_zero+"\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" MINUS TEMP "+(CurrentTemp()-3)+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" L"+AssignLabel()+"\n";
	   spiglet_code += "\tERROR\n";
	   spiglet_code += "L"+CurrentLabel()+"\tNOOP\n";
	   
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES "+save_expr+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS TEMP "+(CurrentTemp()-1)+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+save_head+" TEMP "+(CurrentTemp()-1)+"\n";
	   
	   spiglet_code += "\tHLOAD "+save_head+" TEMP "+CurrentTemp()+" 0\n";
	   
	   expr = "ArrayLookup";
	   id_string = new String(save_head);
	   look = true;
	   if(flag_call)
	   {
		   flag_call = false;
		   arg_count++;
		   arguments.put(arg_count, id_string);
	   }
   }
   
 
   
   /* PrimaryExpression */
   /**
    * <PRE>
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    * </PRE>
    */
   public void visit(PrimaryExpression n) throws Exception, SemError{	 
	    System.out.println("here in primary expression");
	    n.f0.accept(this);	   		
   }
   
   /* IntegerLiteral */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> &lt;INTEGER_LITERAL&gt;
    * </PRE>
    */
   public void visit(IntegerLiteral n) throws Exception, SemError{
	   System.out.println("here in Integer literal");
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" "+n.f0.toString() + " \n";
	   expr = "integerliteral";
	   id_string = "TEMP "+CurrentTemp();
	   if(value.equals("call"))
	   {
		   arg_count++;
		   arguments.put(arg_count, id_string);
	   }
   }
   
   /* ThisExpression */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "this"
    * </PRE>
    */
   public void visit(ThisExpression n) throws Exception, SemError{
	   System.out.println("here in This expression");
	    //spiglet_code += " TEMP 0 ";
   		expr = "this";
   }
   
   /* TRUE */
   /**
    * <PRE>
    * f0 -> "true"
    * </PRE>
    */
   public void visit(TrueLiteral n) throws Exception, SemError{
	    System.out.println("here in true literal");
	    spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 1\n";
	    id_string = "TEMP "+CurrentTemp();
	    if(value.equals("call"))
	    {
		    arg_count++;
		    arguments.put(arg_count, id_string);
	    }
	    expr = "true";
   }
   
   /* FALSE */
   /**
    * <PRE>
    * f0 -> "false"
    * </PRE>
    */
   public void visit(FalseLiteral n) throws Exception, SemError{
	    System.out.println("here in true literal");
	    spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 0\n";
	    id_string = "TEMP "+CurrentTemp();
	    if(value.equals("call"))
	    {
		    arg_count++;
		    arguments.put(arg_count, id_string);
	    }
	    expr = "false";
   }
   
   
   /* Allocation Expression */
   /**
    * <PRE>
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    * </PRE>
    */
   public void visit(AllocationExpression n) throws Exception, SemError{
	  System.out.println("here in AllocationExpression");
	  String Name = n.f1.f0.toString();
	  String class_FUN = Name;   //global?
	  System.out.println("Name = "+Name);	  
	  int fun_bits = this.VTable.get(Name).size();
	  int y = fun_bits;
	  int id_bits = this.IdsTable.get(Name).size();
	  int i = fun_bits-1;
	  int max_table = (id_bits + 1)*4;
	  
	  
	  /* Allocate Space */
	  int save_vtable = AssignTemp();
	  int save_table = AssignTemp();
	  spiglet_code += "\tMOVE TEMP "+ save_vtable +" HALLOCATE "+fun_bits*4+"\n";
	  spiglet_code += "\tMOVE TEMP "+ save_table +" HALLOCATE "+(id_bits*4+4)+"\n";
	  
	  while( y > 0)
	  {
		  y--;
		  spiglet_code += "\tMOVE TEMP "+AssignTemp()+" "+this.VTable.get(Name).get(i)+"\n";
		  spiglet_code += "\tHSTORE TEMP "+ (save_vtable) +" "+y*4+" TEMP "+CurrentTemp()+"\n";
		  i--;
	  }
	  spiglet_code += "\tHSTORE TEMP "+save_table+" 0 TEMP "+save_vtable+"\n";
	  int save_zero = AssignTemp();
	  if(max_table > 4) spiglet_code += "\tMOVE TEMP "+save_zero+" 0\n";
	  
	  //spiglet_code += "\tMOVE TEMP "+ AssignTemp() +" "+fun_bits+"\n";
	  
	  /* Initiate all null*/
	  //spiglet_code += "L"+ AssignLabel() +"\tCJUMP LT TEMP "+CurrentTemp()+" "+(id_bits*4+4)+" L"+ AssignLabel()+"\n"; 
	  //spiglet_code += "\tHSTORE PLUS TEMP "+(CurrentTemp()-1)+" TEMP "+CurrentTemp()+ " 0 0\n";
	  //spiglet_code += "\tMOVE TEMP "+CurrentTemp()+" PLUS TEMP "+CurrentTemp()+" 4\n";
	  //spiglet_code += "\tJUMP L"+(CurrentLabel()-1)+"\n";
	  
	  for(int j=4;j<max_table;j=j+4)
	  {
		  spiglet_code += "\tHSTORE TEMP "+save_table+" "+j+" TEMP "+save_zero+"\n";
	  }
	  
	 
	  /* exit loop*/
	  //spiglet_code += "L"+CurrentLabel()+"\tHSTORE TEMP "+ (CurrentTemp()-1)+" 0 TEMP "+(CurrentTemp()-2)+"\n";
	  
	  
	  //spiglet_code +=" RETURN\nTEMP "+(this.temp_noumber-1)+"\n";
      //this.pigle_code +="END\n";
      
	  previous_table_temp = save_table; 
      re_temp = "TEMP "+(CurrentTemp()-1);
      expr = Name;
      this.expr_mes = new String(Name);
      id_string = "TEMP "+save_table;
   }
   
   
   /* NotExpression */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "!"
    * f1 -> Expression()
    * </PRE>
    */
   public void visit(NotExpression n) throws Exception, SemError{
	   value = "right";
	   n.f1.accept(this);
	   String melos = new String(id_string);
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 1\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" MINUS TEMP "+(CurrentTemp()-1)+" "+melos+"\n";
	   
	   expr = "notexpr";
	   id_string = "TEMP "+CurrentTemp();
   }
   
   
   /* ArrayLength */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    * </PRE>
    */
   public void visit(ArrayLength n) throws Exception, SemError{
	   boolean flag_call = false;
	   if(value.equals("call")) flag_call = true;
	   
	   value = "right";
	   n.f0.accept(this);
	   String save_array = new String(id_string);
	   spiglet_code += "\tHLOAD TEMP "+AssignTemp()+" "+save_array+" 0\n";
	   String max_size = "TEMP "+CurrentTemp();
	   
	   String len = "TEMP "+AssignTemp();
	   String count = "TEMP "+AssignTemp();
	   spiglet_code += "\tMOVE "+len+" 1\n";
	   spiglet_code += "\tMOVE "+count+" 4\n";
	   
	   spiglet_code += "L"+AssignLabel()+"\tNOOP\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT "+count+" "+max_size+"\n";
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" L"+AssignLabel()+"\n";
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 1\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+len+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tMOVE "+len+" TEMP "+CurrentTemp()+"\n";
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+count+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tMOVE "+count+" TEMP "+CurrentTemp()+"\n";
	   spiglet_code += "\tJUMP L"+(CurrentLabel()-1)+"\n";
	   spiglet_code += "L"+CurrentLabel()+"\tNOOP\n";
	   
	   id_string = new String(len);
	   expr = "arraylength";
	   
	   if(flag_call)
	   {
		   flag_call = false;
		   arg_count++;
		   arguments.put(arg_count, id_string);
	   }
	   
   }
   
   /* Message Send */
   /**
    * <PRE>
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    * </PRE>
    */
   public void visit(MessageSend n) throws Exception, SemError{
	   System.out.println("here in MessageSend");
	   String func = n.f2.f0.toString();
	   value = "call";
	   n.f0.accept(this);
	   String first = new String(expr_mes);
	   System.out.println("ffffffffff "+first);
	   String ident = new String(expr_mes);
	   
	   String loc;
	   String temp = "TEMP 0";
	   
	   if(first.equals("this"))
	   {
		   loc = class_name+"_"+func;
		   first = class_name;
	   }
	   else if(DeclClasses.containsKey(first))
	   {
		   loc = first+"_"+func;
	   }
	   else
	   {
		   System.out.println("**********1 "+first);
		   Fun_or_Ident foi = Table.get(class_name).get("#"+this.method_name);
		   LinkedHashMap<String, String> args = foi.arg;
		   LinkedHashMap<String, String> vars = foi.var;
		   
		   if( args.containsKey(first) )
		   {
			   System.out.println("**********2");
			   first = args.get(first);			   
			   
		   }
		   else if ( vars.containsKey(first) )/* Search identifier's type on declared vars  */
		   {
			   System.out.println("**********3");
			   first = vars.get(first);	
		   }
		   else
		   {
			   String className = class_name;
			   String extendClass;
			   foi = Table.get(className).get(first);
			   while(foi == null)
			   {
				   extendClass = this.DeclClasses.get(className);
				   className = extendClass;
				   foi = Table.get(className).get(first);
			   }
			   first = foi.Type;
		   }
		   
		   loc = first+"_"+func;
		   temp = this.arg.get(ident);
		   
	   }
	   
	   int pos = 0;
	   int table_size = VTable.get(first).size();
	   for(int i=0; i<table_size; i++)
	   {
			String name = this.VTable.get(first).get(i);
			if(name.equals(loc))
			{
				pos = i;
				break;
			}
	   }
	   
	   int save_temp_this = AssignTemp();
	   if(previous_table_temp != 0)
	   {
		   temp = "TEMP "+previous_table_temp;
		   previous_table_temp = 0;
	   }
	   spiglet_code += "\tMOVE TEMP "+ save_temp_this +" "+temp+"\n";
	   spiglet_code += "\tHLOAD TEMP "+ AssignTemp() +" TEMP "+ (CurrentTemp() - 1) + " 0\n";
	   int save_temp_num = AssignTemp();
	   spiglet_code += "\tHLOAD TEMP "+ save_temp_num +" TEMP "+ ( CurrentTemp() - 1 ) +" "+ pos*4 + "\n";
	   
	   arg_count = 0;
	   arguments = new LinkedHashMap<Integer,String>();
	   value = "call";
	   n.f4.accept(this);
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" CALL "+"TEMP "+save_temp_num+"( TEMP "+save_temp_this+" ";
	   //+(CurrentTemp()-1)+" )\n";
	   //System.out.println("/////////"+arguments.size());
	   for(int i=1;i<=arguments.size();i++)
	   { 
		   System.out.println("*************"+arguments.get(i));
		   spiglet_code += arguments.get(i)+" ";
	   }
	   spiglet_code += ")\n";
	   id_string = "TEMP "+CurrentTemp();
   }
   
   
   /* ArrayAllocationExpression */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    * </PRE>
    */
   public void visit(ArrayAllocationExpression n) throws Exception, SemError{
	   
	   value = "right";
	   n.f3.accept(this);
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+id_string+" 1\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES TEMP "+(CurrentTemp()-2)+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" HALLOCATE TEMP "+(CurrentTemp()-1)+"\n";
	   String save_array = "TEMP "+CurrentTemp();
	   
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 4\n";
	   String count = "TEMP "+CurrentTemp();
	   spiglet_code += "L"+AssignLabel()+"\tNOOP\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+id_string+" 1\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES TEMP "+(CurrentTemp()-2)+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" LT "+count+" TEMP "+(CurrentTemp()-1)+"\n";
	   spiglet_code += "\tCJUMP TEMP "+CurrentTemp()+" L"+AssignLabel()+"\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" PLUS "+save_array+" "+count+"\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 0\n";
	   spiglet_code += "\tHSTORE TEMP "+(CurrentTemp()-1)+" 0 TEMP "+CurrentTemp()+"\n";
	   spiglet_code += "\tMOVE "+count+" PLUS "+count+" 4\n";
	   spiglet_code += "\tJUMP L"+(CurrentLabel()-1)+"\n";
	   
	   spiglet_code += "L"+CurrentLabel()+"\tNOOP\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" 4\n";
	   spiglet_code += "\tMOVE TEMP "+AssignTemp()+" TIMES "+id_string+" TEMP "+(CurrentTemp()-1)+"\n";
	   
	   spiglet_code += "\tHSTORE "+save_array+" 0 TEMP "+CurrentTemp()+"\n";
	   id_string = new String(save_array);
   }
		
   
   
   /* Identifier */
   /**
    * Grammar production:
    * <PRE>
    * f0 -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(Identifier n) throws Exception, SemError{
	   System.out.println("here in IDENT");
	   
	   String id = n.f0.toString();
	   String className = this.class_name;
	   String extendClass = null;
	   boolean exist = false;
	   int pos = 0;
	   
	   //elenxo an briskete mesa sta class ids
	   if(this.value != null)
	   {
			extendClass = className;//DeclClasses.get(className);
			while(extendClass != null)
			{
				if(this.IdsTable.get(className).containsValue(className+"_"+id))
				{
					exist = true;
					break;
				}
				extendClass = DeclClasses.get(extendClass);
				className = extendClass;
			}
			
			
			if(exist)
			{
				int table_size = IdsTable.get(className).size();
				for(int i=0; i<table_size; i++)
				{
					String id_name = this.IdsTable.get(className).get(i);
					if(id_name.equals(className+"_"+id))
					{
						pos = i+1;
						break;
					}
				}
				
				if(value.equals("left"))
				{
					class_ident = true;
					spiglet_code += "\tHSTORE TEMP 0 "+ pos*4 +" ";
					this.expr_mes = n.f0.toString();
				}
				else if(value.equals("right"))
				{
					class_ident = true;
					id_string = " \tHLOAD TEMP "+ AssignTemp() +" TEMP 0 "+ pos*4+"\n";
					spiglet_code += id_string;
					id_string = "TEMP "+CurrentTemp();
					this.expr = new String(id_string);
					this.expr_mes = n.f0.toString();
					return;
				}
				else if(value.equals("call"))
				{
					class_ident = true;
					id_string = " \tHLOAD TEMP "+ AssignTemp() +" TEMP 0 "+ pos*4+"\n";
					spiglet_code += id_string;
					id_string = "TEMP "+CurrentTemp();
					//System.out.println("*/*/*/ CALL "+id_string);
					this.expr = new String(id_string);
					arg_count++;
					arguments.put(arg_count, id_string);
					this.expr_mes = n.f0.toString();
					return;
				}
				else if(value.equals("left_array"))
				{
					class_ident = true;
					temp_z = true;
					id_string = "TEMP 0 "+ pos*4;
					this.expr = new String(id_string);
					this.expr_mes = n.f0.toString();
					return;
				}
				else if(value.equals("list"))
				{
					class_ident = true;
					spiglet_code += "\tHLOAD TEMP "+ AssignTemp() +" TEMP 0 "+pos*4 + "\n";
					this.expr_mes = n.f0.toString();
				}
				
			}
			else if(value.equals("call"))
			{
				id_string = arg.get(id)+" ";
				this.expr = new String(id_string);
				//System.out.println("*/*/*/ CALL "+id_string);
				arg_count++;
				arguments.put(arg_count, id_string);
				this.expr_mes = n.f0.toString();
				return;
			}
			else
			{
				if(value.equals("left")) spiglet_code += "\tMOVE "+ arg.get(id)+" ";
				else if(value.equals("left_array")) temp_z = false; 
				//else if( value.equals("right") || value.equals("list") ) //spiglet_code += arg.get(id)+" ";
				id_string = arg.get(id)+" ";
				this.expr = new String(id_string);
				this.expr_mes = n.f0.toString();
				return;
			}
	   }
	   else if( arg.get(id) != null )
	   {
		   spiglet_code += arg.get(id)+" ";
	   }
	   id_string = id;
	   this.expr = new String(id_string);
	   this.expr_mes = n.f0.toString();
	   return;
	   
	   
	   
   }
   
   
}