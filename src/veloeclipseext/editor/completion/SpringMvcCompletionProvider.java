package veloeclipseext.editor.completion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import veloeclipseext.jdt.JdtManager;

import com.googlecode.veloeclipse.editor.VelocityEditor;
import com.googlecode.veloeclipse.editor.completion.ICompletionProvider;
import com.googlecode.veloeclipse.vaulttec.ui.editor.text.VelocityTextGuesser;
import com.googlecode.veloeclipse.vaulttec.ui.model.ITreeNode;
import com.googlecode.veloeclipse.vaulttec.ui.model.ModelTools;

public class SpringMvcCompletionProvider implements ICompletionProvider {
    
    private static Map<Integer, String> guesserTypeMap = new HashMap<Integer, String>();
    
    static {
        try {
            Field[] fields = VelocityTextGuesser.class.getFields();
            VelocityTextGuesser object = VelocityTextGuesser.class.newInstance();
            for (Field field : fields) {
               guesserTypeMap.put(field.getInt(object), field.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection getExtraProposals(VelocityEditor editor, IFile file, IDocument doc, VelocityTextGuesser prefix,
            int offset) throws CoreException {

        if (prefix.getType() == VelocityTextGuesser.TYPE_VARIABLE) {
//            return getVariableProposals(editor, prefix, offset);
        } else if (prefix.getType() == VelocityTextGuesser.TYPE_MEMBER_QUALIFIER) {
            
        }
        
        return getVariableProposals(editor, prefix, offset);
//        return new ArrayList<ICompletionProposal>();
    }

    private List<ICompletionProposal> getVariableProposals(VelocityEditor editor, VelocityTextGuesser prefix, int offset) {
        ModelTools modelTools = new ModelTools(editor);
        ITreeNode nodeByLine = modelTools.getNodeByLine(prefix.getLine());
        
        ITreeNode rootNode = editor.getRootNode();
        ITreeNode lastRootNode = editor.getLastRootNode();
        Object[] rootElements = editor.getRootElements();
        
        JdtManager jdtManager = new JdtManager();
        IJavaProject firstJavaProject = jdtManager.getFirstJavaProject();
        
        SearchPattern pattern = SearchPattern.createPattern("java.lang.Object()", IJavaSearchConstants.METHOD, IJavaSearchConstants.QUALIFIED_REFERENCE, SearchPattern.R_PATTERN_MATCH);
        SearchEngine engine = new SearchEngine();
        
        
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
//        String showText = "type=" + guesserTypeMap.get(prefix.getType()) + ";text=" + prefix.getText() + ";variable=" + prefix.getVariable() + ";line=" + prefix.getLine();
        String showText = "test";
        proposals.add(new CompletionProposal(showText, offset, prefix.getText().length(), showText.length(), null, showText, null, null));
        return proposals;
    }

}
