package com.matthewtamlin.spyglass.integration_tests.annotation_combination_tests.float_handler_combinations;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.matthewtamlin.spyglass.integration_tests.R;
import com.matthewtamlin.spyglass.integration_tests.framework.ReceivedValue;
import com.matthewtamlin.spyglass.markers.annotations.default_annotations.DefaultToFractionResource;
import com.matthewtamlin.spyglass.markers.annotations.value_handler_annotations.FloatHandler;

public class WithDefaultToFractionUsingParentFractionAndParentMultiplier extends FloatHandlerTestTargetBase {
	public static final int MULTIPLIER = 44;

	public WithDefaultToFractionUsingParentFractionAndParentMultiplier(final Context context) {
		super(context);
		init(null, 0, 0);
	}

	public WithDefaultToFractionUsingParentFractionAndParentMultiplier(final Context context,
			final AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0, 0);
	}

	public WithDefaultToFractionUsingParentFractionAndParentMultiplier(final Context context, final AttributeSet attrs,
			final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs, defStyleAttr, 0);
	}

	@TargetApi(21)
	@RequiresApi(21)
	public WithDefaultToFractionUsingParentFractionAndParentMultiplier(
			final Context context,
			final AttributeSet attrs,
			final int defStyleAttr,
			final int defStyleRes) {

		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs, defStyleAttr, defStyleRes);
	}

	@FloatHandler(attributeId = R.styleable.FloatHandlerTestTargetBase_floatHandlerAttr)
	@DefaultToFractionResource(resId = R.fraction.parent_fraction_for_testing, parentMultiplier = MULTIPLIER)
	public void handlerMethod(final float f) {
		setReceivedValue(ReceivedValue.of(f));
	}

	private void init(final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
		WithDefaultToFractionUsingParentFractionAndParentMultiplier_SpyglassCompanion
				.builder()
				.withTarget(this)
				.withContext(getContext())
				.withStyleableResource(R.styleable.FloatHandlerTestTargetBase)
				.withAttributeSet(attrs)
				.withDefaultStyleAttribute(defStyleAttr)
				.withDefaultStyleResource(defStyleRes)
				.build()
				.passDataToMethods();
	}
}