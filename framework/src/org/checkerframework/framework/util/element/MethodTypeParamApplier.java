package org.checkerframework.framework.util.element;

import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedTypeVariable;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TargetType;
import static com.sun.tools.javac.code.TargetType.*;
import org.checkerframework.javacutil.ErrorReporter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Applies the annotations present for a method type parameter onto an AnnotatedTypeVariable.
 */
public class MethodTypeParamApplier extends TypeParamElementAnnotationApplier {


    public static void apply(AnnotatedTypeVariable type, Element element, AnnotatedTypeFactory typeFactory) {
        new MethodTypeParamApplier(type, element, typeFactory).extractAndApply();
    }

    /**
     * @return True if element represents a type parameter for a method.
     */
    public static boolean accepts(final AnnotatedTypeMirror type, final Element element) {
        return element.getKind() == ElementKind.TYPE_PARAMETER &&
               element.getEnclosingElement() instanceof Symbol.MethodSymbol;
    }

    private final Symbol.MethodSymbol enclosingMethod;

    MethodTypeParamApplier(AnnotatedTypeVariable type, Element element, AnnotatedTypeFactory typeFactory) {
        super(type, element, typeFactory);

        if( !( element.getEnclosingElement() instanceof Symbol.MethodSymbol ) ) {
            ErrorReporter.errorAbort("TypeParameter not enclosed by method?  Type( " + type + " ) " +
                    "Element ( " + element + " ) ");
        }

        enclosingMethod = (Symbol.MethodSymbol) element.getEnclosingElement();
    }

    /**
     * @return TargetType.METHOD_TYPE_PARAMETER
     */
    @Override
    protected TargetType lowerBoundTarget() {
        return TargetType.METHOD_TYPE_PARAMETER;
    }

    /**
     * @return TargetType.METHOD_TYPE_PARAMETER_BOUND
     */
    @Override
    protected TargetType upperBoundTarget() {
        return TargetType.METHOD_TYPE_PARAMETER_BOUND;
    }

    /**
     * @return The index of element in the type parameter list of its enclosing method
     */
    @Override
    public int getElementIndex() {
        return enclosingMethod.getTypeParameters().indexOf(element);
    }

    /**
     * @inherit
     */
    @Override
    protected TargetType[] validTargets() {
        return new TargetType[]{
            METHOD_RETURN, METHOD_FORMAL_PARAMETER, METHOD_RECEIVER, THROWS, LOCAL_VARIABLE,
            RESOURCE_VARIABLE, EXCEPTION_PARAMETER, NEW, CAST, INSTANCEOF, METHOD_INVOCATION_TYPE_ARGUMENT,
            CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT, METHOD_REFERENCE, CONSTRUCTOR_REFERENCE,
            METHOD_REFERENCE_TYPE_ARGUMENT, CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT, };
    }

    /**
     * @return The TypeCompounds (annotations) of the declaring element
     */
    @Override
    protected Iterable<Attribute.TypeCompound> getRawTypeAttributes() {
        return enclosingMethod.getRawTypeAttributes();
    }

    @Override
    protected boolean isAccepted() {
        return accepts(type, element);
    }
}
