function retval = bones(b)

    boneaxis=[0;1;0];
    p0=[0;0;0];
    q0=[1;0;0;0];
    
    for i=1:rows(b)
        v = b(i, 1:3)' - p0;
        p0 = b(i, 1:3)';
        a = b(i, 4);
        q1 = vectortoq(boneaxis, v, a);
        orientation = qmult(qinv(q0), q1)';
        q0 = q1;
        printf("<orientation w=\"%+1.6f\" x=\"%+1.6f\" y=\"%+1.6f\" z=\"%+1.6f\"/>  <base x=\"0.00000\" y=\"%1.5f\" z=\"0.00000\"/>\n", orientation, sqrt(sumsq(v)));
    endfor

    retval = 0;
endfunction
