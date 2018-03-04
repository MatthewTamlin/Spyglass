package com.matthewtamlin.spyglass.integrationtests.reactivex.completable;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import com.matthewtamlin.spyglass.integrationtests.framework.ReceivedValue;

public class CompletableTestTargetBase extends View {
  private ReceivedValue<Boolean> receivedValue = ReceivedValue.none();
  
  public CompletableTestTargetBase(final Context context) {
    super(context);
  }
  
  public CompletableTestTargetBase(final Context context, @Nullable final AttributeSet attrs) {
    super(context, attrs);
  }
  
  public CompletableTestTargetBase(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  
  @RequiresApi(21)
  @TargetApi(21)
  public CompletableTestTargetBase(
      final Context context,
      @Nullable final AttributeSet attrs,
      final int defStyleAttr,
      final int defStyleRes) {
    
    super(context, attrs, defStyleAttr, defStyleRes);
  }
  
  public ReceivedValue<Boolean> getReceivedValue() {
    return receivedValue;
  }
  
  protected void setReceivedValue(final ReceivedValue<Boolean> receivedValue) {
    this.receivedValue = receivedValue;
  }
}