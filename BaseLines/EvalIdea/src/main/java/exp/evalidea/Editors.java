package exp.evalidea;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Editors {
    public static Document getCurrentDocument(String path){
        System.out.println("absolutePath:"+path);
        //        VirtualFile fileByRelativePath = LocalFileSystem.getInstance().findFileByPath(path);
//        VfsUtil.markDirtyAndRefresh(true,true,true, new File("E:/VariableNameGeneration/BaseLine/BaseLineExperiment/TestProject/alibaba_jstorm"));
        VirtualFile fileByRelativePath = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
        Document document = null;
        if(fileByRelativePath!=null)
            document = FileDocumentManager.getInstance().getDocument(fileByRelativePath);
        else{
            System.out.println("fileByRelativePath == null");
        }
        return document;
    }

    public static Editor createSourceEditor(Project project, String path, String language, boolean readOnly) {
        final EditorFactory factory = EditorFactory.getInstance();
        Document document = getCurrentDocument(path);
        if(document==null) return null;
        //        editor.getSettings().setRefrainFromScrolling(false);
//        editor.getSettings().setUseCustomSoftWrapIndent(true);
        return factory.createEditor(document, project, FileTypeManager.getInstance()
                .getFileTypeByExtension(language), readOnly);

    }

    public static void release(Editor editor) {
        EditorFactory.getInstance().releaseEditor(editor);
    }
}
