package com.matthewtamlin.spyglass.processors.code_generation;

import com.matthewtamlin.spyglass.processors.annotation_utils.CallHandlerAnnotationUtil;
import com.matthewtamlin.spyglass.processors.annotation_utils.ValueHandlerAnnotationUtil;
import com.matthewtamlin.spyglass.processors.grouper.TypeGrouper;
import com.matthewtamlin.spyglass.processors.util.TypeUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static com.matthewtamlin.java_utilities.checkers.NullChecker.checkEachElementIsNotNull;
import static com.matthewtamlin.java_utilities.checkers.NullChecker.checkNotNull;
import static com.matthewtamlin.spyglass.processors.annotation_utils.CallHandlerAnnotationUtil.hasCallHandlerAnnotation;
import static com.matthewtamlin.spyglass.processors.annotation_utils.DefaultAnnotationUtil.hasDefaultAnnotation;
import static com.matthewtamlin.spyglass.processors.annotation_utils.UseAnnotationUtil.hasUseAnnotation;
import static com.matthewtamlin.spyglass.processors.annotation_utils.ValueHandlerAnnotationUtil.hasValueHandlerAnnotation;
import static javax.lang.model.element.Modifier.PUBLIC;

public class CallerGenerator {
	private final CallerComponentGenerator callerComponentGenerator;

	private final InvocationLiteralGenerator invocationLiteralGenerator;

	public CallerGenerator(final Elements elementUtil) {
		checkNotNull(elementUtil, "Argument \'elementUtil\' cannot be null.");

		callerComponentGenerator = new CallerComponentGenerator(elementUtil);
		invocationLiteralGenerator = new InvocationLiteralGenerator(elementUtil);
	}

	public TypeSpec generateCaller(final ExecutableElement method) {
		checkNotNull(method, "Argument \'method\' cannot be null.");

		if (hasCallHandlerAnnotation(method)) {
			return generateCallerForCallHandlerCase(method);

		} else if (hasValueHandlerAnnotation(method)) {
			return hasDefaultAnnotation(method) ?
					generateCallerForValueHandlerWithDefaultCase(method) :
					generateCallerForValueHandlerWithoutDefaultCase(method);

		} else {
			throw new IllegalArgumentException("Argument \'method\' has neither a value handler annotation nor a call" +
					" handler annotation.");
		}
	}

	private TypeSpec generateCallerForCallHandlerCase(final ExecutableElement e) {
		/* General structure
		 *
		 * 	new Caller {
		 * 		public void callMethod(T target, Context context, TypedArray attrs) {
		 * 			if (shouldCallMethod(attrs) {
		 * 				// Variable implementation, generated by invocationLiteralGenerator
		 * 				target.<generated method call>
		 * 			}
		 * 		}
		 *
		 *		public boolean shouldCallMethod(TypedArray attrs) {
		 *			// Variable implementation, generated by callerComponentGenerator
		 *			<generated boolean with return>
		 *		}
		 * 	}
		 */

		final TypeName targetType = TypeName.get(TypeUtil.getEnclosingType(e).asType());
		final AnnotationMirror callHandlerAnno = CallHandlerAnnotationUtil.getCallHandlerAnnotationMirror(e);

		final MethodSpec shouldCallMethod = callerComponentGenerator.generateShouldCallMethodSpecFor(callHandlerAnno);

		final MethodSpec callMethod = getEmptyCallMethodSpec(targetType)
				.addCode(CodeBlock
						.builder()
						.beginControlFlow("if ($N(attrs))", shouldCallMethod)
						.addStatement("$L.$L", "target", invocationLiteralGenerator.generateLiteralWithoutExtraArg(e))
						.endControlFlow()
						.build())
				.build();

		return getEmptyAnonymousCallerSpec(targetType)
				.addMethod(callMethod)
				.addMethod(shouldCallMethod)
				.build();
	}

	private TypeSpec generateCallerForValueHandlerWithoutDefaultCase(final ExecutableElement e) {
		/* General structure
		 *
		 * 	new Caller {
		 *		public void callMethod(T target, Context context, TypedArray attrs) {		 *
		 *			if (valueIsAvailable(attrs) {
		 *				V value = (V) getValue(attrs);
		 *
		 *				// Variable implementation, generated by invocationLiteralGenerator
		 *				target.<generated method call using "value">
		 *			}
		 *		}
		 *
		 *		public void valueIsAvailable(TypedArray attrs) {
		 *			// Variable implementation, generated by callerComponentGenerator
		 *			<generated boolean with return>
		 *		}
		 *
		 *		public V getValue(TypedArray attrs) {
		 *			// Variable implementation, generated by callerComponentGenerator
		 *			<generated code with return>
		 *		}
		 * }
		 */

		final TypeName targetType = TypeName.get(TypeUtil.getEnclosingType(e).asType());
		final TypeName nonUseParamType = getTypeNameOfNonUseParameter(e);
		final AnnotationMirror valueHandlerAnno = ValueHandlerAnnotationUtil.getValueHandlerAnnotationMirror(e);

		final MethodSpec valueIsAvailable = callerComponentGenerator.generateValueIsAvailableSpecFor(valueHandlerAnno);
		final MethodSpec getValue = callerComponentGenerator.generateGetValueSpecFor(valueHandlerAnno);

		final MethodSpec callMethod = getEmptyCallMethodSpec(targetType)
				.addCode(CodeBlock
						.builder()
						.beginControlFlow("if ($N(attrs))", valueIsAvailable)
						.addStatement("$T value = ($T) $N(attrs)", nonUseParamType, nonUseParamType, getValue)
						.addStatement(
								"$L.$L",
								"target",
								invocationLiteralGenerator.generateLiteralWithExtraArg(e, "value"))
						.endControlFlow()
						.build())
				.build();

		return getEmptyAnonymousCallerSpec(targetType)
				.addMethod(callMethod)
				.addMethod(valueIsAvailable)
				.addMethod(getValue)
				.build();
	}

	private TypeSpec generateCallerForValueHandlerWithDefaultCase(final ExecutableElement e) {
		/* General structure
		 *
		 * 	new Caller {
		 *		public void callMethod(T target, Context context, TypedArray attrs) {
		 *			V value = valueIsAvailable(attrs) ? (V) getValue(attrs) : (V) getDefault(attrs);
		 *
		 *			// Variable implementation, generated by invocationLiteralGenerator
		 *			target.<generated method call using "value">
		 *		}
		 *
		 *		public void valueIsAvailable(TypedArray attrs) {
		 *			// Variable implementation, generated by callerComponentGenerator
		 *			<generated boolean with return>
		 *		}
		 *
		 *		public V getValue(TypedArray attrs) {
		 *			// Variable implementation, generated by callerComponentGenerator
		 *			<generated code with return>
		 *		}
		 *
		 *		public V getDefault(Context context, TypedArray attrs) {
		 *			// Variable implementation, generated by callerComponentGenerator
		 *			<generated code with return>
		 *		}
		 *	}
		 */

		final TypeName targetType = TypeName.get(TypeUtil.getEnclosingType(e).asType());
		final TypeName nonUseParamType = getTypeNameOfNonUseParameter(e);
		final AnnotationMirror valueHandler = ValueHandlerAnnotationUtil.getValueHandlerAnnotationMirror(e);

		final MethodSpec valueIsAvailable = callerComponentGenerator.generateValueIsAvailableSpecFor(valueHandler);
		final MethodSpec getValue = callerComponentGenerator.generateGetValueSpecFor(valueHandler);
		final MethodSpec getDefault = callerComponentGenerator.generateGetDefaultValueSpecFor(valueHandler);

		final MethodSpec callMethod = getEmptyCallMethodSpec(targetType)
				.addCode(CodeBlock
						.builder()
						.addStatement(
								"$1T value = $2N(attrs) ? ($1T) $3N(attrs) : ($1T) $4N(context, attrs)",
								nonUseParamType,
								valueIsAvailable,
								getValue,
								getDefault)
						.addStatement(
								"target.$L",
								invocationLiteralGenerator.generateLiteralWithExtraArg(e, "value"))
						.build())
				.build();

		return getEmptyAnonymousCallerSpec(targetType)
				.addMethod(callMethod)
				.addMethod(valueIsAvailable)
				.addMethod(getValue)
				.addMethod(getDefault)
				.build();
	}

	private TypeSpec.Builder getEmptyAnonymousCallerSpec(final TypeName targetType) {
		final ClassName genericCaller = ClassName.get(CallerDef.PACKAGE, CallerDef.INTERFACE_NAME);
		final TypeName specificCaller = ParameterizedTypeName.get(genericCaller, targetType);

		return TypeSpec
				.anonymousClassBuilder("")
				.addSuperinterface(specificCaller);
	}

	private MethodSpec.Builder getEmptyCallMethodSpec(final TypeName targetType) {
		return MethodSpec
				.methodBuilder(CallerDef.METHOD_NAME)
				.returns(void.class)
				.addModifiers(PUBLIC)
				.addParameter(targetType, "target")
				.addParameter(AndroidClassNames.CONTEXT, "context")
				.addParameter(AndroidClassNames.TYPED_ARRAY, "attrs");
	}

	private TypeName getTypeNameOfNonUseParameter(final ExecutableElement e) {
		for (final VariableElement parameter : e.getParameters()) {
			if (!hasUseAnnotation(parameter)) {
				return ClassName.get(parameter.asType());
			}
		}

		throw new RuntimeException("No non-use argument found.");
	}
}