function retval = bones(b)

    boneaxis=[0;1;0];
    p0=[0;0;0];
    q0=[1;0;0;0];
    
    for i=1:rows(b)
        v = b(i, 1:3)' - p0;
        p0 = b(i, 1:3)';
        a = -b(i, 4);
        if (i > 1)
            q1 = vectortoq(boneaxis, v, a);
            q = qmult(qinv(q0), q1);
            q0 = q1;
            orientation = q'
        endif
    endfor

    retval = 0;
endfunction
