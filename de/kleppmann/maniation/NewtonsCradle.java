package de.kleppmann.maniation;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.dynamics.Body;
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
            setCoMPosition(new Vector3D(0.02*(number - 2), 0.0, 0.02));
            if (number < 2) {
                setLinearMomentum(new Vector3D(0.012, 0, 0));
                setAngularMomentum(new Vector3D(0, -8.75e-6, 0));
            }
            this.world = world;
            nail = new NailConstraint(world, this, new Vector3D(0, 0, 0.08),
                    new Vector3D(0.02*(number - 2), 0, 0.1));
        }
        
        int getNumber() {
            return number;
        }
        
        public void interaction(SimulationObject partner, InteractionList result, boolean allowReverse) {
            super.interaction(partner, result, allowReverse);
            if (partner == world) result.addInteraction(nail);
            if ((partner == previous) && (collPrev.getPenalty().getComponent(0) <= 0))
                result.addInteraction(collPrev);
            if ((partner == next) && (collNext.getPenalty().getComponent(0) <= 0))
                result.addInteraction(collNext);
        }

        public void applyImpulse(Vector impulse) {
            super.applyImpulse(impulse);
            /*System.out.println("body " + number + ": linear impulse (" +
                    impulse.getComponent(0) + ", " +
                    impulse.getComponent(1) + ", " +
                    impulse.getComponent(2) + "), angular impulse (" +
                    impulse.getComponent(3) + ", " +
                    impulse.getComponent(4) + ", " +
                    impulse.getComponent(5) + ")");*/
        }
    }
    
    
    private class BallDistance implements InequalityConstraint {

        private Ball ball1, ball2;
        private double dist;
        private double x1, y1, z1, xd1, yd1, zd1, x2, y2, z2, xd2, yd2, zd2;
        
        public BallDistance(Ball ball1, Ball ball2, double dist) {
            this.ball1 = ball1; this.ball2 = ball2; this.dist = dist;
        }
        
        private void update() {
            x1 = ball1.getCoMPosition().getComponent(0);
            y1 = ball1.getCoMPosition().getComponent(1);
            z1 = ball1.getCoMPosition().getComponent(2);
            x2 = ball2.getCoMPosition().getComponent(0);
            y2 = ball2.getCoMPosition().getComponent(1);
            z2 = ball2.getCoMPosition().getComponent(2);
            xd1 = ball1.getCoMVelocity().getComponent(0);
            yd1 = ball1.getCoMVelocity().getComponent(1);
            zd1 = ball1.getCoMVelocity().getComponent(2);
            xd2 = ball2.getCoMVelocity().getComponent(0);
            yd2 = ball2.getCoMVelocity().getComponent(1);
            zd2 = ball2.getCoMVelocity().getComponent(2);
        };
        
        public int getDimension() {
            return 1;
        }

        public Map<Body, Matrix> getJacobian() {
            update();
            double[][] m1 = {{2*(x1-x2), 2*(y1-y2), 2*(z1-z2)}};
            double[][] m2 = {{2*(x2-x1), 2*(y2-y1), 2*(z2-z1)}};
            Map<Body, Matrix> map = new java.util.HashMap<Body,Matrix>();
            map.put(ball1, new MatrixImpl(m1));
            map.put(ball2, new MatrixImpl(m2));
            return map;
        }

        public Map<Body, Matrix> getJacobianDot() {
            update();
            double[][] m1 = {{2*(xd1-xd2), 2*(yd1-yd2), 2*(zd1-zd2)}};
            double[][] m2 = {{2*(xd2-xd1), 2*(yd2-yd1), 2*(zd2-zd1)}};
            Map<Body, Matrix> map = new java.util.HashMap<Body,Matrix>();
            map.put(ball1, new MatrixImpl(m1));
            map.put(ball2, new MatrixImpl(m2));
            return map;
        }

        public Vector getPenalty() {
            update();
            double[] v = {(x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1) - dist*dist};
            return new VectorImpl(v);
        }

        public Vector getPenaltyDot() {
            update();
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

        /*public void setToZero() {
            Vector3D d = ball2.getCoMPosition().subtract(ball1.getCoMPosition());
            d = d.mult(0.5 * (d.magnitude() - dist) / d.magnitude());
            ball1.setCoMPosition(ball1.getCoMPosition().add(d));
            ball2.setCoMPosition(ball2.getCoMPosition().subtract(d));
        }*/
    }
}
