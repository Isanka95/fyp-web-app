package com.teamir.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.*;
import utils.models.ReferenceModel;
import utils.models.Triple;

public class NLPUtils {

    public AnnotationPipeline pipeline;

    public NLPUtils() {

    }

    public NLPUtils(String annotatorList) {
        Properties props = new Properties();
        props.setProperty("annotators", annotatorList);
        this.pipeline = new StanfordCoreNLP(props);
    }

    public NLPUtils(Properties properties) {
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public NLPUtils(Properties properties, String host, int port) {
        this.pipeline = new StanfordCoreNLPClient(properties, host, port);
    }

    public Annotation annotate(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        return annotation;
    }

    public ArrayList<String> getProNouns(Annotation annotation){
        ArrayList<String> nouns = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // proper nouns and pronouns are considered
                if ("PRP".equals(pos) || "PRP$".equals(pos)) {
                    nouns.add(word.toLowerCase());
                }
            }
        }

        return nouns;
    }

    public ArrayList<String> getNouns(Annotation annotation) {
        ArrayList<String> nouns = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // proper nouns and pronouns are considered
                if ("NN".equals(pos) || "NNS".equals(pos) || "NNP".equals(pos) ||
                        "NNPS".equals(pos) || "PRP".equals(pos) || "PRP$".equals(pos)) {
                    nouns.add(word.toLowerCase());
                }
            }
        }

        return nouns;
    }

    public ArrayList<String> getVerbs(Annotation annotation) {
        ArrayList<String> verbs = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // "be" verbs are considered
                if ("VB".equals(pos) || "VBD".equals(pos) || "VBG".equals(pos) ||
                        "VBN".equals(pos) || "VBP".equals(pos) || "VBZ".equals(pos)) {
                    verbs.add(word.toLowerCase());
                }
            }
        }

        return verbs;
    }

    public ArrayList<String> getLemmaVerbs(Annotation annotation) {
        ArrayList<String> verbs = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // "be" verbs are considered
                if ("VB".equals(pos) || "VBD".equals(pos) || "VBG".equals(pos) ||
                        "VBN".equals(pos) || "VBP".equals(pos) || "VBZ".equals(pos)) {
                    verbs.add(lemma);
                }
            }
        }

        return verbs;
    }

    public ArrayList<String> getVerbsWithOutBe(Annotation annotation) {
        ArrayList<String> verbsWithOutBe = new ArrayList<>();

        ArrayList<String> allVerbs = getVerbs(annotation);

        // TODO: 4/5/18 change variable location
        ArrayList<String> present = new ArrayList<>(Arrays.asList(
                "be", "is", "are", "am", "being", "has", "have", "do", "does"));
        ArrayList<String> past = new ArrayList<>(Arrays.asList(
                "was", "were", "been", "would", "should", "did"));
        ArrayList<String> future = new ArrayList<>(Arrays.asList("will", "shall"));

        for (String verb : allVerbs) {
            String verbLowerCase = verb.toLowerCase();
            if (present.contains(verbLowerCase) || past.contains(verbLowerCase) || future.contains(verbLowerCase)) {
                continue;
            } else {
                verbsWithOutBe.add(verbLowerCase);
            }
        }

        return verbsWithOutBe;
    }

    public ArrayList<String> getLemmaVerbsWithOutBe(Annotation annotation) {
        ArrayList<String> verbsWithOutBe = new ArrayList<>();

        ArrayList<String> allVerbs = getLemmaVerbs(annotation);

        // TODO: 4/5/18 change variable location
        ArrayList<String> present = new ArrayList<>(Arrays.asList(
                "be", "is", "are", "am", "being", "has", "have", "do", "does"));
        ArrayList<String> past = new ArrayList<>(Arrays.asList(
                "was", "were", "been", "would", "should", "did"));
        ArrayList<String> future = new ArrayList<>(Arrays.asList("will", "shall"));

        for (String verb : allVerbs) {
            String verbLowerCase = verb.toLowerCase();
            if (present.contains(verbLowerCase) || past.contains(verbLowerCase) || future.contains(verbLowerCase)) {
                continue;
            } else {
                verbsWithOutBe.add(verbLowerCase);
            }
        }

        return verbsWithOutBe;
    }

    public ArrayList<String> getAdjectives(Annotation annotation) {
        ArrayList<String> adjectives = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                if ("JJ".equals(pos) || "JJR".equals(pos) || "JJS".equals(pos)) {
                    adjectives.add(word.toLowerCase());
                }
            }
        }

        return adjectives;
    }

    public ArrayList<String> getSubjects(Annotation annotation) {
        ArrayList<String> subjects = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SemanticGraph dependencies = sentence
                    .get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
            //			IndexedWord root = dependencies.getFirstRoot();
            //			System.out.printf("root(ROOT-0, %s-%d)%n", root.word(), root.index());
            for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
                //				System.out.printf ("%s(%s-%d, %s-%d)%n", edge.getRelation().toString(), edge.getGovernor().word(), edge.getGovernor().index(), edge.getDependent().word(), edge.getDependent().index());
                String relation = edge.getRelation().toString();
                // nominal subject, passive nominal subject, clausal subject, clausal passive subject
                // controlling subjects all considered
                if ("nsubj".equals(relation) || "nsubjpass".equals(relation) ||
                        "nsubj:xsubj".equals(relation) || "nsubjpass:xsubj".equals(relation) ||
                        "csubj".equals(relation) || "csubjpass".equals(relation) ||
                        "csubj:xsubj".equals(relation) || "csubjpass:xsubj".equals(relation)) {
                    subjects.add(edge.getDependent().word().toLowerCase());
                }
            }
        }

        return subjects;
    }

    public ArrayList<String> getObjects(Annotation annotation) {
        ArrayList<String> objects = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SemanticGraph dependencies = sentence
                    .get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
            for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
                String relation = edge.getRelation().toString();
                // direct object, indirect object considered
                // prepositional object not considered
                if ("dobj".equals(relation) || "iobj".equals(relation)) {
                    objects.add(edge.getDependent().word().toLowerCase());
                }
            }
        }

        return objects;
    }

    public ArrayList<String> getEntities(Annotation annotation) {
        ArrayList<String> entities = new ArrayList<>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the NER tag of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if ("PERSON".equals(ne) || "ORGANIZATION".equals(ne) || "LOCATION".equals(ne) ||
                        "MONEY".equals(ne) || "PERCENT".equals(ne) || "DATE".equals(ne) ||
                        "TIME".equals(ne)) {
                    entities.add(word.toLowerCase());
                }
            }
        }

        return entities;
    }

    /**
     * @param annotation annotated string - targetSentence + " " + sourceSentence
     * @return arraylist containing two changed sentences sourceSentence-0 , targetSentence-1
     */
    public ArrayList<String> replaceCoreferencesNoHis(Annotation annotation) {
        try {
            ArrayList<ArrayList<String>> sentenceWords = new ArrayList<>();
            ArrayList<ArrayList<String>> replaceSentenceWords = new ArrayList<>();
            ArrayList<ArrayList<Integer>> unReplacableIndices = new ArrayList<>();
            ArrayList<ReferenceModel> referencesSentence1 = new ArrayList<>();
            ArrayList<ReferenceModel> referencesSentence2 = new ArrayList<>();
            ArrayList<ArrayList<ReferenceModel>> referencedSentences = new ArrayList<>();

            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {

                ArrayList<String> tokenizedWords = new ArrayList<>();
                ArrayList<Integer> unreplacableWordIndices = new ArrayList<>();
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    tokenizedWords.add(word);
                    // proper nouns and pronouns are considered
                    if ("PRP$".equals(pos)) {
                        unreplacableWordIndices.add(token.index());
                    }
                }
                System.out.println("senWords :" + tokenizedWords.toString());
                sentenceWords.add(tokenizedWords);
                unReplacableIndices.add(unreplacableWordIndices);
            }
            for (CorefChain chain : annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
                String represent = chain.getRepresentativeMention().mentionSpan;
                represent = represent.replaceAll("\\$", "&");
                System.out.println("represent: " + represent);
                for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {

                    int sentNo = mention.sentNum;
                    int startIndex = mention.startIndex;
                    int endIndex = mention.endIndex;
                    String word = mention.mentionSpan;

                    //adding details to reference model
                    ReferenceModel referenceModel = new ReferenceModel();
                    referenceModel.setRepresent(represent);
                    referenceModel.setReplacableWord(word);
                    referenceModel.setStartIndex(startIndex);
                    referenceModel.setEndIndex(endIndex);

                    if (sentNo == 1) {
                        referencesSentence1.add(referenceModel);
                    } else if (sentNo == 2) {
                        referencesSentence2.add(referenceModel);
                    }
                }
            }

            referencedSentences.add(referencesSentence1);
            referencedSentences.add(referencesSentence2);

            for (int j = 0; j < sentenceWords.size(); j++) {
                ArrayList<String> currentSentence = sentenceWords.get(j);
                ArrayList<String> replaceSingleSentence = new ArrayList<>();

                for (int i = 0; i < currentSentence.size(); i++) {
                    boolean wordAdded = false;
                    int m = i + 1;

                    for (ReferenceModel referenceModel : referencedSentences.get(j)) {

                        if (referenceModel.getStartIndex() == m) {
                            if (!unReplacableIndices.get(j).contains(referenceModel.getStartIndex())) {
                                int additionalLenght = referenceModel.getEndIndex() - referenceModel.getStartIndex();
                                i += additionalLenght - 1;
                                replaceSingleSentence.add(referenceModel.getRepresent());
                                wordAdded = true;
                                break;
                            }
                        }
                    }
                    if (!wordAdded) {
                        replaceSingleSentence.add(currentSentence.get(i));
                    }
                }
                replaceSentenceWords.add(replaceSingleSentence);

            }

            if (replaceSentenceWords.size() == 0) {
                return null;
            }

            String targetSentence = this.referenceReplacedSentence(replaceSentenceWords.get(0));
            String sourceSentence = "";
            //String sourceSentence = this.referenceReplacedSentence(replaceSentenceWords.get(1));

            return new ArrayList<>(Arrays.asList(sourceSentence, targetSentence));
        }catch (Exception e){
            return null;
        }
    }

    public AnnotationPipeline getPipeline() {
        return pipeline;
    }

	/* coreference access
	for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
		System.out.println("---");
		System.out.println("mentions");
		for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
			System.out.println("\t" + m);
		}
	}
	Map<Integer, CorefChain> graph = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	for(Map.Entry<Integer, CorefChain> entry : graph) {
		CorefChain c =   entry.getValue();
		println "ClusterId: " + entry.getKey();
		CorefMention cm = c.getRepresentativeMention();
		println "Representative Mention: " + aText.subSequence(cm.startIndex, cm.endIndex);
		List<CorefMention> cms = c.getCorefMentions();
		println  "Mentions:  ";
		cms.each { it ->
				print aText.subSequence(it.startIndex, it.endIndex) + "|";
		}
	}
	*/

    public String replaceCoreferences(Annotation annotation) {
        try {
            ArrayList<ArrayList<String>> sentenceWords = new ArrayList<>();
            ArrayList<ArrayList<String>> replaceSentenceWords = new ArrayList<>();
            ArrayList<ArrayList<Integer>> unReplacableIndices = new ArrayList<>();
            ArrayList<ReferenceModel> referencesSentence1 = new ArrayList<>();
            ArrayList<ReferenceModel> referencesSentence2 = new ArrayList<>();
            ArrayList<ArrayList<ReferenceModel>> referencedSentences = new ArrayList<>();

            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                ArrayList<String> tokenizedWords = new ArrayList<>();
                ArrayList<Integer> unreplacableWordIndices = new ArrayList<>();
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    tokenizedWords.add(word);
                    // proper nouns and pronouns are considered
                    if ("PRP$".equals(pos)) {
                        unreplacableWordIndices.add(token.index());
                    }
                }
                sentenceWords.add(tokenizedWords);
                unReplacableIndices.add(unreplacableWordIndices);
            }

            for (CorefChain chain : annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
                String represent = chain.getRepresentativeMention().mentionSpan;
                represent = represent.replaceAll("\\$", "&");
                System.out.println("represent: " + represent);
                for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {

                    int sentNo = mention.sentNum;
                    int startIndex = mention.startIndex;
                    int endIndex = mention.endIndex;
                    String word = mention.mentionSpan;

                    //adding details to reference model
                    ReferenceModel referenceModel = new ReferenceModel();
                    referenceModel.setRepresent(represent);
                    referenceModel.setReplacableWord(word);
                    referenceModel.setStartIndex(startIndex);
                    referenceModel.setEndIndex(endIndex);

                    if (sentNo == 1) {
                        referencesSentence1.add(referenceModel);
                    } else if (sentNo == 2) {
                        referencesSentence2.add(referenceModel);
                    }
                }
            }

            referencedSentences.add(referencesSentence1);
            referencedSentences.add(referencesSentence2);

            for (int j = 0; j < sentenceWords.size(); j++) {
                ArrayList<String> currentSentence = sentenceWords.get(j);
                ArrayList<String> replaceSingleSentence = new ArrayList<String>();

                for (int i = 0; i < currentSentence.size(); i++) {
                    boolean wordAdded = false;
                    int m = i + 1;

                    for (ReferenceModel referenceModel : referencedSentences.get(j)) {

                        if (referenceModel.getStartIndex() == m) {
                            int additionalLength = referenceModel.getEndIndex() - referenceModel.getStartIndex();
                            i += additionalLength - 1;
                            if (!unReplacableIndices.get(j).contains(referenceModel.getStartIndex())) {
                                replaceSingleSentence.add(referenceModel.getRepresent());
                            } else {
                                replaceSingleSentence.add(" " + referenceModel.getRepresent() + "'s");
                                //replaceSingleSentence.add(referenceModel.getRepresent());
                            }
                            wordAdded = true;
                            break;
                        }
                    }
                    if (!wordAdded) {
                        replaceSingleSentence.add(currentSentence.get(i));
                    }
                }
                replaceSentenceWords.add(replaceSingleSentence);

            }

            if (replaceSentenceWords.size() == 0) {
                return null;
            }
            String targetSentence = this.referenceReplacedSentence(replaceSentenceWords.get(0)).trim();

            //String sourceSentence = this.referenceReplacedSentence(replaceSentenceWords.get(1)).trim();

           // return new ArrayList<>(Arrays.asList(sourceSentence, targetSentence));
            return targetSentence;
        }catch (Exception e){
            return null;
        }
    }

    private String referenceReplacedSentence(ArrayList<String> tokenList) {
        Pattern p = Pattern.compile("(\\w+)");
        String referencedSentence = "";

        for (int i = 0; i < tokenList.size(); i++) {
            String token = tokenList.get(i);
            if (i == 0) {
                referencedSentence = referencedSentence + token;
            } else {
                String[] words = token.split(" ");
                Matcher m1 = p.matcher(words[0]);
                if (m1.matches()) {
                    referencedSentence = referencedSentence + " " + token;
                } else {
                    referencedSentence = referencedSentence + token;
                }
            }
        }

        return referencedSentence;
    }

    public Tree constituentParse(Annotation annotation) {

        // get tree
        Tree tree =
                annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
                        .get(TreeCoreAnnotations.TreeAnnotation.class);
        return tree;
       /* System.out.println(tree);
        Set<Constituent> treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());
        for (Constituent constituent : treeConstituents) {
            if (constituent.label() != null &&
                    (constituent.label().toString().equals("S") )) {
                System.err.println("found constituent: " + constituent.toString());
                System.err.println(tree.getLeaves().subList(constituent.start(), constituent.end() + 1));
            }
        }*/
    }

    public ArrayList<SemanticGraph> getSemanticDependencyGraph(Annotation annotation) {
        ArrayList<SemanticGraph> graphs = new ArrayList<SemanticGraph>();
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SemanticGraph dependencies = sentence
                    .get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
            graphs.add(dependencies);
        }
        return graphs;
    }

    public ArrayList<Triple> getTriples(Annotation annotation) {
        ArrayList<Triple> triples = new ArrayList<Triple>();

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            Collection<RelationTriple> stanfordTriples =
                    sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            Triple triple = new Triple();
            for (RelationTriple stanfordTriple : stanfordTriples) {
                triple.subject = stanfordTriple.subjectGloss();
                triple.subjectLemma = stanfordTriple.subjectLemmaGloss();
                triple.object = stanfordTriple.objectGloss();
                triple.objectLemma = stanfordTriple.objectLemmaGloss();
                triple.relation = stanfordTriple.relationGloss();
                triple.relationLemma = stanfordTriple.relationLemmaGloss();
                triple.confidence = stanfordTriple.confidence;

                triples.add(triple);
            }
        }

        return triples;
    }

    public static Tree parseTree(Annotation ann) {
        List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            return tree;
        }
        return null;
    }

    public Tree parseTreeVP(Annotation ann) {
        List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            return tree;
        }
        return null;
    }

    //returned parse tree processed in this method
    public List<String> processParseTree(String text) {

        //to split from the pattern SBAR IN
        //String[] phraseList = text.split("\\(SBAR \\(IN [a-z]+\\)");
        String[] phraseList = text.split("\\(SBAR \\(IN [a-z]+\\)|\\(SBAR \\(WHNP \\(WDT [a-z]+\\)");
        List<String> output=new ArrayList<String>();
        int count = 0;
        for (String phrase : phraseList) {

            //parantheses and parse tree nodes (Uppercase) are removed
            phrase = phrase.replaceAll("\\(", "").replaceAll("\\)", "").
                    replaceAll("[A-Z$]+ ", "").replaceAll(" [\\.]", " ").
                    replaceAll(" [\\,]", "").trim() + ".";
            if (phrase.length()>1) {
                phraseList[count] = phrase;
                output.add(phrase);
            }


        }
        return output;
    }

    public List<String> getPhrases(String sub) {
        ArrayList<String> phrases = new ArrayList<>();
        Properties prop = new Properties();
        prop.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse,sentiment");
        StanfordCoreNLP pipeline2 = new StanfordCoreNLP(prop);
        Annotation annotation2 = new Annotation(sub);
        pipeline2.annotate(annotation2);
        String subject = "";
        List<CoreMap> sentences = annotation2.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SemanticGraph dependencies = sentence
                    .get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
            for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
                String relation = edge.getRelation().toString();
                // nominal subject, passive nominal subject, clausal subject, clausal passive subject
                // controlling subjects all considered
                if ("nsubj".equals(relation) || "nsubjpass".equals(relation)) {
                    subject = edge.getDependent().word().toLowerCase();
                }
            }
        }
        String np = extractNounPhrase(parseTreeVP(annotation2));
        String vp = extractVerbPhrase(parseTreeVP(annotation2));

        vp = vp.replaceAll("\\(", "").replaceAll("\\)", "").
                replaceAll("[A-Z$]+ ", "").replaceAll(" [\\.]", " ").
                replaceAll(" [\\,]", "").trim() + ".";
        np = np.replaceAll("\\(", "").replaceAll("\\)", "").
                replaceAll("[A-Z$]+ ", "").replaceAll(" [\\.]", " ").
                replaceAll(" [\\,]", "").trim() + ".";
        phrases.add(np);
        phrases.add(vp);
        //phrases.add(subject);
        return phrases;
    }

    public static String extractVerbPhrase(Tree tree){
        List<Tree> subTreeList = tree.subTreeList();
        for (Tree subTree : subTreeList) {
            if(subTree.label().value().equals("S")){
                List<Tree> list1 = subTree.getLeaves();
                StringBuilder vp = new StringBuilder();
                String subTree_S = subTree.toString();
                ArrayList<Character> openedBracs = new ArrayList<>();
                ArrayList<Character> closedBracs = new ArrayList<>();
                for (int i =0; i < subTree_S.length(); i++){
                    char c = subTree_S.charAt(i);
                    if (Character.valueOf(c).equals('(')){
                        openedBracs.add(c);
                    }
                    if (Character.valueOf(c).equals(')')){
                        closedBracs.add(c);
                    }

                    if (openedBracs.size() == (closedBracs.size()+2) && Character.valueOf(subTree_S.charAt(i)).equals('(') && Character.valueOf(subTree_S.charAt(i+1)).equals('V') && Character.valueOf(subTree_S.charAt(i+2)).equals('P')){
                        for (int j = i; j < subTree_S.length(); j++) {
                            vp.append(subTree_S.charAt(j));
                        }
                        break;
                    }
                }
                return vp.toString();
            }
        }
        return null;

    }

    public static String extractNounPhrase(Tree tree){
            List<Tree> subTreeList = tree.subTreeList();
            for (Tree subTree : subTreeList) {
                if(subTree.label().value().equals("S")){
                    String np = "";
                    String subTree_S = subTree.toString();
                    ArrayList<Character> openedBracs = new ArrayList<>();
                    ArrayList<Character> closedBracs = new ArrayList<>();
                    for (int i =0; i < subTree_S.length(); i++){
                        char c = subTree_S.charAt(i);
                        if (Character.valueOf(c).equals('(')){
                            openedBracs.add(c);
                        }
                        if (Character.valueOf(c).equals(')')){
                            closedBracs.add(c);
                        }
                        if (openedBracs.size() == (closedBracs.size()+2) && Character.valueOf(subTree_S.charAt(i)).equals('(') && Character.valueOf(subTree_S.charAt(i+1)).equals('N') && Character.valueOf(subTree_S.charAt(i+2)).equals('P')){
                            ArrayList<Character> openedBracs1 = new ArrayList<>();
                            ArrayList<Character> closedBracs1 = new ArrayList<>();
                            for (int j = i; j < subTree_S.length(); j++) {
                                np = np+(subTree_S.charAt(j));
                                if (Character.valueOf(subTree_S.charAt(j)).equals('(')){
                                    openedBracs1.add(subTree_S.charAt(j));
                                }
                                if (Character.valueOf(subTree_S.charAt(j)).equals(')')){
                                    closedBracs1.add(subTree_S.charAt(j));
                                }
                                if (openedBracs1.size() == (closedBracs1.size())){
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    np = np.replaceAll("\\(", "").replaceAll("\\)", "").
                            replaceAll("[A-Z$]+ ", "").replaceAll(" [\\.]", " ").
                            replaceAll(" [\\,]", "").trim();
                    return np;
                }
            }
            return null;
        }
    }


//         String vp= extractVerbPrase(parseTree(annotation2));
//         return vp;
//     }

//     public static String extractVerbPrase(Tree tree){
//         List<String> list=new ArrayList<String>();
//         List<Tree> subTreeList = tree.subTreeList();
//         for (Tree tree1 : subTreeList) {
//             System.out.println(tree1);
//             if(tree1.label().value().equals("VP")){
//                 // System.out.println(tree1);
//                 List<Tree> list1=tree1.getLeaves();
//                 String s ="";
//                 for(Tree l: list1){
//                     s= s+l.toString()+" ";
//                 }
//                 return s;


//             }

//         }
//        return null;

//     }

// }

