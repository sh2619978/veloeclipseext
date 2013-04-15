package veloeclipseext.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Document;

public class JdtManager {

    public IProject[] getProjects() {
        // Get the root of the workspace
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject[] projects = root.getProjects();
        return projects;
    }

    public IProject getProject(String name) {
        IProject[] projects = this.getProjects();
        // Loop over all projects
        for (IProject project : projects) {
            if (project.getName().equals(name)) {
                return project;
            }
        }
        return null;
    }

    public List<IJavaProject> getJavaProjects() {
        List<IJavaProject> result = new ArrayList<IJavaProject>();
        IProject[] projects = this.getProjects();
        for (IProject project : projects) {
            try {
                if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
                    IJavaProject javaProject = JavaCore.create(project);
                    result.add(javaProject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public IJavaProject getFirstJavaProject() {
        List<IJavaProject> javaProjects = this.getJavaProjects();
        if (javaProjects != null && javaProjects.size() > 0) {
            return javaProjects.get(0);
        }
        return null;
    }

    public IPackageFragment[] getPackages(IJavaProject javaProject) throws JavaModelException {
        IPackageFragment[] packages = javaProject.getPackageFragments();
        return packages;
    }

    private void printICompilationUnitInfo(IPackageFragment mypackage) throws JavaModelException {
        for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
            printCompilationUnitDetails(unit);

        }
    }

    private void printIMethods(ICompilationUnit unit) throws JavaModelException {
        IType[] allTypes = unit.getAllTypes();
        for (IType type : allTypes) {
            printIMethodDetails(type);
        }
    }

    private void printCompilationUnitDetails(ICompilationUnit unit) throws JavaModelException {
        System.out.println("Source file " + unit.getElementName());
        Document doc = new Document(unit.getSource());
        System.out.println("Has number of lines: " + doc.getNumberOfLines());
        printIMethods(unit);
    }

    private void printIMethodDetails(IType type) throws JavaModelException {
        IMethod[] methods = type.getMethods();
        for (IMethod method : methods) {

            System.out.println("Method name " + method.getElementName());
            System.out.println("Signature " + method.getSignature());
            System.out.println("Return Type " + method.getReturnType());

        }
    }

}
