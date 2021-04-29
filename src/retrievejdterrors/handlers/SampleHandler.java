package retrievejdterrors.handlers;

import java.util.ArrayList;

import java.util.Collections;

import java.util.List;



import org.eclipse.core.commands.AbstractHandler;

import org.eclipse.core.commands.ExecutionEvent;

import org.eclipse.core.commands.ExecutionException;

import org.eclipse.ui.IWorkbenchWindow;

import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.core.resources.IFile;

import org.eclipse.core.resources.IFolder;

import org.eclipse.core.resources.IMarker;

import org.eclipse.core.resources.IProject;

import org.eclipse.core.resources.IResource;

import org.eclipse.core.resources.IWorkspace;

import org.eclipse.core.resources.IWorkspaceRoot;

import org.eclipse.core.resources.IncrementalProjectBuilder;

import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.runtime.IPath;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.jdt.core.IClasspathEntry;

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.core.IJavaModelMarker;

import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jdt.core.IMethod;

import org.eclipse.jdt.core.IPackageFragment;

import org.eclipse.jdt.core.IPackageFragmentRoot;

import org.eclipse.jdt.core.IType;

import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.AST;

import org.eclipse.jdt.core.dom.ASTNode;

import org.eclipse.jdt.core.dom.ASTParser;

import org.eclipse.jdt.core.dom.ASTVisitor;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import org.eclipse.jdt.core.dom.MethodInvocation;

import org.eclipse.jdt.core.dom.Name;

import org.eclipse.jdt.core.dom.NodeFinder;

import org.eclipse.jdt.core.dom.QualifiedName;

import org.eclipse.jdt.core.dom.SimpleName;

import org.eclipse.jdt.core.dom.SimpleType;

import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;

import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import org.eclipse.jface.text.BadLocationException;

import org.eclipse.jface.text.Document;

import org.eclipse.jface.text.IDocument;

import org.eclipse.text.edits.MalformedTreeException;

import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.core.filebuffers.*;
public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"RetrieveJDTErrors",
				"Hello, Eclipse world");


		System.out.println(" in plugin");

		

		try 

        {

		//IPath path = new Path("TestVog2/src/testvog2.java");

		IPath path = new Path("AddressBook");

		IProject myproject = ResourcesPlugin.getWorkspace().getRoot().getProject(path.lastSegment());

		 IProgressMonitor myProgressMonitor=new NullProgressMonitor();

		 

         myproject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, myProgressMonitor);

         if (myproject.hasNature(JavaCore.NATURE_ID)) {

			    IJavaProject javaProject = JavaCore.create(myproject);

			    javaProject.open(null);

			     //javaProject.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);

			    

			    for(IPackageFragment packageFrag : javaProject.getPackageFragments()){

			    	

			    	

			    	if(packageFrag.getPath().getFileExtension() == null){

 			    		

 			    		

 			    		for(IJavaElement javaEle : packageFrag.getChildren()){

 			    			

 			    			if(javaEle instanceof ICompilationUnit){

 			    				

 			    				ICompilationUnit compilUnit= JavaCore.createCompilationUnitFrom((IFile)javaEle.getCorrespondingResource());

 			    			
 			    			    

 			    				ASTParser parser = ASTParser.newParser(AST.JLS8);

 			    			       parser.setResolveBindings(true);

 			    			       parser.setKind(ASTParser.K_COMPILATION_UNIT);

 			    			      

 			    			       parser.setBindingsRecovery(true);

 			    			       parser.setSource(compilUnit);

 			    			    

 			    			      parser.setResolveBindings(true);

 			    			     

 			    			     

 			    			      parser.setBindingsRecovery(true);

 			    			    	

 			    			     CompilationUnit cu = (CompilationUnit) parser.createAST( null);

 			    			    IPath pathcu = cu.getJavaElement().getPath();

 			    			  cu.recordModifications();
 			    			   System.out.println(" in pathcu "+pathcu.toString());

 			    			  
 			    			     AST ast= cu.getAST();

 			    			  
 			    				if (compilUnit != null) {

 			    				 System.out.println("Compilation unit : "+compilUnit.getElementName());
 			    				
 			    				 
 			    				 
 			    	            IMarker[] ml =findJavaProblemMarkers(compilUnit);

 			    				System.out.println("ERRORS' Number: "+ml.length); 

 			    				for (int i = 0; i < ml.length; ++i) {

 			    					 

 			    				 System.out.println("Error found "+ml[i].toString());
 
 			    				

 			    				 /** ast part **/

 			    				

 			    				int start = ml[i].getAttribute(IMarker.CHAR_START, 0);

   			    			    int end = ml[i].getAttribute(IMarker.CHAR_END, 0);

   			    			     

   			    			  NodeFinder nf = new NodeFinder(cu.getRoot(), start, end-start);

			    			  ASTNode an=nf.getCoveringNode();

			    			    

			    			   

			    			     System.out.println(" ASTNode ERROR: "+an);

			    			     

			    			     /**  end method declaration **/

			    			     /** Rename type**/

			    			     

			    			     if(an instanceof SimpleName)

			    			     {

			    			    	 System.out.println("  Retrieved ASTNode is SimpleName");
			    			    	 Document document2 = new Document("import java.util.List;\nclass X {}\n");
			    			    	 ASTParser parser2 = ASTParser.newParser(AST.JLS8);
			    			    	 parser2.setResolveBindings(true);
			    			         parser2.setStatementsRecovery(true);
			    			         parser2.setBindingsRecovery(true);
			    			         parser2.setKind(ASTParser.K_COMPILATION_UNIT);
			    			         parser2.setSource(document2.get().toCharArray());
			    			         CompilationUnit cu2 = (CompilationUnit) parser2.createAST(null);
			    			         AST ast2 = cu2.getAST();
			    			       
			    			    	 ImportDeclaration id = ast2.newImportDeclaration();
			    			         id.setName(ast2.newName(new String[] {"java", "util", "Set"}));
			    			         ASTRewrite rewriter = ASTRewrite.create(ast2);
			    			      
			    			         ListRewrite lrw = rewriter.getListRewrite(cu2, CompilationUnit.IMPORTS_PROPERTY);
			    			       //  System.out.println("  TEST 2 ");
			    			         lrw.insertLast(id, null);
			    			       //  System.out.println("  TEST 3"+ lrw.getOriginalList().size());
			    			         TextEdit edits2 = rewriter.rewriteAST(document2, null);
			    			         edits2.apply(document2);
			    			        
			    			         // MODIF ON ERRORS CU
			    			     
			    			         Document document = new Document(compilUnit.getSource());
			    			         ImportDeclaration id1 = ast.newImportDeclaration();
			    			         id1.setName(ast.newName(new String[] {"java", "util", "Set"}));
			    			        // ((SimpleName)an).setIdentifier("newID");
			    			         ASTRewrite rewriter1 = ASTRewrite.create(ast);
			    			      rewriter1.set((SimpleName)an, SimpleName.IDENTIFIER_PROPERTY, "NEWID", null);
			    			         ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
			    			    
			    			         lrw1.insertLast(id1, null);
			    			   
			    			         TextEdit edits = rewriter1.rewriteAST(document, null);
			    			         edits.apply(document);
			    			        
			    			       
			    			         
			    			         ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 		
			    			         try { 			    			 		
			    			        	 // connect the path 			    
			    			        	 bufferManager.connect(pathcu, null);  
			    			        	 ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(pathcu); 	
			    			        	 TextEdit edit3 = rewriter1.rewriteAST(document, null); 
			    			        	 System.out.println("text edit content  " +edit3.hasChildren()); 	
			    			        	 // apply the changes to the document 		
			    			        	 IDocument documenti = textFileBuffer.getDocument(); 
			    			        	 edit3.apply(documenti);  			    
			    			        	 // write the changes from the buffer to the file 	
			    			        	 textFileBuffer.commit(null /* ProgressMonitor */, false /* Overwrite */);
			    			        	 System.out.println("rewriting ast 2"); 
			    			        	 } 
			    			         catch (MalformedTreeException e) { 	
			    			        	 e.printStackTrace(); 			  
			    			        	 } catch (BadLocationException e) { 
			    			        		 e.printStackTrace(); 			  
			    			        		 } finally
			    			         { 			    	
			    			        			 // disconnect the path 	
			    			        			 bufferManager.disconnect(pathcu, null); 	
			    			        			 } 			    			 		
			    			         
			    			         
			    			       // Trying to use savemodifs class   
			    			       //  compilUnit.getBuffer().setContents(document.get());
			    			      
			    			       //  SaveModifs.saveModifiedUnit(cu);
			    			         //// end of savemodifs class
			    			 		
			    			
			    			   	
			    			    	 

	 

			    			     }

			    			    

			    			     /** END rename type **/

    		       			     

			    			   /*  Job job = Job.create("Saving changes", monitor -> {

			 			    			try {



			 			    		     saveChanges((ICompilationUnit) cu.getJavaElement(),monitor,rewriter,rewrite);

			 			    			}

			 			    			catch(Exception e) {}

			 			    			});

			 			    		job.schedule();

			 			    		*/

			    			    /** AST part end **/

			    			   

   			    			     

 			    				 }

 			    			

 			    				}

 			    				

 			    				

 			    			}

 			    		}

 			    				 

 			    	}

			    }

			    

         }

         }

         catch (Exception e)

         {

        	 

         }

      //IFile file = workspace.getRoot().getFile(path);

      // CompilationUnit compilationUnit =(CompilationUnit) JavaCore.create(file);

   //  ICompilationUnit element= JavaCore.createCompilationUnitFrom(file);

     

     

     // assert "import java.util.List; \nimport java.util.Set;\nclass X {}\n".equals(document.get());

      

       
		return null;
	}
	 public IMarker[] findJavaProblemMarkers(ICompilationUnit cu) 

		      throws CoreException {

	

		 System.out.println(" Compilation unit path : "+ cu.getPath());

		 

		      IResource javaSourceFile = cu.getUnderlyingResource();

		     

		      IMarker[] markers = 

		         javaSourceFile.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,

		            true, IResource.DEPTH_INFINITE);

		     if(markers.length==0)

		      System.out.println("No error detected ");

		      return markers;



		   }
}
