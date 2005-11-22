function retval = qtransform(q, v)
    % Transform a vector v by the rotation specified by the quaternion q
    r = qmult(qinv(q), qmult([v(1); v(2); v(3); 0], q));
    retval = [r(1); r(2); r(3)];
endfunction
