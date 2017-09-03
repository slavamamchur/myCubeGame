package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.cubegames.slava.cubegame.gl_render.GLRenderConsts;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public abstract class PNode extends BitmapTexturedObject {
    protected float mass;
    protected int tag;
    private RigidBody _body;
    private CollisionShape _shape;

    public PNode(Context context, GLRenderConsts.GLObjectType type, int textureResId, GLShaderProgram program,
                 float mass, int tag) {
        super(context, type, textureResId, program);

        this.mass = mass;
        this.tag = tag;
    }

    public PNode(Context context, GLRenderConsts.GLObjectType type, String mapID, GLShaderProgram program,
                 float mass, int tag) {
        super(context, type, mapID, program);

        this.mass = mass;
        this.tag = tag;
    }

    public PNode(Context context, GLRenderConsts.GLObjectType type, GLShaderProgram program, int color,
                 float mass, int tag) {
        super(context, type, program, color);

        this.mass = mass;
        this.tag = tag;
    }

    public float getMass() {
        return mass;
    }
    public RigidBody get_body() {
        return _body;
    }
    public int getTag() {
        return tag;
    }

    protected void createCollisionShape(float[] vertexes) {
        //new BoxShape(new Vector3f(0.1f, 0.1f, 0.1f));
        ObjectArrayList<Vector3f> points = new ObjectArrayList<Vector3f>();
        _shape = new ConvexHullShape(points);
        for (int i = 0; i < vertexes.length; i+=5)
        {
            Vector3f btv = new Vector3f(vertexes[i], vertexes[i + 1], vertexes[i + 2]);
            ((ConvexHullShape)_shape).addPoint(btv);
        }
    }

    public void createRigidBody() {
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(getModelMatrix())));

        float mass = this.mass;
        Vector3f bodyInertia = new Vector3f();
        _shape.calculateLocalInertia(mass, bodyInertia);

        RigidBodyConstructionInfo bodyCI = new RigidBodyConstructionInfo(mass, motionState, _shape, bodyInertia);
        bodyCI.restitution = 0.0025f;
        bodyCI.friction = 0.5f;//rnd

        _body = new RigidBody(bodyCI);
        _body.setUserPointer(this);
        _body.setLinearVelocity(new Vector3f(1f,1f,1f));
    }
}
