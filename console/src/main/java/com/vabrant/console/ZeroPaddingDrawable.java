
package com.vabrant.console;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

public class ZeroPaddingDrawable implements TransformDrawable {

	private Drawable drawable;

	public ZeroPaddingDrawable (Drawable drawable) {
		this(drawable, false);
	}

	public ZeroPaddingDrawable (Drawable drawable, boolean copy) {
		if (copy) {
			try {
				Class<? extends Drawable> c = drawable.getClass();
				drawable = c.getDeclaredConstructor(c).newInstance(drawable);
			} catch (Exception e) {
				throw new ConsoleRuntimeException(e);
			}
		} else {
			this.drawable = drawable;
		}
	}

	@Override
	public void draw (Batch batch, float x, float y, float width, float height) {
		drawable.draw(batch, x, y, width, height);
	}

	@Override
	public void draw (Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
		float scaleY, float rotation) {
		if (drawable instanceof TransformDrawable) {
			TransformDrawable d = (TransformDrawable)drawable;
			d.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
		}
	}

	@Override
	public float getLeftWidth () {
		return 0;
	}

	@Override
	public void setLeftWidth (float leftWidth) {

	}

	@Override
	public float getRightWidth () {
		return 0;
	}

	@Override
	public void setRightWidth (float rightWidth) {

	}

	@Override
	public float getTopHeight () {
		return 0;
	}

	@Override
	public void setTopHeight (float topHeight) {

	}

	@Override
	public float getBottomHeight () {
		return 0;
	}

	@Override
	public void setBottomHeight (float bottomHeight) {

	}

	@Override
	public float getMinWidth () {
		return 0;
	}

	@Override
	public void setMinWidth (float minWidth) {

	}

	@Override
	public float getMinHeight () {
		return 0;
	}

	@Override
	public void setMinHeight (float minHeight) {

	}
}
