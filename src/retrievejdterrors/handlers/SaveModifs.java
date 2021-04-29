package retrievejdterrors.handlers;



import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class SaveModifs {

	public static void saveModifiedUnit( CompilationUnit unit)
			throws CoreException {
		save(unit, new TextEditProvider() {
			public TextEdit getTextEdit(IDocument document) {
				return unit.rewrite(document, null);
			}
		});
	}
	
	public static void save(CompilationUnit unit,
			TextEditProvider textEditProvider) throws CoreException {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		
//		ITextFileBufferManager bufferManager = ITextFileBufferManager.DEFAULT;
		
		IPath path = unit.getJavaElement().getPath();
		System.out.println("path of saving"+path);
		System.out.println("rewriting ast 1");
		try {
			// connect the path
			bufferManager.connect(path, null);

			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
			
//			ResourceTextFileBuffer b = new ResourceTextFileBuffer();
//			SynchronizableDocument s = new SynchronizableDocument();
			
			System.out.println("textFileBuffer "+textFileBuffer.getClass());
			// retrieve the buffer
			IDocument document = textFileBuffer
					.getDocument();
			System.out.println("document "+document.getClass());
//			Document d = new Document();
			
			//path.toFile().
			
			// ask the textEditProvider for the change information
			TextEdit edit = textEditProvider.getTextEdit(document);
			System.out.println("text edit content  " +edit.hasChildren());
			// apply the changes to the document
			edit.apply(document);

			// write the changes from the buffer to the file
			textFileBuffer
					.commit(null /* ProgressMonitor */, false /* Overwrite */);
			System.out.println("rewriting ast 2");
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} finally {
			// disconnect the path
			bufferManager.disconnect(path, null);
		}
	}
	
	public interface TextEditProvider {

		/**
		 * Provides a {@link TextEdit} document.
		 *
		 * @param document
		 *            the docuement the {@link TextEdit} object will be applied
		 *            to
		 * @return the {@link TextEdit} instance
		 */
		TextEdit getTextEdit(IDocument document);
	}
}


