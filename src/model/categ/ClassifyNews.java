package categ;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.catfeed.dao.PostDAO;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.JointClassifier;
import com.aliasi.classify.JointClassifierEvaluator;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;

public class ClassifyNews {

    private static File TRAINING_DIR
        = new File("src");

    private static String[] CATEGORIES
        = { "cotidiano",
            "esportes",
            "politica",
            "religiao",
            "tecnologia",};

    private static int NGRAM_SIZE = 6;

    public static void main(String[] args) throws ClassNotFoundException, IOException
    {

        DynamicLMClassifier<NGramProcessLM> classifier
            = DynamicLMClassifier.createNGramProcess(CATEGORIES,NGRAM_SIZE);

        for(int i=0; i<CATEGORIES.length; ++i)
        {
            File classDir = new File(TRAINING_DIR,CATEGORIES[i]);
        
            if (!classDir.isDirectory())
            {
                String msg = "Could not find training directory="
                    + classDir
                    + "\nHave you unpacked 4 newsgroups?";
                System.out.println(msg); // in case exception gets lost in shell
                throw new IllegalArgumentException(msg);
            }

            String[] trainingFiles = classDir.list();
            for (int j = 0; j < trainingFiles.length; ++j)
            {
                File file = new File(classDir,trainingFiles[j]);
                String text = Files.readFromFile(file,"ISO-8859-1");
                System.out.println("Training on " + CATEGORIES[i] + "/" + trainingFiles[j]);
                Classification classification
                    = new Classification(CATEGORIES[i]);
                Classified<CharSequence> classified
                    = new Classified<CharSequence>(text,classification);
                classifier.handle(classified);
            }
        }
        
        //compiling
        System.out.println("Compiling");
        @SuppressWarnings("unchecked") // we created object so know it's safe
        JointClassifier<CharSequence> compiledClassifier
            = (JointClassifier<CharSequence>)
            AbstractExternalizable.compile(classifier);

        boolean storeCategories = true;
        JointClassifierEvaluator<CharSequence> evaluator
            = new JointClassifierEvaluator<CharSequence>(compiledClassifier,
                                                         CATEGORIES,
                                                         storeCategories);
        
        //novo c�digo
        	PostDAO postDAO = new PostDAO();
        	
        	List<org.catfeed.Post> listaPosts = postDAO.recuperarPostsSemCategoria();

        	for(org.catfeed.Post post: listaPosts)
        	{
        	
	        	String text = post.getMensagem();
	        	Classification classification = new Classification(CATEGORIES[0]);
	        	Classified<CharSequence> classified = new Classified<CharSequence>(text, classification);
	        	evaluator.handle(classified);
	        	JointClassification jc = compiledClassifier.classify(text);
	        	String bestCategory = jc.bestCategory();
	        	System.out.println("Mensagem: " +text);
	        	System.out.println("Melhor categoria: " +bestCategory);
	        	System.out.println(jc.toString());
        	
        	}
    }
}
