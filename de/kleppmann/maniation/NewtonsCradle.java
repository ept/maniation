package de.kleppmann.maniation;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.dynamics.GeneralizedBody;
import de.kleppmann.maniation.dynamics.Constraint;
import de.kleppmann.maniation.dynamics.Cylinder;
import de.kleppmann.maniation.dynamics.InequalityConstraint;
import de.kleppmann.maniation.dynamics.InteractionList;
import de.kleppmann.maniation.dynamics.NailConstraint;
import de.kleppmann.maniation.dynamics.Simulation;
import de.kleppmann.maniation.dynamics.SimulationObject;
import de.kleppmann.maniation.dynamics.World;
import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class NewtonsCradle {
    
    private Ball[] balls;
    
    public NewtonsCradle(Simulation simulation) {
        balls = new Ball[5];
        for (int i=0; i<5; i++) balls[i] = new Ball(simulation.getWorld(), i);
        for (int i=1; i<5; i++) {
            balls[i].previous = balls[i-1];
            balls[i].collPrev = new BallDistance(balls[i-1], balls[i], 0.02);
        }
        for (int i=0; i<4; i++) {
            balls[i].next = balls[i+1];
            balls[i].collNext = balls[i+1].collPrev;
        }
        for (int i=0; i<5; i++) simulation.addBody(balls[i]);
    }

    
    private class Ball extends Cylinder {
        private int number;
        private Ball previous, next;
        private World world;
        private Constraint nail, collPrev, collNext;

        public Ball(World world, int number) {
            super(new Vector3D(0, 0, 1), 0.01, 0.02, 0.03);
            this.number = number;
            this.world = world;
            this.nail = new NailConstraint(world, this, new Vector3D(0, 0, 0.08),
                    new Vector3D(0.02*(number - 2), 0, 0.1));
        }
        
        int getNumber() {
            return number;
        }
        
        @Override
        protected Vector3D getInitialPosition() {
            return getInitialOrientation().transform(new Vector3D(0, 0, -0.08)).add(
                    new Vector3D(0.02*(number - 2), 0.0, 0.1));
        }

        @Override
        protected Quaternion getInitialOrientation() {
            if (number == 0) return Quaternion.fromYRotation(Math.PI/8.0);
            return new Quaternion();
        }

        @Override
        public void interaction(SimulationObject.State ownState, SimulationObject.State partnerState,
                InteractionList result, boolean allowReverse) {
            super.interaction(ownState, partnerState, result, allowReverse);
            SimulationObject partner = partnerState.getOwner();
            if (partner == world) result.addInteraction(nail);
            if (partner == previous) result.addInteraction(collPrev);
            if (partner == next) result.addInteraction(collNext);
        }
    }
    
    
    private class BallDistance implements InequalityConstraint {

        private Ball ball1, ball2;
        private Body.State ball1State, ball2State;
        private double dist;
        private double x1, y1, z1, xd1, yd1, zd1, x2, y2, z2, xd2, yd2, zd2;
        
        public BallDistance(Ball ball1, Ball ball2, double dist) {
            this.ball1 = ball1; this.ball2 = ball2; this.dist = dist;
        }
        
        public int getDimension() {
            return 1;
        }

        public void setStateMapping(Map<GeneralizedBody, GeneralizedBody.State> states) {
            try {
                ball1State = (Body.State) states.get(ball1);
                ball2State = (Body.State) states.get(ball2);
            } catch (ClassCastException e) {
                throw new IllegalStateException(e);
            }
            x1 = ball1State.getCoMPosition().getComponent(0);
            y1 = ball1State.getCoMPosition().getComponent(1);
            z1 = ball1State.getCoMPosition().getComponent(2);
            x2 = ball2State.getCoMPosition().getComponent(0);
            y2 = ball2State.getCoMPosition().getComponent(1);
            z2 = ball2State.getCoMPosition().getComponent(2);
            xd1 = ball1State.getCoMVelocity().getComponent(0);
            yd1 = ball1State.getCoMVelocity().getComponent(1);
            zd1 = ball1State.getCoMVelocity().getComponent(2);
            xd2 = ball2State.getCoMVelocity().getComponent(0);
            yd2 = ball2State.getCoMVelocity().getComponent(1);
            zd2 = ball2State.getCoMVelocity().getComponent(2);
        }

        public Map<GeneralizedBody, Matrix> getJacobian() {
            double[][] m1 = {{2*(x1-x2), 2*(y1-y2), 2*(z1-z2)}};
            double[][] m2 = {{2*(x2-x1), 2*(y2-y1), 2*(z2-z1)}};
            Map<GeneralizedBody, Matrix> map = new java.util.HashMap<GeneralizedBody,Matrix>();
            map.put(ball1, new MatrixImpl(m1));
            map.put(ball2, new MatrixImpl(m2));
            return map;
        }

        public Map<GeneralizedBody, Matrix> getJacobianDot() {
            double[][] m1 = {{2*(xd1-xd2), 2*(yd1-yd2), 2*(zd1-zd2)}};
            double[][] m2 = {{2*(xd2-xd1), 2*(yd2-yd1), 2*(zd2-zd1)}};
            Map<GeneralizedBody, Matrix> map = new java.util.HashMap<GeneralizedBody,Matrix>();
            map.put(ball1, new MatrixImpl(m1));
            map.put(ball2, new MatrixImpl(m2));
            return map;
        }

        public Vector getPenalty() {
            double[] v = {(x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1) - dist*dist};
            return new VectorImpl(v);
        }

        public Vector getPenaltyDot() {
            double[] v = {2*(x2-x1)*(xd2-xd1) + 2*(y2-y1)*(yd2-yd1) + 2*(z2-z1)*(zd2-zd1)};
            return new VectorImpl(v);
        }

        public List<SimulationObject> getObjects() {
            List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
            result.add(ball1);
            result.add(ball2);
            return result;
        }

        public boolean isInequality() {
            return true;
        }

        public Map<Body, Vector3D> setToZero() {
            Map<Body, Vector3D> result = new java.util.HashMap<Body, Vector3D>();
            Vector3D d = ball2State.getCoMPosition().subtract(ball1State.getCoMPosition());
            d = d.mult(0.5 * (d.magnitude() - dist) / d.magnitude());
            result.put(ball1, ball1State.getCoMPosition().add(d));
            result.put(ball2, ball2State.getCoMPosition().subtract(d));
            return result;
        }

        public double getElasticity() {
            return Simulation.ELASTICITY;
        }
    }
}
