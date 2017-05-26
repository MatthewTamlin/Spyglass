package com.matthewtamlin.spyglass.processors.annotation_utils.annotation_mirror_util;

import com.matthewtamlin.java_compiler_utilities.element_supplier.ElementId;

public class Data {
	public static final String SPECIFIED_VALUE = "specified value";

	@ElementId("get annotation mirror: without annotation")
	public String field1;

	@ElementId("get annotation mirror: with annotation")
	@AnnotationWithValues
	public String field2;

	@ElementId("get annotation value ignoring defaults: no value")
	@AnnotationWithValues()
	public Object field3;

	@ElementId("get annotation value ignoring defaults: with value")
	@AnnotationWithValues(value = SPECIFIED_VALUE)
	public Object field4;

	@ElementId("get annotation value with defaults: no value")
	@AnnotationWithValues()
	public Object field5;

	@ElementId("get annotation value with defaults: with value")
	@AnnotationWithValues(value = SPECIFIED_VALUE)
	public Object field6;
}