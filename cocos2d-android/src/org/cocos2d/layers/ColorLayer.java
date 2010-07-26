package org.cocos2d.layers;

import org.cocos2d.config.ccConfig;
import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ColorLayer extends Layer implements CCNode.CocosNodeRGBA, CCNode.CocosNodeSize {
    protected ccColor3B color_;
    protected int opacity_;

    private FloatBuffer squareVertices_;
    private ByteBuffer squareColors_;

    public static ColorLayer node(ccColor4B color) {
        return new ColorLayer(color, CCDirector.sharedDirector().winSize().width, CCDirector.sharedDirector().winSize().height);
    }

    public static ColorLayer node(ccColor4B color, float w, float h) {
        return new ColorLayer(color, w, h);
    }

    protected ColorLayer(ccColor4B color) {
        this(color, CCDirector.sharedDirector().winSize().width, CCDirector.sharedDirector().winSize().height);
    }

    protected ColorLayer(ccColor4B color, float w, float h) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        squareVertices_ = vbb.asFloatBuffer();

        squareColors_ = ByteBuffer.allocateDirect(4 * 4);

        color_ = new ccColor3B(color.r, color.g, color.b);
        opacity_ = color.a;

        for (int i = 0; i < (4 * 2); i++) {
            squareVertices_.put(i, 0);
        }
        squareVertices_.position(0);

        updateColor();
        setContentSize(CGSize.make(w, h));
    }

    private void updateColor() {
        for (int i = 0; i < squareColors_.limit(); i++) {
            switch (i % 4) {
                case 0:
                    squareColors_.put(i, (byte) color_.r);
                    break;
                case 1:
                    squareColors_.put(i, (byte) color_.g);
                    break;
                case 2:
                    squareColors_.put(i, (byte) color_.b);
                    break;
                default:
                    squareColors_.put(i, (byte) opacity_);
            }
            squareColors_.position(0);
        }
    }

    @Override
    public void draw(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);


        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices_);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, squareColors_);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        if (opacity_ != 255)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);


        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if (opacity_ != 255)
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        // Clear the vertex and color arrays
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

    }


    public ccColor3B getColor() {
        return new ccColor3B(color_.r, color_.g, color_.b);
    }

    // Color Protocol
    public void setColor(ccColor3B color) {
        color_.r = color.r;
        color_.g = color.g;
        color_.b = color.b;
        updateColor();
    }

    // Opacity Protocol

    public void setOpacity(int o) {
        opacity_ = o;
        updateColor();
    }

    public int getOpacity() {
        return opacity_;
    }

    // Size protocol

    @Override
    public float getWidth() {
        return squareVertices_.get(2);
    }

    @Override
    public float getHeight() {
        return squareVertices_.get(5);
    }

    @Override
    public void setContentSize(CGSize size) {

        // Layer default ctor calls setContentSize priot to nio alloc
        if (squareVertices_ != null) {
            squareVertices_.put(2, size.width);
            squareVertices_.put(5, size.height);
            squareVertices_.put(6, size.width);
            squareVertices_.put(7, size.height);
        }

        super.setContentSize(size);
    }

    public void changeWidthAndHeight(float w, float h) {
        setContentSize(CGSize.make(w, h));
    }

    public void changeWidth(float w) {
        setContentSize(CGSize.make(w, getHeight()));
    }

    public void changeHeight(float h) {
        setContentSize(CGSize.make(getWidth(), h));
    }

}

