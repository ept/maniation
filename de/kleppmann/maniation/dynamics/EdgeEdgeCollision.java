package de.kleppmann.maniation.dynamics;

import java.util.List;
import java.util.Map;

import de.kleppmann.maniation.maths.Matrix;
import de.kleppmann.maniation.maths.MatrixImpl;
import de.kleppmann.maniation.maths.Vector;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.maths.VectorImpl;

public class EdgeEdgeCollision implements InequalityConstraint {
    
    private Body.State body1State, body2State;
    private Vector3D point1, direction1, point2, direction2;
    private double a1, a2, a3, b1, b2, b3, ad1, ad2, ad3, bd1, bd2, bd3, w1, w2, w3, p1, p2, p3,
            s1, s2, s3, t1, t2, t3, u1, u2, u3, v1, v2, v3, h;
    private Matrix jacB1, jacB2, jDotB1, jDotB2;
    
    // All vectors are in world coordinates!
    public EdgeEdgeCollision(Body.State body1State, Vector3D point1, Vector3D direction1,
            Body.State body2State, Vector3D point2, Vector3D direction2) {
        this.body1State = body1State;
        this.point1 = point1;
        this.direction1 = direction1.normalize();
        this.body2State = body2State;
        this.point2 = point2;
        this.direction2 = direction2.normalize();
    }
    
    private void update() {
        a1 = body1State.getCoMPosition().getComponent(0);
        a2 = body1State.getCoMPosition().getComponent(1);
        a3 = body1State.getCoMPosition().getComponent(2);
        b1 = body2State.getCoMPosition().getComponent(0);
        b2 = body2State.getCoMPosition().getComponent(1);
        b3 = body2State.getCoMPosition().getComponent(2);
        ad1 = body1State.getCoMVelocity().getComponent(0);
        ad2 = body1State.getCoMVelocity().getComponent(1);
        ad3 = body1State.getCoMVelocity().getComponent(2);
        bd1 = body2State.getCoMVelocity().getComponent(0);
        bd2 = body2State.getCoMVelocity().getComponent(1);
        bd3 = body2State.getCoMVelocity().getComponent(2);
        w1 = body1State.getAngularVelocity().getComponent(0);
        w2 = body1State.getAngularVelocity().getComponent(1);
        w3 = body1State.getAngularVelocity().getComponent(2);
        p1 = body2State.getAngularVelocity().getComponent(0);
        p2 = body2State.getAngularVelocity().getComponent(1);
        p3 = body2State.getAngularVelocity().getComponent(2);
        s1 = point1.getComponent(0) - a1;
        s2 = point1.getComponent(1) - a2;
        s3 = point1.getComponent(2) - a3;
        t1 = point2.getComponent(0) - b1;
        t2 = point2.getComponent(1) - b2;
        t3 = point2.getComponent(2) - b3;
        u1 = direction1.getComponent(0);
        u2 = direction1.getComponent(1);
        u3 = direction1.getComponent(2);
        v1 = direction2.getComponent(0);
        v2 = direction2.getComponent(1);
        v3 = direction2.getComponent(2);
        h = 1.0/direction1.cross(direction2).magnitude();
    }
    
    private void generateJacobian() {
        update();
        double t6 = -u1*v3+u3*v1;
        double t10 = u1*v2-u2*v1;
        double t14 = b1+t1-a1-s1;
        double t15 = h*h;
        double t18 = u2*v3-u3*v2;
        double t19 = t18*u2;
        double t21 = t18*u3;
        double t27 = b2+t2-a2-s2;
        double t28 = t27*t15;
        double t29 = t6*u2;
        double t31 = t6*u3;
        double t35 = b3+t3-a3-s3;
        double t36 = t35*t15;
        double t37 = t10*u2;
        double t39 = t10*u3;
        double t43 = t14*(1.0-t15*(t19*v3-t21*v2))-t28*(t29*v3-t31*v2)-t36*(t37*v3-t39*v2);
        double t45 = t14*t15;
        double t46 = t18*u1;
        double t51 = t6*u1;
        double t56 = t10*u1;
        double t63 = -t45*(t46*v2-t19*v1)-t28*(t51*v2-t29*v1)+t35*(1.0-t15*(t56*v2-t37*v1));
        double t65 = -t43*v3+t63*v1;
        double t82 = -t45*(-t46*v3+t21*v1)+t27*(1.0-t15*(-t51*v3+t31*v1))-t36*(-t56*v3+t39*v1);
        double t84 = t43*v2-t82*v1;
        double t94 = t82*v3-t63*v2;
        double t113 = -t43*u3+t63*u1;
        double t117 = t43*u2-t82*u1;
        double t127 = t82*u3-t63*u2;
        double[][] j1 = {{
                -h*t18,
                -h*t6,
                -h*t10,
                h*(t6*s3-t10*s2+t65*u3-t84*u2),
                h*(-t18*s3+t10*s1-t94*u3+t84*u1),
                h*(t18*s2-t6*s1+t94*u2-t65*u1)}};
        double[][] j2 = {{
                h*t18,
                h*t6,
                h*t10,
                -h*(t6*t3-t10*t2-t113*v3+t117*v2),
                -h*(-t18*t3+t10*t1+t127*v3-t117*v1),
                -h*(t18*t2-t6*t1-t127*v2+t113*v1)}};
        jacB1 = new MatrixImpl(j1);
        jacB2 = new MatrixImpl(j2);
    }
    
    private void generateJacobianDot() {
        update();
        double t4 = u1*v3;
        double t5 = u3*v1;
        double t6 = -t4+t5;
        double t7 = w1*s2;
        double t8 = w2*s1;
        double t9 = t7-t8;
        double t11 = u1*v2;
        double t12 = u2*v1;
        double t13 = t11-t12;
        double t14 = w3*s1;
        double t15 = w1*s3;
        double t16 = -t14+t15;
        double t20 = p2*t3;
        double t21 = p3*t2;
        double t22 = w2*s3;
        double t23 = w3*s2;
        double t24 = bd1+t20-t21-ad1-t22+t23;
        double t26 = h*h;
        double t27 = u2*v3;
        double t28 = u3*v2;
        double t29 = t27-t28;
        double t30 = t29*u2;
        double t32 = t29*u3;
        double t34 = t30*v3-t32*v2;
        double t38 = t26*h;
        double t39 = p3*t1;
        double t40 = p1*t3;
        double t41 = bd2+t39-t40-ad2-t14+t15;
        double t42 = t38*t41;
        double t43 = t6*u2;
        double t45 = t6*u3;
        double t47 = t43*v3-t45*v2;
        double t49 = p1*t2;
        double t50 = p2*t1;
        double t51 = bd3+t49-t50-ad3-t7+t8;
        double t52 = t38*t51;
        double t53 = t13*u2;
        double t55 = t13*u3;
        double t57 = t53*v3-t55*v2;
        double t59 = h*t24*(1.0-t26*t34)-t42*t47-t52*t57;
        double t61 = t38*t24;
        double t62 = t29*u1;
        double t65 = t62*v2-t30*v1;
        double t67 = t6*u1;
        double t70 = t67*v2-t43*v1;
        double t73 = t13*u1;
        double t76 = t73*v2-t53*v1;
        double t80 = -t61*t65-t42*t70+h*t51*(1.0-t26*t76);
        double t82 = -t59*v3+t80*v1;
        double t88 = -t62*v3+t32*v1;
        double t93 = -t67*v3+t45*v1;
        double t99 = -t73*v3+t55*v1;
        double t101 = -t61*t88+h*t41*(1.0-t26*t93)-t52*t99;
        double t103 = t59*v2-t101*v1;
        double t106 = b1+t1-a1-s1;
        double t109 = w1*u2-w2*u1;
        double t113 = -w3*u1+w1*u3;
        double t119 = -p1*v2+p2*v1;
        double t120 = h*t119;
        double t125 = p3*v1-p1*v3;
        double t126 = h*t125;
        double t131 = -t34*v3+t65*v1;
        double t135 = t34*v2-t88*v1;
        double t139 = t38*u3;
        double t141 = t38*u2;
        double t143 = -t139*v2+t141*v3;
        double t144 = t143*u2;
        double t146 = t143*u3;
        double t148 = t144*v3-t146*v2;
        double t150 = t143*u1;
        double t153 = t150*v2-t144*v1;
        double t156 = p2*v3-p3*v2;
        double t158 = t148*t119+t153*t156;
        double t164 = -t150*v3+t146*v1;
        double t165 = -t156;
        double t167 = t148*t125+t164*t165;
        double t170 = v3*u3;
        double t171 = v2*u2;
        double t172 = -t170-t171;
        double t179 = t172*w1+t11*w2+t4*w3-t172*p1-t12*p2-t5*p3;
        double t180 = t38*t179;
        double t183 = t180*t27-t180*t28;
        double t187 = t180*t11-t180*t12;
        double t189 = -t183*v3+t187*v1;
        double t195 = -t180*t4+t180*t5;
        double t197 = t183*v2-t195*v1;
        double t200 = -t113;
        double t204 = -t119;
        double t206 = t26*u2;
        double t208 = t26*u3;
        double t212 = t26*u1;
        double t217 = v1*u1;
        double t218 = -t170-t217;
        double t224 = t12*w1+t218*w2+t27*w3-t11*p1-t218*p2-t28*p3;
        double t231 = -t171-t217;
        double t236 = t5*w1+t28*w2+t231*w3-t4*p1-t27*p2-t231*p3;
        double t238 = (t206*v3-t208*v2)*t179+(-t212*v3+t208*v1)*t224+(t212*v2-t206*v1)*t236;
        double t239 = t238*u2;
        double t242 = t238*u3;
        double t245 = t200*v3-t109*v2-t125*u3+t204*u2-3.0*t239*v3+3.0*t242*v2;
        double t246 = t29*t245;
        double t250 = w2*u3-w3*u2;
        double t255 = t238*u1;
        double t260 = t250*v2-t200*v1-t156*u2+t125*u1-3.0*t255*v2+3.0*t239*v1;
        double t261 = t29*t260;
        double t263 = -t246*v3+t261*v1;
        double t274 = -t250*v3+t109*v1+t156*u3-t204*u1+3.0*t255*v3-3.0*t242*v1;
        double t275 = t29*t274;
        double t277 = t246*v2-t275*v1;
        double t283 = b2+t2-a2-s2;
        double t284 = h*v1;
        double t286 = h*t165;
        double t291 = -t47*v3+t70*v1;
        double t295 = t47*v2-t93*v1;
        double t300 = t38*u1;
        double t302 = t139*v1-t300*v3;
        double t303 = t302*u2;
        double t305 = t302*u3;
        double t307 = t303*v3-t305*v2;
        double t309 = t302*u1;
        double t312 = t309*v2-t303*v1;
        double t314 = t307*t119+t312*t156;
        double t320 = -t309*v3+t305*v1;
        double t322 = t307*t125+t320*t165;
        double t325 = t38*t224;
        double t328 = t325*t27-t325*t28;
        double t332 = t325*t11-t325*t12;
        double t334 = -t328*v3+t332*v1;
        double t340 = -t325*t4+t325*t5;
        double t342 = t328*v2-t340*v1;
        double t345 = t6*t245;
        double t347 = t6*t260;
        double t349 = -t345*v3+t347*v1;
        double t352 = t6*t274;
        double t354 = t345*v2-t352*v1;
        double t360 = b3+t3-a3-s3;
        double t362 = h*t156;
        double t367 = -t57*v3+t76*v1;
        double t371 = t57*v2-t99*v1;
        double t377 = -t141*v1+t300*v2;
        double t378 = t377*u2;
        double t380 = t377*u3;
        double t382 = t378*v3-t380*v2;
        double t384 = t377*u1;
        double t387 = t384*v2-t378*v1;
        double t389 = t382*t119+t387*t156;
        double t395 = -t384*v3+t380*v1;
        double t397 = t382*t125+t395*t165;
        double t400 = t38*t236;
        double t403 = t400*t27-t400*t28;
        double t407 = t400*t11-t400*t12;
        double t409 = -t403*v3+t407*v1;
        double t415 = -t400*t4+t400*t5;
        double t417 = t403*v2-t415*v1;
        double t420 = t13*t245;
        double t422 = t13*t260;
        double t424 = -t420*v3+t422*v1;
        double t427 = t13*t274;
        double t429 = t420*v2-t427*v1;
        double t437 = t22-t23;
        double t443 = t101*v3-t80*v2;
        double t448 = h*v2;
        double t454 = t88*v3-t65*v2;
        double t455 = -t109;
        double t461 = -t125;
        double t463 = t164*t204+t153*t461;
        double t470 = t195*v3-t187*v2;
        double t477 = t275*v3-t261*v2;
        double t488 = h*t204;
        double t495 = t93*v3-t70*v2;
        double t502 = t320*t204+t312*t461;
        double t509 = t340*v3-t332*v2;
        double t516 = t352*v3-t347*v2;
        double t524 = h*t461;
        double t529 = t99*v3-t76*v2;
        double t536 = t395*t204+t387*t461;
        double t543 = t415*v3-t407*v2;
        double t550 = t427*v3-t422*v2;
        double t567 = h*v3;
        double t568 = -t250;
        double t637 = t49-t50;
        double t639 = -t39+t40;
        double t645 = -t59*u3+t80*u1;
        double t650 = t59*u2-t101*u1;
        double t659 = -t34*u3+t65*u1;
        double t663 = t34*u2-t88*u1;
        double t669 = -t183*u3+t187*u1;
        double t674 = t183*u2-t195*u1;
        double t679 = -t246*u3+t261*u1;
        double t683 = t246*u2-t275*u1;
        double t693 = -t47*u3+t70*u1;
        double t697 = t47*u2-t93*u1;
        double t703 = -t328*u3+t332*u1;
        double t708 = t328*u2-t340*u1;
        double t713 = -t345*u3+t347*u1;
        double t717 = t345*u2-t352*u1;
        double t726 = -t57*u3+t76*u1;
        double t730 = t57*u2-t99*u1;
        double t736 = -t403*u3+t407*u1;
        double t741 = t403*u2-t415*u1;
        double t746 = -t420*u3+t422*u1;
        double t750 = t420*u2-t427*u1;
        double t758 = t20-t21;
        double t764 = t101*u3-t80*u2;
        double t772 = t88*u3-t65*u2;
        double t779 = t195*u3-t187*u2;
        double t786 = t275*u3-t261*u2;
        double t799 = t93*u3-t70*u2;
        double t806 = t340*u3-t332*u2;
        double t813 = t352*u3-t347*u2;
        double t824 = t99*u3-t76*u2;
        double t831 = t415*u3-t407*u2;
        double t838 = t427*u3-t422*u2;
        double[][] j1 = {{0, 0, 0,
            h*(t6*t9+t13*t16)+2.0*t82*u3-2.0*t103*u2+t106*(h*(-v3*t109+v2*
                    t113)+2.0*t120*u3-2.0*t126*u2-t38*(t131*t109+t135*t113)-2.0*t158*u3+2.0*t167*u2
                    -2.0*t189*u3+2.0*t197*u2-t38*(t263*u3-t277*u2))+t283*(-t284*t113-2.0*t286*u2-
                    t38*(t291*t109+t295*t113)-2.0*t314*u3+2.0*t322*u2-2.0*t334*u3+2.0*t342*u2-t38*(
                    t349*u3-t354*u2))+t360*(t284*t109+2.0*t362*u3-t38*(t367*t109+t371*t113)-2.0*
                    t389*u3+2.0*t397*u2-2.0*t409*u3+2.0*t417*u2-t38*(t424*u3-t429*u2)),
            h*(-t29*t9+t13*t437)-2.0*t443*u3+2.0*t103*u1+t106*(t448*t250+
                    2.0*t126*u1-t38*(t454*t455+t135*t250)+2.0*t463*u3-2.0*t167*u1+2.0*t470*u3-2.0*
                    t197*u1-t38*(-t477*u3+t277*u1))+t283*(h*(v3*t455-v1*t250)-2.0*t488*u3+2.0*t286*
                    u1-t38*(t495*t455+t295*t250)+2.0*t502*u3-2.0*t322*u1+2.0*t509*u3-2.0*t342*u1-
                    t38*(-t516*u3+t354*u1))+t360*(-t448*t455-2.0*t524*u3-t38*(t529*t455+t371*t250)+
                    2.0*t536*u3-2.0*t397*u1+2.0*t543*u3-2.0*t417*u1-t38*(-t550*u3+t429*u1)),
            h*(-t29*t16-t6*t437)+2.0*t443*u2-2.0*t82*u1+t106*(-t567*t568
                    -2.0*t120*u1-t38*(t454*t200+t131*t568)-2.0*t463*u2+2.0*t158*u1-2.0*t470*u2+2.0*
                    t189*u1-t38*(t477*u2-t263*u1))+t283*(t567*t200+2.0*t488*u2-t38*(t495*t200+t291*
                    t568)-2.0*t502*u2+2.0*t314*u1-2.0*t509*u2+2.0*t334*u1-t38*(t516*u2-t349*u1))+
                    t360*(h*(-v2*t200+v1*t568)+2.0*t524*u2-2.0*t362*u1-t38*(t529*t200+t367*t568)
                    -2.0*t536*u2+2.0*t389*u1-2.0*t543*u2+2.0*t409*u1-t38*(t550*u2-t424*u1))
        }};
        double[][] j2 = {{0, 0, 0,
            -h*(t6*t637+t13*t639)-2.0*t645*v3+2.0*t650*v2-t106*(h*(-u3*
                    t204+u2*t461)-t38*(t659*t204+t663*t461)-2.0*t669*v3+2.0*t674*v2-t38*(t679*v3-
                    t683*v2))-t283*(-h*u1*t461-t38*(t693*t204+t697*t461)-2.0*t703*v3+2.0*t708*v2-
                    t38*(t713*v3-t717*v2))-t360*(t488*u1-t38*(t726*t204+t730*t461)-2.0*t736*v3+2.0*
                    t741*v2-t38*(t746*v3-t750*v2)),
            -h*(-t29*t637+t13*t758)+2.0*t764*v3-2.0*t650*v1-t106*(t362*u2-
                    t38*(t772*t119+t663*t156)+2.0*t779*v3-2.0*t674*v1-t38*(-t786*v3+t683*v1))-t283*
                    (h*(u3*t119-u1*t156)-t38*(t799*t119+t697*t156)+2.0*t806*v3-2.0*t708*v1-t38*(-
                    t813*v3+t717*v1))-t360*(-h*u2*t119-t38*(t824*t119+t730*t156)+2.0*t831*v3-2.0*
                    t741*v1-t38*(-t838*v3+t750*v1)),
            -h*(-t29*t639-t6*t758)-2.0*t764*v2+2.0*t645*v1-t106*(-h*u3*
                    t165-t38*(t772*t125+t659*t165)-2.0*t779*v2+2.0*t669*v1-t38*(t786*v2-t679*v1))-
                    t283*(t126*u3-t38*(t799*t125+t693*t165)-2.0*t806*v2+2.0*t703*v1-t38*(t813*v2-
                    t713*v1))-t360*(h*(-u2*t125+u1*t165)-t38*(t824*t125+t726*t165)-2.0*t831*v2+2.0*
                    t736*v1-t38*(t838*v2-t746*v1))
        }};
        jDotB1 = new MatrixImpl(j1);
        jDotB2 = new MatrixImpl(j2);
    }

    public boolean isInequality() {
        return true;
    }

    public Vector getPenalty() {
        Vector3D normal = direction1.cross(direction2).normalize();
        double[] v = {point2.subtract(point1).mult(normal)};
        return new VectorImpl(v);
    }

    public Vector getPenaltyDot() {
        generateJacobian();
        return jacB1.mult(body1State.getVelocities()).add(jacB2.mult(body2State.getVelocities()));
    }

    public Map<GeneralizedBody, Matrix> getJacobian() {
        generateJacobian();
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        result.put(body1State.getOwner(), jacB1);
        result.put(body2State.getOwner(), jacB2);
        return result;
    }

    public Map<GeneralizedBody, Matrix> getJacobianDot() {
        generateJacobianDot();
        Map<GeneralizedBody, Matrix> result = new java.util.HashMap<GeneralizedBody, Matrix>();
        result.put(body1State.getOwner(), jDotB1);
        result.put(body2State.getOwner(), jDotB2);
        return result;
    }

    public int getDimension() {
        return 1;
    }

    public List<SimulationObject> getObjects() {
        List<SimulationObject> result = new java.util.ArrayList<SimulationObject>();
        result.add(body1State.getOwner());
        result.add(body2State.getOwner());
        return result;
    }
}
