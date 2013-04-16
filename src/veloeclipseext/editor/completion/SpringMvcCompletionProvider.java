package veloeclipseext.editor.completion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
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
            // return getVariableProposals(editor, prefix, offset);
        } else if (prefix.getType() == VelocityTextGuesser.TYPE_MEMBER_QUALIFIER) {

        }

        return getVariableProposals(editor, prefix, offset);
        // return new ArrayList<ICompletionProposal>();
    }

    private List<ICompletionProposal> getVariableProposals(VelocityEditor editor, final VelocityTextGuesser prefix,
            final int offset) throws CoreException {
        final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

        ModelTools modelTools = new ModelTools(editor);
        ITreeNode nodeByLine = modelTools.getNodeByLine(prefix.getLine());

        ITreeNode rootNode = editor.getRootNode();
        ITreeNode lastRootNode = editor.getLastRootNode();
        Object[] rootElements = editor.getRootElements();

        JdtManager jdtManager = new JdtManager();
        IJavaProject firstJavaProject = jdtManager.getFirstJavaProject();
        if (!firstJavaProject.isOpen()) {
            firstJavaProject.open(null);
        }

        SearchEngine engine = new SearchEngine();
        SearchPattern pattern = SearchPattern.createPattern("java.util.Date", IJavaSearchConstants.TYPE,
                IJavaSearchConstants.PARAMETER_DECLARATION_TYPE_REFERENCE, SearchPattern.R_EXACT_MATCH);

        final List<IMethod> methodList = new ArrayList<IMethod>();
        SearchRequestor requestor = new SearchRequestor() {

            @Override
            public void acceptSearchMatch(SearchMatch match) throws CoreException {
                IJavaElement element = (IJavaElement) match.getElement();
                // String showText = "type=" + guesserTypeMap.get(prefix.getType()) + ";text=" + prefix.getText() +
                // ";variable=" + prefix.getVariable() + ";line=" + prefix.getLine();
                String showText = (element.getElementType() == IJavaElement.METHOD ? "method  " : "  ")
                        + element.toString();
                proposals.add(new CompletionProposal(showText, offset, prefix.getText().length(), showText.length(),
                        null, showText, null, null));
                if (element instanceof IMethod) {
                    methodList.add((IMethod) element);
                }
            }
        };

        engine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, SearchEngine
                .createJavaSearchScope(new IJavaElement[] { firstJavaProject.findPackageFragmentRoot(firstJavaProject
                        .getPath().append("/src")) }), requestor, null);

        
        for (IMethod method : methodList) {
            if (!method.getElementName().equals("function")) {
                continue;
            }
            SearchRequestor secondRequestor = new SearchRequestor() {

                @Override
                public void acceptSearchMatch(SearchMatch match) throws CoreException {
                    IJavaElement element = (IJavaElement) match.getElement();
                    System.out.println("offset="+ match.getOffset() + ";length=" + match.getLength());
                }
            };
            
            engine.search(SearchPattern.createPattern(method.getParameters()[0],
                    IJavaSearchConstants.REFERENCES),
                    new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, SearchEngine
                            .createJavaSearchScope(new IJavaElement[]{method}), secondRequestor, null);
        }
        
        return proposals;
    }

    public static void main(String[] args) {
        System.out.println();
    }

}
