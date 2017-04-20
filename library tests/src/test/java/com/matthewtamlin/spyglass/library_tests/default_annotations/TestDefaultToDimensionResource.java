package com.matthewtamlin.spyglass.library_tests.util.default_annotations;

import android.support.test.runner.AndroidJUnit4;

import com.matthewtamlin.spyglass.library.default_adapters.DefaultToDimensionResourceAdapter;
import com.matthewtamlin.spyglass.library.default_annotations.DefaultToDimensionResource;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.annotation.Annotation;

@RunWith(JUnit4.class)
public class TestDefaultToDimensionResource extends BaseTest {
	@Override
	public Class<? extends Annotation> getAnnotationUnderTest() {
		return DefaultToDimensionResource.class;
	}

	@Override
	public Class getExpectedAdapterClass() {
		return DefaultToDimensionResourceAdapter.class;
	}
}