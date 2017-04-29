package com.matthewtamlin.spyglass.processors.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.matthewtamlin.java_utilities.checkers.NullChecker.checkNotNull;
import static javax.lang.model.element.ElementKind.CLASS;

public class Grouper {
	public static Map<TypeElement, Set<Element>> groupByEnclosingClass(
			final Set<Element> elements) {

		final Map<TypeElement, Set<Element>> map = new HashMap<>();

		for (final Element e : elements) {
			final TypeElement parent = getEnclosingClass(e);

			if (!map.containsKey(parent)) {
				map.put(parent, new HashSet<Element>());
			}

			map.get(parent).add(e);
		}

		return map;
	}

	/**
	 * Recursively searches up the element model until the enclosing class is found or the top of
	 * the model is reached. The enclosing class may be an anonymous inner class, a nested class,
	 * or a top level class. If the element has no enclosing class, null is returned.
	 *
	 * @param element
	 * 		the element to find the enclosing class of, not null
	 *
	 * @return the enclosing class as an element, null if none exists
	 *
	 * @throws IllegalArgumentException
	 * 		if {@code element} is null
	 */
	private static TypeElement getEnclosingClass(final Element element) {
		checkNotNull(element, "Argument \'element\' cannot be null.");

		final Element enclosingElement = element.getEnclosingElement();

		if (enclosingElement == null) {
			// No enclosing class exists
			return null;
		} else if (enclosingElement.getKind() == CLASS) {
			// The enclosing class has been found
			return (TypeElement) enclosingElement;
		} else {
			// Keep searching up
			return getEnclosingClass(enclosingElement);
		}
	}

	private Grouper() {
		throw new RuntimeException("Util class. Do not instantiate.");
	}
}