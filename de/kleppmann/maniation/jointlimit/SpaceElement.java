package de.kleppmann.maniation.jointlimit;

public class SpaceElement {
    
    public static final double MIN_X = -1.8;
    public static final double MAX_X =  1.8;
    public static final double MIN_Y = -1.8;
    public static final double MAX_Y =  1.8;
    public static final double MIN_Z = -1.8;
    public static final double MAX_Z =  1.8;
    
    public static int creations = 0;
    
    private int level, levelmask, idx, idy, idz;
    private double minx, posx, maxx, miny, posy, maxy, minz, posz, maxz;
    private boolean leaf, satisfied;
    private SpaceElement[][][] children =
        {{{null, null}, {null, null}}, {{null, null}, {null, null}}};
    private double[][][][] values = {{{{}, {}}, {{}, {}}}, {{{}, {}}, {{}, {}}}};
    
    public SpaceElement(Inequalities ineq, SpaceElement parent, boolean highx, boolean highy, boolean highz) {
        creations++;
        if (parent != null) {
            this.level = parent.level - 1;
            assert(level >= 0);
            if (highx) {
                this.minx = parent.posx; this.maxx = parent.maxx; this.idx = parent.idx | (1 << level);
            } else {
                this.minx = parent.minx; this.maxx = parent.posx; this.idx = parent.idx;
            }
            if (highy) {
                this.miny = parent.posy; this.maxy = parent.maxy; this.idy = parent.idy | (1 << level);
            } else {
                this.miny = parent.miny; this.maxy = parent.posy; this.idy = parent.idy;
            }
            if (highz) {
                this.minz = parent.posz; this.maxz = parent.maxz; this.idz = parent.idz | (1 << level);
            } else {
                this.minz = parent.minz; this.maxz = parent.posz; this.idz = parent.idz;
            }
        } else {
            this.level = 31;
            minx = MIN_X; maxx = MAX_X; idx = 0;
            miny = MIN_Y; maxy = MAX_Y; idy = 0;
            minz = MIN_Z; maxz = MAX_Z; idz = 0;
        }
        posx = (minx + maxx) / 2.0;
        posy = (miny + maxy) / 2.0;
        posz = (minz + maxz) / 2.0;
        levelmask = 0x7fffffff;
        for (int i=0; i<level; i++) levelmask ^= 1 << i;
        values[0][0][0] = ineq.eval(minx, miny, minz);
        values[0][0][1] = ineq.eval(minx, miny, maxz);
        values[0][1][0] = ineq.eval(minx, maxy, minz);
        values[0][1][1] = ineq.eval(minx, maxy, maxz);
        values[1][0][0] = ineq.eval(maxx, miny, minz);
        values[1][0][1] = ineq.eval(maxx, miny, maxz);
        values[1][1][0] = ineq.eval(maxx, maxy, minz);
        values[1][1][1] = ineq.eval(maxx, maxy, maxz);
        boolean splitx = (maxx - minx > 0.1) ||
            signsDiffer(values[0][0][0], values[1][0][0]) ||
            signsDiffer(values[0][0][1], values[1][0][1]) ||
            signsDiffer(values[0][1][0], values[1][1][0]) ||
            signsDiffer(values[0][1][1], values[1][1][1]);
        boolean splity = (maxy - miny > 0.1) ||
            signsDiffer(values[0][0][0], values[0][1][0]) ||
            signsDiffer(values[0][0][1], values[0][1][1]) ||
            signsDiffer(values[1][0][0], values[1][1][0]) ||
            signsDiffer(values[1][0][1], values[1][1][1]);
        boolean splitz = (maxz - minz > 0.1) ||
            signsDiffer(values[0][0][0], values[0][0][1]) ||
            signsDiffer(values[0][1][0], values[0][1][1]) ||
            signsDiffer(values[1][0][0], values[1][0][1]) ||
            signsDiffer(values[1][1][0], values[1][1][1]);
        leaf = (!splitx && !splity && !splitz) || (level < 27);
        if (!leaf) {
            children[0][0][0] = new SpaceElement(ineq, this, false, false, false);
            children[0][0][1] = new SpaceElement(ineq, this, false, false, true );
            children[0][1][0] = new SpaceElement(ineq, this, false, true , false);
            children[0][1][1] = new SpaceElement(ineq, this, false, true , true );
            children[1][0][0] = new SpaceElement(ineq, this, true , false, false);
            children[1][0][1] = new SpaceElement(ineq, this, true , false, true );
            children[1][1][0] = new SpaceElement(ineq, this, true , true , false);
            children[1][1][1] = new SpaceElement(ineq, this, true , true , true );
        } else {
            satisfied = true;
            for (int i=0; i<values[0][0][0].length; i++)
                if (values[0][0][0][i] < 0.0) satisfied = false;
        }
    }
    
    private boolean signsDiffer(double[] a1, double[] a2) {
        boolean result = false;
        for (int i=0; i<a1.length; i++)
            if (a1[i]*a2[i] < 0.0) result = true;
        return result;
    }
    
    private boolean isOnSurface() {
        boolean result = true; int sat = 0;
        SpaceElement c;
        c = children[0][0][0]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[0][0][1]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[0][1][0]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[0][1][1]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[1][0][0]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[1][0][1]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[1][1][0]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        c = children[1][1][1]; if (c.leaf) { if (c.satisfied) sat++; } else result = false;
        return result && (sat != 0) && (sat != 8);
    }
    
    public void findSurface() {
        if (leaf) return;
        if (isOnSurface()) {
            System.out.println("r: " + (posx*posx + posy*posy + posz*posz));
        }
        children[0][0][0].findSurface();
        children[0][0][1].findSurface();
        children[0][1][0].findSurface();
        children[0][1][1].findSurface();
        children[1][0][0].findSurface();
        children[1][0][1].findSurface();
        children[1][1][0].findSurface();
        children[1][1][1].findSurface();
    }
}
