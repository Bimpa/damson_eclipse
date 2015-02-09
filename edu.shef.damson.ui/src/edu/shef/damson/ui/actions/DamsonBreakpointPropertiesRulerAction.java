package edu.shef.damson.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import edu.shef.damson.core.breakpoints.DamsonLineBreakpoint;

public class DamsonBreakpointPropertiesRulerAction extends Action implements IUpdate{

	private ITextEditor fEditor;
    private IVerticalRulerInfo fRulerInfo;
    
    public DamsonBreakpointPropertiesRulerAction(ITextEditor editor, IVerticalRulerInfo rulerInfo)
    {
    	fEditor = editor;
        fRulerInfo = rulerInfo;
        setText("Breakpoint &Properties...");
    }
    
    public void run()
    {
    	if (getBreakpoint() != null)
        {
        	PropertyDialogAction action = new PropertyDialogAction(fEditor.getEditorSite(), new ISelectionProvider() {
                public void addSelectionChangedListener(ISelectionChangedListener listener) {
                }

                public ISelection getSelection() {
                    return new StructuredSelection(getBreakpoint());
                }

                public void removeSelectionChangedListener(ISelectionChangedListener listener) {
                }

                public void setSelection(ISelection selection) {
                }
            });
            action.run();
        }
    }

	@Override
	public void update() {
		IBreakpoint breakpoint = getBreakpoint();
        if ((breakpoint != null) && (breakpoint instanceof DamsonLineBreakpoint))
        {
            setEnabled(true);
        }
        else
        {
            setEnabled(false);
        }
		
	}
	
	public IBreakpoint getBreakpoint()
	{
		IAnnotationModel annotationModel = fEditor.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
        IDocument document = fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput());
        if (annotationModel != null) {
            @SuppressWarnings("unchecked")
			Iterator<Object> iterator = annotationModel.getAnnotationIterator();
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (object instanceof SimpleMarkerAnnotation) {
                    SimpleMarkerAnnotation markerAnnotation = (SimpleMarkerAnnotation) object;
                    IMarker marker = markerAnnotation.getMarker();
                    try {
                        if (marker.isSubtypeOf(IBreakpoint.BREAKPOINT_MARKER)) {
                            Position position = annotationModel.getPosition(markerAnnotation);
                            int line = document.getLineOfOffset(position.getOffset());
                            if (line == fRulerInfo.getLineOfLastMouseButtonActivity()) {
                                IBreakpoint breakpoint = DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(marker);
                                if (breakpoint != null) {
                                    return breakpoint;
                                }
                            }
                        }
                    } catch (CoreException e) {
                    } catch (org.eclipse.jface.text.BadLocationException e) {
					}
                }
            }
        }
        return null;
	}
}
