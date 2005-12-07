function retval = bones(b)

    q0=[0;0;0;1];
    p0=[0;0;0];
    
    for i=1:rows(b)
        v = b(i, 1:3)' - p0;
        p0 = b(i, 1:3)';
        a = -b(i, 4);
        if (i > 1)
            q = vectortoq([0;1;0], qtransform(qinv(q0), v), a);
            q0 = qmult(q, q0);
            orientation = q'
            %len = sqrt(sumsq(v));
            %diff=qtransform(q0, [0;len;0]) - v
        endif
    endfor

    retval = 0;
endfunction
