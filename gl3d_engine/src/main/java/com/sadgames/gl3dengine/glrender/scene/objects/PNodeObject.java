package com.sadgames.gl3dengine.glrender.scene.objects;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public abstract class PNodeObject extends BitmapTexturedObject {

    public static final int COLLISION_OBJECT = 1;
    public static final int MOVING_OBJECT = 2;

    protected float mass;
    protected int tag;
    private RigidBody _body = null;
    protected CollisionShape _shape = null;
    private Transform worldTransformOld = new Transform(new Matrix4f(new float[16]));

    public PNodeObject(SysUtilsWrapperInterface sysUtilsWrapper, GLRenderConsts.GLObjectType type, String textureResName, GLShaderProgram program,
                       float mass, int tag) {

        super(sysUtilsWrapper, type, textureResName, program);
        init(mass, tag);
    }

    public PNodeObject(SysUtilsWrapperInterface sysUtilsWrapper, GLRenderConsts.GLObjectType type, GLShaderProgram program, int color,
                       float mass, int tag) {

        super(sysUtilsWrapper, type, program, color);
        init(mass, tag);
    }

    protected void init(float mass, int tag) {
        this.mass = mass;
        this.tag = tag;
    }

    public float getMass() {
        return mass;
    }

    public RigidBody get_body() {
        return _body;
    }

    public void set_body(RigidBody _body) {
        this._body = _body;
    }

    public int getTag() {
        return tag;
    }

    public Transform getWorldTransformOld() {
        return worldTransformOld;
    }

    public Transform getWorldTransformActual() {
        Transform transform = new Transform(new Matrix4f(new float[16]));

        return get_body() == null ? /*new Transform(new Matrix4f(getModelMatrix()))*/ null : get_body().getWorldTransform(transform);
    }

    public void setWorldTransformMatrix(Transform transform) {
        worldTransformOld = transform;

        float[] mat = new float[16];
        transform.getOpenGLMatrix(mat);
        setModelMatrix(mat);
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
        Random rnd = new Random(System.currentTimeMillis());
        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(getModelMatrix())));

        float mass = this.mass;
        Vector3f bodyInertia = new Vector3f();
        _shape.calculateLocalInertia(mass, bodyInertia);

        RigidBodyConstructionInfo bodyCI = new RigidBodyConstructionInfo(mass, motionState, _shape, bodyInertia);
        bodyCI.restitution = 0.0125f + rnd.nextInt(125) * 1f / 10000f;
        bodyCI.friction = 0.5f + rnd.nextInt(4) * 1f / 10f;

        _body = new RigidBody(bodyCI);
        _body.setUserPointer(this);
    }
}
