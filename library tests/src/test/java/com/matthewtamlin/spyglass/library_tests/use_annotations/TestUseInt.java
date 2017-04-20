package com.matthewtamlin.spyglass.library_tests.util.use_annotations;

import com.matthewtamlin.spyglass.library.use_adapters.UseIntAdapter;
import com.matthewtamlin.spyglass.library.use_annotations.UseInt;

import java.lang.annotation.Annotation;

@RunWith(JUnit4.class)
public class TestUseInt extends BaseTest {
	@Override
	public Class<? extends Annotation> getAnnotationUnderTest() {
		return UseInt.class;
	}

	@Override
	public Class getExpectedAdapterClass() {
		return UseIntAdapter.class;
	}
}