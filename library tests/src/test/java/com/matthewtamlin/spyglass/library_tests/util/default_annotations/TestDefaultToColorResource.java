package com.matthewtamlin.spyglass.library_tests.util.default_annotations;

import com.matthewtamlin.spyglass.library.default_adapters.DefaultToColorResourceAdapter;
import com.matthewtamlin.spyglass.library.default_annotations.DefaultToColorResource;

import java.lang.annotation.Annotation;

public class TestDefaultToColorResource extends BaseTest {
	@Override
	public Class<? extends Annotation> getAnnotationUnderTest() {
		return DefaultToColorResource.class;
	}

	@Override
	public Class getExpectedAdapterClass() {
		return DefaultToColorResourceAdapter.class;
	}
}