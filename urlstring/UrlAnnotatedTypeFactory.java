import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;


import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedIntersectionType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedTypeVariable;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedWildcardType;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ImplicitsTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.PropagationTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.util.GraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;
import qual.*;

public class UrlAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    protected final AnnotationMirror URL, UBOTTOM, PARTIALURL, POLYU;
    protected final AnnotationMirror SCHEME, HOST, PATH;
    protected final AnnotationMirror QUERY, FRAGMENT, EMPTY;

    private final ExecutableElement partialUrlValue;
	private final ExecutableElement hostStringValue;
	private final ExecutableElement pathStringValue;
	private final ExecutableElement queryStringValue;
	private final ExecutableElement fragmentStringValue;

    protected final ExecutableElement UrlParameters;
    protected final ExecutableElement ExistScheme;


    public UrlAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);

		//get the method of value() in PartialUrl.class
        partialUrlValue =
                TreeUtils.getMethod(
                        qual.PartialUrl.class.getName(),
                        "value",
                        0,
                        processingEnv);

		//creating annotation for each annotation				
        URL = AnnotationBuilder.fromClass(elements, U.class);
        UBOTTOM = AnnotationBuilder.fromClass(elements, UBottom.class);
        PARTIALURL = AnnotationBuilder.fromClass(elements, PartialUrl.class);
        POLYU = AnnotationBuilder.fromClass(elements, PolyUrl.class);
        SCHEME = AnnotationBuilder.fromClass(elements, Scheme.class);
        HOST = AnnotationBuilder.fromClass(elements, Host.class);
        PATH = AnnotationBuilder.fromClass(elements, Path.class);
        QUERY = AnnotationBuilder.fromClass(elements, Query.class);
        FRAGMENT = AnnotationBuilder.fromClass(elements, Fragment.class);
		EMPTY = AnnotationBuilder.fromClass(elements, EmptyString.class);

		//get the method of parameters() in U.class
        UrlParameters =
                TreeUtils.getMethod(
                        qual.U.class.getName(),
                        "parameters",
                        0,
                        processingEnv);
        
		//get the method of hasScheme() in U.class
		ExistScheme =
                TreeUtils.getMethod(
                        qual.U.class.getName(),
                        "hasScheme",
                        0,
                        processingEnv);
	
		hostStringValue =
			TreeUtils.getMethod(
					qual.Host.class.getName(),
					"value",
					0,
					processingEnv);
					
		pathStringValue =
			TreeUtils.getMethod(
					qual.Path.class.getName(),
					"value",
					0,
					processingEnv);
					
		queryStringValue =
			TreeUtils.getMethod(
					qual.Query.class.getName(),
					"value",
					0,
					processingEnv);
					
		fragmentStringValue =
			TreeUtils.getMethod(
					qual.Fragment.class.getName(),
					"value",
					0,
					processingEnv);
						

        addAliasedAnnotation(qual.PolyUrl.class, POLYU);

        this.postInit();
    }

    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return getBundledTypeQualifiersWithPolyAll(
                U.class, PartialUrl.class,
                UBottom.class, PossiblyUrl.class);
    }

    public CFTransfer createFlowTransferFunction(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        return new CFTransfer((CFAnalysis)analysis);
    }


    /*package-scope*/ AnnotationMirror createAnnotationURL(boolean scheme, int param) {
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, U.class);
        builder.setValue("hasScheme", scheme);
        builder.setValue("parameters", param);
        return builder.build();
    }
	
	
    /*
    createAnnotaionScheme
    createAnnotationHost
    createAnnotationPath
    createAnnotationPN
    createAnnotationQuery
    createAnnotationFragment

    //Just .addAnnotation(Scheme.class);
    AnnotationMirror createAnnotationScheme() {
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, Scheme.class);
        return builder.build();
    }
		*/
		
    AnnotationMirror createAnnotationHost(String host) {
			AnnotationBuilder builder = new AnnotationBuilder(processingEnv, Host.class);
			builder.setValue("value", host);
			return builder.build();
    }

	
    AnnotationMirror createAnnotationPath(String path) {
			AnnotationBuilder builder = new AnnotationBuilder(processingEnv, Path.class);
			builder.setValue("value", path);
			return builder.build();
    }
	AnnotationMirror createAnnotationQuery(String query) {
			AnnotationBuilder builder = new AnnotationBuilder(processingEnv, Query.class);
			builder.setValue("value", query);
			return builder.build();
    }
	AnnotationMirror createAnnotationFragment(String frag) {
			AnnotationBuilder builder = new AnnotationBuilder(processingEnv, Fragment.class);
			builder.setValue("value", frag);
			return builder.build();
    }


    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new UQualifierHierarchy(factory, UBOTTOM);
    }

    /**
     * A custom qualifier hierarchy
     */
    private final class UQualifierHierarchy extends GraphQualifierHierarchy {

        public UQualifierHierarchy(MultiGraphFactory f, AnnotationMirror bottom) {
            super(f, bottom);
        }

        @Override
		//Procedurally defining the qualifier hierarchy
        public boolean isSubtype(AnnotationMirror subAnno, AnnotationMirror superAnno) {
            if (AnnotationUtils.areSameIgnoringValues(subAnno, URL)
                    && AnnotationUtils.areSameIgnoringValues(superAnno, URL)) {
                boolean rhasScheme = getHasSchemeValue(subAnno);
				boolean lhasScheme = getHasSchemeValue(superAnno);
				boolean sameOnScheme = (rhasScheme==lhasScheme);
				if(sameOnScheme){
					int rParamValue = getParameterValue(subAnno);
					int lParamValue = getParameterValue(superAnno);
					return rParamValue <= lParamValue; //U without path is a subtype of U with path; same structure if extended to include query and fragment
				}
				return lhasScheme; //if only one has scheme, the superAnno should be the one with scheme
			}
 			if (AnnotationUtils.areSameIgnoringValues(subAnno, URL)
                    && AnnotationUtils.areSameIgnoringValues(superAnno, HOST)) {
                boolean rhasScheme = getHasSchemeValue(subAnno);

				int rParamValue = getParameterValue(subAnno);
				boolean endAtHost = (rParamValue == 0);
				return (!rhasScheme && endAtHost); 
			} 
            if (AnnotationUtils.areSameIgnoringValues(superAnno, URL)) {
                superAnno = URL;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, URL)) {
                subAnno = URL;
            }
		    if (AnnotationUtils.areSameIgnoringValues(superAnno, PARTIALURL)) {
                superAnno = PARTIALURL;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, PARTIALURL)) {
                subAnno = PARTIALURL;
            }
            if (AnnotationUtils.areSameIgnoringValues(superAnno, SCHEME)) {
                superAnno = SCHEME;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, SCHEME)) {
                subAnno = SCHEME;
            }
            if (AnnotationUtils.areSameIgnoringValues(superAnno, HOST)) {
                superAnno = HOST;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, HOST)) {
                subAnno = HOST;
            }
            if (AnnotationUtils.areSameIgnoringValues(superAnno, PATH)) {
                superAnno = PATH;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, PATH)) {
                subAnno = PATH;
            }
            if (AnnotationUtils.areSameIgnoringValues(superAnno, QUERY)) {
                superAnno = QUERY;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, QUERY)) {
                subAnno = QUERY;
            }
            if (AnnotationUtils.areSameIgnoringValues(superAnno, FRAGMENT)) {
                superAnno = FRAGMENT;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, FRAGMENT)) {
                subAnno = FRAGMENT;
            }
			if (AnnotationUtils.areSameIgnoringValues(superAnno, EMPTY)) {
                superAnno = EMPTY;
            }
            if (AnnotationUtils.areSameIgnoringValues(subAnno, EMPTY)) {
                subAnno = EMPTY;
            }
            return super.isSubtype(subAnno, superAnno);
        }

		private boolean getHasSchemeValue(AnnotationMirror anno) {
        return  (Boolean)
                AnnotationUtils.getElementValuesWithDefaults(anno)
                        .get(ExistScheme)
                        .getValue();
		}
        /** Gets the value out of a U.class annotation. */
        private int getParameterValue(AnnotationMirror anno) {
            return (Integer)
                    AnnotationUtils.getElementValuesWithDefaults(anno)
                            .get(UrlParameters)
                            .getValue();
        }	
    }

    /**
     * Returns the parameters value of the given annotation of U.class 
	 * Future:need to consider if cannot get paramValue: when supress warning
     */
    public int getParameterCount(AnnotationMirror anno) {
        AnnotationValue parameterValue =
                AnnotationUtils.getElementValuesWithDefaults(anno).get(UrlParameters);

        return (Integer)parameterValue.getValue();
    }

	 /**
     * Input: an U.class annotation of a variable
     * return true if anno has annotation U that has path
     */	 
    public boolean ifUrlHasPath(AnnotationMirror anno) {
        int paramValue = getParameterCount(anno);
        if(paramValue >=1){
            return true;
        }else{
            return false;
        }
    }

    public boolean ifUrlHasQuery(AnnotationMirror anno) {
        int paramValue = getParameterCount(anno);
        if(paramValue >=2){
            return true;
        }else{
            return false;
        }
    }

    public boolean ifUrlHasFragment(AnnotationMirror anno) {
        int paramValue = getParameterCount(anno);
        if(paramValue ==3){
            return true;
        }else{
            return false;
        }
    }
	 
	 /**
     * Input: an U.class annotation of a variable
     * return true if anno has annotation U has scheme
     */	 
    public boolean getHasSchemeValue(AnnotationMirror anno) {
        return  (Boolean)
                AnnotationUtils.getElementValuesWithDefaults(anno)
                        .get(ExistScheme)
                        .getValue();
    }

	
    public boolean containScheme(String s) {
        String regex = "^https?://(.)*";
		boolean matches = Pattern.matches(regex, s);
        return matches;
    }

    public boolean containHost(String s){
        String regex = "(.)*([0-9a-z_-]{1,62}\\.){1,126}(com|edu|gov|info|int|jobs|net|org|uk|ca|de|jp|fr|au|us|ru|ch|it|nl|se|no|es|mil)(.)*";
        boolean matches = Pattern.matches(regex, s);

        return matches;
    }
	 
    public boolean containPath(String s){
        String regex = 	"(.)*(([/]{1}[\\w-.]*)+)(.)*";
        boolean matches = Pattern.matches(regex, s);
        return matches;
    }

	 //future methods
	 /**
     public boolean containPort(String s) {
     }
     */
	 

	 public boolean containQuery(String s) {
         String regex = "(.)*([?]{1}[\\w-]+=\\S+(?:&[\\w-]+=\\S+)*)(.)*";
         boolean matches = Pattern.matches(regex, s);
         return matches;
	 }

	 public boolean containFragment(String s) {
         String regex = "(.)*^([#]{1}[\\\\w-]+)$";
         boolean matches = Pattern.matches(regex, s);
         return matches;
	 }


    public int getParameterCount(String s) {
        if(containFragment(s)) {
            return 3;
        }else if(containQuery(s)) {
            return 2;
        }else if(containPath(s)) {
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * Check if inputString is a possible valid URL with http protocol
     */
    @SuppressWarnings("purity") 
    @Pure
    public boolean isURL(String s) {
        String regex = "^(https?://)?([0-9a-z_-]{1,62}\\.){1,126}(com|edu|gov|info|int|jobs|net|org)(([/]{1}[\\w-.]*)+)?([?]{1}[\\w-]+=\\S+(?:&[\\w-]+=\\S+)*)?([#]{1}[\\w-]+)?$";
		boolean matches = Pattern.matches(regex, s);
        return matches;
    }

	 /**
     * Check if inputString is a http scheme
     */
    public boolean isScheme(String s) {
        String regex = "^https?://$";
        boolean matches = Pattern.matches(regex, s);
        return matches;
    }
	
    /**
     * Check if inputString is a valid host
     */
    public boolean isHost(String s) {
        String regex = "^([0-9a-z_-]{1,62}\\.){1,126}(com|edu|gov|info|int|jobs|net|org|uk|ca|de|jp|fr|au|us|ru|ch|it|nl|se|no|es|mil)$";
        boolean matches = Pattern.matches(regex, s);
        String tldReg = "\\.(com|edu|gov|info|int|jobs|net|org|uk|ca|de|jp|fr|au|us|ru|ch|it|nl|se|no|es|mil)";
        Pattern tldP = Pattern.compile(tldReg);
        Matcher tldM = tldP.matcher(s);
        int count = 0;
        while (tldM.find()) {
            count++;
        }
        boolean oneTLD = (count ==1);
        return (matches && oneTLD);
    }

	/**
     * Check if inputString is a valid path
     */
    public boolean isPath(String s) {
        String regex = 	"^(([/]{1}[\\w-.]*)+)?$";
        boolean matches = Pattern.matches(regex, s);

        return matches;
    }
	
	/**future methods
	public boolean isPortNumber(String s) {
        String regex = "^(:\\d{1,5})?$";
        boolean matches = Pattern.matches(regex, s);

        return matches;
    }
     */
	public boolean isQuery(String s) {
        String regex = 	"^([?]{1}[\\w-]+=\\S+(?:&[\\w-]+=\\S+)*)?$";
        boolean matches = Pattern.matches(regex, s);

        return matches;
    }
	
    public boolean isFragment(String s) {
        String regex = 	"^([#]{1}[\\w-]+)?$";
        boolean matches = Pattern.matches(regex, s);

        return matches;
    }

	
    @Override
    public TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(
                new ImplicitsTreeAnnotator(this),
                new UTreeAnnotator(this),
                new UPropagationAnnotator(this));
    }

    private static class UPropagationAnnotator extends PropagationTreeAnnotator {

        public UPropagationAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        public Void visitBinary(BinaryTree node, AnnotatedTypeMirror type) {
            return null;
        }
    }

    private class UTreeAnnotator extends TreeAnnotator {

        public UTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        /**
         * Case 1: Judge types of the string
         */
        @Override
        public Void visitLiteral(LiteralTree tree, AnnotatedTypeMirror type) {
            if (!type.isAnnotatedInHierarchy(URL)) {
                String str = null;
                if (tree.getKind() == Tree.Kind.STRING_LITERAL) {
                    str = (String) tree.getValue();
                } else if (tree.getKind() == Tree.Kind.CHAR_LITERAL) {
                    str = Character.toString((Character) tree.getValue());
                }
                if (str != null) {
					if(str.equals("")){
						type.addAnnotation(EmptyString.class);
					}else if(isHost(str)) {
                        type.addAnnotation(createAnnotationHost(str));
                    }else if (isURL(str)) {
                        boolean containScheme = containScheme(str);
                        int paramCount = getParameterCount(str);
						if(containScheme || paramCount>0){
							type.addAnnotation(createAnnotationURL(containScheme, paramCount));
						}
                    }else if(isScheme(str)) {
                        type.addAnnotation(Scheme.class);
                    }else if(isPath(str)) {
                        type.addAnnotation(createAnnotationPath(str));
                    }else if(isQuery(str)) {
                        type.addAnnotation(createAnnotationQuery(str));
                    }else if(isFragment(str)){
					    type.addAnnotation(createAnnotationFragment(str));
                    }else{
                        type.addAnnotation(createPartialUrl(str));
					}
                }
            }
            return super.visitLiteral(tree, type);
        }

        /**
         * Case 2: judge result of string A + string B
         * concatenation .
         */
        @Override
        public Void visitBinary(BinaryTree tree, AnnotatedTypeMirror type) {
            if (!type.isAnnotatedInHierarchy(URL) && TreeUtils.isStringConcatenation(tree)) {
                AnnotatedTypeMirror L = getAnnotatedType(tree.getLeftOperand());
                AnnotatedTypeMirror R = getAnnotatedType(tree.getRightOperand());
				
				boolean isLURLhasScheme, isRURLhasScheme;
                boolean isLURLhasPath, isRURLhasPath;
                boolean isLURLhasQuery, isRURLhasQuery;
                boolean isLURLhasFragment, isRURLhasFragment;

                boolean isLScheme = L.hasAnnotation(Scheme.class);
                boolean isLHost = L.hasAnnotation(Host.class);
                boolean isRHost = R.hasAnnotation(Host.class);
                boolean isLPath = L.hasAnnotation(Path.class);
                boolean isRPath = R.hasAnnotation(Path.class);
                boolean isLQuery = L.hasAnnotation(Query.class);
                boolean isRQuery = R.hasAnnotation(Query.class);
                boolean isRFragment = R.hasAnnotation(Fragment.class);

                boolean isLURL = L.hasAnnotation(U.class);
                boolean isRURL = R.hasAnnotation(U.class);
                boolean isLPartial = L.hasAnnotation(PartialUrl.class);
                boolean isRPartial = R.hasAnnotation(PartialUrl.class);
				
				String lValue, rValue, concat;

                if(isLScheme&& isRURL) {
					AnnotationMirror RAnno = R.getAnnotation(U.class);
					isRURLhasScheme = getHasSchemeValue(RAnno);
					if(!isRURLhasScheme){
						isRURLhasPath = ifUrlHasPath(RAnno);
                        isRURLhasQuery = ifUrlHasQuery(RAnno);
                        isRURLhasFragment = ifUrlHasFragment(RAnno);
						type.removeAnnotationInHierarchy(URL);
						if (isRURLhasFragment) {
                            type.addAnnotation(createAnnotationURL(true, 3));
                        }else if(isRURLhasQuery) {
                            type.addAnnotation(createAnnotationURL(true, 2));
                        }else if(isRURLhasPath){
						    type.addAnnotation(createAnnotationURL(true,1));
						} else {
							type.addAnnotation(createAnnotationURL(true, 0));
						}
					}
                }else if(isLHost&& isRPath){
                    type.removeAnnotationInHierarchy(URL);
                    type.addAnnotation(createAnnotationURL(false, 1));
				}else if(isLHost&& isRQuery){
					type.removeAnnotationInHierarchy(URL);
					type.addAnnotation(createAnnotationURL(false, 2));
				}else if(isLHost&& isRFragment){
					type.removeAnnotationInHierarchy(URL);
					type.addAnnotation(createAnnotationURL(false, 3));
				}else if(isLHost&& isRPartial){
					lValue = getHostValue(L);
                    rValue = getPartialUrlValue(R);
                    concat = lValue + rValue;
					if(isURL(concat)){
                    type.removeAnnotationInHierarchy(URL);
                        if(containFragment(concat)) {
                            type.addAnnotation(createAnnotationURL(false, 3));
                        }else if(containQuery(concat)) {
                            type.addAnnotation(createAnnotationURL(false, 2));
                        }else if(containPath(concat)){
							type.addAnnotation(createAnnotationURL(false, 1));
						}else{
							type.addAnnotation(createAnnotationURL(false, 0));
						}
					}
                }else if(isLURL){
					AnnotationMirror LAnno = L.getAnnotation(U.class);
					isLURLhasScheme = getHasSchemeValue(LAnno);
                    isLURLhasQuery = ifUrlHasQuery(LAnno);
                    isLURLhasFragment = ifUrlHasFragment(LAnno);
                    if(isRPath && !isLURLhasQuery && !isLURLhasFragment) {
                        type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationURL(isLURLhasScheme, 1));
                    }else if(isRQuery && !isLURLhasQuery && !isLURLhasFragment){
                        type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationURL(isLURLhasScheme, 2));
                    }else if(isRFragment && !isLURLhasFragment ){
                        type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationURL(isLURLhasScheme, 3));
                    }
                }else if(isLScheme && isRHost) {
                    type.removeAnnotationInHierarchy(URL);
                    type.addAnnotation(createAnnotationURL(true, 0));
                }else if(isLHost && isRQuery){
                    type.removeAnnotationInHierarchy(URL);
                    type.addAnnotation(createAnnotationURL(false, 2));
                }else if(isLHost && isRFragment){
                    type.removeAnnotationInHierarchy(URL);
                    type.addAnnotation(createAnnotationURL(false, 3));
                }else if(isLPartial && isRHost){
                    lValue = getPartialUrlValue(L);
					rValue = getHostValue(R);
					concat = lValue+rValue;
                    if(isHost(concat)){
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationHost(concat));
                    }
                }else if(isLPath && isRPath){
					type.removeAnnotationInHierarchy(URL);
                    type.addAnnotation(Path.class);
                }else if(isLPath && isRQuery){
					lValue = getPathValue(L);
                    rValue = getQueryValue(R);
                    concat = lValue + rValue;
					type.removeAnnotationInHierarchy(URL);
                    type.addAnnotation(createPartialUrl(concat));
				}else if(isLPath && isRFragment){
					lValue = getPathValue(L);
                    rValue = getFragmentValue(R);
                    concat = lValue + rValue;
					type.removeAnnotationInHierarchy(URL);
					type.addAnnotation(createPartialUrl(concat));
				}else if(isLQuery && isRFragment){
					lValue = getQueryValue(L);
                    rValue = getFragmentValue(R);
                    concat = lValue + rValue;
					type.removeAnnotationInHierarchy(URL);
					type.addAnnotation(createPartialUrl(concat));
				}else if(isLPartial && isRPartial){
                    lValue = getPartialUrlValue(L);
                    rValue = getPartialUrlValue(R);
                    concat = lValue + rValue;
                    if(isHost(concat)){
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(Host.class);
                    }else if(isURL(concat)){
                        boolean concatHasScheme = containScheme(concat);
                        if(containFragment(concat)) {
                            type.addAnnotation(createAnnotationURL(concatHasScheme, 3));
                        }else if(containQuery(concat)) {
                            type.addAnnotation(createAnnotationURL(concatHasScheme, 2));
                        }else if(containPath(concat)){
							type.addAnnotation(createAnnotationURL(concatHasScheme, 1));
						}else{
							type.addAnnotation(createAnnotationURL(concatHasScheme, 0));
						}
                    }else if(isScheme(concat)){
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(Scheme.class);
                    }else if(isPath(concat)){
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationPath(concat));
                    }else if(isQuery(concat)){
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationQuery(concat));
                    }else if(isFragment(concat)){
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createAnnotationFragment(concat));
                    }else{
						type.removeAnnotationInHierarchy(URL);
                        type.addAnnotation(createPartialUrl(concat));
                    }
                }
            }
            return null; 
        }
		
		@Override
        public Void visitCompoundAssignment(CompoundAssignmentTree node, AnnotatedTypeMirror type) {
            return null; 
        }
		
		/** Returns a new annotation of PartialUrl with the given string. */
		private AnnotationMirror createPartialUrl(String partial) {
			AnnotationBuilder builder = new AnnotationBuilder(processingEnv, PartialUrl.class);
			builder.setValue("value", partial);
			return builder.build();
		}

		/** Returns the value of a PartialUrl annotation. */
		private String getPartialUrlValue(AnnotatedTypeMirror type) {
			return (String)
					AnnotationUtils.getElementValuesWithDefaults(
							type.getAnnotation(PartialUrl.class))
							.get(partialUrlValue)
							.getValue();
		}
	
		/** Returns the value of a Host annotation. */
		private String getHostValue(AnnotatedTypeMirror type) {
			return (String)
					AnnotationUtils.getElementValuesWithDefaults(
							type.getAnnotation(Host.class))
							.get(hostStringValue)
							.getValue();
		}
		
		/** Returns the value of a Path annotation. */
		private String getPathValue(AnnotatedTypeMirror type) {
			return (String)
					AnnotationUtils.getElementValuesWithDefaults(
							type.getAnnotation(Path.class))
							.get(pathStringValue)
							.getValue();
		}
		/** Returns the value of a Query annotation. */
		private String getQueryValue(AnnotatedTypeMirror type) {
			return (String)
					AnnotationUtils.getElementValuesWithDefaults(
							type.getAnnotation(Query.class))
							.get(queryStringValue)
							.getValue();
		}
		
		private String getFragmentValue(AnnotatedTypeMirror type) {
			return (String)
					AnnotationUtils.getElementValuesWithDefaults(
							type.getAnnotation(Fragment.class))
							.get(fragmentStringValue)
							.getValue();
		}
	}
}
